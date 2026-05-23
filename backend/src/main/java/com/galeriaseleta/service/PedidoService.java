package com.galeriaseleta.service;

import com.galeriaseleta.dto.request.CriarPedidoRequest;
import com.galeriaseleta.dto.response.PedidoResponse;
import com.galeriaseleta.model.*;
import com.galeriaseleta.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final ProdutoRepository produtoRepository;
    private final CupomRepository cupomRepository;
    private final OpcaoFreteRepository opcaoFreteRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ItemPedidoRepository itemPedidoRepository,
                         UsuarioRepository usuarioRepository,
                         EnderecoRepository enderecoRepository,
                         ProdutoRepository produtoRepository,
                         CupomRepository cupomRepository,
                         OpcaoFreteRepository opcaoFreteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.enderecoRepository = enderecoRepository;
        this.produtoRepository = produtoRepository;
        this.cupomRepository = cupomRepository;
        this.opcaoFreteRepository = opcaoFreteRepository;
    }

    public List<PedidoResponse> listar(String status) {
        List<Pedido> pedidos = (status != null && !status.isBlank())
                ? pedidoRepository.findByStatus(status)
                : pedidoRepository.findAll();

        return pedidos.stream()
                .map(pedido -> PedidoResponse.from(
                        pedido,
                        itemPedidoRepository.findByPedidoId(pedido.getId())))
                .toList();
    }

    public PedidoResponse buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id));
        List<ItemPedido> itens = itemPedidoRepository.findByPedidoId(pedido.getId());
        return PedidoResponse.from(pedido, itens);
    }

    public PedidoResponse criar(CriarPedidoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId().intValue())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + request.getUsuarioId()));
        Endereco endereco = enderecoRepository.findById(request.getEnderecoId().intValue())
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado: " + request.getEnderecoId()));

        List<Produto> produtos = request.getProdutoIds().stream()
                .map(pid -> produtoRepository.findById(pid)
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + pid)))
                .toList();

        BigDecimal subtotal = produtos.stream()
                .map(Produto::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorFrete = BigDecimal.ZERO;
        OpcaoFrete frete = null;
        if (request.getFreteId() != null) {
            frete = opcaoFreteRepository.findById(request.getFreteId()).orElse(null);
            if (frete != null) valorFrete = frete.getPreco();
        }

        BigDecimal desconto = BigDecimal.ZERO;
        Cupom cupom = null;
        if (request.getCodigoCupom() != null) {
            cupom = cupomRepository.findByCodigo(request.getCodigoCupom()).orElse(null);
            if (cupom != null && Boolean.TRUE.equals(cupom.getAtivo())) {
                desconto = calcularDesconto(subtotal, cupom);
            }
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEndereco(endereco);
        pedido.setCupom(cupom);
        pedido.setFrete(frete);
        pedido.setSubtotal(subtotal);
        pedido.setValorFrete(valorFrete);
        pedido.setDesconto(desconto);
        pedido.setTotal(subtotal.add(valorFrete).subtract(desconto));
        pedido.setStatus("pendente");

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        List<ItemPedido> itensSalvos = produtos.stream().map(produto -> {
            ItemPedido item = new ItemPedido();
            item.setPedido(pedidoSalvo);
            item.setProduto(produto);
            item.setQuantidade(1);
            item.setPrecoPago(produto.getPreco());
            return itemPedidoRepository.save(item);
        }).toList();

        return PedidoResponse.from(pedidoSalvo, itensSalvos);
    }

    public void cancelar(Long id) {
        Pedido pedido = pedidoRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id));
        pedido.setStatus("cancelado");
        pedidoRepository.save(pedido);
    }

    public void atualizarStatus(Long id, String status) {
        Pedido pedido = pedidoRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id));
        pedido.setStatus(status);
        pedidoRepository.save(pedido);
    }

    private BigDecimal calcularDesconto(BigDecimal subtotal, Cupom cupom) {
        if ("percentual".equals(cupom.getTipoDesconto())) {
            return subtotal.multiply(cupom.getValorDesconto()).divide(BigDecimal.valueOf(100));
        }
        return cupom.getValorDesconto();
    }
}
