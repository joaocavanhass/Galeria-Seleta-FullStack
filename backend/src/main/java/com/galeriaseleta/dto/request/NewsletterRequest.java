// ============================================================
// ARQUIVO: NewsletterRequest.java
// FUNÇÃO: DTO de entrada para inscrição na newsletter da loja.
// Qualquer visitante pode se inscrever informando apenas o email.
//
// FLUXO: Rodapé do site → POST /api/newsletter/inscrever
// → ContatoController → ContatoService → salva ou ativa no banco
//
// EXEMPLO DE JSON recebido:
// { "email": "joao@email.com" }
// ============================================================

package com.galeriaseleta.dto.request;

public class NewsletterRequest {

    private String email; // Email para receber novidades da loja

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
