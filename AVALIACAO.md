# Auto-avaliação — Agregador de Saldos Open Finance

**Grupo:** Leanderson (Desenvolvedor), Relder (Arquiteto), Matheus (PO)

**Tema/domínio:** Sistema de consolidação de saldos de múltiplas instituições Open Finance com ingestão desacoplada, processamento assíncrono e consulta otimizada. Foco em resiliência a picos de volumetria sem perda de transações.

**Perfil de execução:** A (Docker/Testcontainers) · **Fallbacks usados:** cache-aside (Caffeine), retry na consulta (3 tentativas), integração Kafka com implicit redelivery.

---

## Evidências por critério

### 1. Decomposição de domínio — 5/5

**Critério:** Três bounded contexts segregados com bases próprias. Sem acoplamento por dados direto.

**Evidência:**
- Módulos Maven independentes: `extrato-ingestor/`, `extrato-gravador/`, `extrato-consulta/`, `shared-contracts/`
- BD segregada: gravador usa H2 (`jdbc:h2:mem:gravador`), ingestor e consulta stateless.
- Integração apenas por contrato: `shared-contracts` com DTOs compartilhados.
- Documentação: `docs/arquitetura.md` (fluxo e responsabilidades).
- ADR: `docs/adr/ADR-001-bases-segregadas-por-servico.md` (trade-off autonomia vs. consistência eventual).

### 2. Comunicação assíncrona — 5/5

**Critério:** Producer/consumer desacoplado. Garantia de entrega documental.

**Evidência:**
- Kafka tópico `lancamentos.gravar` (hardcoded em `KafkaConfig`).
- Producer: `extrato-ingestor/.../LancamentoPublisher.java` publica `GravarLancamentoCommand`.
- Consumer: `extrato-gravador/.../LancamentoListener.java` com `@KafkaListener` e grupo `extrato-gravador`.
- Garantia: at-least-once nativa do Kafka; consumidor idempotente cobre o redelivery.
- Documentação: `docs/adr/ADR-002-comunicacao-assincrona-kafka.md`.
- Configuração: `extrato-ingestor/application.yml` e `extrato-gravador/application.yml` (bootstrap-servers, serialization).

### 3. Idempotência e consistência — 5/5

**Critério:** Consumidor idempotente. Sem duplicação de transação/saldo.

**Evidência:**
- Chave: `eventoId` (UUID único gerado na ingestão).
- Entidade: `extrato-gravador/.../entity/EventoProcessadoEntity.java` (deduplicação por `eventoId`).
- Implementação: `extrato-gravador/.../service/GravadorService.java:37-60` — consulta `EventoProcessado`, ignora se já existe, persiste transação+saldo+marcação em @Transactional único.
- Teste: `extrato-gravador/.../service/GravadorServiceTest.java` (deveIgnorarEventoDuplicado).
- ADR: `docs/adr/ADR-003-idempotencia-eventprocessado.md`.

### 4. Cache — 4/5

**Critério:** Estratégia cache-aside explícita. TTL, invalidação e fallback.

**Evidência:**
- Estratégia: Cache-Aside (lazy-load).
- Tecnologia: Caffeine (perfil padrão puro-JVM).
- Configuração: `extrato-consulta/.../configuration/CacheConfig.java` — cache `extratos`, TTL 10 min, size 10.000.
- Implementação: `extrato-consulta/.../service/ConsultaService.java:21` (@Cacheable, key="#clienteId").
- Fallback: cache miss → chamada HTTP ao gravador (GravadorClient).
- Limitação: sem invalidação por evento explícita (fora de escopo atual).
- Teste: `extrato-consulta/.../service/ConsultaServiceTest.java` (hit/miss scenarios).
- ADR: `docs/adr/ADR-004-cache-aside.md`.

*Auto-atribuição 4/5: estratégia completa, sem invalidação por evento.*

### 5. Resiliência — 3/5

**Critério:** Retry com backoff, DLQ, timeout, circuit breaker.

**Evidência:**
- Retry implementado: `extrato-consulta/.../service/ConsultaService.java:13,23-28` — 3 tentativas na consulta (sem backoff/intervalo).
- Timeout: via RestTemplate default (HTTP client).
- **Não implementado:** DLQ (fila morta), circuit breaker, retry automático com backoff no Kafka.
- Teste: `extrato-consulta/.../service/ConsultaServiceTest.java:42-50` (reprocessamento validado).

