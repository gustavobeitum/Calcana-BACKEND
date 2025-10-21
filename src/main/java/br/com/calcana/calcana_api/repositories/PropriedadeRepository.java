package br.com.calcana.calcana_api.repositories;

import br.com.calcana.calcana_api.model.Propriedade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropriedadeRepository extends JpaRepository<Propriedade, Long> {
    List<Propriedade> findAllByAtivoTrue();
    List<Propriedade> findByFornecedorIdFornecedorAndAtivoTrue(Long idFornecedor);
}
