// ============================================================
// ARQUIVO: Categoria.java
// FUNÇÃO: Model que representa a tabela "categorias" no banco.
// Suporta hierarquia: uma categoria pode ter uma "categoria mãe",
// permitindo criar subcategorias (ex: Roupas > Camisetas).
//
// RELACIONAMENTOS:
// - Categoria → Categoria (categoriaMae): auto-referência — uma categoria
//   pode pertencer a outra categoria (ManyToOne para si mesma)
//
// CONEXÕES: usada por CategoriaService, CategoriaController e Produto.
// ============================================================

package com.galeriaseleta.model;

// @JsonIgnore: quando este campo for serializado para JSON, ele será omitido.
// Usado para evitar loops infinitos: Categoria → categoriaMae → Categoria → ...
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

    // @JsonIgnore: impede que a categoria mãe seja incluída no JSON de resposta,
    //   evitando loops de serialização (A aponta para B que aponta para A infinitamente)
    // @ManyToOne: muitas categorias podem ter a mesma categoria mãe
    // @JoinColumn(name = "categoria_mae_id"): coluna de chave estrangeira no banco
    // Pode ser null: categorias de nível raiz não têm mãe
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_mae_id")
    private Categoria categoriaMae;

    @Column(nullable = false)
    private String nome; // Ex: "Camisetas"

    // nomeUrl é o "slug" — versão da URL sem acentos ou espaços
    // Ex: "Acessórios" → "acessorios" (usado em /api/categorias/acessorios)
    // unique = true: não podem existir duas categorias com o mesmo slug
    @Column(name = "nome_url", nullable = false, unique = true)
    private String nomeUrl;

    // Define se a categoria está ativa (visível para os usuários)
    @Column(nullable = false)
    private Boolean ativo = true; // Padrão: ativa ao criar

    // Getters e Setters
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
