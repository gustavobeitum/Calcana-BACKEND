package br.com.calcana.calcana_api.repositories;

import br.com.calcana.calcana_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    List<Usuario> findAllByAtivoTrue();
}