// ============================================================
// ARQUIVO: FreteController.java
// FUNÇÃO: Controlador das opções de frete disponíveis (/api/frete).
//
// ROTA (pública — não precisa de JWT):
// - GET /api/frete → lista todas as opções de frete com preço e prazo
//
// PARTICULARIDADE:
// Assim como o CupomController, este controlador acessa o repositório
// diretamente sem um service dedicado, pois a lógica é apenas
// "buscar todos e converter para DTO".
//
// O frontend usa esta lista no checkout para o usuário escolher
// entre as opções de entrega (PAC, SEDEX, etc.).
//
// CONEXÕES: usa OpcaoFreteRepository diretamente.
// ============================================================

package com.galeriaseleta.controller;

import com.galeriaseleta.dto.response.OpcaoFreteResponse;
import com.galeriaseleta.repository.OpcaoFreteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController: controlador REST (retorna JSON)
// @RequestMapping("/api/frete"): todas as rotas começam com este prefixo
@RestController
@RequestMapping("/api/frete")
public class FreteController {

    // Repositório para acessar as opções de frete cadastradas no banco
    private final OpcaoFreteRepository opcaoFreteRepository;

    // Construtor com injeção de dependência
    public FreteController(OpcaoFreteRepository opcaoFreteRepository) {
        this.opcaoFreteRepository = opcaoFreteRepository;
    }

    // -------------------------------------------------------
    // GET /api/frete
    // Retorna todas as opções de frete disponíveis.
    // As opções são cadastradas pelo admin e carregadas no checkout.
    //
    // opcaoFreteRepository.findAll(): busca todos os registros da tabela.
    // .stream().map(OpcaoFreteResponse::from).toList():
    //   converte cada OpcaoFrete (entidade do banco) para OpcaoFreteResponse (DTO).
    // -------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<OpcaoFreteResponse>> listar() {
        return ResponseEntity.ok(opcaoFreteRepository.findAll().stream()
                .map(OpcaoFreteResponse::from) // Converte entidade para DTO de resposta
                .toList());
    }
}
