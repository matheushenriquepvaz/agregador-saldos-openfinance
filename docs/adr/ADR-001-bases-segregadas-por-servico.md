# ADR-001 — Bases de dados segregadas por serviço

## Contexto

A solução de consolidação de saldos Open Finance possui três responsabilidades distintas (ingestão, gravação/consolidação, consulta). Precisamos decidir o modelo de dados e isolamento entre os serviços. Sem segregação, diferentes equipas tentariam coordenar mudanças de schema, criando acoplamento e reduzindo autonomia.

## Decisão

Cada microsserviço é dono de seus próprios dados e possui sua própria base de dados dedicada (H2 no perfil B, PostgreSQL/MySQL no perfil A). Nenhum serviço lê tabelas de outro serviço diretamente. Integração entre serviços é sempre por contrato explícito: eventos (Kafka), REST, ou mensagens.

**Implementação:**
- `extrato-ingestor/`: sem persistência (valida e publica evento).
- `extrato-gravador/`: BD própria com `Transacao`, `SaldoCliente`, `EventoProcessado`.
- `extrato-consulta/`: sem BD persistente (usa cache em-memory ou Redis).
- `shared-contracts/`: modelos compartilhados (DTOs) para contrato entre serviços.

## Alternativas consideradas

- **Base compartilhada** — todos os serviços na mesma BD relacional. Simples no começo, mas cria acoplamento por dados: mudança de schema quebra todos, evolução é coordenada, e os serviços deixam de ser independentes no deploy. **Rejeitada.**
- **Bases por serviço** — como adotado. Mais peças operacionais, consistência eventual entre elas, mas independência real de evolução, deploy e escalabilidade. **Escolhida.**

## Consequências

**Ganhamos:**
- Isolamento real de dados por bounded context.
- Autonomia de deploy: mudança no esquema do gravador não afeta consulta.
- Escalabilidade independente: gravador pode ter BD maior, consulta pode ser estateless.

**Abrimos mão de:**
- Consistência imediata (tratada via eventos e retry).
- Simplicidade de joins e queries cross-serviço (exige coordenação por eventos/REST).

**Fica mais difícil:**
- Debugging de inconsistências entre serviços (exige correlação por eventoId).
- Relatórios que precisam de dados de múltiplos serviços (exige agregação ou denormalização).

## Status

Aceita

## Implementação no código

- `extrato-gravador/pom.xml`: `spring-boot-starter-data-jpa`, `h2` (perfil B).
- `extrato-consulta/pom.xml`: sem JPA, apenas `spring-boot-starter-web` e cache.
- `shared-contracts/src/main/java/`: DTOs reutilizáveis.

