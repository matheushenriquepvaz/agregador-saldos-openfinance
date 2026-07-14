# ADR-006 — Três perfis de execução (A, B, C)

## Contexto

O projeto precisa ser executável em diferentes ambientes com diferentes restricoes:
1. **Produção/referência** — all Docker, Kafka externo, Redis externo (Perfil A).
2. **Desenvolvimento local** — pura-JVM, sem Docker, tudo embarcado (Perfil B).
3. **Conceitual/prototipagem** — simples demais (Perfil C, event bus in-process).

Sem perfis, o pom.xml fica inchado ou exigir setup pesado toda vez.

## Decisão

Implementar três perfis Maven (`<profile>`) no pom.xml pai:

**Perfil A (plano-a-docker):**
- `spring-boot-starter-data-redis`
- `spring-boot-starter-amqp`
- `spring-kafka`
- `testcontainers`
- Uso: `mvn -Pplano-a-docker ...`

**Perfil B (plano-b-jvm) — ATIVO POR PADRÃO:**
- `spring-boot-starter-cache` + `caffeine`
- `spring-boot-starter-amqp` + `qpid-broker`
- `spring-kafka` + `spring-kafka-test` (EmbeddedKafka)
- Uso: `mvn clean compile` (ativa por padrão)

**Perfil C (plano-c-conceitual):**
- `spring-boot-starter-cache` + `caffeine`
- Event bus in-process (ApplicationEventPublisher ou BlockingQueue).
- Uso: `mvn -Pplano-c-conceitual ...`

## Alternativas consideradas

- **Single profile** — forçar todos a instalar Docker e infrastructura. **Rejeitada por barreira de entrada.**
- **Sem perfis, dependências condicionalizadas** — messy no pom. **Rejeitada.**
- **Três pom.xml separados** — difícil de manter sincronizados. **Rejeitada.**
- **Três perfis Maven** — como adotado. Limpo, versionado junto, escolha no build-time. **Escolhida.**

## Consequências

**Ganhamos:**
- Flexibilidade: novo dev roda localmente em 5 min (Perfil B).
- Referência de produção clara (Perfil A).
- Escalabilidade: mesmo código, diferentes ambientes.
- Testes inclusos em cada perfil (Testcontainers, EmbeddedKafka).

**Abrimos mão de:**
- Build é mais longo quando há muitas dependências.
- Exige documentação clara (qual perfil para quê).

**Fica mais difícil:**
- Debugar diferenças entre perfis (precisa rodar nos dois).
- Sincronizar versions de deps entre perfis.

## Status

Aceita

## Documentação de uso

```bash
# Perfil B (padrão, pura-JVM)
mvn clean install
mvn -pl extrato-ingestor spring-boot:run

# Perfil A (Docker/Testcontainers)
mvn -Pplano-a-docker clean install
docker compose -f infra/docker-compose.yml up -d

# Perfil C (conceitual)
mvn -Pplano-c-conceitual clean install
```

## Implementação no código

- `pom.xml` — seção `<profiles>` com `plano-a-docker`, `plano-b-jvm`, `plano-c-conceitual`.
- `pom.xml` — `<activeByDefault>true</activeByDefault>` no Perfil B.
- `pom.xml` — comentário no topo explicando os perfis e como rodar.
- README.md com tabela de perfis.

