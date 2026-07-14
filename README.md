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

## Uso crítico de IA no projeto

### Como foi utilizada

A IA foi empregada como ferramenta de apoio ao processo de engenharia, não como fonte de verdade arquitetural:

- **Consolidação de documentação:** estruturacao de templates para ADRs, sessions de requisitos e user stories.
- **Refinamento textual:** clareza e padronizacao de descrições tecnicas em documentos compilados.
- **Geração de rasunchos iniciais:** user stories com formato Dado/Quando/Entao; personas baseadas em padrões de contexto.
- **Revisão de completude:** sugestões sobre quais tópicos faltavam em seções de requisitos ou arquitetura.

### O que foi validado manualmente (rigorosamente)

1. **Decisões arquiteturais:** cada ADR passou por validacao comparativa — alternativas sugeridas vs. decisão tomada nas sessões de requisitos. Trade-offs reais explicitados.

2. **Numericos críticos:** 3 tentativas de retry, 10 minutos de TTL, 4/5/1 de formato de agência/conta/dígito — verificados contra sessions e código.

3. **Termos de negócio:** linguagem ubíqua ("lançamento", "consolidação", "extrato", "eventoId") alinhada com sessions originais em `docs_referencia/requisitos/`.

4. **Propriedades de código:**
   - Idempotência: verificação manual que `EventoProcessado` e `@Transactional` funcionam juntos.
   - Cache: validação de que Caffeine com TTL está configurado.
   - Retry: confirmação de que `ConsultaService` tenta 3 vezes.

5. **Compliance e regulatório:** retenção (5 anos), LGPD, auditoria — mapeados como fora de escopo MVP, não inventados como implementados.

6. **Execução:** comandos de build e run foram testados manualmente no ambiente.

### Risco mitigado

**Não confiamos em IA pura para:**
- Gerar código de produção sem validação (toda geração de código foi revisada e testada).
- Decidir trade-offs arquiteturais (decisões vieram de análise humana de alternativas).
- Compilar requisitos sem confronto com transcrições originais (cada requisito foi mapeado à sessão de origem).

### Conclusão crítica

A IA acelerou o processo de documentação e estruturação, mas a **validação técnica e de design foi e permanecer exclusivamente humana**. Sem essa validação rigorosa, a documentação teria sido superficial e enganosa. O time manteve a IA como ferramenta de produtividade, não como autoridade técnica.

Referência completa: `REFLEXAO-USO-IA.md`.
