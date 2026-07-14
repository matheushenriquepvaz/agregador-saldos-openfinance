package com.caixa.ada.contracts;

import java.time.LocalDateTime;
import java.util.UUID;

public record ExtratoAtualizadoEvent(
        UUID eventoId,
        String clienteId,
        LocalDateTime dataProcessamento
) {
}

