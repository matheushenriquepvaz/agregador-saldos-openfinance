package com.caixa.ada.contracts;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Detalhe de uma transação do extrato")
public record TransacaoView(
        @Schema(description = "Identificador do evento")
        UUID eventoId,
        @Schema(description = "Identificador da instituição")
        String instituicaoId,
        @Schema(description = "Natureza do lançamento")
        TipoLancamento tipo,
        @Schema(description = "Valor da transação")
        BigDecimal valor,
        @Schema(description = "Descrição da transação")
        String descricao,
        @Schema(description = "Data de lançamento")
        LocalDate dataLancamento,
        @Schema(description = "Data e hora de processamento")
        LocalDateTime dataProcessamento
) {
}
