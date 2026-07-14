package com.caixa.ada.contracts;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransacaoView(
        UUID eventoId,
        String instituicaoId,
        TipoLancamento tipo,
        BigDecimal valor,
        String descricao,
        LocalDate dataLancamento,
        LocalDateTime dataProcessamento
) {
}

