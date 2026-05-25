package com.galeriaseleta.controller;

import com.galeriaseleta.dto.response.CupomResponse;
import com.galeriaseleta.repository.CupomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/cupons")
public class CupomController {

    private final CupomRepository cupomRepository;

    public CupomController(CupomRepository cupomRepository) {
        this.cupomRepository = cupomRepository;
    }

    /** Valida um cupom pelo código. Retorna 200 com dados do desconto ou 404 se inválido/expirado. */
    @GetMapping("/{codigo}")
    public ResponseEntity<CupomResponse> validar(@PathVariable String codigo) {
        return cupomRepository.findByCodigo(codigo.toUpperCase().trim())
                .filter(c -> Boolean.TRUE.equals(c.getAtivo()))
                .filter(c -> c.getExpiraEm() == null || c.getExpiraEm().isAfter(LocalDateTime.now()))
                .map(c -> ResponseEntity.ok(CupomResponse.from(c)))
                .orElse(ResponseEntity.notFound().build());
    }
}
