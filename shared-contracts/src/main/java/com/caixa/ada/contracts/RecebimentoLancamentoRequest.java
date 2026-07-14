package com.caixa.ada.contracts;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecebimentoLancamentoRequest(
        String clienteId,
        String instituicaoId,
        TipoLancamento tipo,
        BigDecimal valor,
        String descricao,
        LocalDate dataLancamento
) {
}

