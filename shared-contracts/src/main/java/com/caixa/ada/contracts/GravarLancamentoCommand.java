package com.caixa.ada.contracts;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Comando publicado para persistir um lançamento")
public record GravarLancamentoCommand(
        @Schema(description = "Identificador do evento")
        UUID eventoId,
        @Schema(description = "Dados recebidos na ingestão")
        RecebimentoLancamentoRequest dados
) {
}
