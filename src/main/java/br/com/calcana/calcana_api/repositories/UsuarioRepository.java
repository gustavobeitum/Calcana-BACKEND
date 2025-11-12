package br.com.calcana.calcana_api.repositories;

import br.com.calcana.calcana_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    List<Usuario> findAllByAtivoTrue();
    List<Usuario> findAllByAtivoFalse();
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailAndIdUsuarioNot(String email, Long idUsuario);

    List<Usuario> findByPerfilDescricaoAndAtivoTrue(String descricao);
    List<Usuario> findByPerfilDescricaoAndAtivoFalse(String descricao);
    List<Usuario> findByPerfilDescricao(String descricao);
}