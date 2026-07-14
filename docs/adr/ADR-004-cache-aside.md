# ADR-004 — Cache-Aside (Lazy-Load) na consulta

## Contexto

A consulta de extrato é a operação mais frequente do sistema e sofre picos altos. Acessar a BD a cada consulta cria gargalo e latência inaceitável. Precisamos de estratégia de cache que minimize latência sem comprometer consistência demais.

## Decisão

Adotar pattern cache-aside (lazy-load): 

1. Cliente requisita GET `/extratos/{clienteId}`.
2. Consultar cache (Redis/Caffeine).
3. **Cache hit**: retornar direto, sem tocar BD.
4. **Cache miss**: buscar na BD do gravador (via REST), **popula o cache**, retorna ao cliente.
5. TTL (Time-To-Live) configurável para evitar data muito velha.

**Implementação:**
- `extrato-consulta/` com `spring-boot-starter-cache` + Caffeine (Perfil B) ou Redis (Perfil A).
- `@Cacheable` anotação no service que chama BD.
- Fallback: em caso de falha no gravador, retornar erro (sem servir do cache para não ocultar falhas).

## Alternativas consideradas

- **Sem cache** — resposta imediata, sempre consistente, mas latência alta em picos. **Rejeitada.**
- **Write-through** — gravar e cachear na mesma transação. Adiciona latência à escrita. **Rejeitada por não ser aplicável (consulta é leitura pura).**
- **Cache-aside (lazy-load)** — como adotado. Simples, não afeta escrita, baixa latência em leitura. **Escolhida.**

## Consequências

**Ganhamos:**
- Latência baixa em leitura (hit = ~1ms em-memory).
- Sem impacto no ingestor/gravador.
- Simples de implementar com Spring `@Cacheable`.

**Abrimos mão de:**
- Consistência imediata: há janela curta em que cliente vê dado antigo.
- TTL requer fine-tuning (muito curto = pouco benefício; muito longo = data muito velha).

**Fica mais difícil:**
- Debug: cliente vê vs. gravador tem versões diferentes.
- Invalidação manual em caso de correção de dados (exige limpar cache ou esperar TTL).

## Status

Aceita

## Implementação no código

- `extrato-consulta/pom.xml`: `spring-boot-starter-cache`.
- `extrato-consulta/src/main/java/`: configuração de Caffeine (Perfil B) ou Redis (Perfil A).
- `ExtratoService` ou similar: `@Cacheable("extratos")` decorando método que chama BD.
- TTL: `spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=5m` (exemplo).
- Testes: verificam que segundo acesso retorna de cache.
- `docs_referencia/arquitetura.md` — Seção 6: estratégia de cache.



