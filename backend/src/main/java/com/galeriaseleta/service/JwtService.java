// ============================================================
// ARQUIVO: JwtService.java
// FUNÇÃO: Serviço responsável por gerar e validar tokens JWT.
//
// O QUE É JWT (JSON Web Token)?
// É uma string codificada em Base64 com três partes separadas por ".":
// 1. HEADER: algoritmo usado (HS256)
// 2. PAYLOAD: dados embutidos (email, userId, validade)
// 3. SIGNATURE: assinatura criptográfica que garante que ninguém alterou o token
//
// EXEMPLO DE TOKEN:
// eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2FvQGVtYWlsLmNvbSIsInVzZXJJZCI6MX0.Xk5Q...
// [   HEADER base64   ].[          PAYLOAD base64          ].[SIGNATURE]
//
// COMO FUNCIONA A SEGURANÇA:
// O servidor cria o token com a chave secreta (jwt.secret).
// O frontend guarda o token e o envia em cada requisição.
// O servidor verifica a assinatura com a mesma chave — se bater, é válido.
// Ninguém pode criar um token falso sem conhecer a chave secreta.
//
// CONEXÕES: usado por AuthService (gerar tokens) e JwtAuthenticationFilter (validar).
// ============================================================

package com.galeriaseleta.service;

import com.galeriaseleta.model.Usuario;
// Claims: representa o payload do JWT (os dados embutidos)
import io.jsonwebtoken.Claims;
// Jwts: builder/parser de tokens JWT da biblioteca JJWT
import io.jsonwebtoken.Jwts;
// Keys: utilitário para criar chaves criptográficas
import io.jsonwebtoken.security.Keys;
// @Value: injeta valores do application.properties nos campos
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey; // Tipo da chave HMAC usada para assinar o token
import java.nio.charset.StandardCharsets;
import java.util.Date;         // Para datas de emissão e expiração
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function; // Para extrair claims de forma genérica

@Service
public class JwtService {

    // Injeta o valor de jwt.secret do application.properties
    @Value("${jwt.secret}")
    private String secret;

    // Injeta o tempo de expiração do access token (em milissegundos) — padrão: 24h
    @Value("${jwt.expiration}")
    private long expiration;

    // Injeta o tempo de expiração do refresh token (em milissegundos) — padrão: 7 dias
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    // Gera o ACCESS TOKEN para o usuário — válido por 24 horas.
    // Inclui no payload: email (subject) e userId (claim extra)
    public String gerarToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuario.getId()); // Dado extra embutido no token
        return buildToken(claims, usuario.getEmail(), expiration);
    }

    // Gera o REFRESH TOKEN — válido por 7 dias.
    // Não inclui userId — serve apenas para renovar o access token
    public String gerarRefreshToken(Usuario usuario) {
        return buildToken(new HashMap<>(), usuario.getEmail(), refreshExpiration);
    }

    // Extrai o email (subject) do token
    public String extrairEmail(String token) {
        return extrairClaim(token, Claims::getSubject); // Claims::getSubject = o "subject" do JWT
    }

    // Extrai o userId do token (claim personalizado que adicionamos)
    public Integer extrairUsuarioId(String token) {
        return extrairClaim(token, claims -> claims.get("userId", Integer.class));
    }

    // Valida se o token é válido: email bate com o usuário E o token não expirou
    public boolean validarToken(String token, UserDetails userDetails) {
        String email = extrairEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpirado(token);
    }

    // Verifica se o token já expirou comparando a data de expiração com a data atual
    public boolean isTokenExpirado(String token) {
        return extrairClaim(token, Claims::getExpiration).before(new Date());
    }

    // Método genérico para extrair qualquer claim do token.
    // Function<Claims, T>: uma função que recebe o payload e retorna um campo específico.
    // Ex: extrairClaim(token, Claims::getSubject) → extrai o email
    //     extrairClaim(token, Claims::getExpiration) → extrai a data de expiração
    public <T> T extrairClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extrairTodosClaims(token));
    }

    // Monta o JWT com todos os campos e assina com a chave secreta
    private String buildToken(Map<String, Object> extraClaims, String subject, long expiry) {
        return Jwts.builder()
                .claims(extraClaims)               // Claims extras (ex: userId)
                .subject(subject)                  // Identidade principal (email)
                .issuedAt(new Date())              // Data/hora de emissão (agora)
                .expiration(new Date(System.currentTimeMillis() + expiry)) // Data de expiração
                .signWith(getSigningKey())          // Assina com a chave secreta HMAC
                .compact();                        // Gera a string final do token
    }

    // Decodifica e valida a assinatura do token, retornando todos os claims (payload)
    private Claims extrairTodosClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Usa a mesma chave para verificar a assinatura
                .build()
                .parseSignedClaims(token)   // Faz o parse e lança exceção se inválido/expirado
                .getPayload();              // Retorna o payload (Claims)
    }

    // Gera a chave criptográfica HMAC-SHA a partir da string secreta do application.properties
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
