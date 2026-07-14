# ADR-002 — Comunicação assíncrona via Kafka e eventos

## Contexto

A ingestão de lancamentos precisa aceitar volumetria variável e picos sem bloquear a consolidação de saldo. Se a ingestão esperasse o gravador confirmar cada transação antes de responder, o throughput seria limitado pelo tempo de persistência. Precisamos desacoplar a recepção do processamento.

## Decisão

Adotar Kafka como broker de eventos para comunicação entre Ingestão e Gravador. A ingestão valida e publica evento `LancamentoRecebido`; o gravador consome e processa de forma desacoplada.

**Implementação:**
- `extrato-ingestor/`: publica `LancamentoRecebido` ao Kafka logo após validação.
- `extrato-gravador/`: consome `LancamentoRecebido` e processa atomicamente (persistência + idempotência).
- Perfil B: EmbeddedKafka + `spring-kafka-test`.
- Perfil A: Kafka externo via Docker.

## Alternativas consideradas

- **HTTP síncrono entre serviços** — simples de implementar, mas cria bloqueio: falha no gravador cancela ingestão, e picos atrasam resposta imediatamente. **Rejeitada.**
- **Fila de mensagens simples (sem log distribuído)** — absorve picos, mas sem replayabilidade nem multi-consumidor. **Rejeitada.**
- **Kafka com tópicos** — oferece buffer, replayabilidade, múltiplos consumidores futuros (BI, notificação), e desacoplamento temporal. **Escolhida.**

## Consequências

**Ganhamos:**
- Absorção de picos de volumetria sem bloquear o ingestor.
- Desacoplamento entre produção e consumo.
- Replayabilidade de eventos para reprocessamento ou auditoria.
- Suporte a múltiplos consumidores no futuro (notificação, BI, antifraude).

**Abrimos mão de:**
- Resposta imediata: o cliente sabe que foi aceito, mas não que foi gravado.
- Transação distribuída síncrona (trade-off consciente para escalabilidade).

**Fica mais difícil:**
- Monitorar lag de consumidor e detectar atrasos.
- Coordenar reprocessamento de eventos falhados (exige DLQ em evolução).

## Status

Aceita

## Implementação no código

- `pom.xml`: `spring-kafka` no Perfil A, `EmbeddedKafka` nos testes do Perfil B.
- `extrato-ingestor/`: Spring Kafka producer publicando `LancamentoRecebido`.
- `extrato-gravador/`: Spring Kafka consumer configurado para consumir `LancamentoRecebido`.
- `docs_referencia/arquitetura.md` — Seção 8: fluxo de eventos.

