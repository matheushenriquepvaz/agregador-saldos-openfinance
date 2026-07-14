# Sessão 2 — Emissão (Ingestão)

- **Projeto:** Agregador de Saldos Open Finance
- **Data:** 13/04/2026 (segunda-feira), 14h00–14h30
- **Duração:** 30 min
- **Plataforma:** Microsoft Teams
- **Participantes:** Marcela Tavares (PO), Daniel Prado (Arquiteto), Roberto Khoury (Gerente), Sandra Lima (Atendimento)

---

**Marcela:** Hoje vamos fechar o comportamento da emissão: contrato de entrada, validação e resposta.

**Daniel:** Emissão no nosso caso é ingestão de lançamento, via `POST /lancamentos`, chamada sistema-a-sistema.

**Roberto:** Qual resposta de sucesso vamos usar?

**Daniel:** `202 Accepted`. O serviço aceita, gera `eventoId` (UUID), devolve `dataHoraRecebimento` e publica comando para processamento assíncrono.

**Sandra:** Então a gravação final não é na mesma chamada HTTP?

**Daniel:** Exato. A ingestão valida e publica `GravarLancamentoCommand` no Kafka. A gravação acontece no `extrato-gravador`.

**Marcela:** Quais validações mínimas estão no endpoint?

**Daniel:** `clienteId`, `instituicaoId`, `tipo`, `valor > 0` e `dataLancamento` obrigatórios.

**Roberto:** Fechado, isso atende o MVP de emissão com aceite assíncrono.

---

## Decisões da Sessão 2

1. Emissão é API de ingestão sistema-a-sistema em `POST /lancamentos`.
2. Sucesso da emissão retorna `202 Accepted`.
3. Resposta inclui `eventoId` (UUID) e `dataHoraRecebimento`.
4. Processamento de gravação é assíncrono via Kafka (`lancamentos.gravar`).
5. Validações mínimas obrigatórias aplicadas no ingestor.

## Action items

- **Daniel:** manter contrato de entrada em `shared-contracts`.
- **Marcela:** registrar no README de requisitos que emissão é aceite assíncrono.
- **Sandra:** levar casos de consulta imediata para sessão de desempenho.

## Glossário acrescentado

- **Aceite assíncrono:** request aceita agora e processa em fluxo desacoplado.
- **`202 Accepted`:** código HTTP de aceite, sem bloqueio de persistência.
- **`eventoId`:** identificador único do evento de ingestão.
- **`GravarLancamentoCommand`:** mensagem publicada no Kafka para o gravador.


