// ============================================================
// ARQUIVO: CarrinhoService.java
// FUNÇÃO: Serviço com a lógica de negócio do carrinho de compras.
//
// LÓGICA DE "ADICIONAR ITEM":
// Se o produto já está no carrinho → incrementa a quantidade
// Se é a primeira vez → cria um novo registro no banco
// Esta lógica usa o padrão map/orElseGet do Optional.
//
// CONEXÕES: chamado por CarrinhoController.
// Usa CarrinhoRepository, ProdutoRepository e UsuarioRepository.
// ============================================================

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

    // Retorna o carrinho completo do usuário com o total calculado
    public CarrinhoResponse obterCarrinho(Long usuarioId) {
        List<Carrinho> itens = carrinhoRepository.findByUsuarioId(usuarioId.intValue());
        return CarrinhoResponse.from(itens); // CarrinhoResponse calcula o total automaticamente
    }

    // Adiciona um produto ao carrinho.
    // Se o produto já existe no carrinho, incrementa a quantidade.
    // Se é novo, cria um registro.
    public CarrinhoItemResponse adicionarItem(Long usuarioId, AdicionarCarrinhoRequest request) {
        // Garante que a quantidade seja pelo menos 1
        int qtd = request.getQuantidade() != null && request.getQuantidade() > 0 ? request.getQuantidade() : 1;

        // Verifica se o produto já está no carrinho deste usuário
        Carrinho item = carrinhoRepository
                .findByUsuarioIdAndProdutoId(usuarioId.intValue(), request.getProdutoId().intValue())
                .map(existente -> {
                    // PRODUTO JÁ NO CARRINHO: incrementa a quantidade
                    existente.setQuantidade(existente.getQuantidade() + qtd);
                    return carrinhoRepository.save(existente);
                })
                .orElseGet(() -> {
                    // PRODUTO NOVO NO CARRINHO: cria um novo registro

                    // Busca o usuário e o produto no banco para garantir que existem
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

    // Remove um item do carrinho pelo ID do item (não pelo ID do produto)
    public void removerItem(Long carrinhoId, Long itemId) {
        carrinhoRepository.deleteById(itemId.intValue());
    }

    // Atualiza a quantidade de um item do carrinho
    // Se a quantidade for 0 ou negativa, ignora (o frontend deve chamar removerItem para 0)
    public void atualizarQuantidade(Long itemId, AtualizarCarrinhoRequest request) {
        Carrinho item = carrinhoRepository.findById(itemId.intValue())
                .orElseThrow(() -> new RuntimeException("Item não encontrado: " + itemId));

        // Só atualiza se a quantidade for válida (maior que 0)
        if (request.getQuantidade() != null && request.getQuantidade() > 0) {
            item.setQuantidade(request.getQuantidade());
            carrinhoRepository.save(item);
        }
    }

    // Remove todos os itens do carrinho do usuário (após finalizar o pedido, por exemplo)
    public void limpar(Long usuarioId) {
        List<Carrinho> itens = carrinhoRepository.findByUsuarioId(usuarioId.intValue());
        if (!itens.isEmpty()) {
            carrinhoRepository.deleteAll(itens); // Deleta todos de uma vez
        }
    }
}
