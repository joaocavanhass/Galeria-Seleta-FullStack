package com.galeriaseleta.controller;

import com.galeriaseleta.dto.request.ContatoRequest;
import com.galeriaseleta.dto.request.NewsletterRequest;
import com.galeriaseleta.service.ContatoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ContatoController {

    private final ContatoService contatoService;

    public ContatoController(ContatoService contatoService) {
        this.contatoService = contatoService;
    }

    /** Recebe uma mensagem do formulário de contato. */
    @PostMapping("/contato")
    public ResponseEntity<Void> enviarMensagem(@RequestBody ContatoRequest request) {
        contatoService.enviarMensagem(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** Inscreve um e-mail na newsletter. */
    @PostMapping("/newsletter/inscrever")
    public ResponseEntity<Void> inscreverNewsletter(@RequestBody NewsletterRequest request) {
        contatoService.inscreverNewsletter(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** Cancela a inscrição de um e-mail na newsletter. Param: email. */
    @DeleteMapping("/newsletter/cancelar")
    public ResponseEntity<Void> cancelarNewsletter(@RequestParam String email) {
        contatoService.cancelarNewsletter(email);
        return ResponseEntity.noContent().build();
    }
}
