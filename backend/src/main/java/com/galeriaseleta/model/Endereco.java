// ============================================================
// ARQUIVO: Endereco.java
// FUNÇÃO: Model que representa a tabela "enderecos" no banco.
// Um usuário pode ter vários endereços cadastrados.
// Um deles pode ser marcado como "principal" (padrão).
// No checkout, o usuário escolhe qual endereço usar para entrega.
//
// RELACIONAMENTOS:
// - Endereco → Usuario: ManyToOne (um usuário pode ter vários endereços)
//
// CONEXÕES: UsuarioService/UsuarioController gerenciam os endereços do usuário.
// PedidoService usa o endereço escolhido ao criar o pedido.
// ============================================================

package com.galeriaseleta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "enderecos")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "endereco_id")
    private Integer id;

    // @JsonIgnore: ao retornar um endereço em JSON, não inclui o usuário completo.
    //   O frontend não precisa dos dados do usuário dentro de cada endereço.
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String rua; // Ex: "Rua das Flores, 123"

    @Column(nullable = false)
    private String cidade; // Ex: "São Paulo"

    @Column(nullable = false)
    private String estado; // Ex: "SP"

    @Column(nullable = false)
    private String cep; // Ex: "01310-100"

    // Indica se este é o endereço padrão do usuário.
    // No checkout, o endereço principal é pré-selecionado.
    @Column(nullable = false)
    private Boolean principal = false; // Padrão: não é o principal

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public Boolean getPrincipal() { return principal; }
    public void setPrincipal(Boolean principal) { this.principal = principal; }
}
