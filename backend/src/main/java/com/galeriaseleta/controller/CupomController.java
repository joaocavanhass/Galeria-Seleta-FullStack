// ============================================================
// ARQUIVO: CupomController.java
// FUNÇÃO: Controlador da rota de validação de cupons (/api/cupons).
//
// PARTICULARIDADE DESTE CONTROLADOR:
// Este é o único controlador que acessa o repositório diretamente,
// sem passar por um service dedicado. Isso é aceitável quando a
// lógica é simples o suficiente para caber em uma única expressão.
//
// ROTA (pública — não precisa de JWT):
// - GET /api/cupons/{codigo} → valida o cupom e retorna dados do desconto
//
// LÓGICA DE VALIDAÇÃO (encadeamento de filtros):
// 1. Busca o cupom pelo código (case-insensitive, sem espaços)
// 2. Filtra: cupom deve estar ativo
// 3. Filtra: cupom não deve estar expirado (ou não ter data de expiração)
// 4. Se passou nos filtros: retorna 200 com os dados do desconto
// 5. Se não encontrou ou foi filtrado: retorna 404
//
// CONEXÕES: usa CupomRepository diretamente (sem service intermediário).
// ============================================================

package com.galeriaseleta.controller;

import com.galeriaseleta.dto.response.CupomResponse;
import com.galeriaseleta.repository.CupomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

// @RestController: controlador REST que retorna JSON
// @RequestMapping("/api/cupons"): prefixo das rotas deste controlador
@RestController
@RequestMapping("/api/cupons")
public class CupomController {

    // Acesso direto ao repositório de cupons (sem service dedicado)
    private final CupomRepository cupomRepository;

    // Construtor com injeção de dependência
    public CupomController(CupomRepository cupomRepository) {
        this.cupomRepository = cupomRepository;
    }

    // -------------------------------------------------------
    // GET /api/cupons/{codigo}
    // Valida se um cupom existe, está ativo e não expirou.
    // Retorna 200 com os dados do desconto ou 404 se inválido.
    //
    // O encadeamento com Optional funciona assim:
    //   findByCodigo() → Optional<Cupom>
    //   .filter(...) → mantém no Optional somente se a condição for verdadeira
    //   .map(...) → transforma o Cupom em ResponseEntity com 200
    //   .orElse(...) → retorna 404 se o Optional estiver vazio
    //
    // codigo.toUpperCase().trim(): normaliza o código recebido
    //   (ex: "PROMO10 " → "PROMO10") para evitar erros de digitação.
    //
    // Boolean.TRUE.equals(c.getAtivo()): verificação segura contra null.
    //   Se getAtivo() retornar null, .equals() não lança exceção.
    //
    // c.getExpiraEm() == null: cupom sem data de expiração = sempre válido.
    // c.getExpiraEm().isAfter(LocalDateTime.now()): ainda não expirou.
    // -------------------------------------------------------
    /** Valida um cupom pelo código. Retorna 200 com dados do desconto ou 404 se inválido/expirado. */
    @GetMapping("/{codigo}")
    public ResponseEntity<CupomResponse> validar(@PathVariable String codigo) {
        return cupomRepository.findByCodigo(codigo.toUpperCase().trim()) // Busca ignorando maiúsculas e espaços
                .filter(c -> Boolean.TRUE.equals(c.getAtivo()))           // Filtra: somente cupons ativos
                .filter(c -> c.getExpiraEm() == null || c.getExpiraEm().isAfter(LocalDateTime.now())) // Filtra: não expirado
                .map(c -> ResponseEntity.ok(CupomResponse.from(c)))       // 200 OK com os dados do cupom
                .orElse(ResponseEntity.notFound().build());               // 404 se não encontrou ou falhou nos filtros
    }
}
