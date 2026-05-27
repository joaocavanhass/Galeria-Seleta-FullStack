// ============================================================
// ARQUIVO: ContatoController.java
// FUNÇÃO: Controlador das rotas de contato e newsletter (/api/contato e /api/newsletter).
//
// NOTA SOBRE O PREFIXO:
// Diferente dos outros controladores que usam /api/[recurso] como prefixo e
// depois definem os subpaths, este usa @RequestMapping("/api") e
// define caminhos mais específicos em cada método.
// Isso resulta em rotas como /api/contato e /api/newsletter/inscrever.
//
// ROTAS (públicas — não precisam de JWT):
// - POST   /api/contato                   → envia mensagem pelo formulário de contato
// - POST   /api/newsletter/inscrever      → inscreve email na newsletter
// - DELETE /api/newsletter/cancelar?email → cancela inscrição na newsletter
//
// CONEXÕES: chama ContatoService para toda a lógica.
// ============================================================

package com.galeriaseleta.controller;

import com.galeriaseleta.dto.request.ContatoRequest;
import com.galeriaseleta.dto.request.NewsletterRequest;
import com.galeriaseleta.service.ContatoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @RestController: controlador REST (retorna JSON)
// @RequestMapping("/api"): prefixo base — as rotas individuais completam o caminho
@RestController
@RequestMapping("/api")
public class ContatoController {

    // Serviço que processa mensagens de contato e gerencia inscrições de newsletter
    private final ContatoService contatoService;

    // Construtor com injeção de dependência
    public ContatoController(ContatoService contatoService) {
        this.contatoService = contatoService;
    }

    // -------------------------------------------------------
    // POST /api/contato
    // Recebe e salva uma mensagem enviada pelo formulário de contato do site.
    // O admin pode visualizar as mensagens no painel administrativo.
    //
    // @PostMapping("/contato"): rota completa = /api/contato
    // @RequestBody: converte o JSON da requisição para ContatoRequest
    //   (nome, sobrenome, email, telefone, mensagem).
    // HttpStatus.CREATED (201): recurso criado com sucesso.
    // .build(): constrói a resposta sem corpo (apenas o status).
    // -------------------------------------------------------
    /** Recebe uma mensagem do formulário de contato. */
    @PostMapping("/contato")
    public ResponseEntity<Void> enviarMensagem(@RequestBody ContatoRequest request) {
        contatoService.enviarMensagem(request); // Salva a mensagem no banco
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created sem corpo
    }

    // -------------------------------------------------------
    // POST /api/newsletter/inscrever
    // Inscreve um email na newsletter.
    // Se o email já estava inscrito mas foi cancelado: reativa.
    // Se nunca foi inscrito: cria nova inscrição.
    // Se já está ativo: não faz nada (operação idempotente).
    // -------------------------------------------------------
    /** Inscreve um e-mail na newsletter. */
    @PostMapping("/newsletter/inscrever")
    public ResponseEntity<Void> inscreverNewsletter(@RequestBody NewsletterRequest request) {
        contatoService.inscreverNewsletter(request); // Lógica de criar/reativar inscrição
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
    }

    // -------------------------------------------------------
    // DELETE /api/newsletter/cancelar?email=usuario@email.com
    // Cancela a inscrição de um email na newsletter.
    // O email não é deletado do banco — apenas marcado como inativo (ativo=false).
    // Isso preserva o histórico de inscrições.
    //
    // @DeleteMapping: HTTP DELETE (remoção/cancelamento de recurso).
    // @RequestParam String email: lê o email do query parameter da URL.
    //   Ex: DELETE /api/newsletter/cancelar?email=joao@email.com
    //   @RequestParam é diferente de @PathVariable:
    //     - @PathVariable: parte da URL  → /cancelar/{email}
    //     - @RequestParam: query string  → /cancelar?email=...
    // -------------------------------------------------------
    /** Cancela a inscrição de um e-mail na newsletter. Param: email. */
    @DeleteMapping("/newsletter/cancelar")
    public ResponseEntity<Void> cancelarNewsletter(@RequestParam String email) {
        contatoService.cancelarNewsletter(email); // Desativa a inscrição (não deleta)
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
