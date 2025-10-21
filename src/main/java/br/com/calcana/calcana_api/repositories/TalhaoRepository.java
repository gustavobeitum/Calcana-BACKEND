package br.com.calcana.calcana_api.repositories;

import br.com.calcana.calcana_api.model.Talhao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TalhaoRepository extends JpaRepository<Talhao, Long> {
    List<Talhao> findAllByAtivoTrue();
    List<Talhao> findByZonaIdZonaAndAtivoTrue(Long idZona);
}
