// ============================================================
// ARQUIVO: CategoriaService.java
// FUNÇÃO: Serviço com a lógica de gerenciamento de categorias.
// Permite criar, listar, atualizar e deletar categorias.
// Suporta hierarquia: uma categoria pode ter uma "categoria mãe".
//
// CONEXÕES: chamado por CategoriaController.
// ============================================================

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

    // Retorna todas as categorias (ativas e inativas)
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    // Busca uma categoria pelo ID — lança exceção se não existir
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + id));
    }

    // Cria uma nova categoria a partir do request do admin
    public Categoria salvar(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNome(request.getNome());
        categoria.setNomeUrl(request.getNomeUrl()); // Slug da URL

        // Se informou uma categoria mãe, busca ela no banco e associa
        // ifPresent: só executa se a categoria mãe existir no banco
        if (request.getCategoriaMaeId() != null) {
            categoriaRepository.findById(request.getCategoriaMaeId())
                    .ifPresent(categoria::setCategoriaMae);
        }

        // Só define ativo se foi informado
        if (request.getAtivo() != null) {
            categoria.setAtivo(request.getAtivo());
        }

        return categoriaRepository.save(categoria);
    }

    // Atualiza uma categoria existente
    public Categoria atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscarPorId(id);

        // Atualiza apenas os campos informados
        if (request.getNome() != null) categoria.setNome(request.getNome());
        if (request.getNomeUrl() != null) categoria.setNomeUrl(request.getNomeUrl());
        if (request.getAtivo() != null) categoria.setAtivo(request.getAtivo());

        // Se categoriaMaeId for null, remove a hierarquia (categoria vira raiz)
        // Se informado, busca e associa a nova categoria mãe
        if (request.getCategoriaMaeId() == null) {
            categoria.setCategoriaMae(null);
        } else {
            categoriaRepository.findById(request.getCategoriaMaeId())
                    .ifPresent(categoria::setCategoriaMae);
        }

        return categoriaRepository.save(categoria);
    }

    // Remove uma categoria pelo ID
    public void deletar(Long id) {
        categoriaRepository.deleteById(id.intValue());
    }
}
