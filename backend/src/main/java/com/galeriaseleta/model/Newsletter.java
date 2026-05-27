// ============================================================
// ARQUIVO: Newsletter.java
// FUNÇÃO: Model que representa a tabela "newsletter" no banco.
// Armazena os emails cadastrados para receber novidades da loja.
// O campo "ativo" permite cancelar a inscrição sem deletar o registro.
//
// CONEXÕES: ContatoController tem os endpoints de inscrever e cancelar.
// ContatoService contém a lógica de negócio.
// ============================================================

package com.galeriaseleta.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "newsletter")
public class Newsletter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "newsletter_id")
    private Integer id;

    // unique = true: cada email só pode aparecer uma vez na lista
    @Column(nullable = false, unique = true)
    private String email;

    // true = inscrito ativamente; false = cancelou a inscrição
    // Preferimos desativar a deletar — mantemos o histórico
    @Column(nullable = false)
    private Boolean ativo = true;

    // Data e hora da inscrição inicial
    @Column(name = "inscrito_em")
    private LocalDateTime inscritoEm = LocalDateTime.now();

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getInscritoEm() { return inscritoEm; }
    public void setInscritoEm(LocalDateTime inscritoEm) { this.inscritoEm = inscritoEm; }
}
