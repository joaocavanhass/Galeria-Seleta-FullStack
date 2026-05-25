package com.galeriaseleta.controller;

import com.galeriaseleta.dto.response.OpcaoFreteResponse;
import com.galeriaseleta.repository.OpcaoFreteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/frete")
public class FreteController {

    private final OpcaoFreteRepository opcaoFreteRepository;

    public FreteController(OpcaoFreteRepository opcaoFreteRepository) {
        this.opcaoFreteRepository = opcaoFreteRepository;
    }

    @GetMapping
    public ResponseEntity<List<OpcaoFreteResponse>> listar() {
        return ResponseEntity.ok(opcaoFreteRepository.findAll().stream()
                .map(OpcaoFreteResponse::from)
                .toList());
    }
}
