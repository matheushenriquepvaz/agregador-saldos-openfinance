# User Stories - Consolidador de Extrato Open Finance

> **Documento compilado por:** Product Owner (consolidado para banca)
> **Data:** 10/07/2026
> **Status:** baseline funcional para desenvolvimento e avaliacao arquitetural

Este documento descreve historias de usuario para os tres contextos delimitados do sistema: Ingestao, Consolidacao e Consulta. Cada historia contem prioridade e criterios de aceitacao no formato Dado/Quando/Entao.

## Glossario (linguagem ubiqua)

- **Lancamento:** movimentacao financeira recebida de uma instituicao.
- **eventoId:** identificador unico do evento, usado para rastreabilidade e idempotencia.
- **Transacao:** registro persistido de um lancamento valido.
- **SaldoCliente:** visao consolidada do saldo por cliente.
- **EventoProcessado:** registro de deduplicacao para evitar reprocessamento.
- **Extrato consolidado:** resposta de consulta com saldo e movimentacoes do cliente.
- **Cache Aside:** estrategia em que a aplicacao consulta cache antes da fonte principal.

---

## Epico 1 - Cadastro e validacao de lancamentos (Ingestao)

### US-01 - Receber lancamento financeiro

**Como** sistema de origem de uma instituicao financeira,  
**Quero** enviar um lancamento para o servico de Ingestao,  
**Para** que ele entre no fluxo de consolidacao.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** um payload valido de lancamento,  
  **Quando** a requisicao HTTP for recebida,  
  **Entao** o servico deve aceitar a requisicao e iniciar o fluxo de publicacao do evento.

### US-02 - Validar campos obrigatorios

**Como** servico de Ingestao,  
**Quero** validar os campos obrigatorios de entrada,  
**Para** evitar publicacao de dados incompletos.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** um payload sem `clienteId`, `instituicaoId`, `tipo`, `valor` ou `dataLancamento`,  
  **Quando** a requisicao for processada,  
  **Entao** o servico deve rejeitar com erro de validacao informando os campos invalidos.

### US-03 - Validar regras basicas de negocio

**Como** servico de Ingestao,  
**Quero** validar regras basicas (ex.: valor positivo e tipo valido),  
**Para** manter integridade minima antes de publicar eventos.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** um lancamento com `valor <= 0` ou `tipo` fora de `CREDITO/DEBITO`,  
  **Quando** a validacao for executada,  
  **Entao** a requisicao deve ser rejeitada com mensagem de erro padronizada.

### US-04 - Gerar e propagar identificador unico

**Como** servico de Ingestao,  
**Quero** gerar um `eventoId` unico para cada lancamento aceito,  
**Para** habilitar rastreabilidade e idempotencia no consumo.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** um lancamento valido,  
  **Quando** o evento for montado,  
  **Entao** o campo `eventoId` deve ser gerado em formato UUID e propagado no payload publicado.

### US-05 - Publicar evento em Kafka

**Como** servico de Ingestao,  
**Quero** publicar `LancamentoRecebido` no broker,  
**Para** desacoplar recepcao de lancamentos e consolidacao.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** um lancamento validado,  
  **Quando** a publicacao for realizada,  
  **Entao** o evento deve estar disponivel no topico configurado com os campos de contrato esperados.

---

## Epico 2 - Consolidacao e saldo (Agregador)

### US-06 - Consumir lancamentos recebidos

**Como** servico Agregador,  
**Quero** consumir eventos `LancamentoRecebido`,  
**Para** iniciar o processamento de consolidacao.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** um evento publicado no Kafka,  
  **Quando** o consumidor estiver ativo,  
  **Entao** o Agregador deve receber o evento e iniciar a validacao de idempotencia.

### US-07 - Garantir idempotencia por evento

**Como** servico Agregador,  
**Quero** verificar se o `eventoId` ja foi processado,  
**Para** impedir duplicidade de transacoes e saldo.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** um evento com `eventoId` ja presente em `EventoProcessado`,  
  **Quando** o evento for consumido novamente,  
  **Entao** ele deve ser ignorado sem novos efeitos no banco.

### US-08 - Persistir transacao consolidada

**Como** servico Agregador,  
**Quero** salvar cada lancamento valido na entidade `Transacao`,  
**Para** manter historico consolidado por cliente.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** um evento inedito e valido,  
  **Quando** o processamento ocorrer,  
  **Entao** deve ser criado um registro em `Transacao` com os dados do evento.

### US-09 - Calcular e atualizar saldo do cliente

