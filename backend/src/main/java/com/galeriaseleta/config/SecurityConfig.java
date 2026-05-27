// ============================================================
// ARQUIVO: SecurityConfig.java
// FUNÇÃO: Configuração central de segurança da aplicação.
// Define quais rotas são públicas (qualquer um pode acessar)
// e quais exigem autenticação (somente usuários logados).
// Também configura CORS (permissão para o frontend acessar a API)
// e o algoritmo de criptografia de senhas.
//
// ANALOGIA: pense neste arquivo como a portaria de um prédio —
// ele decide quem pode entrar em cada andar sem precisar de crachá
// e quem precisa se identificar antes de passar.
// ============================================================

package com.galeriaseleta.config;

// Importa o filtro JWT que verifica o token em cada requisição
import com.galeriaseleta.security.JwtAuthenticationFilter;

// @Bean: marca um método cujo retorno deve ser gerenciado pelo Spring como componente
import org.springframework.context.annotation.Bean;
// @Configuration: marca esta classe como arquivo de configuração do Spring
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Enum com os métodos HTTP: GET, POST, PUT, etc.

// Classes do Spring Security para configurar autenticação
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// @EnableWebSecurity: ativa o módulo de segurança web do Spring
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// SessionCreationPolicy.STATELESS: não usa sessão no servidor (JWT é stateless)
import org.springframework.security.config.http.SessionCreationPolicy;
// BCryptPasswordEncoder: algoritmo seguro de hash para senhas
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// SecurityFilterChain: cadeia de filtros de segurança aplicados em cada requisição
import org.springframework.security.web.SecurityFilterChain;
// UsernamePasswordAuthenticationFilter: filtro padrão do Spring para login com usuário/senha
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Classes para configuração de CORS
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// @Configuration: esta classe contém configurações do Spring
// @EnableWebSecurity: ativa a segurança web (intercepta todas as requisições HTTP)
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // O filtro JWT é injetado aqui para ser adicionado à cadeia de segurança.
    // Ele verifica o token em cada requisição antes de liberar o acesso.
    private final JwtAuthenticationFilter jwtAuthFilter;

    // Construtor com injeção de dependência do filtro JWT
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // Define a cadeia de filtros de segurança — o coração deste arquivo.
    // Cada requisição HTTP passa por esses filtros na ordem configurada.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF (Cross-Site Request Forgery): desativado porque a API usa JWT,
            // que já protege contra esse tipo de ataque por ser stateless.
            .csrf(csrf -> csrf.disable())

            // Aplica as regras de CORS definidas no método corsConfigurationSource()
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // STATELESS: o servidor não guarda sessão do usuário.
            // Cada requisição deve trazer o token JWT para se identificar.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Regras de autorização: define quem pode acessar o quê
            .authorizeHttpRequests(auth -> auth

                // ROTAS PÚBLICAS — qualquer pessoa pode acessar sem estar logada:

                // Login, cadastro, recuperação de senha e refresh de token são públicos
                .requestMatchers(HttpMethod.POST,
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/forgot-password",
                        "/api/auth/refresh",
                        "/api/auth/logout").permitAll()

                // Catálogo de produtos e categorias: leitura pública (apenas GET)
                // Qualquer visitante pode ver os produtos sem precisar de conta
                .requestMatchers(HttpMethod.GET, "/api/produtos/**", "/api/categorias/**").permitAll()

                // Opções de frete: consulta pública
                .requestMatchers(HttpMethod.GET, "/api/frete/**").permitAll()

                // Formulário de contato e inscrição na newsletter: envio público
                .requestMatchers(HttpMethod.POST, "/api/contato", "/api/newsletter/**").permitAll()

                // TUDO que não foi listado acima exige autenticação (token JWT válido)
                .anyRequest().authenticated()
            )

            // Adiciona o filtro JWT ANTES do filtro padrão de autenticação do Spring.
            // Isso garante que o token seja verificado antes de qualquer outra checagem.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Define o algoritmo de criptografia de senhas como BCrypt.
    // BCrypt é considerado seguro pois é lento intencionalmente,
    // dificultando ataques de força bruta.
    // @Bean faz com que o Spring disponibilize este objeto para outros componentes
    // (ex: AuthService usa para verificar senhas no login).
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // O AuthenticationManager gerencia o processo de autenticação do Spring Security.
    // É usado pelo AuthService para validar credenciais de login.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Configuração de CORS (Cross-Origin Resource Sharing).
    // CORS é uma política de segurança dos navegadores que bloqueia requisições
    // feitas de um domínio diferente. Como o frontend (porta 4200) acessa o
    // backend (porta 8080), precisamos autorizar explicitamente esse acesso.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Apenas o frontend Angular em localhost:4200 pode fazer requisições ao backend
        config.setAllowedOrigins(List.of("http://localhost:4200"));

        // Métodos HTTP permitidos nas requisições do frontend
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Qualquer header pode ser enviado (incluindo "Authorization" com o JWT)
        config.setAllowedHeaders(List.of("*"));

        // Permite envio de cookies e credenciais nas requisições
        config.setAllowCredentials(true);

        // Aplica esta configuração para todas as rotas que começam com /api/
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
