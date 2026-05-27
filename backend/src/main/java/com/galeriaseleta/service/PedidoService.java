// ============================================================
// ARQUIVO: PedidoService.java
// FUNÇÃO: Serviço com toda a lógica de criação e gerenciamento de pedidos.
//
// FLUXO DE CRIAÇÃO DE PEDIDO (método criar()):
// 1. Busca usuário e endereço no banco
// 2. Busca cada produto da lista enviada
// 3. Calcula o subtotal (soma dos preços)
// 4. Aplica o frete (se selecionado)
// 5. Aplica o desconto do cupom (se usado)
// 6. Calcula o total: subtotal + frete - desconto
// 7. Salva o pedido no banco
// 8. Agrupa produtos iguais (evitar duplicatas) e salva os itens
// 9. Retorna o PedidoResponse completo
//
// CONEXÕES: chamado por PedidoController.
// Usa 7 repositórios diferentes — o service mais complexo do projeto.
// ============================================================

package com.galeriaseleta.service;

import com.galeriaseleta.dto.request.CriarPedidoRequest;
import com.galeriaseleta.dto.response.PageResponse;
import com.galeriaseleta.dto.response.PedidoResponse;
import com.galeriaseleta.model.*;
import com.galeriaseleta.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
// Sort: define ordenação dos resultados (por campo e direção)
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
// Collectors: utilitários para coletar streams em coleções
import java.util.stream.Collectors;

@Service
public class PedidoService {

    // Injeta todos os repositórios necessários para criar um pedido completo
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

    // Lista pedidos com filtros opcionais de usuário e status.
    // Admin (usuarioId null): vê todos os pedidos.
    // Cliente (usuarioId informado): vê apenas seus pedidos.
    public List<PedidoResponse> listar(Integer usuarioId, String status) {
        List<Pedido> pedidos;
        if (usuarioId != null) {
            // Filtra por usuário (e opcionalmente por status)
            pedidos = (status != null && !status.isBlank())
                    ? pedidoRepository.findByUsuarioIdAndStatus(usuarioId, status)
                    : pedidoRepository.findByUsuarioId(usuarioId);
        } else {
            // Admin: sem filtro de usuário
            pedidos = (status != null && !status.isBlank())
                    ? pedidoRepository.findByStatus(status)
                    : pedidoRepository.findAll();
        }

        // Para cada pedido, busca seus itens e monta o PedidoResponse completo
        return pedidos.stream()
                .map(pedido -> PedidoResponse.from(
                        pedido,
                        itemPedidoRepository.findByPedidoId(pedido.getId())))
                .toList();
    }

    // Versão paginada do listar, ordenada pelo ID decrescente (pedidos mais recentes primeiro)
    public PageResponse<PedidoResponse> listarPaginado(Integer usuarioId, String status, int page, int size) {
        // Sort.by(DESC, "id"): ordena pelo ID em ordem decrescente (mais recente primeiro)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Pedido> resultado;

        if (usuarioId != null) {
            resultado = (status != null && !status.isBlank())
                    ? pedidoRepository.findByUsuarioIdAndStatus(usuarioId, status, pageable)
                    : pedidoRepository.findByUsuarioId(usuarioId, pageable);
        } else {
            resultado = (status != null && !status.isBlank())
                    ? pedidoRepository.findByStatus(status, pageable)
                    : pedidoRepository.findAll(pageable);
        }

        // Converte Page<Pedido> → PageResponse<PedidoResponse>
        return PageResponse.from(resultado,
                pedido -> PedidoResponse.from(pedido, itemPedidoRepository.findByPedidoId(pedido.getId())));
    }

