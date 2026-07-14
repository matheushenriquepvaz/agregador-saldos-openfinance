package com.caixa.ada.contracts;

import java.time.LocalDateTime;
import java.util.UUID;

public record LancamentoAceito(
        UUID eventoId,
        LocalDateTime dataHoraRecebimento
) {
}

