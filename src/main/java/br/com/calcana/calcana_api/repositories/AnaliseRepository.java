package br.com.calcana.calcana_api.repositories;

import br.com.calcana.calcana_api.model.Analises;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnaliseRepository extends JpaRepository<Analises, Long> {
}