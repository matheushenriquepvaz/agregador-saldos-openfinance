# Sessão 4 — Confiabilidade e integrações

- **Projeto:** Agregador de Saldos Open Finance
- **Data:** 27/04/2026 (segunda-feira), 14h00–14h30
- **Duração:** 30 min
- **Plataforma:** Microsoft Teams
- **Participantes:** Marcela Tavares (PO), Daniel Prado (Arquiteto), Roberto Khoury (Gerente), Sandra Lima (Atendimento)

---

**Marcela:** Vamos fechar confiabilidade do processamento assíncrono e o que já está coberto.

**Daniel:** Integração entre ingestão e gravação é via Kafka (`lancamentos.gravar`). O gravador consome no grupo `extrato-gravador`.

**Roberto:** Como evitamos lançar efeito duplicado se o mesmo evento chegar de novo?

**Daniel:** Idempotência por `eventoId`: antes de processar, o serviço consulta `EventoProcessadoEntity`. Se já existe, ignora.

**Sandra:** E quando é evento novo?

**Daniel:** O gravador salva transação, atualiza saldo e registra evento processado dentro da mesma transação.

**Marcela:** Então confiabilidade entregue hoje: consumo assíncrono + idempotência transacional.

**Daniel:** Isso. DLQ e políticas mais avançadas de retry ficam para evolução.

---

## Decisões da Sessão 4

1. Integração assíncrona oficial por Kafka no tópico `lancamentos.gravar`.
2. Consumidor único de gravação no grupo `extrato-gravador`.
3. Idempotência obrigatória por `eventoId`.
4. Persistência de transação, saldo e marcação de evento em fluxo transacional.

## Action items

- **Daniel:** manter cobertura de teste para duplicidade de evento.
- **Marcela:** registrar backlog de DLQ/retry avançado para próximo ciclo.
- **Sandra:** monitorar impacto de duplicidade evitada no atendimento.

## Glossário acrescentado

- **Idempotência:** processar evento repetido sem produzir novo efeito.
- **`EventoProcessado`:** registro de deduplicação por `eventoId`.
- **Grupo consumidor:** identidade de consumo Kafka (`extrato-gravador`).
- **Transação de gravação:** unidade atômica para manter consistência do agregado.


