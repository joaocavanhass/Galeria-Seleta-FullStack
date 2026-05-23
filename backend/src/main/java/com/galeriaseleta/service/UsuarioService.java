package com.galeriaseleta.service;

import com.galeriaseleta.dto.request.AlterarSenhaRequest;
import com.galeriaseleta.dto.request.AtualizarPerfilRequest;
import com.galeriaseleta.dto.request.EnderecoRequest;
import com.galeriaseleta.model.Endereco;
import com.galeriaseleta.model.Usuario;
import com.galeriaseleta.repository.EnderecoRepository;
import com.galeriaseleta.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository, EnderecoRepository enderecoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.enderecoRepository = enderecoRepository;
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

        if (request.getNome() != null) usuario.setNome(request.getNome());
        if (request.getTelefone() != null) usuario.setTelefone(request.getTelefone());

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
