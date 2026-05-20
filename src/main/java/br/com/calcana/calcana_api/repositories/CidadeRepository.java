package br.com.calcana.calcana_api.repositories;

import br.com.calcana.calcana_api.model.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {
    Optional<Cidade> findByNomeIgnoreCase(String nome);
}
