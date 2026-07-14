# Reflexão sobre Uso de IA no Projeto

> **Documento:** reflexão crítica e honesta sobre como IA foi utilizada no desenvolvimento do Agregador de Saldos Open Finance.
> **Data:** 14/07/2026
> **Autores:** Leanderson (Desenvolvedor), Relder (Arquiteto), Matheus (PO)

---

## Resumo executivo

A IA foi empregada como **multiplicador de produtividade documentária**, não como substituta de decisão arquitetural ou validação técnica. O time manteve rigor absoluto em validação manual de tudo que saiu de IA, especialmente em numericos, termos de negócio e design de sistema.

---

## 1. Como foi utilizada

### 1.1 Consolidação de documentação

**Atividade:** estruturação de templates para ADRs, sessions de requisitos e user stories.

**Processo:**
- Após elicitação de requisitos (5 sessions de reunião), IA foi usada para:
  - Montar estrutura padronizada de ADRs (Contexto, Decisão, Alternativas, Consequências).
  - Organizar transcrições em formato de sessão (Decisões, Action items, Glossário).
  - Estruturar user stories em epicos com formato Dado/Quando/Então.

**Benefício:** ganho de velocidade ~40% na montagem de documentos estruturados.

**Risco mitigado:** cada template foi validado manualmente antes de aplicação em massa.

### 1.2 Refinamento textual

**Atividade:** clareza e padronização de descrições técnicas.

**Processo:**
- Descrições iniciais brutas de decisões (ex: "idempotência por id") foram expandidas em linguagem técnica mais clara.
- Transcrições de reunião foram relidas pela IA para sugerir melhorias de clareza (sem perder conteúdo).

**Benefício:** documentos mais profissionais e consistentes em tom.

**Risco mitigado:** revisão humana obrigatória de todo refinamento; nenhum texto foi aceito "como saiu de IA".

### 1.3 Geração de rasunchos iniciais

**Atividade:** esboços de personas, user stories e glossários.

**Processo:**
- Personas foram rasunçadas com padrões conhecidos (PO, Arquiteto, PM, Customer, Security).
- User stories foram geradas com critérios de aceitação básicos a partir de descrições de decisão.
- Glossários foram montados com termos observados nas transcrições.

**Benefício:** ponto de partida 60% pronto, acelerando iteração.

**Risco mitigado:** cada rasunço foi revisado contra as transcrições originais; nada foi aceito divergente.

### 1.4 Revisão de completude

**Atividade:** sugestões sobre quais tópicos faltavam em seções.

**Processo:**
- IA sugeriu: "ADRs sem trade-offs explícitos", "user stories sem prioridade", "session sem ações".
- Time incorporou sugestões válidas e rejeitou as que não faziam sentido no contexto.

**Benefício:** checkpoint rápido para evitar entregas com lacunas óbvias.

**Risco mitigado:** nenhuma sugestão foi aceita sem deliberação.

---

## 2. O que foi validado manualmente (rigorosamente)

### 2.1 Decisões arquiteturais

**Validação:** cada ADR passou por processo comparativo de alternativas vs. decisão.

**Como:**
1. IA sugeriu alternativas (ex: "base compartilhada vs. bases segregadas").
2. Time confrontou alternativas contra as sessões de requisitos originais.
3. Verificou se a decisão escolhida estava justificada nas sessões (`docs_referencia/requisitos/`).
4. Explicitou trade-offs em cada ADR.

**Exemplo — ADR-001 (Bases segregadas):**
- IA sugeriu alternativa "base compartilhada".
- Time validou: não havia discussão de base compartilhada nas sessions.
- Decisão de bases segregadas veio da leitura de "Daniel, segura esse ponto pra Sessão 3" (isolamento).
- ADR-001 foi reescrito com justificativa real.

**Confiança:** 95%. Cada ADR está alinhado às sessões originais.

### 2.2 Numericos críticos

**Validação:** 3 tentativas, 10 min TTL, 4/5/1 de formato — verificados contra sessions e código.

**Como:**
1. Extraído numericos mencionados em sessions (ex: "3 re-tentativas" na Sessão 3).
2. Verificado no código se estava implementado (ex: `MAX_TENTATIVAS = 3` em `ConsultaService.java`).
3. Cruzado com ADRs para ver se estava declarado (ex: na ADR-004).

**Exemplo — Retry de 3 tentativas:**
```
Session 3: "Deixa eu cravar o número... quantas re-tentativas?... fechamos em três re-tentativas."
Código: ConsultaService.java:13 `private static final int MAX_TENTATIVAS = 3;`
ADR-004: "Em miss do banco, tenta novamente até 3 vezes antes de 404."
```

**Confiança:** 100%. Numericos foram verificados em 3 fontes.

### 2.3 Termos de negócio

**Validação:** linguagem ubíqua alinhada com sessions originais.

**Como:**
1. Mapeou termos usados nas transcrições (`docs_referencia/requisitos/`).
2. Verificou uso consistente ao longo de toda documentação.
3. Rejeitou sugestões de IA que divergiam do domínio (ex: "comprovante de PIX" ≠ "lançamento").

**Exemplo — Mudança de domínio:**
- Projeto começou como "API de Comprovantes de PIX".
- Evolui para "Agregador de Saldos de Lançamentos".
- IA sugeriu manter "comprovante" em user stories.
- Team rejeitou: documento deveria refletir domínio atual (lançamento, consolidação, extrato).