**Como** servico Agregador,  
**Quero** recalcular o `SaldoCliente` apos cada transacao,  
**Para** disponibilizar visao consolidada correta.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** uma nova transacao de credito ou debito,  
  **Quando** o processamento for concluido,  
  **Entao** `saldoAtual`, `quantidadeTransacoes` e `ultimaAtualizacao` devem ser atualizados.

### US-10 - Registrar evento processado

**Como** servico Agregador,  
**Quero** registrar o `eventoId` em `EventoProcessado`,  
**Para** garantir deduplicacao em entregas futuras.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** um evento processado com sucesso,  
  **Quando** a transacao e o saldo forem persistidos,  
  **Entao** o `eventoId` deve ser registrado como processado no mesmo fluxo transacional.

### US-11 - Expor API de extrato consolidado

**Como** servico de Consulta,  
**Quero** consultar o Agregador por API REST,  
**Para** obter saldo e movimentacoes consolidadas por cliente.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** um `clienteId` existente,  
  **Quando** a API do Agregador for chamada,  
  **Entao** deve retornar extrato consolidado no contrato definido entre os servicos.

---

## Epico 3 - Consulta com cache e invalidacao (Servico de Consulta)

### US-12 - Retornar extrato por cache (cache hit)

**Como** cliente consumidor da API de consulta,  
**Quero** receber extrato rapidamente quando os dados estiverem no Redis,  
**Para** reduzir latencia na consulta.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** a chave `extrato:{clienteId}` existente no Redis,  
  **Quando** `GET /extratos/{clienteId}` for chamado,  
  **Entao** o servico deve retornar o conteudo de cache sem chamar o Agregador.

### US-13 - Buscar no Agregador em cache miss

**Como** servico de Consulta,  
**Quero** consultar o Agregador quando nao houver cache,  
**Para** preencher Redis e atender a requisicao.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** ausencia da chave `extrato:{clienteId}` no Redis,  
  **Quando** a consulta for solicitada,  
  **Entao** o servico deve buscar no Agregador, salvar o resultado no Redis e retornar ao cliente.

### US-14 - Invalidar cache por evento de atualizacao

**Como** servico de Consulta,  
**Quero** remover o cache quando receber evento de alteracao de extrato,  
**Para** evitar retorno de dados desatualizados.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** um evento `ExtratoAtualizado` para determinado cliente,  
  **Quando** o evento for consumido,  
  **Entao** a chave `extrato:{clienteId}` deve ser removida do Redis.

---

## Epico 4 - Confiabilidade, observabilidade e auditoria

### US-15 - Tratar falhas temporarias de processamento

**Como** operacao de plataforma,  
**Quero** aplicar retry em falhas temporarias no consumo/processamento,  
**Para** aumentar resiliencia sem perda de lancamentos.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** falha temporaria de infraestrutura (ex.: timeout de dependencia),  
  **Quando** o evento nao puder ser processado na primeira tentativa,  
  **Entao** o sistema deve reprocessar conforme politica configurada antes de classificar falha definitiva.

### US-16 - Observar saude e desempenho dos servicos

**Como** time de SRE/engenharia,  
**Quero** metricas, logs correlacionados e traces,  
**Para** monitorar latencia, erros e atrasos de consumo.

**Prioridade:** Media

**Criterios de aceitacao**

- **Dado** o funcionamento dos tres servicos,  
  **Quando** houver consulta operacional,  
  **Entao** devem existir metricas de throughput, latencia, taxa de erro e lag de consumidor com correlacao por `eventoId`.

### US-17 - Garantir trilha de auditoria de consultas

**Como** area de compliance,  
**Quero** registrar quem consultou, qual cliente e quando,  
**Para** atender exigencias de auditoria e governanca de dados.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** uma consulta de extrato executada com sucesso ou erro,  
  **Quando** a resposta for finalizada,  
  **Entao** deve existir registro auditavel contendo identificacao do solicitante, `clienteId`, data/hora e resultado da operacao.

### US-18 - Validar contratos entre servicos

**Como** time de engenharia,  
**Quero** executar contract tests para APIs e eventos,  
**Para** evitar quebra de integracao entre Ingestao, Agregador e Consulta.

**Prioridade:** Media

**Criterios de aceitacao**

- **Dado** uma alteracao de contrato de API ou evento,  
  **Quando** o pipeline CI for executado,  
  **Entao** os testes de contrato devem validar compatibilidade antes de promover deploy.

---

## Fora do escopo imediato

- Interface grafica para usuario final.
- Conciliacao contabil completa entre instituicoes (alem da consolidacao de extrato).
- Motor de notificacoes multicanal (push/SMS/e-mail) como componente separado.
