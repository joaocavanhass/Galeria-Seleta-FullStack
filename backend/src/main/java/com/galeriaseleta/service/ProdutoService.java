// ============================================================
// ARQUIVO: ProdutoService.java
// FUNÇÃO: Serviço com toda a lógica de negócio dos produtos.
// É o intermediário entre o ProdutoController e o banco de dados.
// O controller recebe a requisição, o service decide o que fazer.
//
// RESPONSABILIDADES:
// - Listar produtos (com filtros de status, categoria, ordenação e paginação)
// - Buscar produto por ID ou nome
// - Listar novidades
// - Criar, atualizar e deletar produtos (admin)
// - Gerenciar fotos dos produtos
//
// CONEXÕES: chamado por ProdutoController.
// Usa ProdutoRepository, CategoriaRepository e FotoProdutoRepository.
// ============================================================

package com.galeriaseleta.service;

import com.galeriaseleta.dto.request.ProdutoRequest;
import com.galeriaseleta.dto.response.PageResponse;
import com.galeriaseleta.dto.response.ProdutoResponse;
import com.galeriaseleta.model.FotoProduto;
import com.galeriaseleta.model.Produto;
import com.galeriaseleta.repository.CategoriaRepository;
import com.galeriaseleta.repository.FotoProdutoRepository;
import com.galeriaseleta.repository.ProdutoRepository;
// PageRequest: cria a configuração de paginação (qual página, quantos itens)
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FotoProdutoRepository fotoProdutoRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository,
                          FotoProdutoRepository fotoProdutoRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.fotoProdutoRepository = fotoProdutoRepository;
    }

    // Retorna todos os produtos sem paginação (usado internamente)
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    // Retorna produtos paginados com filtros opcionais de status e categoria.
    // Se categoriaId for informado, filtra por categoria.
    // Se status for null/vazio, usa "disponivel" como padrão.
    public PageResponse<ProdutoResponse> listarPaginado(String status, Long categoriaId, int page, int size) {
        // PageRequest.of(page, size): cria a paginação com número da página e tamanho
        Pageable pageable = PageRequest.of(page, size);

        if (categoriaId != null) {
            // Filtra por categoria com paginação
            return PageResponse.from(produtoRepository.findByCategoriaId(categoriaId.intValue(), pageable), ProdutoResponse::from);
        }

        // Usa "disponivel" como padrão se status não foi informado
        String filtroStatus = (status == null || status.isBlank()) ? "disponivel" : status;
        return PageResponse.from(produtoRepository.findByStatus(filtroStatus, pageable), ProdutoResponse::from);
    }

    // Retorna apenas os produtos com status "disponivel"
    public List<Produto> listarAtivos() {
        return produtoRepository.findByStatus("disponivel");
    }

    // Busca um produto pelo ID — lança exceção se não encontrar
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));
    }

    // Retorna todos os produtos de uma categoria específica
    public List<Produto> buscarPorCategoria(Long categoriaId) {
        return produtoRepository.findByCategoriaId(categoriaId.intValue());
    }

    // Busca produtos cujo nome contém o termo (case-insensitive)
    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    // Retorna produtos marcados como novidade (novidade = true)
    public List<Produto> listarNovidades() {
        return produtoRepository.findByNovidade(true);
    }

    // Retorna todos os produtos ordenados do menor para o maior preço
    public List<Produto> listarPorMenorPreco() {
        return produtoRepository.findAllOrderByPrecoAsc();
    }

    // Retorna todos os produtos ordenados do maior para o menor preço
    public List<Produto> listarPorMaiorPreco() {
        return produtoRepository.findAllOrderByPrecoDesc();
    }

    // Cria um novo produto a partir do ProdutoRequest do admin
    public Produto salvar(ProdutoRequest request) {
        Produto produto = new Produto();
        preencherProduto(produto, request); // Reutiliza o método de preenchimento
        return produtoRepository.save(produto);
    }

    // Atualiza um produto existente — busca pelo ID e aplica as mudanças
    public Produto atualizar(Long id, ProdutoRequest request) {
        Produto produto = buscarPorId(id);
        preencherProduto(produto, request);
        return produtoRepository.save(produto);
    }

    // Define a foto principal de um produto.
    // Remove todas as fotos antigas e adiciona a nova como principal.
    public FotoProduto setPrincipalFoto(Long produtoId, String url) {
        Produto produto = buscarPorId(produtoId);

        // Remove todas as fotos existentes antes de adicionar a nova
        fotoProdutoRepository.deleteAll(produto.getFotos());

        // Cria e salva a nova foto principal
        FotoProduto foto = new FotoProduto();
        foto.setProduto(produto);
        foto.setUrl(url);
        foto.setPrincipal(true); // Marca como foto principal (capa)
        foto.setOrdem(0);        // Primeira posição na galeria
        return fotoProdutoRepository.save(foto);
    }

    // Remove o produto e todas as suas fotos do banco de dados
    public void deletar(Long id) {
        Produto produto = buscarPorId(id);
        fotoProdutoRepository.deleteAll(produto.getFotos()); // Deleta fotos primeiro (chave estrangeira)
        produtoRepository.deleteById(id.intValue());
    }

    // Método auxiliar: preenche os campos do produto com os dados do request.
    // Só atualiza o campo se o valor não for null (atualização parcial).
    // Isso permite que o admin atualize apenas alguns campos sem sobrescrever os outros.
    private void preencherProduto(Produto produto, ProdutoRequest request) {
        if (request.getNome() != null)     produto.setNome(request.getNome());
        if (request.getDescricao() != null) produto.setDescricao(request.getDescricao());
        if (request.getStatus() != null)   produto.setStatus(request.getStatus());
        if (request.getNovidade() != null)  produto.setNovidade(request.getNovidade());
        if (request.getPreco() != null)    produto.setPreco(request.getPreco());

        // Se informou uma categoria, busca ela no banco e associa ao produto
        if (request.getCategoriaId() != null) {
            categoriaRepository.findById(request.getCategoriaId())
                    .ifPresent(produto::setCategoria); // Equivale a: if(exists) produto.setCategoria(cat)
        }
    }
}
