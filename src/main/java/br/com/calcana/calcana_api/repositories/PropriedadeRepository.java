package br.com.calcana.calcana_api.repositories;

import br.com.calcana.calcana_api.model.Fornecedor;
import br.com.calcana.calcana_api.model.Propriedade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PropriedadeRepository extends JpaRepository<Propriedade, Long>, JpaSpecificationExecutor<Propriedade> {
    Page<Propriedade> findAllByAtivoTrue(Pageable pageable);
    Page<Propriedade> findAllByAtivoFalse(Pageable pageable);
    List<Propriedade> findByFornecedorIdFornecedorAndAtivoTrue(Long idFornecedor);

    Optional<Propriedade> findByNomeAndFornecedor(String nome, Fornecedor fornecedor);

    long countByAtivoTrue();
    boolean existsByCidadeIdCidade(Long idCidade);
}