// ============================================================
// ARQUIVO: PageResponse.java
// FUNÇÃO: DTO de saída genérico para respostas paginadas.
// Usado quando a API retorna uma lista grande de itens dividida em páginas.
// O <T> é um "tipo genérico" — funciona com qualquer DTO (Produto, Pedido, etc.)
//
// PAGINAÇÃO: em vez de retornar 1000 produtos de uma vez, retornamos
// por exemplo 10 por vez. O frontend escolhe qual página quer.
//
// EXEMPLO DE JSON retornado:
// {
//   "content": [...],    // Lista de itens da página atual
//   "page": 0,           // Número da página atual (começa em 0)
//   "size": 10,          // Quantos itens por página
//   "totalElements": 150, // Total de itens em todas as páginas
//   "totalPages": 15     // Total de páginas disponíveis
// }
//
// USO: PageResponse.from(page, ProdutoResponse::from)
// O segundo parâmetro é uma função de conversão (Model → DTO)
// ============================================================

package com.galeriaseleta.dto.response;

import org.springframework.data.domain.Page; // Objeto de paginação do Spring Data

import java.util.List;
import java.util.function.Function; // Interface funcional: recebe E e retorna T
import java.util.stream.Collectors;

// <T>: tipo genérico — pode ser qualquer DTO (ProdutoResponse, PedidoResponse, etc.)
public class PageResponse<T> {

    private List<T> content;      // Lista dos itens da página atual
    private int page;             // Índice da página atual (base 0)
    private int size;             // Tamanho da página (quantos itens por vez)
    private long totalElements;   // Total de registros no banco com esse filtro
    private int totalPages;       // Total de páginas disponíveis

    // Método genérico estático de fábrica:
    // <E> = tipo original (model, ex: Produto)
    // <T> = tipo convertido (DTO, ex: ProdutoResponse)
    // page = objeto Page do Spring com os dados paginados
    // mapper = função que converte E → T (ex: ProdutoResponse::from)
    public static <E, T> PageResponse<T> from(Page<E> page, Function<E, T> mapper) {
        PageResponse<T> response = new PageResponse<>();

        // Converte a lista de models para lista de DTOs usando a função mapper
        response.content = page.getContent().stream().map(mapper).collect(Collectors.toList());
        response.page = page.getNumber();           // Número da página atual
        response.size = page.getSize();             // Tamanho configurado da página
        response.totalElements = page.getTotalElements(); // Total de registros
        response.totalPages = page.getTotalPages();       // Total de páginas
        return response;
    }

    public List<T> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
}
