# User Stories - Consolidador de Extrato Open Finance

> **Documento compilado por:** Product Owner (consolidado para banca)
> **Data:** 14/07/2026
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

- **Dado** um lancamento com `valor <= 0` ou `tipo` ausente,  
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

### US-05 - Publicar comando no Kafka

**Como** servico de Ingestao,  
**Quero** publicar `GravarLancamentoCommand` no broker,  
**Para** desacoplar recepcao de lancamentos e consolidacao.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** um lancamento validado,  
  **Quando** a publicacao for realizada,  
  **Entao** a mensagem deve estar disponivel no topico `lancamentos.gravar` com os campos de contrato esperados.

---

## Epico 2 - Consolidacao e saldo (Agregador)

### US-06 - Consumir comandos recebidos

**Como** servico Agregador,  
**Quero** consumir comandos `GravarLancamentoCommand`,  
**Para** iniciar o processamento de consolidacao.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** uma mensagem publicada no Kafka,  
  **Quando** o consumidor estiver ativo,  
  **Entao** o Agregador deve receber a mensagem e iniciar validacao de idempotencia.

### US-07 - Garantir idempotencia por evento

**Como** servico Agregador,  
**Quero** verificar se o `eventoId` ja foi processado,  
**Para** impedir duplicidade de transacoes e saldo.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** um comando com `eventoId` ja presente em `EventoProcessado`,  
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

## Epico 3 - Consulta com cache (Servico de Consulta)

### US-12 - Retornar extrato por cache (cache hit)

**Como** cliente consumidor da API de consulta,  
**Quero** receber extrato rapidamente quando os dados estiverem no cache,  
**Para** reduzir latencia na consulta.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** a chave de cliente existente no cache `extratos`,  
  **Quando** `GET /extratos/{clienteId}` for chamado,  
  **Entao** o servico deve retornar o conteudo de cache sem chamar o Agregador.

### US-13 - Buscar no Agregador em cache miss

**Como** servico de Consulta,  
**Quero** consultar o Agregador quando nao houver cache,  
**Para** preencher cache e atender a requisicao.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** ausencia da chave no cache,  
  **Quando** a consulta for solicitada,  
  **Entao** o servico deve buscar no Agregador, salvar o resultado no cache e retornar ao cliente.

### US-14 - Re-tentar antes do 404

**Como** servico de Consulta,  
**Quero** realizar novas tentativas antes de responder nao encontrado,  
**Para** reduzir falso negativo em janela curta de processamento.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** ausencia de extrato na primeira tentativa,  
  **Quando** o fluxo de consulta continuar,  
  **Entao** o servico deve tentar novamente ate 3 vezes antes de retornar `404`.

---

## Epico 4 - Confiabilidade, qualidade e governanca

### US-15 - Tratar falhas temporarias de consulta

**Como** operacao de plataforma,  
**Quero** aplicar retries na consulta ao Agregador,  
**Para** aumentar resiliencia sem perda de experiencia do cliente.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** falha temporaria ou ausencia de dado no momento da leitura,  
  **Quando** a consulta for executada,  
  **Entao** o sistema deve tentar novamente conforme politica configurada (3 tentativas).

### US-16 - Expor saude basica dos servicos

**Como** time de engenharia,  
**Quero** consultar endpoints de saude e informacao,  
**Para** validar disponibilidade minima dos servicos.

**Prioridade:** Media

**Criterios de aceitacao**

- **Dado** os servicos em execucao,  
  **Quando** os endpoints de actuator forem chamados,  
  **Entao** devem retornar informacoes de `health` e `info`.

### US-17 - Centralizar contratos entre servicos

**Como** time de engenharia,  
**Quero** manter contratos em modulo compartilhado,  
**Para** reduzir quebra de integracao entre Ingestao, Agregador e Consulta.

**Prioridade:** Alta

**Criterios de aceitacao**

- **Dado** os servicos do projeto,  
  **Quando** compilados,  
  **Entao** devem usar DTOs e contratos definidos em `shared-contracts`.

### US-18 - Cobrir regras criticas com testes automatizados

**Como** time de engenharia,  
**Quero** manter testes para fluxo principal,  
**Para** evitar regressao funcional.

**Prioridade:** Media

**Criterios de aceitacao**

- **Dado** alteracoes nos servicos,  
  **Quando** os testes forem executados,  
  **Entao** devem validar ingestao (validacao/publicacao), gravacao (idempotencia/saldo) e consulta (tentativas/cache).

---

## Fora do escopo imediato

- DLQ para mensagens envenenadas.
- Circuit breaker entre consulta e gravador.
- Invalidação de cache por evento de atualização.
- Interface grafica para usuario final.

