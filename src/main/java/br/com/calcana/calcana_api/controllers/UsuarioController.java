package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.exceptions.ResourceNotFoundException;
import br.com.calcana.calcana_api.model.Perfil;
import br.com.calcana.calcana_api.model.Usuario;
import br.com.calcana.calcana_api.repositories.PerfilRepository;
import br.com.calcana.calcana_api.repositories.UsuarioRepository;
import br.com.calcana.calcana_api.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('GESTOR')")
    public List<Usuario> listarTodosAtivos() {
        return usuarioRepository.findAllByAtivoTrue();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario novoUsuario) {
        Usuario usuarioSalvo = usuarioService.cadastrar(novoUsuario);
        return ResponseEntity.status(201).body(usuarioSalvo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody Usuario dadosParaAtualizar) {
        Perfil perfil = perfilRepository.findById(dadosParaAtualizar.getPerfil().getIdPerfil())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil com o ID informado não foi encontrado!"));

        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    usuarioExistente.setNome(dadosParaAtualizar.getNome());
                    usuarioExistente.setEmail(dadosParaAtualizar.getEmail());
                    usuarioExistente.setPerfil(perfil);
                    Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);
                    return ResponseEntity.ok(usuarioAtualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Usuario> atualizarParcialmente(@PathVariable Long id, @RequestBody Usuario dadosParaAtualizar) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    if (dadosParaAtualizar.getNome() != null) {
                        usuarioExistente.setNome(dadosParaAtualizar.getNome());
                    }
                    if (dadosParaAtualizar.getEmail() != null) {
                        usuarioExistente.setEmail(dadosParaAtualizar.getEmail());
                    }
                    if (dadosParaAtualizar.getPerfil() != null && dadosParaAtualizar.getPerfil().getIdPerfil() != null) {
                        Perfil perfil = perfilRepository.findById(dadosParaAtualizar.getPerfil().getIdPerfil())
                                .orElseThrow(() -> new ResourceNotFoundException("Perfil com o ID informado não foi encontrado!"));
                        usuarioExistente.setPerfil(perfil);
                    }

                    if (dadosParaAtualizar.getAtivo() != null) {
                        usuarioExistente.setAtivo(dadosParaAtualizar.getAtivo());
                    }

                    Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);
                    return ResponseEntity.ok(usuarioAtualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioRepository.findById(id).get();

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        return ResponseEntity.noContent().build();
    }
}