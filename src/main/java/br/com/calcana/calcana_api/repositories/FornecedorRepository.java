package br.com.calcana.calcana_api.repositories;

import br.com.calcana.calcana_api.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    List<Fornecedor> findAllByAtivoTrue();
    List<Fornecedor> findAllByAtivoFalse();

    long countByAtivoTrue();

    Optional<Fornecedor> findByEmail(String email);
    Optional<Fornecedor> findByEmailAndIdFornecedorNot(String email, Long idFornecedor);
    Optional<Fornecedor> findByNome(String nome);
    Optional<Fornecedor> findByNomeAndIdFornecedorNot(String nome, Long idFornecedor);
}