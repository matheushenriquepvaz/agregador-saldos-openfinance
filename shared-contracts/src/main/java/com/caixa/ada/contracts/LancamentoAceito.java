package com.caixa.ada.contracts;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Resposta de aceite de um lançamento")
public record LancamentoAceito(
        @Schema(description = "Identificador do evento gerado")
        UUID eventoId,
        @Schema(description = "Momento em que a ingestão foi recebida")
        LocalDateTime dataHoraRecebimento
) {
}
