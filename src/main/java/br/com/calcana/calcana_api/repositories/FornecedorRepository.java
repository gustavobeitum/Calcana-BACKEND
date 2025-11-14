package br.com.calcana.calcana_api.repositories;

import br.com.calcana.calcana_api.model.Fornecedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long>, JpaSpecificationExecutor<Fornecedor> {
    Page<Fornecedor> findAllByAtivoTrue(Pageable pageable);
    Page<Fornecedor> findAllByAtivoFalse(Pageable pageable);

    long countByAtivoTrue();

    Optional<Fornecedor> findByEmail(String email);
    Optional<Fornecedor> findByEmailAndIdFornecedorNot(String email, Long idFornecedor);
    Optional<Fornecedor> findByNome(String nome);
    Optional<Fornecedor> findByNomeAndIdFornecedorNot(String nome, Long idFornecedor);
}