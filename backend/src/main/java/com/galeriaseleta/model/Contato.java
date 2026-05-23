package com.galeriaseleta.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contatos")
public class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contato_id")
    private Integer id;

    @Column(nullable = false)
    private String nome;

    private String sobrenome;

    @Column(nullable = false)
    private String email;

    private String telefone;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "recebido_em")
    private LocalDateTime recebidoEm = LocalDateTime.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

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

    public LocalDateTime getRecebidoEm() { return recebidoEm; }
    public void setRecebidoEm(LocalDateTime recebidoEm) { this.recebidoEm = recebidoEm; }
}
