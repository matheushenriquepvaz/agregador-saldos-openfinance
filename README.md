# Consolidador de Extrato Open Finance

Projeto arquitetural orientado a microsservicos para consolidar lancamentos financeiros de multiplas instituicoes e disponibilizar consultas de extrato com alta performance.

## Visao geral

O sistema recebe lancamentos, consolida saldo por cliente e oferece consulta de extrato com cache. A solucao foi desenhada para:

- alta taxa de leitura;
- resiliencia a picos;
- desacoplamento entre escrita e consulta;
- rastreabilidade e governanca de dados.

## Arquitetura

A arquitetura possui tres microsservicos com responsabilidades segregadas:

1. **Servico de Ingestao**
   - recebe HTTP com lancamentos;
   - valida regras basicas;
   - gera `eventoId`;
   - publica `LancamentoRecebido` no Kafka.

2. **Servico Agregador**
   - consome eventos de ingestao;
   - garante idempotencia com `EventoProcessado`;
   - persiste `Transacao`;
   - atualiza `SaldoCliente`;
   - expoe API REST para consulta pelo servico de Consulta;
   - publica evento `ExtratoAtualizado` para invalidacao de cache.

3. **Servico de Consulta**
   - expoe APIs de consulta;
   - usa Redis com Cache Aside;
   - consulta o Agregador por HTTP;
   - invalida cache ao consumir `ExtratoAtualizado`.

Documentos detalhados:

- `docs/arquitetura.md`
- `docs/adrs.md`
- `user-stories.md`

## Tecnologias

- Java / Spring Boot (microsservicos)
- Apache Kafka (mensageria)
- Redis (cache)
- Banco relacional no Agregador (persistencia de consolidacao)
- Docker e Docker Compose (execucao local no **Perfil A**)
- Testes de contrato (API e eventos)

> A stack exata de versoes pode ser ajustada conforme padrao da turma/projeto final.

## Como executar (Perfil A - Docker)

Fluxo recomendado para avaliacao local:

1. Subir infraestrutura (Kafka, Redis e banco do Agregador).
2. Subir os tres servicos.
3. Publicar/receber lancamentos e consultar extrato.

Comandos de referencia (ajustar conforme nomes reais de containers e compose):

```powershell
docker compose up -d
docker compose ps
```

Se os servicos estiverem separados por compose/projeto:

```powershell
docker compose -f docker-compose.infra.yml up -d
docker compose -f docker-compose.apps.yml up -d
```

## Fluxo de eventos

```text
Ingestao (HTTP) -> Kafka (LancamentoRecebido) -> Agregador (processa/persiste/atualiza saldo)
Agregador -> Kafka (ExtratoAtualizado) -> Consulta (invalida Redis)
Cliente -> Consulta (cache hit/miss) -> Agregador (quando miss)
```

## Atendimento aos criterios da avaliacao

- **Decomposicao por dominio:** 3 bounded contexts (Ingestao, Consolidacao, Consulta).
- **Bases segregadas:** Agregador possui base propria; Consulta usa Redis e nao acessa banco de outro servico.
- **Comunicacao assincrona:** eventos em Kafka entre servicos.
- **Consumidor idempotente:** `EventoProcessado` evita duplicidade por `eventoId`.
- **Cache com invalidacao explicita:** Cache Aside com remocao por evento `ExtratoAtualizado`.
- **Contract Tests:** validacao de contratos REST e eventos no pipeline.
- **ADRs:** registrados em `docs/adrs.md`.
- **README arquitetural:** este documento + detalhamento em `docs/arquitetura.md`.

## Uso de IA no projeto

O projeto utilizou IA como apoio em:

- estruturacao inicial de documentacao arquitetural;
- refinamento de textos tecnicos e padronizacao de criterios;
- revisao de completude dos entregaveis (ADRs, user stories e arquitetura).

A validacao final de decisoes e consistencia tecnica foi realizada pelo time.

## Perfil de execucao B

O **perfil oficial da entrega** e o **Perfil A (Docker)**.

Ainda assim, o perfil B pode existir para execucao local simplificada (sem containers) em ambiente de desenvolvimento, mantendo os mesmos contratos e fluxos.

## Justificativa dos fallbacks utilizados

- **Fallback de consulta:** em cache miss, buscar no Agregador e repopular Redis.
- **Fallback de processamento:** reprocessamento por retry em falhas temporarias.
- **Fallback de consistencia:** invalidacao de cache por evento para reduzir defasagem de leitura.

Esses fallbacks priorizam disponibilidade e desempenho sem comprometer a consistencia de negocio no Agregador.
