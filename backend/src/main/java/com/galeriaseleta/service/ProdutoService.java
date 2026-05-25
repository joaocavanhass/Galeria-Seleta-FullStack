package com.galeriaseleta.service;

import com.galeriaseleta.dto.request.ProdutoRequest;
import com.galeriaseleta.dto.response.PageResponse;
import com.galeriaseleta.dto.response.ProdutoResponse;
import com.galeriaseleta.model.FotoProduto;
import com.galeriaseleta.model.Produto;
import com.galeriaseleta.repository.CategoriaRepository;
import com.galeriaseleta.repository.FotoProdutoRepository;
import com.galeriaseleta.repository.ProdutoRepository;
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

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public PageResponse<ProdutoResponse> listarPaginado(String status, Long categoriaId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (categoriaId != null) {
            return PageResponse.from(produtoRepository.findByCategoriaId(categoriaId.intValue(), pageable), ProdutoResponse::from);
        }
        String filtroStatus = (status == null || status.isBlank()) ? "disponivel" : status;
        return PageResponse.from(produtoRepository.findByStatus(filtroStatus, pageable), ProdutoResponse::from);
    }

    public List<Produto> listarAtivos() {
        return produtoRepository.findByStatus("disponivel");
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));
    }

    public List<Produto> buscarPorCategoria(Long categoriaId) {
        return produtoRepository.findByCategoriaId(categoriaId.intValue());
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Produto> listarNovidades() {
        return produtoRepository.findByNovidade(true);
    }

    public List<Produto> listarPorMenorPreco() {
        return produtoRepository.findAllOrderByPrecoAsc();
    }

    public List<Produto> listarPorMaiorPreco() {
        return produtoRepository.findAllOrderByPrecoDesc();
    }

    public Produto salvar(ProdutoRequest request) {
        Produto produto = new Produto();
        preencherProduto(produto, request);
        return produtoRepository.save(produto);
    }

    public Produto atualizar(Long id, ProdutoRequest request) {
        Produto produto = buscarPorId(id);
        preencherProduto(produto, request);
        return produtoRepository.save(produto);
    }

    public FotoProduto setPrincipalFoto(Long produtoId, String url) {
        Produto produto = buscarPorId(produtoId);
        fotoProdutoRepository.deleteAll(produto.getFotos());
        FotoProduto foto = new FotoProduto();
        foto.setProduto(produto);
        foto.setUrl(url);
        foto.setPrincipal(true);
        foto.setOrdem(0);
        return fotoProdutoRepository.save(foto);
    }

    public void deletar(Long id) {
        Produto produto = buscarPorId(id);
        fotoProdutoRepository.deleteAll(produto.getFotos());
        produtoRepository.deleteById(id.intValue());
    }

    private void preencherProduto(Produto produto, ProdutoRequest request) {
        if (request.getNome() != null) produto.setNome(request.getNome());
        if (request.getDescricao() != null) produto.setDescricao(request.getDescricao());
        if (request.getStatus() != null) produto.setStatus(request.getStatus());
        if (request.getNovidade() != null) produto.setNovidade(request.getNovidade());
        if (request.getPreco() != null) produto.setPreco(request.getPreco());

        if (request.getCategoriaId() != null) {
            categoriaRepository.findById(request.getCategoriaId())
                    .ifPresent(produto::setCategoria);
        }
    }
}
