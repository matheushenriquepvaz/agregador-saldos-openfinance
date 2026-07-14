# Sessão 1 — Kickoff: contexto, problema e escopo

- **Projeto:** Agregador de Saldos Open Finance
- **Data:** 06/04/2026 (segunda-feira), 14h00–14h30
- **Duração:** 30 min
- **Plataforma:** Microsoft Teams
- **Participantes:** Marcela Tavares (PO, facilitadora), Roberto Khoury (Gerente de Produto), Sandra Lima (Coordenadora de Atendimento), Daniel Prado (Arquiteto de Soluções)
- **Ausente:** Dra. Helena Sasaki (compliance — entra na Sessão 5)

---

**Marcela:** Pessoal, kickoff de requisitos. Hoje precisamos fechar problema, escopo e o que vai para MVP.

**Sandra:** A dor é consulta lenta e inconsistente para cliente em pico. Atendimento sofre quando o extrato não volta rápido.

**Roberto:** Isso impacta custo de atendimento e satisfação. Se consulta falha em dia de alto volume, vira incidente de negócio.

**Daniel:** Precisamos separar responsabilidades: receber lançamento, gravar/consolidar e consultar. Se fizer tudo junto, o sistema trava nos picos.

**Marcela:** Então o escopo em alto nível é ingestão assíncrona + consolidação + consulta rápida, certo?

**Daniel:** Exato. No código isso fica em 3 serviços: `extrato-ingestor`, `extrato-gravador` e `extrato-consulta`.

**Sandra:** Para atendimento, o mais crítico é a consulta responder rápido e sem falso "não encontrado" logo após processamento.

**Daniel:** Perfeito. Vamos tratar isso na sessão de desempenho com cache e re-tentativas na consulta.

**Marcela:** Fechando: MVP é receber lançamento, consolidar saldo por cliente e consultar extrato por `clienteId`.

---

## Decisões da Sessão 1

1. O sistema terá três capacidades no MVP: ingestão, gravação/consolidação e consulta.
2. A integração entre ingestão e gravação será assíncrona para suportar picos.
3. A consulta será prioridade de experiência (latência e previsibilidade).
4. Compliance detalhado (retenção/auditoria) será fechado na Sessão 5.

## Action items

- **Marcela:** consolidar linguagem ubiqua da squad.
- **Daniel:** detalhar aceite assíncrono da ingestão na Sessão 2.
- **Sandra:** trazer casos de consulta em pico para Sessão 3.
- **Marcela:** reservar com Helena os temas de compliance para Sessão 5.

## Glossário inicial (linguagem ubíqua)

- **Lançamento:** entrada financeira recebida pelo sistema.
- **Ingestão:** serviço de entrada (`POST /lancamentos`) que valida e publica comando.
- **Consolidação:** atualização de transações e saldo por cliente no gravador.
- **Extrato:** visão consolidada de saldo e últimas transações.
- **Pico de volumetria:** período com aumento abrupto de chamadas e eventos.


