package com.caixa.ada.contracts;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Payload enviado para registrar um lançamento")
public record RecebimentoLancamentoRequest(
        @Schema(description = "Identificador do cliente")
        String clienteId,
        @Schema(description = "Identificador da instituição de origem")
        String instituicaoId,
        @Schema(description = "Tipo do lançamento")
        TipoLancamento tipo,
        @Schema(description = "Valor do lançamento")
        BigDecimal valor,
        @Schema(description = "Descrição livre do lançamento")
        String descricao,
        @Schema(description = "Data contábil do lançamento")
        LocalDate dataLancamento
) {
}
