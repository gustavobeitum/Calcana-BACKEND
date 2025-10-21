package br.com.calcana.calcana_api.repositories;

import br.com.calcana.calcana_api.model.Zona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZonaRepository extends JpaRepository<Zona, Long> {
    List<Zona> findAllByAtivoTrue();
    List<Zona> findByPropriedadeIdPropriedadeAndAtivoTrue(Long idPropriedade);
}
