// ============================================================
// ARQUIVO: Usuario.java
// FUNÇÃO: Model (entidade) que representa a tabela "usuarios" no banco de dados.
// Cada campo desta classe corresponde a uma coluna na tabela.
// Quando o Hibernate (ORM) lê uma linha do banco, ele cria um objeto
// desta classe automaticamente — e vice-versa ao salvar.
//
// CONEXÕES: usado por AuthService (login/registro), UsuarioService (perfil),
// Carrinho, Pedido, Endereco (todos têm uma referência ao usuário dono).
// ============================================================

package com.galeriaseleta.model;

// @JsonIgnoreProperties: quando o Jackson (biblioteca JSON) serializar este objeto,
// ele deve ignorar essas propriedades internas do Hibernate para não causar erros.
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// jakarta.persistence.*: importa todas as anotações do JPA (Java Persistence API),
// que é a especificação que o Hibernate implementa para mapear objetos ↔ banco de dados.
import jakarta.persistence.*;
import java.time.LocalDateTime; // Tipo para armazenar data e hora sem fuso horário

// @JsonIgnoreProperties: evita erros de serialização JSON com propriedades internas do Hibernate
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

// @Entity: marca esta classe como uma entidade JPA — ou seja, ela é mapeada para uma tabela
@Entity

// @Table(name = "usuarios"): define o nome da tabela no banco de dados
@Table(name = "usuarios")
public class Usuario {

    // @Id: marca este campo como chave primária da tabela
    // @GeneratedValue(strategy = GenerationType.IDENTITY): o banco gera o ID automaticamente
    //   (equivale ao AUTO_INCREMENT no SQLite/MySQL)
    // @Column(name = "usuario_id"): o nome da coluna no banco é "usuario_id"
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer id;

    // @Column(nullable = false): esta coluna não pode ser nula no banco
    @Column(nullable = false)
    private String nome;

    // unique = true: garante que dois usuários não podem ter o mesmo email no banco
    @Column(nullable = false, unique = true)
    private String email;

    // A senha é armazenada CRIPTOGRAFADA pelo BCrypt — nunca em texto puro
    @Column(nullable = false)
    private String senha;

    // Campos opcionais (nullable por padrão)
    private String telefone;
    private String cpf;

    // Papel do usuário: "cliente" (padrão) ou "admin".
    // Determina o que o usuário pode fazer no sistema.
    @Column(nullable = false)
    private String papel = "cliente"; // Valor padrão ao criar um usuário

    // Data e hora de criação da conta. LocalDateTime.now() define a hora atual como padrão.
    @Column(name = "criado_em")
    private LocalDateTime criadoEm = LocalDateTime.now();

    // ============================================================
    // GETTERS E SETTERS
    // São métodos públicos para ler (get) e alterar (set) os campos privados.
    // Em Java, campos privados não podem ser acessados diretamente de fora da classe,
    // então criamos esses métodos de acesso.
    // Exemplo: usuario.getNome() retorna o nome; usuario.setNome("João") define o nome.
    // ============================================================

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

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

    public String getPapel() { return papel; }
    public void setPapel(String papel) { this.papel = papel; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
