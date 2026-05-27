// ============================================================
// ARQUIVO: EnderecoRequest.java
// FUNÇÃO: DTO de entrada para adicionar um novo endereço de entrega.
//
// FLUXO: Perfil do usuário ou checkout → POST /api/usuarios/me/enderecos
// → UsuarioController → UsuarioService → salva no banco
//
// EXEMPLO DE JSON recebido:
// {
//   "rua": "Rua das Flores, 123",
//   "cidade": "São Paulo",
//   "estado": "SP",
//   "cep": "01310-100",
//   "principal": true
// }
// ============================================================

package com.galeriaseleta.dto.request;

public class EnderecoRequest {

    private String rua;       // Rua e número (ex: "Rua das Flores, 123")
    private String cidade;    // Cidade (ex: "São Paulo")
    private String estado;    // Estado em sigla (ex: "SP")
    private String cep;       // CEP formatado (ex: "01310-100")
    private Boolean principal; // true = este será o endereço padrão do usuário

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
