package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.Usuario;
import java.time.LocalDateTime;

public class UsuarioResponse {

    private Integer id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private String papel;
    private LocalDateTime criadoEm;

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

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getCpf() { return cpf; }
    public String getPapel() { return papel; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