*Auto-atribuição 3/5: retry básico sem backoff; sem DLQ/circuit breaker.*

### 6. Testabilidade — 4/5

**Critério:** Contract tests executáveis. Testes rodam em perfil puro-JVM sem Docker.

**Evidência:**
- Testes unitários:
  - `extrato-ingestor/.../service/IngestaoServiceTest.java` (validação, publicação).
  - `extrato-gravador/.../service/GravadorServiceTest.java` (idempotência, saldo).
  - `extrato-consulta/.../service/ConsultaServiceTest.java` (retry, cache).
- Pact configurado: `pom.xml` (dependency `au.com.dius.pact:junit5:4.6.14`).
- **Pact files:** infraestrutura pronta; sem arquivos `.pact` versionados no workspace.
- Perfil B (puro-JVM): `pom.xml:98-102` (activeByDefault), EmbeddedKafka, sem dependência de Docker.
- Build: `mvn -Pplano-b-jvm test` roda sem containers.

*Auto-atribuição 4/5: testes unitários completos; Pact infraestrutura sem arquivo de contrato versionado.*

### 7. Decisões arquiteturais — 5/5

**Critério:** ADRs com contexto, alternativas consideradas, decisão e trade-offs reais.

**Evidência:**
- 6 ADRs completas em `docs/adr/`:
  - ADR-001: Bases segregadas (autonomia vs. consistência eventual).
  - ADR-002: Kafka (desacoplamento vs. complexidade operacional).
  - ADR-003: Idempotência via EventoProcessado (simplicidade vs. overhead de consulta).
  - ADR-004: Cache-Aside (latência vs. stale data).
  - ADR-005: Shared-contracts (reutilização vs. acoplamento de versão).
  - ADR-006: Três perfis de execução (flexibilidade vs. versões de deps).
- Estrutura: Contexto, Decisão, Alternativas Consideradas, Consequências, Status.
- Trade-offs: todos explícitos e justificados.

### 8. Uso crítico de IA — 4/5

**Como usamos IA:**
- Consolidação de documentos arquiteturais (templates, seções).
- Refinamento de textos técnicos em sessions e user stories.
- Rasunho de ADRs com orientação de alternativas.
- Revisão de completude de requisitos.

**O que validamos manualmente:**
- Decisões arquiteturais: regressão de alternativas vs. decisão tomada em sessions.
- Numericos críticos: 3 tentativas, 10 min TTL, 4/5/1 de agência/conta/dígito.
- Termos de negócio: alinhamento com sessions originais.
- Compliance: retenção 5 anos, LGPD, auditoria (registrado como fora de escopo MVP).
- Código validado: deployments manuais, testes rodados.

*Documentação: `REFLEXAO-USO-IA.md`.*

*Auto-atribuição 4/5: uso criterioso; validação manual rigorosa; sem sobreavaliação.*

### 9. Execução comprovada — 5/5

**Perfil declarado:** A (Docker/Testcontainers)

**Como rodar:**

```bash
# Infraestrutura
docker compose -f infra/docker-compose.yml up -d

# Build e testes (Perfil A)
mvn -Pplano-a-docker clean install

# Subir serviços (portas 8081, 8082, 8083)
mvn -pl extrato-ingestor spring-boot:run
mvn -pl extrato-gravador spring-boot:run
mvn -pl extrato-consulta spring-boot:run
```

**Documentação:** `README.md` com seção "Como executar localmente".

**Evidência de execução:**
- `pom.xml`: perfis definidos e declarados (plano-a-docker ativo no Perfil A).
- `infra/docker-compose.yml`: Kafka, Redis, Zookeeper.
- `extrato-*/target/`: jars compiladas (`.jar` e `.jar.original`).
- Testes passando: `surefire-reports/` com resultados `TEST-*.xml`.

---

## Opcionais entregues

**Observação:** Grupo de 3 pessoas; sem entregáveis opcionais especiais além da baseline.

- Documentação robusta: `docs/arquitetura.md`, `docs/adr/`, `docs/requisitos/` com 5 sessions + personas + user-stories.
- README estruturado com arquitetura, requisitos, como executar.
- Reflexão crítica sobre uso de IA.
- Transcrições de requisitos consolidadas em estrutura formal.

