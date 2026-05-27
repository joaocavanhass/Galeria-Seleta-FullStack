// ============================================================
// ARQUIVO: UsuarioService.java
// FUNÇÃO: Serviço com a lógica de gerenciamento de usuários e endereços.
//
// RESPONSABILIDADES:
// - Listar usuários (admin)
// - Promover/rebaixar papel (admin)
// - Atualizar perfil (nome, telefone, CPF)
// - Alterar senha (verifica a senha atual antes)
// - Deletar conta
// - Gerenciar endereços (listar, adicionar, remover)
//
// SEGURANÇA NA TROCA DE SENHA:
// O método alterarSenha() exige a senha atual para confirmar identidade.
// Isso previne que alguém que apenas pegou o dispositivo desbloqueado
// troque a senha sem saber a senha original.
//
// CONEXÕES: chamado por UsuarioController.
// ============================================================

package com.galeriaseleta.service;

import com.galeriaseleta.dto.request.AlterarSenhaRequest;
import com.galeriaseleta.dto.request.AtualizarPerfilRequest;
import com.galeriaseleta.dto.request.EnderecoRequest;
import com.galeriaseleta.dto.response.PageResponse;
import com.galeriaseleta.dto.response.UsuarioResponse;
import com.galeriaseleta.model.Endereco;
import com.galeriaseleta.model.Usuario;
import com.galeriaseleta.repository.EnderecoRepository;
import com.galeriaseleta.repository.UsuarioRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          EnderecoRepository enderecoRepository,
                          BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.enderecoRepository = enderecoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Retorna todos os usuários (sem paginação) — uso interno
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // Retorna usuários paginados, ordenados pelo ID crescente
    // Sort.by("id"): ordena por ID em ordem crescente (padrão)
    public PageResponse<UsuarioResponse> listarPaginado(int page, int size) {
        return PageResponse.from(
                usuarioRepository.findAll(PageRequest.of(page, size, Sort.by("id"))),
                UsuarioResponse::from); // Converte cada Usuario para UsuarioResponse (sem senha)
    }

    // Altera o papel de um usuário: "cliente" ↔ "admin" (somente admin pode fazer isso)
    public Usuario atualizarPapel(Long id, String papel) {
        Usuario usuario = buscarPorId(id);
        usuario.setPapel(papel);
        return usuarioRepository.save(usuario);
    }

    // Busca um usuário pelo ID — lança exceção se não existir
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));
    }

    // Busca um usuário pelo email — usado para identificar o usuário logado
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));
    }

    // Atualiza os dados do perfil do usuário.
    // Só atualiza os campos que foram informados (não sobrescreve com null).
    public Usuario atualizar(Long id, AtualizarPerfilRequest request) {
        Usuario usuario = buscarPorId(id);

        if (request.getNome() != null)     usuario.setNome(request.getNome());
        if (request.getTelefone() != null) usuario.setTelefone(request.getTelefone());
        if (request.getCpf() != null)      usuario.setCpf(request.getCpf());

        return usuarioRepository.save(usuario);
    }

    // Altera a senha do usuário após confirmar a senha atual
    public void alterarSenha(Long id, AlterarSenhaRequest request) {
        Usuario usuario = buscarPorId(id);

        // matches(): compara a senha atual enviada com o hash armazenado no banco
        // Se não bater, rejeita a operação
        if (!passwordEncoder.matches(request.getSenhaAtual(), usuario.getSenha())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        // Criptografa e salva a nova senha
        usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        usuarioRepository.save(usuario);
    }

    // Deleta a conta do usuário permanentemente
    public void deletar(Long id) {
        usuarioRepository.deleteById(id.intValue());
    }

    // Retorna todos os endereços cadastrados de um usuário
    public List<Endereco> listarEnderecos(Long usuarioId) {
        return enderecoRepository.findByUsuarioId(usuarioId.intValue());
    }

    // Adiciona um novo endereço ao usuário
    public Endereco adicionarEndereco(Long usuarioId, EnderecoRequest request) {
        Usuario usuario = buscarPorId(usuarioId);

        Endereco endereco = new Endereco();
        endereco.setUsuario(usuario);
        endereco.setRua(request.getRua());
        endereco.setCidade(request.getCidade());
        endereco.setEstado(request.getEstado());
        endereco.setCep(request.getCep());

        // Só define "principal" se o campo foi informado (não sobrescreve com null)
        if (request.getPrincipal() != null) {
            endereco.setPrincipal(request.getPrincipal());
        }

        return enderecoRepository.save(endereco);
    }

    // Remove um endereço pelo ID
    public void removerEndereco(Long enderecoId) {
        enderecoRepository.deleteById(enderecoId.intValue());
    }
}
