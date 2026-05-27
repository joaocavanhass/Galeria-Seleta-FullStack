// ============================================================
// ARQUIVO: CupomResponse.java
// FUNÇÃO: DTO de saída com os dados públicos de um cupom validado.
// Retornado quando o cliente consulta GET /api/cupons/{codigo}.
// Não expõe o ID interno nem o campo "ativo" (desnecessários para o frontend).
// ============================================================

package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.Cupom;
import java.math.BigDecimal;

public class CupomResponse {

    private String codigo;           // O código do cupom (ex: "DESCONTO10")
    private String tipoDesconto;     // "percentual" ou "fixo"
    private BigDecimal valorDesconto; // Ex: 10.00 (10% ou R$10,00)

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
