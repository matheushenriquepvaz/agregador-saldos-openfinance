# ADR-003 — Idempotência garantida por EventoProcessado

## Contexto

Em arquiteturas assíncronas, redelivery é comum: falhas de rede, timeouts, reinício de consumidor, ou retry automático pode fazer o mesmo evento ser entregue mais de uma vez. Sem idempotência, um mesmo `LancamentoRecebido` pode criar múltiplas `Transacao`s e corromper o `SaldoCliente`. Precisamos garantir que o efeito de um evento é aplicado uma única vez, não importa quantas vezes seja entregue.

## Decisão

Manter entidade `EventoProcessado` no banco do gravador: para cada `eventoId`, registrar que já foi processado. Antes de aplicar efeitos (persistir transação e atualizar saldo), consultar se `EventoProcessado` já existe. Se existir, ignorar; se não, processar e registrar.

**Implementação:**
- Entidade `EventoProcessado` com chave primária em `eventoId`.
- Ao consumir evento: `SELECT * FROM evento_processado WHERE evento_id = ?`
- Se encontrado: retornar sucesso (sem novos efeitos).
- Se não encontrado: executar em transação: INSERT `Transacao`, UPDATE `SaldoCliente`, INSERT `EventoProcessado`.
- Todos os três no mesmo fluxo transacional para garantir atomicidade.

## Alternativas consideradas

- **Garantia exactly-once do broker** — Kafka oferece semântica at-least-once, nenhuma garante exactly-once fim-a-fim. **Insuficiente.**
- **Deduplicação apenas em memória** — rápida, mas perde-se após reinício do consumidor. **Rejeitada.**
- **Chave única apenas em `Transacao`** (com `eventoId`)— evita inserts duplicados no banco, mas não protege contra duplicação de lógica ou re-updates do saldo em memória antes de persistir. **Parcial.**
- **Entidade explícita `EventoProcessado`** — como adotado. Oferece controle total, è explícita no modelo de domínio, e audita qual evento foi visto. **Escolhida.**

## Consequências

**Ganhamos:**
- Garantia de idempotência real, robusta a redelivery.
- Auditoria: sabemos exatamente qual evento foi processado quando.
- Simplicidade de lógica: não precisa detectar duplicatas por regra de negócio, apenas por identificador.

**Abrimos mão de:**
- Uma consulta extra por evento (SELECT `EventoProcessado`), mas negligenciável.
- Armazenamento extra (uma linha por evento processado).

**Fica mais difícil:**
- Debugging: se quiser reprocessar um evento, tem que deletar o registro em `EventoProcessado`.

## Status

Aceita

## Implementação no código

- `extrato-gravador/src/main/java/`: entidade JPA `EventoProcessado` com `@Id eventoId`.
- Service no gravador: consulta `EventoProcessado` no início do consumidor.
- Método `@Transactional` que encapsula: consulta, valida, persiste `Transacao`, atualiza `SaldoCliente`, registra `EventoProcessado`.
- Testes em `extrato-gravador/src/test/`: verificam que reprocessamento não duplica transação.
- `docs_referencia/arquitetura.md` — Seção 7: estratégia de idempotência.

