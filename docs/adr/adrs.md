# ADRs - Consolidador de Extrato Open Finance

## ADR-001 - Decomposicao em tres bounded contexts

- **Status:** Aceito
- **Data:** 2026-07-10

### Contexto

Precisamos processar lancamentos de varias instituicoes, consolidar saldo por cliente e responder consultas com baixa latencia, mantendo autonomia por responsabilidade.

### Alternativas consideradas

1. Monolito com modulos internos.
2. Dois servicos (escrita + leitura).
3. Tres servicos (Ingestao, Agregador, Consulta).

### Decisao adotada

Adotar **tres bounded contexts** em microsservicos:

- Ingestao;
- Consolidacao (Agregador);
- Consulta.

### Vantagens

- Separacao clara de responsabilidades de dominio.
- Escalabilidade independente por perfil de carga.
- Evolucao de negocio isolada por contexto.
- Menor acoplamento entre fluxo de escrita e leitura.

### Desvantagens e trade-offs

- Maior custo operacional (deploy, observabilidade, redes).
- Mais contratos e pontos de falha distribuidos.
- Exige governanca de eventos e versionamento de APIs.

---

## ADR-002 - Comunicacao assincrona via Kafka

- **Status:** Aceito
- **Data:** 2026-07-10

### Contexto

A ingestao precisa absorver picos sem bloquear a consolidacao. Tambem e necessario desacoplar produtores e consumidores.

### Por que utilizar eventos

Eventos permitem aceite rapido na borda, processamento posterior e elasticidade de consumo conforme capacidade do Agregador.

### Alternativas consideradas

1. HTTP sincrono entre Ingestao e Agregador.
2. Mensageria gerenciada por fila simples (sem log distribuido).
3. Kafka como backbone de eventos.

### Decisao adotada

Utilizar **Kafka** para transporte de eventos de dominio entre servicos.

### Vantagens

- Buffer para picos de volumetria.
- Desacoplamento temporal entre produtor e consumidor.
- Melhor suporte a reprocessamento controlado.
- Integracao natural com multiplos consumidores.

### Trade-offs

- Complexidade de operacao (topicos, particoes, lag, retention).
- Necessidade de idempotencia no consumidor.
- Maior esforco de observabilidade ponta a ponta.

---

## ADR-003 - Estrategia Cache Aside com Redis

- **Status:** Aceito
- **Data:** 2026-07-10

### Contexto

Consulta de extrato e operacao de alta frequencia, com expectativa de resposta rapida.

### Estrategia escolhida

Adotar **Cache Aside** no servico de Consulta com Redis:

- primeiro tenta cache;
- em miss, busca no Agregador;
- salva no cache para proximas consultas.

### Invalidação

Quando o extrato/saldo de um cliente muda, o Agregador publica `ExtratoAtualizado`; o servico de Consulta consome e remove a chave `extrato:{clienteId}`.

### Beneficios

- Reducao de latencia de leitura.
- Menor carga na API do Agregador.
- Simplicidade de implementacao no lado da consulta.

### Trade-offs

- Possivel stale data em janela curta.
- Necessidade de invalidacao correta para consistencia percebida.
- Mais um componente operacional (Redis).

---

## ADR-004 - Idempotencia baseada em EventoProcessado

- **Status:** Aceito
- **Data:** 2026-07-10

### Contexto

Em arquiteturas assincronas, redelivery e comum. Sem idempotencia, eventos duplicados podem duplicar transacoes e corromper saldo.

### Problema de reprocessamento

Um mesmo `eventoId` pode ser entregue mais de uma vez por falhas de rede, timeout de commit, reinicio de consumidor ou retry automatico.

### Abordagem adotada

No Agregador, manter entidade `EventoProcessado` e verificar `eventoId` antes de aplicar efeitos:

- se ja processado, ignorar;
- se novo, persistir transacao + atualizar saldo + registrar processamento.

### Alternativas consideradas

1. Garantia exactly-once fim a fim no broker e no consumidor.
2. Deduplicacao apenas em memoria.
3. Chave unica apenas em `Transacao` sem registro explicito de processamento.

### Motivo da decisao

`EventoProcessado` oferece controle explicito de deduplicacao no dominio, robusto a reinicios e facil de auditar.

### Consequencias

- Exige armazenamento adicional e indexacao por `eventoId`.
- Simplifica rastreabilidade e operacao em cenarios de reprocessamento.

