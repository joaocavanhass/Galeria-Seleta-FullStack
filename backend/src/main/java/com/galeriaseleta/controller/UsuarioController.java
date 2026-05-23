package com.galeriaseleta.controller;

import com.galeriaseleta.dto.request.AlterarSenhaRequest;
import com.galeriaseleta.dto.request.AtualizarPerfilRequest;
import com.galeriaseleta.dto.request.EnderecoRequest;
import com.galeriaseleta.dto.response.EnderecoResponse;
import com.galeriaseleta.dto.response.UsuarioResponse;
import com.galeriaseleta.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** Retorna o perfil do usuário autenticado. O ID será extraído do contexto de autenticação. */
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> obterPerfil() {
        return ResponseEntity.ok(UsuarioResponse.from(usuarioService.buscarPorId(1L)));
    }

    /** Atualiza nome e telefone do usuário autenticado. */
    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> atualizarPerfil(@RequestBody AtualizarPerfilRequest request) {
        return ResponseEntity.ok(UsuarioResponse.from(usuarioService.atualizar(1L, request)));
    }

    /** Altera a senha do usuário autenticado. */
    @PatchMapping("/me/senha")
    public ResponseEntity<Void> alterarSenha(@RequestBody AlterarSenhaRequest request) {
        usuarioService.alterarSenha(1L, request);
        return ResponseEntity.ok().build();
    }

    /** Remove a conta do usuário autenticado. */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deletarConta() {
        usuarioService.deletar(1L);
        return ResponseEntity.noContent().build();
    }

    /** Lista os endereços de entrega do usuário autenticado. */
    @GetMapping("/me/enderecos")
    public ResponseEntity<List<EnderecoResponse>> listarEnderecos() {
        List<EnderecoResponse> enderecos = usuarioService.listarEnderecos(1L).stream()
                .map(EnderecoResponse::from)
                .toList();
        return ResponseEntity.ok(enderecos);
    }

    /** Adiciona um endereço de entrega ao perfil do usuário. */
    @PostMapping("/me/enderecos")
    public ResponseEntity<EnderecoResponse> adicionarEndereco(@RequestBody EnderecoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EnderecoResponse.from(usuarioService.adicionarEndereco(1L, request)));
    }

    /** Remove um endereço de entrega do perfil do usuário. */
    @DeleteMapping("/me/enderecos/{enderecoId}")
    public ResponseEntity<Void> removerEndereco(@PathVariable Long enderecoId) {
        usuarioService.removerEndereco(enderecoId);
        return ResponseEntity.noContent().build();
    }
}
