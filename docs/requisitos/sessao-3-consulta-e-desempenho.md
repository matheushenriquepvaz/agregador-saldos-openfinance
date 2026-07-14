# Sessão 3 — Consulta e desempenho

- **Projeto:** Agregador de Saldos Open Finance
- **Data:** 20/04/2026 (segunda-feira), 14h00–14h30
- **Duração:** 30 min
- **Plataforma:** Microsoft Teams
- **Participantes:** Marcela Tavares (PO), Sandra Lima (Atendimento), Daniel Prado (Arquiteto), Roberto Khoury (Gerente)

---

**Marcela:** Hoje o foco é consulta e latência. O atendimento precisa de resposta rápida.

**Sandra:** O problema recorrente é cliente consultar e receber "não encontrado" cedo demais.

**Daniel:** No código, a consulta faz até 3 tentativas antes do 404. Isso reduz falso negativo em janela curta de processamento.

**Roberto:** E para latência em pico?

**Daniel:** Usamos cache-aside no `extrato-consulta`: primeiro cache Caffeine, depois chamada HTTP ao gravador se houver miss.

**Marcela:** Quais parâmetros de cache estão em produção local?

**Daniel:** Cache `extratos`, chave `clienteId`, TTL de 10 minutos e limite de 10.000 entradas.

**Sandra:** Isso já melhora muito o atendimento nas consultas repetidas.

**Daniel:** Sim. Limite atual: as 3 tentativas são imediatas (sem backoff).

---

## Decisões da Sessão 3

1. Consulta do MVP é `GET /extratos/{clienteId}`.
2. Estratégia de leitura: cache-aside.
3. Em miss no cache, consulta chama o gravador via HTTP.
4. Em ausência de dado, faz até 3 tentativas antes de `404`.
5. Cache configurado com TTL 10 min e capacidade de 10.000 entradas.

## Action items

- **Daniel:** avaliar backoff entre tentativas em evolução futura.
- **Marcela:** manter RNF de baixa latência explícito nos requisitos.
- **Sandra:** acompanhar impacto de cache hit no tempo médio de atendimento.

## Glossário acrescentado

- **Cache-aside:** consulta no cache antes da fonte principal.
- **Cache miss:** ausência da chave no cache, exigindo busca no gravador.
- **Retry de consulta:** novas tentativas antes de concluir `404`.
- **TTL:** tempo de vida do item em cache.


