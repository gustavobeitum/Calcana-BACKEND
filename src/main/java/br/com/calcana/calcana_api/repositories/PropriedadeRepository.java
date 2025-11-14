package br.com.calcana.calcana_api.repositories;

import br.com.calcana.calcana_api.model.Propriedade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PropriedadeRepository extends JpaRepository<Propriedade, Long>, JpaSpecificationExecutor<Propriedade> {
    Page<Propriedade> findAllByAtivoTrue(Pageable pageable);
    Page<Propriedade> findAllByAtivoFalse(Pageable pageable);
    List<Propriedade> findByFornecedorIdFornecedorAndAtivoTrue(Long idFornecedor);

    long countByAtivoTrue();
    boolean existsByCidadeIdCidade(Long idCidade);
}