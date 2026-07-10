# Arquitetura - Consolidador de Extrato Open Finance

## 1. Visao geral

O **Consolidador de Extrato Open Finance** recebe lancamentos financeiros de multiplas instituicoes, consolida essas movimentacoes por cliente e disponibiliza consulta de extrato com baixa latencia por meio de cache.

### Problema resolvido

Sem consolidacao central, os dados de extrato ficam fragmentados por instituicao e a consulta ao saldo consolidado torna-se lenta e custosa. Esta arquitetura resolve:

- ingestao padronizada de lancamentos;
- processamento assincro com resiliencia;
- consolidacao de saldo por cliente com idempotencia;
- consulta performatica com Redis e invalidacao explicita.

## 2. Contextos delimitados (DDD)

### 2.1 Contexto de Ingestao

**Responsabilidade:** aceitar requisicoes HTTP com lancamentos, validar regras basicas, gerar `eventoId` e publicar em Kafka.

**Nao responsabilidade:** persistencia relacional, consolidacao de saldo, consultas e cache.

**Evento publicado:** `LancamentoRecebido`

```json
{
  "eventoId": "UUID",
  "clienteId": "123",
  "instituicaoId": "CAIXA",
  "tipo": "CREDITO",
  "valor": 1000.00,
  "descricao": "SALARIO",
  "dataLancamento": "2026-07-10"
}
```

### 2.2 Contexto de Consolidacao (Agregador)

**Responsabilidade:** consumir eventos, garantir idempotencia, persistir transacoes, atualizar saldo consolidado e expor API REST para consulta.

**Entidades principais:**

- `Transacao` (`id`, `eventoId`, `clienteId`, `instituicaoId`, `tipo`, `valor`, `descricao`, `dataLancamento`)
- `SaldoCliente` (`clienteId`, `saldoAtual`, `quantidadeTransacoes`, `ultimaAtualizacao`)
- `EventoProcessado` (`eventoId`, `dataProcessamento`)

### 2.3 Contexto de Consulta

**Responsabilidade:** expor APIs de extrato para clientes, usar Redis com Cache Aside e consumir eventos de atualizacao para invalidar cache.

**Nao responsabilidade:** acesso direto ao banco do Agregador.

## 3. Justificativa da divisao dos dominios

A divisao em tres bounded contexts reduz acoplamento e melhora evolucao independente:

- **Ingestao** otimiza disponibilidade e throughput de escrita sem depender do tempo de consolidacao.
- **Consolidacao** concentra regras de negocio criticas (idempotencia e saldo).
- **Consulta** otimiza leitura e escala horizontalmente para picos de acesso.

Com isso, cada contexto escala por caracteristica de carga (write-heavy, CPU/consistencia, read-heavy), com banco segregado e ownership claro.

## 4. Diagrama textual da arquitetura

```text
[Sistema Origem]
      |
      | HTTP POST /lancamentos
      v
[Servico de Ingestao]
      |
      | publica evento LancamentoRecebido
      v
    [Kafka] ------------------------------.
      |                                   |
      v                                   |
[Servico Agregador]                       |
  - idempotencia                          |
  - persistencia                          |
  - saldo consolidado                     |
  - API REST /extratos/{clienteId}        |
      |                                   |
      | publica evento ExtratoAtualizado  |
      '-----------------------------------'
                      |
                      v
              [Servico de Consulta]
              - Cache Aside (Redis)
              - API GET /extratos/{clienteId}
                      |
                      v
                   [Cliente]
```

## 5. Fluxo completo dos eventos

1. Ingestao recebe lancamento e valida formato/regras minimas.
2. Ingestao gera e propaga `eventoId`.
3. Ingestao publica `LancamentoRecebido` no Kafka.
4. Agregador consome o evento.
5. Agregador consulta `EventoProcessado`:
   - se existir, ignora (idempotencia);
   - se nao existir, continua o processamento.
6. Agregador persiste `Transacao`.
7. Agregador recalcula e persiste `SaldoCliente`.
8. Agregador registra `EventoProcessado`.
9. Agregador publica `ExtratoAtualizado` para invalidacao de cache.
10. Consulta consome `ExtratoAtualizado` e remove `extrato:{clienteId}` no Redis.

## 6. Estrategia de cache

Padrao adotado: **Cache Aside** no servico de Consulta.

Fluxo:

1. Cliente chama `GET /extratos/{clienteId}` no servico de Consulta.
2. Consulta busca chave `extrato:{clienteId}` no Redis.
3. Em **cache hit**, retorna diretamente.
4. Em **cache miss**, consulta API do Agregador, armazena no Redis e retorna.

Motivacao: alta frequencia de leitura e necessidade de baixa latencia.

## 7. Estrategia de idempotencia

A idempotencia e garantida no Agregador por `EventoProcessado`:

- chave de negocio: `eventoId`;
- regra: um mesmo `eventoId` nao pode produzir efeitos mais de uma vez;
- comportamento: duplicatas sao reconhecidas e descartadas sem alterar saldo/transacoes.

Beneficio: protege contra redelivery natural de sistemas assincros.

## 8. Estrategia de comunicacao assincrona

Broker: **Kafka**.

- Topico de entrada: eventos de `LancamentoRecebido`.
- Topico de saida do Agregador: eventos de `ExtratoAtualizado`.
- Processamento desacoplado entre produtores e consumidores.
- Suporte a picos de carga com buffer no broker.

## 9. Estrategia de consistencia

Modelo adotado: **consistencia eventual** entre os contextos.

- Ingestao confirma recebimento antes da consolidacao final.
- Consolidacao garante estado correto por idempotencia + persistencia transacional.
- Consulta pode retornar valor em cache ate invalidacao; evento de atualizacao reduz janela de stale data.

Trade-off principal: pequena janela de defasagem em troca de escalabilidade e resiliencia.

## 10. Contract tests

A solucao preve contract tests para reduzir regressao entre servicos:

- **Provider contracts (Agregador):** schema e semantica da API REST de consulta.
- **Consumer contracts (Consulta):** expectativas sobre payload e codigos de resposta do Agregador.
- **Contracts de evento (Kafka):** validacao de schema de `LancamentoRecebido` e `ExtratoAtualizado`.
- **Pipeline CI:** contratos versionados e validados a cada PR.

## 11. Decisoes e trade-offs

- Separar em 3 microsservicos aumenta custo operacional, mas melhora autonomia e escalabilidade.
- Kafka adiciona complexidade (observabilidade, reprocessamento, ordenacao), mas oferece desacoplamento e resiliancia em picos.
- Redis reduz latencia de consulta, mas exige estrategia de invalidacao para evitar respostas desatualizadas.
- Idempotencia por `eventoId` simplifica deduplicacao, mas requer disciplina de geracao e propagacao do identificador unico.
- Consistencia eventual reduz bloqueios sincronos, mas exige monitoramento de atraso entre ingestao e disponibilidade do saldo atualizado.

