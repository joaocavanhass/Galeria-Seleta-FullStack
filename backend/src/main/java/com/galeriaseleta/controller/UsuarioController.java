// ============================================================
// ARQUIVO: UsuarioController.java
// FUNÇÃO: Controlador das rotas de usuários e endereços (/api/usuarios).
//
// ROTAS E CONTROLE DE ACESSO:
//
// ADMIN ONLY (verificado manualmente no método):
// - GET   /api/usuarios            → lista todos os usuários (paginado)
// - PATCH /api/usuarios/{id}/papel → altera o papel de um usuário
//
// USUÁRIO LOGADO (/me = "eu mesmo"):
// - GET    /api/usuarios/me                    → meus dados de perfil
// - PUT    /api/usuarios/me                    → atualiza meus dados
// - PATCH  /api/usuarios/me/senha              → altera minha senha
// - DELETE /api/usuarios/me                    → deleta minha conta
// - GET    /api/usuarios/me/enderecos           → lista meus endereços
// - POST   /api/usuarios/me/enderecos           → adiciona um endereço
// - DELETE /api/usuarios/me/enderecos/{id}      → remove um endereço
//
// NOTA SOBRE /me:
// "/me" é um padrão de API REST que significa "o usuário autenticado".
// Evita que o frontend precise saber o ID do usuário para operações no próprio perfil.
//
// CONEXÕES: chama UsuarioService para toda a lógica.
// ============================================================

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

