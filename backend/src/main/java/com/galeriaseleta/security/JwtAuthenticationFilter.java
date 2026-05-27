// ============================================================
// ARQUIVO: JwtAuthenticationFilter.java
// FUNÇÃO: Filtro que intercepta CADA requisição HTTP para verificar
// se o token JWT é válido. Se for, autentica o usuário no contexto
// do Spring Security.
//
// FLUXO DE CADA REQUISIÇÃO:
// 1. Requisição chega do frontend
// 2. Este filtro a intercepta antes de qualquer controller
// 3. Extrai o token do header "Authorization: Bearer <token>"
// 4. Valida o token com JwtService
// 5. Carrega o usuário do banco (UserDetailsServiceImpl)
// 6. Registra a autenticação no SecurityContextHolder
// 7. Libera a requisição para o controller
//
// OncePerRequestFilter: garante que este filtro executa apenas UMA VEZ
// por requisição (não duplica em requisições com forward/redirect).
//
// CONEXÕES: registrado no SecurityConfig antes do filtro padrão do Spring.
// ============================================================

package com.galeriaseleta.security;

import com.galeriaseleta.service.JwtService;
// FilterChain: cadeia de filtros — após processar, passamos para o próximo filtro
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// @NonNull: indica que o parâmetro não pode ser null
import org.springframework.lang.NonNull;
// UsernamePasswordAuthenticationToken: objeto de autenticação do Spring Security
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// SecurityContextHolder: armazena a autenticação atual da thread (da requisição)
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
// WebAuthenticationDetailsSource: adiciona detalhes da requisição ao token de autenticação
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
// OncePerRequestFilter: base que garante execução única por requisição
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;               // Para validar e extrair dados do token
    private final UserDetailsService userDetailsService; // Para carregar o usuário do banco

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // Executado automaticamente a cada requisição HTTP recebida pelo servidor
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,   // A requisição que chegou
            @NonNull HttpServletResponse response, // A resposta que será enviada
            @NonNull FilterChain filterChain       // Próximos filtros na cadeia
    ) throws ServletException, IOException {

        // Pega o header "Authorization" da requisição
        // Ex: "Authorization: Bearer eyJhbGci..."
        String authHeader = request.getHeader("Authorization");

        // Se não tem header ou não começa com "Bearer ", não é uma requisição autenticada
        // Passa para o próximo filtro sem autenticar (rotas públicas funcionarão normalmente)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Remove o prefixo "Bearer " (7 caracteres) para ficar só com o token JWT
        String token = authHeader.substring(7);

        try {
            // Extrai o email (subject) do token JWT
            String email = jwtService.extrairEmail(token);

            // Verifica se o email foi extraído e se o usuário ainda não está autenticado
            // (SecurityContextHolder.getContext().getAuthentication() == null significa "não autenticado")
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Carrega os detalhes do usuário do banco de dados pelo email
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Valida se o token é válido para este usuário (email bate e não está expirado)
                if (jwtService.validarToken(token, userDetails)) {

                    // Cria o objeto de autenticação do Spring Security
                    // null no segundo parâmetro = não precisamos de credenciais (já validamos pelo JWT)
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // Adiciona detalhes da requisição (IP, session, etc.) ao token de autenticação
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Registra a autenticação no contexto de segurança da thread atual
                    // A partir daqui, o Spring sabe que o usuário está autenticado nesta requisição
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception ignored) {
            // Token inválido, expirado ou malformado: simplesmente não autentica.
            // A requisição continua sem autenticação — o SecurityConfig decidirá se permite ou bloqueia.
        }

        // Passa a requisição para o próximo filtro na cadeia (ou para o controller)
        filterChain.doFilter(request, response);
    }
}
