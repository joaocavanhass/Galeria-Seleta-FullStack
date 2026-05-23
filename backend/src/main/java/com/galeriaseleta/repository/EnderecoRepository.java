package com.galeriaseleta.repository;

import com.galeriaseleta.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {
    List<Endereco> findByUsuarioId(Integer usuarioId);
    Optional<Endereco> findByUsuarioIdAndPrincipalTrue(Integer usuarioId);
}
