package com.galeriaseleta.service;

import com.galeriaseleta.dto.request.CategoriaRequest;
import com.galeriaseleta.model.Categoria;
import com.galeriaseleta.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + id));
    }

    public Categoria salvar(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNome(request.getNome());
        categoria.setNomeUrl(request.getNomeUrl());

        if (request.getCategoriaMaeId() != null) {
            categoriaRepository.findById(request.getCategoriaMaeId())
                    .ifPresent(categoria::setCategoriaMae);
        }

        if (request.getAtivo() != null) {
            categoria.setAtivo(request.getAtivo());
        }

        return categoriaRepository.save(categoria);
    }

    public Categoria atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscarPorId(id);

        if (request.getNome() != null) categoria.setNome(request.getNome());
        if (request.getNomeUrl() != null) categoria.setNomeUrl(request.getNomeUrl());
        if (request.getAtivo() != null) categoria.setAtivo(request.getAtivo());

        if (request.getCategoriaMaeId() == null) {
            categoria.setCategoriaMae(null);
        } else {
            categoriaRepository.findById(request.getCategoriaMaeId())
                    .ifPresent(categoria::setCategoriaMae);
        }

        return categoriaRepository.save(categoria);
    }

    public void deletar(Long id) {
        categoriaRepository.deleteById(id.intValue());
    }
}
