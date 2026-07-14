package com.caixa.ada.contracts;

import java.util.UUID;

public record GravarLancamentoCommand(
        UUID eventoId,
        RecebimentoLancamentoRequest dados
) {
}

