package com.caixa.ada.contracts;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ExtratoView(
        String clienteId,
        BigDecimal saldoAtual,
        long quantidadeTransacoes,
        LocalDateTime ultimaAtualizacao,
        List<TransacaoView> transacoes
) {
}

