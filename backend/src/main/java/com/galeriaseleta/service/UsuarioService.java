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

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public PageResponse<UsuarioResponse> listarPaginado(int page, int size) {
        return PageResponse.from(
                usuarioRepository.findAll(PageRequest.of(page, size, Sort.by("id"))),
                UsuarioResponse::from);
    }

    public Usuario atualizarPapel(Long id, String papel) {
        Usuario usuario = buscarPorId(id);
        usuario.setPapel(papel);
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));
    }

    public Usuario atualizar(Long id, AtualizarPerfilRequest request) {
        Usuario usuario = buscarPorId(id);

        if (request.getNome() != null)     usuario.setNome(request.getNome());
        if (request.getTelefone() != null) usuario.setTelefone(request.getTelefone());
        if (request.getCpf() != null)      usuario.setCpf(request.getCpf());

        return usuarioRepository.save(usuario);
    }

    public void alterarSenha(Long id, AlterarSenhaRequest request) {
        Usuario usuario = buscarPorId(id);

        if (!passwordEncoder.matches(request.getSenhaAtual(), usuario.getSenha())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        usuarioRepository.save(usuario);
    }

    public void deletar(Long id) {
        usuarioRepository.deleteById(id.intValue());
    }

    public List<Endereco> listarEnderecos(Long usuarioId) {
        return enderecoRepository.findByUsuarioId(usuarioId.intValue());
    }

    public Endereco adicionarEndereco(Long usuarioId, EnderecoRequest request) {
        Usuario usuario = buscarPorId(usuarioId);

        Endereco endereco = new Endereco();
        endereco.setUsuario(usuario);
        endereco.setRua(request.getRua());
        endereco.setCidade(request.getCidade());
        endereco.setEstado(request.getEstado());
        endereco.setCep(request.getCep());

        if (request.getPrincipal() != null) {
            endereco.setPrincipal(request.getPrincipal());
        }

        return enderecoRepository.save(endereco);
    }

    public void removerEndereco(Long enderecoId) {
        enderecoRepository.deleteById(enderecoId.intValue());
    }
}