// @RestController: controlador REST (retorna JSON)
// @RequestMapping("/api/usuarios"): prefixo de todas as rotas desta classe
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    // Serviço com a lógica de usuários e endereços
    private final UsuarioService usuarioService;

    // Construtor com Injeção de Dependência
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // -------------------------------------------------------
    // GET /api/usuarios?page=0&size=20
    // Lista todos os usuários paginados — somente para admins.
    //
    // CONTROLE DE ACESSO MANUAL:
    // Embora o Spring Security proteja rotas via SecurityConfig,
    // esta rota usa verificação adicional dentro do método.
    // Se o usuário logado não for admin, retorna 403 Forbidden.
    //
    // ResponseEntity<?>: "?" aceita tanto PageResponse quanto String (mensagem de erro).
    // -------------------------------------------------------
    @GetMapping
    public ResponseEntity<?> listarTodos(
            @AuthenticationPrincipal UsuarioDetails ud,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // Verifica se o usuário logado tem papel de admin; nega acesso caso contrário
        if (ud == null || !"admin".equals(ud.getUsuario().getPapel())) {
            return ResponseEntity.status(403).body("Acesso negado."); // 403 Forbidden
        }

        PageResponse<UsuarioResponse> resultado = usuarioService.listarPaginado(page, size);
        return ResponseEntity.ok(resultado);
    }

    // -------------------------------------------------------
    // PATCH /api/usuarios/{id}/papel
    // Altera o papel de um usuário ("cliente" ↔ "admin").
    // Somente admins podem executar esta operação.
    //
    // Body: { "papel": "admin" } ou { "papel": "cliente" }
    // Map<String, String> body: lê o JSON como dicionário de chave/valor.
    // -------------------------------------------------------
    @PatchMapping("/{id}/papel")
    public ResponseEntity<?> atualizarPapel(
            @AuthenticationPrincipal UsuarioDetails ud,
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> body) {

        // Verifica permissão de admin antes de executar
        if (ud == null || !"admin".equals(ud.getUsuario().getPapel())) {
            return ResponseEntity.status(403).body("Acesso negado."); // 403 Forbidden
        }

        String papel = body.get("papel"); // Extrai o novo papel do corpo JSON
        return ResponseEntity.ok(UsuarioResponse.from(usuarioService.atualizarPapel(id, papel)));
    }

    // -------------------------------------------------------
    // GET /api/usuarios/me
    // Retorna os dados do perfil do usuário logado.
    // "me" é um alias para o próprio usuário autenticado.
    //
    // UsuarioResponse.from(usuario): converte o Usuario para DTO,
    // omitindo a senha e outros campos sensíveis.
    // -------------------------------------------------------
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> obterPerfil(@AuthenticationPrincipal UsuarioDetails ud) {
        // Busca os dados completos do usuário pelo ID extraído do JWT
        return ResponseEntity.ok(UsuarioResponse.from(usuarioService.buscarPorId((long) ud.getUsuario().getId())));
    }

    // -------------------------------------------------------
    // PUT /api/usuarios/me
    // Atualiza os dados do perfil do usuário logado.
    // Só os campos informados no request são alterados (não sobrescreve com null).
    //
    // @PutMapping: HTTP PUT (atualização de recurso).
    // AtualizarPerfilRequest: DTO com nome, telefone, CPF (todos opcionais).
    // -------------------------------------------------------
    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> atualizarPerfil(
            @AuthenticationPrincipal UsuarioDetails ud,
            @RequestBody AtualizarPerfilRequest request) {
        return ResponseEntity.ok(UsuarioResponse.from(
                usuarioService.atualizar((long) ud.getUsuario().getId(), request)));
    }

    // -------------------------------------------------------
    // PATCH /api/usuarios/me/senha
    // Altera a senha do usuário logado.
    // Exige a senha atual para confirmar a identidade antes de trocar.
    // Se a senha atual estiver errada, o serviço lança RuntimeException
    // que o GlobalExceptionHandler converte em 401 Unauthorized.
    //
    // AlterarSenhaRequest: DTO com senhaAtual e novaSenha.
    // -------------------------------------------------------
    @PatchMapping("/me/senha")
    public ResponseEntity<Void> alterarSenha(
            @AuthenticationPrincipal UsuarioDetails ud,
            @RequestBody AlterarSenhaRequest request) {
        usuarioService.alterarSenha((long) ud.getUsuario().getId(), request);
        return ResponseEntity.ok().build(); // 200 OK sem corpo
    }

    // -------------------------------------------------------
    // DELETE /api/usuarios/me
    // Deleta permanentemente a conta do usuário logado.
    // Operação irreversível — remove o registro do banco de dados.
    // -------------------------------------------------------
    @DeleteMapping("/me")
    public ResponseEntity<Void> deletarConta(@AuthenticationPrincipal UsuarioDetails ud) {
        usuarioService.deletar((long) ud.getUsuario().getId()); // Deleta pelo ID do token
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // -------------------------------------------------------
    // GET /api/usuarios/me/enderecos
    // Lista todos os endereços cadastrados pelo usuário logado.
    // Retorna uma lista de EnderecoResponse (DTOs sem referência ao usuário).
    // -------------------------------------------------------
    @GetMapping("/me/enderecos")
    public ResponseEntity<List<EnderecoResponse>> listarEnderecos(@AuthenticationPrincipal UsuarioDetails ud) {
        List<EnderecoResponse> enderecos = usuarioService.listarEnderecos((long) ud.getUsuario().getId()).stream()
                .map(EnderecoResponse::from) // Converte cada Endereco para o DTO de resposta
                .toList();
        return ResponseEntity.ok(enderecos);
    }

    // -------------------------------------------------------
    // POST /api/usuarios/me/enderecos
    // Adiciona um novo endereço ao perfil do usuário logado.
    // Retorna 201 Created com os dados do endereço salvo.
    //
    // EnderecoRequest: DTO com rua, cidade, estado, cep, principal.
    // -------------------------------------------------------
    @PostMapping("/me/enderecos")
    public ResponseEntity<EnderecoResponse> adicionarEndereco(
            @AuthenticationPrincipal UsuarioDetails ud,
            @RequestBody EnderecoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EnderecoResponse.from(
                        usuarioService.adicionarEndereco((long) ud.getUsuario().getId(), request)));
    }

    // -------------------------------------------------------
    // DELETE /api/usuarios/me/enderecos/{enderecoId}
    // Remove um endereço pelo ID do endereço.
    // Operação simples: apenas deleta o registro do banco.
    // -------------------------------------------------------
    @DeleteMapping("/me/enderecos/{enderecoId}")
    public ResponseEntity<Void> removerEndereco(@PathVariable Long enderecoId) {
        usuarioService.removerEndereco(enderecoId); // Remove o endereço pelo ID
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
