# Arquitetura — Agregador de Saldos Open Finance

Documento vivo. Este arquivo descreve apenas o que esta implementado no codigo atual.

## Contextos e fluxo

```text
                 POST /lancamentos
                        |
                        | 202 Accepted + eventoId
                        v
              +---------------------+
              | extrato-ingestor    | bounded context: INGESTAO
              +----------+----------+
                         |
                         | publica GravarLancamentoCommand
                         | topico Kafka: lancamentos.gravar
                         v
              +---------------------+
              | extrato-gravador    | bounded context: GRAVACAO
              | base propria (H2)   |
              +----------+----------+
                         |
                         | GET /extratos/{clienteId}
                         v
              +---------------------+
              | extrato-consulta    | bounded context: CONSULTA
              | cache-aside         |
              +---------------------+
                         |
                         | cache miss -> chama gravador (HTTP)
                         | ate 3 tentativas
                         v
                      404 (se nao encontrar)
```

## Servicos e responsabilidades

### 1) Ingestao (`extrato-ingestor`)
- Endpoint: `POST /lancamentos`.
- Valida payload (`clienteId`, `instituicaoId`, `tipo`, `valor > 0`, `dataLancamento`).
- Gera `eventoId` (UUID).
- Publica `GravarLancamentoCommand` em Kafka (`lancamentos.gravar`).
- Retorna `202 Accepted` com `LancamentoAceito`.

Arquivos-base:
- `extrato-ingestor/src/main/java/com/caixa/ada/ingestor/controller/LancamentoController.java`
- `extrato-ingestor/src/main/java/com/caixa/ada/ingestor/service/IngestaoService.java`
- `extrato-ingestor/src/main/java/com/caixa/ada/ingestor/publisher/LancamentoPublisher.java`

### 2) Gravacao (`extrato-gravador`)
- Consome `GravarLancamentoCommand` do Kafka.
- Aplica idempotencia por `eventoId` via `EventoProcessadoEntity`.
- Persiste `TransacaoEntity`.
- Atualiza agregado `SaldoClienteEntity` (credito/debito).
- Exponibiliza consulta consolidada: `GET /extratos/{clienteId}`.

Arquivos-base:
- `extrato-gravador/src/main/java/com/caixa/ada/gravador/listener/LancamentoListener.java`
- `extrato-gravador/src/main/java/com/caixa/ada/gravador/service/GravadorService.java`
- `extrato-gravador/src/main/java/com/caixa/ada/gravador/controller/ExtratoController.java`

### 3) Consulta (`extrato-consulta`)
- Endpoint: `GET /extratos/{clienteId}`.
- Usa cache-aside com Caffeine (`cache extratos`).
- Em cache miss, chama `extrato-gravador` via HTTP.
- Faz ate 3 tentativas antes de retornar `404`.
- Nao persiste base propria (servico stateless + cache local).

Arquivos-base:
- `extrato-consulta/src/main/java/com/caixa/ada/consulta/controller/ConsultaController.java`
- `extrato-consulta/src/main/java/com/caixa/ada/consulta/service/ConsultaService.java`
- `extrato-consulta/src/main/java/com/caixa/ada/consulta/configuration/CacheConfig.java`
- `extrato-consulta/src/main/java/com/caixa/ada/consulta/client/GravadorClient.java`

## Contratos entre servicos (`shared-contracts`)

Contratos implementados:
- `RecebimentoLancamentoRequest`
- `GravarLancamentoCommand`
- `LancamentoAceito`
- `ExtratoView`
- `TransacaoView`
- `TipoLancamento`

Arquivos-base:
- `shared-contracts/src/main/java/com/caixa/ada/contracts/`

## Persistencia e consistencia

- Base de escrita/consolidacao no `extrato-gravador` (H2 em memoria no perfil atual de desenvolvimento).
- `extrato-ingestor` e `extrato-consulta` sem JPA.
- Consistencia eventual entre ingestao e disponibilidade de leitura.
- Idempotencia no consumidor para evitar reprocessamento de evento.

## Cache e desempenho

- Estrategia: cache-aside no `extrato-consulta`.
- Tecnologia: Caffeine (`@EnableCaching`, `@Cacheable`).
- Chave de cache: `clienteId`.
- Politica atual: `expireAfterWrite(10 minutos)` e `maximumSize(10_000)`.

## Resiliencia implementada

- Retry de consulta no `extrato-consulta` com 3 tentativas.
- Tratamento de `404` do gravador como `Optional.empty()` para permitir novas tentativas.

## Itens nao implementados no codigo atual

- DLQ.
- Circuit breaker.
- Evento de atualizacao de extrato para invalidacao de cache.
- Arquivos Pact versionados em `shared-contracts/src/pacts`.

## Perfil de execucao e infraestrutura

- Perfil A (`plano-a-docker`): Redis, Kafka e AMQP externos (referencia com Docker).
- Perfil B (`plano-b-jvm`, default): Caffeine + Kafka de teste + Qpid embarcado.
- Perfil C (`plano-c-conceitual`): foco conceitual com event bus in-process.

Referencias:
- `pom.xml` (perfis)
- `infra/docker-compose.yml`

## Mapa de decisoes (ADRs)

- `docs/adr/ADR-001-bases-segregadas-por-servico.md`
- `docs/adr/ADR-002-comunicacao-assincrona-kafka.md`
- `docs/adr/ADR-003-idempotencia-eventprocessado.md`
- `docs/adr/ADR-004-cache-aside.md`
- `docs/adr/ADR-005-shared-contracts.md`
- `docs/adr/ADR-006-perfis-execucao.md`

