package com.galeriaseleta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_id")
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_mae_id")
    private Categoria categoriaMae;

    @Column(nullable = false)
    private String nome;

    @Column(name = "nome_url", nullable = false, unique = true)
    private String nomeUrl;

    @Column(nullable = false)
    private Boolean ativo = true;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Categoria getCategoriaMae() { return categoriaMae; }
    public void setCategoriaMae(Categoria categoriaMae) { this.categoriaMae = categoriaMae; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNomeUrl() { return nomeUrl; }
    public void setNomeUrl(String nomeUrl) { this.nomeUrl = nomeUrl; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
