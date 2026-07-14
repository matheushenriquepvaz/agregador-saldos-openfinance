# Sessão 5 — Compliance e fechamento

- **Projeto:** Agregador de Saldos Open Finance
- **Data:** 04/05/2026 (segunda-feira), 14h00–14h30
- **Duração:** 30 min
- **Plataforma:** Microsoft Teams
- **Participantes:** Marcela Tavares (PO), Dra. Helena Sasaki (Compliance), Daniel Prado (Arquiteto), Roberto Khoury (Gerente)

---

**Marcela:** Última sessão. Vamos fechar baseline e registrar riscos/regulatórios.

**Helena:** Precisamos garantir governança mínima: clareza de contratos e rastreabilidade técnica entre serviços.

**Daniel:** Isso está coberto no desenho atual: contratos em `shared-contracts`, `eventoId` no fluxo e ADRs documentando decisões.

**Roberto:** O que fica para próximo ciclo?

**Helena:** Auditoria de acesso de consulta e política operacional de retenção automatizada ainda não estão implementadas.

**Daniel:** Também podemos evoluir resiliência operacional (DLQ/circuit breaker), mas isso não faz parte da entrega implementada atual.

**Marcela:** Fechamos então o baseline do que está em produção de desenvolvimento e o backlog técnico.

---

## Decisões da Sessão 5

1. Baseline final é o que está implementado em código e ADRs.
2. Contratos compartilhados e rastreabilidade por `eventoId` ficam como requisitos de governança técnica.
3. Itens de compliance/resiliência não implementados entram no backlog da próxima etapa.

## Action items

- **Marcela:** manter requisitos sincronizados com código e ADRs.
- **Helena:** revisar backlog de compliance para próxima fase.
- **Daniel:** priorizar plano técnico para evoluções de resiliência.

## Glossário acrescentado

- **Baseline de entrega:** conjunto de funcionalidades efetivamente implementadas.
- **Rastreabilidade técnica:** capacidade de seguir o fluxo por `eventoId` entre serviços.
- **Governança de contrato:** manutenção de DTOs compartilhados no módulo `shared-contracts`.
- **Backlog técnico:** itens previstos para evolução fora do escopo atual.


