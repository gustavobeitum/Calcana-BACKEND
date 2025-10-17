package br.com.calcana.calcana_api.repositories;

import br.com.calcana.calcana_api.model.Propriedade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropriedadeRepository extends JpaRepository<Propriedade, Long> {
}
