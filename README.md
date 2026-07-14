# Agregador de Saldos Open Finance

Projeto final de arquitetura e microsservicos para consolidar lancamentos financeiros de multiplas instituicoes e disponibilizar consulta de extrato com baixa latencia, resiliencia e rastreabilidade.

## Objetivo

Entregar um sistema orientado a dominio que:

- receba lancamentos de forma desacoplada;
- consolide saldo por cliente com idempotencia;
- exponha consulta de extrato com estrategia de cache;
- atenda requisitos de confiabilidade, desempenho e compliance.

## Escopo do MVP

Inclui:

- ingestao de lancamentos via API;
- processamento assincrono e consolidacao de saldo;
- consulta de extrato por `clienteId`;
- cache-aside na leitura;
- rastreabilidade de processamento.

Fora do escopo imediato:

- interface grafica para usuario final;
- notificacoes ao cliente multicanal;
- analiticos avancados e motor dedicado de BI.

## Arquitetura da solucao

O desenho arquitetural esta representado em `arquitetura.png` e detalhado em `docs_referencia/arquitetura.md`.

### Bounded contexts

1. **Ingestao (`extrato-ingestor`)**
   - recebe e valida lancamentos;
   - gera identificador de evento;
   - publica evento para processamento.

2. **Gravacao/Consolidacao (`extrato-gravador`)**
   - consome eventos;
   - garante idempotencia por `eventoId`;
   - persiste transacoes e atualiza saldo consolidado.

3. **Consulta (`extrato-consulta`)**
   - expoe API de leitura;
   - aplica cache-aside;
   - consulta a fonte consolidada em caso de cache miss.

4. **Contratos compartilhados (`shared-contracts`)**
   - centraliza DTOs e contratos para reduzir acoplamento sem duplicacao.

## Decisoes arquiteturais principais

- Decomposicao em tres contextos para escalar escrita e leitura de forma independente.
- Comunicacao assincrona por eventos para absorver picos de volumetria.
- Idempotencia no consumidor para evitar duplicidade de transacao/saldo.
- Cache-aside na consulta para reduzir latencia.
- Consistencia eventual entre servicos.

Detalhamento em `docs/adr/`.

## Requisitos nao funcionais cobertos

- **Desempenho:** baixa latencia na consulta, inclusive em picos.
- **Confiabilidade:** retries em falhas temporarias e estrategia para mensagens problematicas.
- **Escalabilidade:** desacoplamento entre recebimento e consolidacao.
- **Auditabilidade:** trilha de processamento e base para trilha de acesso.
- **Compliance:** diretrizes de LGPD e retencao documental conforme requisitos.

## Estrutura do repositorio

- `extrato-ingestor/`: API de ingestao e publicacao de eventos.
- `extrato-gravador/`: processamento, persistencia e consolidacao.
- `extrato-consulta/`: API de consulta e cache.
- `shared-contracts/`: contratos compartilhados.
- `infra/docker-compose.yml`: infraestrutura local (Kafka/Redis).
- `docs_referencia/`: arquitetura, requisitos, ADRs e materiais de apoio.

## Como executar localmente

Com base no `pom.xml` da raiz, o perfil padrao e `plano-b-jvm` (ativo por default). O perfil `plano-a-docker` tambem existe para executar com infraestrutura conteinerizada.

### Build e testes

```powershell
mvn clean test
```

### Infraestrutura (opcional para perfil docker)

```powershell
docker compose -f infra/docker-compose.yml up -d
docker compose -f infra/docker-compose.yml ps
```

### Subir os servicos por modulo (exemplo)

```powershell
mvn -pl extrato-ingestor spring-boot:run
mvn -pl extrato-gravador spring-boot:run
mvn -pl extrato-consulta spring-boot:run
```

## Documentacao da entrega

- `docs/`: arquitetura, ADRs estruturados por decisão.
- `docs_referencia/arquitetura.md`: desenho arquitetural detalhado e fluxos.
- `docs_referencia/adr/adrs.md`: ADRs da referência.
- `docs_referencia/requisitos/TRANSCRICOES-CONSOLIDADAS.md`: consolidado das sessoes de requisitos.
- `AVALIACAO.md`: matriz de aderencia aos criterios de avaliacao.
- `REFLEXAO-USO-IA.md`: reflexao sobre aplicacao de IA no processo.

## Evidencias para banca

Este repositorio contem os artefatos de arquitetura, requisitos e rastreabilidade solicitados para avaliacao final, incluindo:

- visao de contexto e decomposicao de dominios;
- justificativas tecnicas registradas em ADR;
- historias de usuario e requisitos consolidados;
- alinhamento com confiabilidade, desempenho e compliance.