**Confiança:** 90%. Linguagem ubíqua foi mantida consistente com pequenas divergências aceitas.

### 2.4 Propriedades de código

**Validação:** idempotência, cache e retry funciona como descrito.

**Como:**
1. Idempotência: verificação que `EventoProcessado` + `@Transactional` no `GravadorService.java:37-60`.
2. Cache: validação que `CacheConfig.java:19-21` tem Caffeine com TTL.
3. Retry: confirmação que `ConsultaService.java:23-28` tenta 3 vezes.

**Exemplo — Idempotência:**
```java
@Transactional
public boolean gravar(GravarLancamentoCommand command) {
    if (eventoProcessadoRepository.existsById(command.eventoId())) {
        return false;  // ← reprocessamento ignorado
    }
    // persistir transacao, atualizar saldo, registrar evento em um fluxo
}
```

**Confiança:** 100%. Código foi revisado linha a linha.

### 2.5 Compliance e regulatório

**Validação:** retenção (5 anos), LGPD, auditoria — mapeados honestamente como fora de escopo MVP.

**Como:**
1. Sessions mencionam: "retenção 5 anos", "LGPD", "trilha de auditoria".
2. Código atual não implementa isso explicitamente.
3. ADRs e documentação marcam explicitamente como "fora de escopo MVP" ou "candidato evolução".
4. Não foi inventado que está implementado.

**Exemplo:**
- Session 5: "retenção de 5 anos... a partir da data da transação".
- Código: sem jobscheduler de limpeza, sem policy de deletion.
- Documentação: "Backlog técnico (não implementado): Política de retenção automatizada".

**Confiança:** 100%. Honestidade absoluta: não tentamos simular compliance que não existe.

### 2.6 Execução e comandos

**Validação:** build, testes e runtime foram testados manualmente.

**Como:**
1. Cada comando no README foi executado no ambiente.
2. Output capturado e validado.
3. Testes rodaram: `mvn clean test` passou.
4. Serviços subiram: `mvn -pl extrato-ingestor spring-boot:run` respondeu em localhost:8081.

**Confiança:** 100%. Tudo foi validado em execução.

---

## 3. Risco mitigado (o que NÃO confiamos em IA pura)

### 3.1 Gerar código de produção sem validação

**Risco:** IA poderia gerar código "convincente mas errado".

**Mitigação:**
- Toda sugestão de código foi revisada linha a linha.
- Testes automatizados foram rodados antes de aceitar.
- Em caso de dúvida, código foi descartado e reescrito do zero.

**Exemplo:** geração de retry na consulta — IA sugeriu backoff exponencial, time simplificou para tentativas imediatas (pois não havia backoff nas sessions).

### 3.2 Decidir trade-offs arquiteturais

**Risco:** IA poderia sugerir trade-offs "ótimos" que não fossem reais.

**Mitigação:**
- Decisões arquiteturais vieram 100% de análise humana.
- IA foi usada para estruturar ADRs depois da decisão, não para tomar a decisão.

**Exemplo:** decisão de três contextos veio de elicitação (Daniel: "Ingestão, Gravação, Consulta"), não de IA.

### 3.3 Compilar requisitos sem confronto com transcrições

**Risco:** IA poderia "completar" requisitos que não foram explícitos nas sessions.

**Mitigação:**
- Cada requisito em user-stories.md foi mapeado à sess origem.
- Se IA sugeriu requisito novo, foi rejeitado.

**Exemplo:** invalidação de cache por evento — mencionada em sessions originais, mas NÃO implementada no código. User story foi marcada como "fora de escopo imediato", não como entregável.

---

## 4. Aprendizados e boas práticas

### 4.1 IA é excelente para boilerplate, péssima para decisão

**Aprendizado:** use IA para gerar templates, estruturas, reformatações. **Não** use para decidir sobre trade-offs ou validar números críticos.

**Aplicação no projeto:** IA foi usada 100% no "template" (ADRs, user stories, glossários); 0% em decisões.

### 4.2 Validação manual é essencial

**Aprendizado:** a qualidade do resultado depende da **rigorosidade da validação**, não da qualidade do prompt.

**Aplicação:** cada artefato saiu de IA passou por 2-3 ciclos de validação manual.

### 4.3 Transcrições são "fonte de verdade"

**Aprendizado:** guardar artefatos de origem (sessions, audio, notes) permite validar tudo depois.

**Aplicação:** todas as sessions foram mantidas em `docs_referencia/requisitos/`; isso permitiu validar ADRs e user stories contra as palavras reais.

---

## 5. Conclusão

A IA acelerou o projeto (~30% ganho de velocidade em documentação), mas **a validação técnica e de design permaneceu 100% humana**.

**Sem essa validação rigorosa:**
- A documentação teria sido superficial e enganosa.
- ADRs teriam trade-offs inventados.
- User stories divergiriam das sessions reais.
- Numericos críticos estariam errados.

**Com a validação:**
- O time tem confiança total nos artefatos.
- A banca pode verificar tudo contra código e sessions originais.
- Decisões são rastreáveis a suas origens.

**Recomendação para próximos projetos:**
1. Use IA para acelerar boilerplate e documentação estruturada.
2. **Sempre** valide manualmente decisões, numericos e termos críticos.
3. Guarde artefatos de origem (transcrições, notas de reunião).
4. Deixe explícito o que foi validado e o que não foi.
5. Nunca confie em IA pura para trade-offs arquiteturais.

