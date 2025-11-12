package br.com.calcana.calcana_api.services;

import org.springframework.security.access.AccessDeniedException;
import br.com.calcana.calcana_api.exceptions.ResourceNotFoundException;
import br.com.calcana.calcana_api.model.Perfil;
import br.com.calcana.calcana_api.model.Usuario;
import br.com.calcana.calcana_api.repositories.PerfilRepository;
import br.com.calcana.calcana_api.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario cadastrar(Usuario novoUsuario) {
        Perfil perfil = perfilRepository.findById(novoUsuario.getPerfil().getIdPerfil())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil com o ID informado não foi encontrado!"));

        if ("GESTOR".equalsIgnoreCase(perfil.getDescricao())) {
            throw new AccessDeniedException("Não é permitido criar usuários com o perfil de GESTOR.");
        }

        novoUsuario.setPerfil(perfil);

        String senhaCriptografada = passwordEncoder.encode(novoUsuario.getSenha());
        novoUsuario.setSenha(senhaCriptografada);

        novoUsuario.setAtivo(true);

        return usuarioRepository.save(novoUsuario);
    }

    public List<Usuario> listarTodos(String status, String perfil) {
        String perfilUpper = (perfil != null && !perfil.isEmpty()) ? perfil.toUpperCase() : null;

        if (perfilUpper != null) {
            if ("inativos".equalsIgnoreCase(status)) {
                return usuarioRepository.findByPerfilDescricaoAndAtivoFalse(perfilUpper);
            } else if ("todos".equalsIgnoreCase(status)) {
                return usuarioRepository.findByPerfilDescricao(perfilUpper);
            } else { // "ativos" ou default
                return usuarioRepository.findByPerfilDescricaoAndAtivoTrue(perfilUpper);
            }
        } else {
            if ("inativos".equalsIgnoreCase(status)) {
                return usuarioRepository.findAllByAtivoFalse();
            } else if ("todos".equalsIgnoreCase(status)) {
                return usuarioRepository.findAll();
            } else {
                return usuarioRepository.findAllByAtivoTrue();
            }
        }
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com ID " + id + " não encontrado!"));
    }

    public Usuario atualizar(Long id, Usuario dadosParaAtualizar) {
        Usuario usuarioExistente = buscarPorId(id);

        verificarRegraGestor(usuarioExistente);

        Perfil perfil = perfilRepository.findById(dadosParaAtualizar.getPerfil().getIdPerfil())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil com o ID informado não foi encontrado!"));

        if ("GESTOR".equalsIgnoreCase(perfil.getDescricao())) {
            throw new AccessDeniedException("Não é permitido definir o perfil de GESTOR.");
        }

        usuarioExistente.setNome(dadosParaAtualizar.getNome());
        usuarioExistente.setEmail(dadosParaAtualizar.getEmail());
        usuarioExistente.setPerfil(perfil);

        return usuarioRepository.save(usuarioExistente);
    }

    @Transactional
    public Usuario atualizarParcialmente(Long id, Usuario dadosParaAtualizar) {
        Usuario usuarioExistente = buscarPorId(id);

        verificarRegraGestor(usuarioExistente);

        if (dadosParaAtualizar.getNome() != null) {
            usuarioExistente.setNome(dadosParaAtualizar.getNome());
        }
        if (dadosParaAtualizar.getEmail() != null) {
            usuarioExistente.setEmail(dadosParaAtualizar.getEmail());
        }
        if (dadosParaAtualizar.getPerfil() != null && dadosParaAtualizar.getPerfil().getIdPerfil() != null) {
            Perfil perfil = perfilRepository.findById(dadosParaAtualizar.getPerfil().getIdPerfil())
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil com o ID informado não foi encontrado!"));

            if ("GESTOR".equalsIgnoreCase(perfil.getDescricao())) {
                throw new AccessDeniedException("Não é permitido definir o perfil de GESTOR.");
            }

            usuarioExistente.setPerfil(perfil);
        }
        if (dadosParaAtualizar.getPerfil() != null && dadosParaAtualizar.getPerfil().getIdPerfil() != null) {
            Perfil perfil = perfilRepository.findById(dadosParaAtualizar.getPerfil().getIdPerfil())
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil com o ID informado não foi encontrado!"));
            usuarioExistente.setPerfil(perfil);
        }
        if (dadosParaAtualizar.getAtivo() != null) {
            usuarioExistente.setAtivo(dadosParaAtualizar.getAtivo());
        }

        return usuarioRepository.save(usuarioExistente);
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuario = buscarPorId(id);

        verificarRegraGestor(usuario);

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void resetarSenha(Long idUsuario, String novaSenha) {
        Usuario usuario = buscarPorId(idUsuario);

        verificarRegraGestor(usuario);

        if (novaSenha == null || novaSenha.length() < 6) {
            throw new RuntimeException("A nova senha deve ter pelo menos 6 caracteres.");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void alterarMinhaSenha(Usuario usuarioAutenticado, String senhaAtual, String novaSenha) {

        if (!passwordEncoder.matches(senhaAtual, usuarioAutenticado.getPassword())) {
            throw new RuntimeException("A 'senhaAtual' está incorreta.");
        }

        if (novaSenha == null || novaSenha.length() < 6) {
            throw new RuntimeException("A nova senha deve ter pelo menos 6 caracteres.");
        }

        if (passwordEncoder.matches(novaSenha, usuarioAutenticado.getPassword())) {
            throw new RuntimeException("A nova senha não pode ser igual à senha atual.");
        }

        usuarioAutenticado.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuarioAutenticado);
    }

    private void verificarRegraGestor(Usuario usuarioAlvo) {

        if ("GESTOR".equalsIgnoreCase(usuarioAlvo.getPerfil().getDescricao())) {
            throw new AccessDeniedException("Ação não permitida. Gestores não podem ser modificados.");
        }
    }
}