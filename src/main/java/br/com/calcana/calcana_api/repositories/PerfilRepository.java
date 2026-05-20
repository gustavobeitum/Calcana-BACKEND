package br.com.calcana.calcana_api.repositories;

import br.com.calcana.calcana_api.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {
    Optional<Perfil> findByDescricaoIgnoreCase(String descricao);
}