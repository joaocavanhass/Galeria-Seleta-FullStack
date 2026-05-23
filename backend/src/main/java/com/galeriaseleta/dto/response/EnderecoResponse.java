package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.Endereco;

public class EnderecoResponse {

    private Integer id;
    private String rua;
    private String cidade;
    private String estado;
    private String cep;
    private Boolean principal;

    public static EnderecoResponse from(Endereco endereco) {
        EnderecoResponse dto = new EnderecoResponse();
        dto.id = endereco.getId();
        dto.rua = endereco.getRua();
        dto.cidade = endereco.getCidade();
        dto.estado = endereco.getEstado();
        dto.cep = endereco.getCep();
        dto.principal = endereco.getPrincipal();
        return dto;
    }

    public Integer getId() { return id; }
    public String getRua() { return rua; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getCep() { return cep; }
    public Boolean getPrincipal() { return principal; }
}