    // Busca um pedido específico pelo ID com todos seus detalhes
    public PedidoResponse buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id));
        List<ItemPedido> itens = itemPedidoRepository.findByPedidoId(pedido.getId());
        return PedidoResponse.from(pedido, itens);
    }

    // Cria um novo pedido — o método mais complexo do sistema
    public PedidoResponse criar(CriarPedidoRequest request) {

        // 1. Busca o usuário e o endereço de entrega no banco
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId().intValue())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + request.getUsuarioId()));
        Endereco endereco = enderecoRepository.findById(request.getEnderecoId().intValue())
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado: " + request.getEnderecoId()));

        // 2. Busca todos os produtos pelo ID (o frontend envia uma lista de IDs)
        List<Produto> produtos = request.getProdutoIds().stream()
                .map(pid -> produtoRepository.findById(pid)
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + pid)))
                .toList();

        // 3. Calcula o subtotal: soma dos preços de todos os produtos
        // reduce(ZERO, add): começa em 0 e vai somando cada preço
        BigDecimal subtotal = produtos.stream()
                .map(Produto::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Busca e aplica o frete (se selecionado)
        BigDecimal valorFrete = BigDecimal.ZERO;
        OpcaoFrete frete = null;
        if (request.getFreteId() != null) {
            frete = opcaoFreteRepository.findById(request.getFreteId()).orElse(null);
            if (frete != null) valorFrete = frete.getPreco();
        }

        // 5. Busca e aplica o cupom de desconto (se informado)
        BigDecimal desconto = BigDecimal.ZERO;
        Cupom cupom = null;
        if (request.getCodigoCupom() != null) {
            cupom = cupomRepository.findByCodigo(request.getCodigoCupom()).orElse(null);
            if (cupom != null && Boolean.TRUE.equals(cupom.getAtivo())) {
                desconto = calcularDesconto(subtotal, cupom);
            }
        }

        // 6. Monta e salva o pedido no banco
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEndereco(endereco);
        pedido.setCupom(cupom);
        pedido.setFrete(frete);
        pedido.setSubtotal(subtotal);
        pedido.setValorFrete(valorFrete);
        pedido.setDesconto(desconto);
        pedido.setTotal(subtotal.add(valorFrete).subtract(desconto)); // total = subtotal + frete - desconto
        pedido.setStatus("pendente"); // Status inicial do pedido

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // 7. Agrupa produtos iguais para calcular a quantidade
        // Ex: se o usuario enviou produtoIds = [1, 1, 2], agrupa como {1: 2, 2: 1}
        // groupingBy: agrupa por produto ID; counting: conta quantas vezes aparece
        Map<Integer, Long> qtdPorProduto = produtos.stream()
                .collect(Collectors.groupingBy(Produto::getId, Collectors.counting()));

        // 8. Cria um ItemPedido para cada produto distinto (com a quantidade correta)
        List<ItemPedido> itensSalvos = qtdPorProduto.entrySet().stream().map(entry -> {
            // Busca o objeto Produto correspondente ao ID
            Produto produto = produtos.stream()
                    .filter(p -> p.getId().equals(entry.getKey()))
                    .findFirst().orElseThrow();

            ItemPedido item = new ItemPedido();
            item.setPedido(pedidoSalvo);
            item.setProduto(produto);
            item.setQuantidade(entry.getValue().intValue()); // Quantidade agrupada
            item.setPrecoPago(produto.getPreco()); // Congela o preço atual no histórico
            return itemPedidoRepository.save(item);
        }).toList();

        return PedidoResponse.from(pedidoSalvo, itensSalvos);
    }

    // Cancela um pedido alterando seu status para "cancelado"
    public void cancelar(Long id) {
        Pedido pedido = pedidoRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id));
        pedido.setStatus("cancelado");
        pedidoRepository.save(pedido);
    }

    // Atualiza o status de um pedido (usado pelo admin para avançar o fluxo)
    public void atualizarStatus(Long id, String status) {
        Pedido pedido = pedidoRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id));
        pedido.setStatus(status);
        pedidoRepository.save(pedido);
    }

    // Calcula o valor do desconto baseado no tipo do cupom:
    // - "percentual": desconto = subtotal * (valor / 100)  Ex: 10% de R$200 = R$20
    // - "fixo": desconto = valor fixo do cupom             Ex: R$20 de desconto
    private BigDecimal calcularDesconto(BigDecimal subtotal, Cupom cupom) {
        if ("percentual".equals(cupom.getTipoDesconto())) {
            return subtotal.multiply(cupom.getValorDesconto()).divide(BigDecimal.valueOf(100));
        }
        return cupom.getValorDesconto();
    }
}
