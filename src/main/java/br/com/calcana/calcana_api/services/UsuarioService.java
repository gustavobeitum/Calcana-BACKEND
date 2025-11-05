package br.com.calcana.calcana_api.services;

import br.com.calcana.calcana_api.exceptions.ResourceNotFoundException;
import br.com.calcana.calcana_api.model.Perfil;
import br.com.calcana.calcana_api.model.Usuario;
import br.com.calcana.calcana_api.repositories.PerfilRepository;
import br.com.calcana.calcana_api.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario cadastrar(Usuario novoUsuario) {
        Perfil perfil = perfilRepository.findById(novoUsuario.getPerfil().getIdPerfil())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil com o ID informado não foi encontrado!"));
        novoUsuario.setPerfil(perfil);

        String senhaCriptografada = passwordEncoder.encode(novoUsuario.getSenha());
        novoUsuario.setSenha(senhaCriptografada);

        return usuarioRepository.save(novoUsuario);
    }

    //futuramente, os métodos de atualizar, deletar, etc. virão para cá
}