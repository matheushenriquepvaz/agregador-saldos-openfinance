# Personas — Agregador de Saldos Open Finance

Fichas das pessoas que participam das reunioes de elicitacao de requisitos. Use-as para entender de onde vem cada fala nas transcricoes e por que cada uma defende o que defende. Mantem voz consistente ao longo das cinco sessoes.

> Contexto institucional: todos sao colaboradores (ou prestadores) da Caixa Economica Federal, lotados na Diretoria de Meios de Pagamento e areas correlatas. As reunioes ocorrem em abril de 2026, semanais, via Microsoft Teams.

---

## Marcela Tavares — Product Owner, Agregacao de Extratos

- **Papel:** facilita as reunioes, conduz pauta, traduz negocio <-> tecnico e compila artefatos finais de requisitos.
- **Objetivos:** entregar MVP de ingestao + consolidacao + consulta com clareza de escopo e priorizacao.
- **Jeito de falar:** estruturada, recapitula decisoes e fecha pendencias com checklist.
- **Vieses (importante):** sob pressao de prazo, tende a resumir demais e pode reduzir nuance tecnica em textos de consolidacao.

## Roberto Khoury — Gerente de Produto de Meios de Pagamento

- **Papel:** patrocinador de negocio e responsavel por priorizacao do MVP.
- **Objetivos:** reduzir dor de atendimento, melhorar tempo de resposta na consulta e diminuir recontato.
- **Jeito de falar:** direto, orientado a impacto de cliente e indicador operacional.
- **Vieses:** prioriza entregas com efeito visivel para cliente e pode empurrar itens de engenharia para fase posterior.

## Dra. Helena Sasaki — Especialista em Compliance e Regulacao

- **Papel:** guardia regulatoria (LGPD, auditoria, governanca de dados).
- **Objetivos:** garantir controles minimos de rastreabilidade e conformidade documental.
- **Jeito de falar:** precisa, formal, exige registro claro de decisoes e limites de escopo.
- **Vieses:** conservadora por desenho; prefere controle mais rigido quando houver ambiguidade.

## Daniel Prado — Arquiteto de Solucoes

- **Papel:** define arquitetura tecnica e escolhas de integracao entre servicos.
- **Objetivos:** sustentar picos sem travar fluxo, manter idempotencia na gravacao e baixa latencia na consulta.
- **Jeito de falar:** orientado a cenarios de falha e trade-offs; explica impacto tecnico em linguagem de negocio.
- **Vieses:** otimiza para robustez e desacoplamento, mesmo com aumento de complexidade operacional.

## Sandra Lima — Coordenadora de Atendimento / Canais

- **Papel:** representa operacao de atendimento e experiencia de ponta no app/call center.
- **Objetivos:** consulta de extrato rapida e previsivel, especialmente em horarios de pico.
- **Jeito de falar:** concreta, baseada em caso real de atendimento e dor do cliente final.
- **Vieses:** prioriza resposta imediata e pode subestimar custo tecnico de requisitos de baixa latencia.

## Téo Mendonca — Analista de Seguranca / Antifraude

- **Papel:** representa seguranca operacional e monitoramento de risco.
- **Objetivos:** garantir rastreabilidade tecnica e apoiar evolucoes de confiabilidade.
- **Jeito de falar:** pragmatico, focado em auditabilidade e observabilidade de fluxo.
- **Vieses:** tende a defender controles extras cedo, mesmo quando ainda estao fora do MVP implementado.

