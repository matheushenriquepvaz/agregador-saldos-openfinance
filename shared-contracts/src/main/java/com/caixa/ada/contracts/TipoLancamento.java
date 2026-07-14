package com.caixa.ada.contracts;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipo de lançamento financeiro")
public enum TipoLancamento {
    CREDITO,
    DEBITO
}
