# Arquitetura e Decisões

Documentação técnica do projeto Agregador de Saldos Open Finance.

## Arquitetura

- [Visão arquitetural atual](arquitetura.md)

## ADRs (Architecture Decision Records)

Decisões arquiteturais registradas e seus trade-offs:

| ADR | Decisão | Status |
|---|---|---|
| [ADR-001](adr/ADR-001-bases-segregadas-por-servico.md) | Bases de dados segregadas por serviço | Aceita |
| [ADR-002](adr/ADR-002-comunicacao-assincrona-kafka.md) | Comunicação assíncrona via Kafka | Aceita |
| [ADR-003](adr/ADR-003-idempotencia-eventprocessado.md) | Idempotência por EventoProcessado | Aceita |
| [ADR-004](adr/ADR-004-cache-aside.md) | Cache-aside na consulta | Aceita |
| [ADR-005](adr/ADR-005-shared-contracts.md) | Contratos compartilhados | Aceita |
| [ADR-006](adr/ADR-006-perfis-execucao.md) | Três perfis de execução (A, B, C) | Aceita |

## Contexto geral

O projeto foi desenvolvido em abordagem orientada a domínio (DDD) com:
- Três bounded contexts segregados (ingestão, gravação, consulta).
- Comunicação assíncrona para absorção de picos.
- Idempotência garantida no consumidor.
- Cache-aside para baixa latência de leitura.

Cada ADR registra o problema, a decisão, alternativas e trade-offs. Para detalhes, consultar o ADR específico.
