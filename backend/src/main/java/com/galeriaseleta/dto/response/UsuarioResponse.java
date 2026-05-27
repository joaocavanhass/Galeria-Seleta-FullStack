// ============================================================
// ARQUIVO: UsuarioResponse.java
// FUNÇÃO: DTO de saída com os dados públicos de um usuário.
// Converte o model Usuario em um objeto seguro para retornar ao frontend.
//
// POR QUE NÃO RETORNAMOS O MODEL DIRETAMENTE?
// O model Usuario contém a senha criptografada. Se retornássemos o model
// diretamente, a senha iria no JSON — o que é uma falha de segurança grave.
// O DTO exclui campos sensíveis (como senha) do retorno.
//
// PADRÃO "from()": método estático de fábrica que converte Model → DTO.
// Uso: UsuarioResponse dto = UsuarioResponse.from(usuario);
// ============================================================

package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.Usuario;
import java.time.LocalDateTime;

public class UsuarioResponse {

    private Integer id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private String papel;         // "cliente" ou "admin"
    private LocalDateTime criadoEm;
    // IMPORTANTE: campo "senha" NÃO está aqui — nunca exposta ao frontend

    // Método estático de fábrica: cria um UsuarioResponse a partir de um Usuario
    // Extrai apenas os campos seguros e ignora a senha
    public static UsuarioResponse from(Usuario usuario) {
        UsuarioResponse dto = new UsuarioResponse();
        dto.id = usuario.getId();
        dto.nome = usuario.getNome();
        dto.email = usuario.getEmail();
        dto.telefone = usuario.getTelefone();
        dto.cpf = usuario.getCpf();
        dto.papel = usuario.getPapel();
        dto.criadoEm = usuario.getCriadoEm();
        return dto;
    }

    // Apenas getters — sem setters para manter o DTO imutável após criação
    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getCpf() { return cpf; }
    public String getPapel() { return papel; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
