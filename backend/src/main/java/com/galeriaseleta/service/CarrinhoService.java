package com.galeriaseleta.service;

import com.galeriaseleta.dto.request.AdicionarCarrinhoRequest;
import com.galeriaseleta.dto.request.AtualizarCarrinhoRequest;
import com.galeriaseleta.dto.response.CarrinhoItemResponse;
import com.galeriaseleta.dto.response.CarrinhoResponse;
import com.galeriaseleta.model.Carrinho;
import com.galeriaseleta.model.Produto;
import com.galeriaseleta.model.Usuario;
import com.galeriaseleta.repository.CarrinhoRepository;
import com.galeriaseleta.repository.ProdutoRepository;
import com.galeriaseleta.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarrinhoService(CarrinhoRepository carrinhoRepository,
                           ProdutoRepository produtoRepository,
                           UsuarioRepository usuarioRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public CarrinhoResponse obterCarrinho(Long usuarioId) {
        List<Carrinho> itens = carrinhoRepository.findByUsuarioId(usuarioId.intValue());
        return CarrinhoResponse.from(itens);
    }

    public CarrinhoItemResponse adicionarItem(Long usuarioId, AdicionarCarrinhoRequest request) {
        int qtd = request.getQuantidade() != null && request.getQuantidade() > 0 ? request.getQuantidade() : 1;

        Carrinho item = carrinhoRepository
                .findByUsuarioIdAndProdutoId(usuarioId.intValue(), request.getProdutoId().intValue())
                .map(existente -> {
                    existente.setQuantidade(existente.getQuantidade() + qtd);
                    return carrinhoRepository.save(existente);
                })
                .orElseGet(() -> {
                    Usuario usuario = usuarioRepository.findById(usuarioId.intValue())
                            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + usuarioId));
                    Produto produto = produtoRepository.findById(request.getProdutoId().intValue())
                            .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + request.getProdutoId()));

                    Carrinho novoItem = new Carrinho();
                    novoItem.setUsuario(usuario);
                    novoItem.setProduto(produto);
                    novoItem.setQuantidade(qtd);
                    return carrinhoRepository.save(novoItem);
                });
        return CarrinhoItemResponse.from(item);
    }

    public void removerItem(Long carrinhoId, Long itemId) {
        carrinhoRepository.deleteById(itemId.intValue());
    }

    public void atualizarQuantidade(Long itemId, AtualizarCarrinhoRequest request) {
        Carrinho item = carrinhoRepository.findById(itemId.intValue())
                .orElseThrow(() -> new RuntimeException("Item não encontrado: " + itemId));
        if (request.getQuantidade() != null && request.getQuantidade() > 0) {
            item.setQuantidade(request.getQuantidade());
            carrinhoRepository.save(item);
        }
    }

    public void limpar(Long usuarioId) {
        List<Carrinho> itens = carrinhoRepository.findByUsuarioId(usuarioId.intValue());
        if (!itens.isEmpty()) {
            carrinhoRepository.deleteAll(itens);
        }
    }
}
