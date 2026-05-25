package com.galeriaseleta.controller;

import com.galeriaseleta.dto.request.AlterarSenhaRequest;
import com.galeriaseleta.dto.request.AtualizarPerfilRequest;
import com.galeriaseleta.dto.request.EnderecoRequest;
import com.galeriaseleta.dto.response.EnderecoResponse;
import com.galeriaseleta.dto.response.PageResponse;
import com.galeriaseleta.dto.response.UsuarioResponse;
import com.galeriaseleta.security.UsuarioDetails;
import com.galeriaseleta.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<?> listarTodos(
            @AuthenticationPrincipal UsuarioDetails ud,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (ud == null || !"admin".equals(ud.getUsuario().getPapel())) {
            return ResponseEntity.status(403).body("Acesso negado.");
        }
        PageResponse<UsuarioResponse> resultado = usuarioService.listarPaginado(page, size);
        return ResponseEntity.ok(resultado);
    }

    @PatchMapping("/{id}/papel")
    public ResponseEntity<?> atualizarPapel(
            @AuthenticationPrincipal UsuarioDetails ud,
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> body) {
        if (ud == null || !"admin".equals(ud.getUsuario().getPapel())) {
            return ResponseEntity.status(403).body("Acesso negado.");
        }
        String papel = body.get("papel");
        return ResponseEntity.ok(UsuarioResponse.from(usuarioService.atualizarPapel(id, papel)));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> obterPerfil(@AuthenticationPrincipal UsuarioDetails ud) {
        return ResponseEntity.ok(UsuarioResponse.from(usuarioService.buscarPorId((long) ud.getUsuario().getId())));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> atualizarPerfil(
            @AuthenticationPrincipal UsuarioDetails ud,
            @RequestBody AtualizarPerfilRequest request) {
        return ResponseEntity.ok(UsuarioResponse.from(usuarioService.atualizar((long) ud.getUsuario().getId(), request)));
    }

    @PatchMapping("/me/senha")
    public ResponseEntity<Void> alterarSenha(
            @AuthenticationPrincipal UsuarioDetails ud,
            @RequestBody AlterarSenhaRequest request) {
        usuarioService.alterarSenha((long) ud.getUsuario().getId(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deletarConta(@AuthenticationPrincipal UsuarioDetails ud) {
        usuarioService.deletar((long) ud.getUsuario().getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/enderecos")
    public ResponseEntity<List<EnderecoResponse>> listarEnderecos(@AuthenticationPrincipal UsuarioDetails ud) {
        List<EnderecoResponse> enderecos = usuarioService.listarEnderecos((long) ud.getUsuario().getId()).stream()
                .map(EnderecoResponse::from)
                .toList();
        return ResponseEntity.ok(enderecos);
    }

    @PostMapping("/me/enderecos")
    public ResponseEntity<EnderecoResponse> adicionarEndereco(
            @AuthenticationPrincipal UsuarioDetails ud,
            @RequestBody EnderecoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EnderecoResponse.from(usuarioService.adicionarEndereco((long) ud.getUsuario().getId(), request)));
    }

    @DeleteMapping("/me/enderecos/{enderecoId}")
    public ResponseEntity<Void> removerEndereco(@PathVariable Long enderecoId) {
        usuarioService.removerEndereco(enderecoId);
        return ResponseEntity.noContent().build();
    }
}
