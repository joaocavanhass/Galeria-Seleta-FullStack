package com.galeriaseleta.dto.request;

public class EnderecoRequest {

    private String rua;
    private String cidade;
    private String estado;
    private String cep;
    private Boolean principal;

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public Boolean getPrincipal() { return principal; }
    public void setPrincipal(Boolean principal) { this.principal = principal; }
}
