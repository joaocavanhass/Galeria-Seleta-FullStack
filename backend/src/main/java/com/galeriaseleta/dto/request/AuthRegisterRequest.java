// ============================================================
// ARQUIVO: AuthRegisterRequest.java
// FUNÇÃO: DTO de entrada para o cadastro de novos usuários.
// Contém todos os dados que o frontend envia ao criar uma conta.
//
// FLUXO: Formulário de cadastro → POST /api/auth/register
// → AuthController → AuthService.registrar() → salva no banco
//
// EXEMPLO DE JSON recebido:
// {
//   "nome": "João",
//   "email": "joao@email.com",
//   "senha": "minhasenha123",
//   "telefone": "(11) 99999-9999",
//   "cpf": "123.456.789-00"
// }
// ============================================================

package com.galeriaseleta.dto.request;

public class AuthRegisterRequest {

    private String nome;      // Nome do usuário
    private String email;     // Email (será único no banco)
    private String senha;     // Senha (será criptografada pelo BCrypt antes de salvar)
    private String telefone;  // Telefone (opcional)
    private String cpf;       // CPF (opcional)

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}
