package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.Cupom;
import java.math.BigDecimal;

public class CupomResponse {

    private String codigo;
    private String tipoDesconto;
    private BigDecimal valorDesconto;

    public static CupomResponse from(Cupom cupom) {
        CupomResponse dto = new CupomResponse();
        dto.codigo = cupom.getCodigo();
        dto.tipoDesconto = cupom.getTipoDesconto();
        dto.valorDesconto = cupom.getValorDesconto();
        return dto;
    }

    public String getCodigo() { return codigo; }
    public String getTipoDesconto() { return tipoDesconto; }
    public BigDecimal getValorDesconto() { return valorDesconto; }
}
