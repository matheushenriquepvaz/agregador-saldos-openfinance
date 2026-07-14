# ADR-005 — Contratos compartilhados em shared-contracts

## Contexto

Com três serviços independentes (ingestor, gravador, consulta), existe risco de divergência de modelo de dados entre produtor e consumidor. O ingestor publica `LancamentoRecebido`, mas qual é o schema? O gravador espera que campos? Sem contrato centralizado, times fazem suposições diferentes, e integrações quebram.

## Decisão

Criar módulo Maven `shared-contracts` com as definições de DTOs/modelos compartilhados:
- Eventos de domínio (ex: `LancamentoRecebido`, `ExtratoAtualizado`).
- Modelos de resposta (ex: `ExtratoResponse`).
- Enums de negócio (ex: tipo de transação).

Todos os três serviços dependem de `shared-contracts` e usam as mesmas classes para serialização/desserialização.

## Alternativas consideradas

- **Sem contrato centralizado** — cada serviço define seu DTO. Flexível, mas frágil: mudança de nomes causa falha silenciosa em JSON. **Rejeitada.**
- **Geração automática de modelos** — ferramentas como OpenAPI generator criariam DTOs do schema. Complexo, exige CI robusto. **Rejeitada por overhead.**
- **Compartilhado em módulo Maven** — como adotado. Simples, versionado junto, garante coerência. **Escolhida.**

## Consequências

**Ganhamos:**
- Single source of truth para modelos.
- Compilação garante compatibilidade.
- Evita divergência silenciosa entre serviços.
- Reutilização sem duplicação de código.

**Abrimos mão de:**
- Acoplamento direto entre artefatos: mudança em `shared-contracts` força rebuild de todos.
- Possibilidade de evolução independente de schema (exige versionamento cuidadoso).

**Fica mais difícil:**
- Evoluir modelo sem quebrar compatibilidade (exige @JsonIgnoreProperties(ignoreUnknown = true) ou similar).

## Status

Aceita

## Versionamento

`shared-contracts` segue versão do projeto pai. Quando houver mudança incompatível:
- Adicionar novo campo com valor default nos antigos (backward-compatible).
- Usar `@JsonProperty(defaultValue = "...")` e getters com default.
- Evitar remover campos; marcar como @Deprecated se necessário.

## Implementação no código

- Módulo `shared-contracts/` no pom.xml pai.
- Dependência em outros módulos: `<dependency><groupId>com.caixa.ada</groupId><artifactId>shared-contracts</artifactId></dependency>`.
- Classes modelo: `LancamentoRecebido`, `ExtratoResponse`, etc. com @Getter/@Setter.
- Gerenciamento de dependencyManagement no pom pai para garantir versão única.
- Build jar: `shared-contracts-0.0.1-SNAPSHOT.jar` reutilizável.

