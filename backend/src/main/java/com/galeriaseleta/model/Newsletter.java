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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "inscrito_em")
    private LocalDateTime inscritoEm = LocalDateTime.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getInscritoEm() { return inscritoEm; }
    public void setInscritoEm(LocalDateTime inscritoEm) { this.inscritoEm = inscritoEm; }
}
