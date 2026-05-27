// ============================================================
// ARQUIVO: ContatoRequest.java
// FUNÇÃO: DTO de entrada para o formulário de contato do site.
// Qualquer visitante pode enviar uma mensagem sem precisar de conta.
//
// FLUXO: Formulário de contato → POST /api/contato
// → ContatoController → ContatoService → salva no banco
//
// EXEMPLO DE JSON recebido:
// {
//   "nome": "Maria",
//   "sobrenome": "Santos",
//   "email": "maria@email.com",
//   "telefone": "(11) 99999-9999",
//   "mensagem": "Gostaria de saber sobre trocas e devoluções."
// }
// ============================================================

package com.galeriaseleta.dto.request;

public class ContatoRequest {

    private String nome;       // Primeiro nome de quem está entrando em contato
    private String sobrenome;  // Sobrenome (opcional)
    private String email;      // Email para retorno
    private String telefone;   // Telefone (opcional)
    private String mensagem;   // Texto da mensagem

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSobrenome() { return sobrenome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}
