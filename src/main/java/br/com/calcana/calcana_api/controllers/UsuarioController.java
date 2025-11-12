package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.model.Usuario;
import br.com.calcana.calcana_api.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import br.com.calcana.calcana_api.security.dto.AlterarMinhaSenhaDTO;
import br.com.calcana.calcana_api.security.dto.ResetarSenhaDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('GESTOR')")
    public List<Usuario> listarTodosAtivos(
            @RequestParam(required = false, defaultValue = "ativos") String status,
            @RequestParam(required = false, defaultValue = "OPERADOR") String perfil
    ) {
        return usuarioService.listarTodos(status, perfil);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
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
        Usuario usuarioAtualizado = usuarioService.atualizar(id, dadosParaAtualizar);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Usuario> atualizarParcialmente(@PathVariable Long id, @RequestBody Usuario dadosParaAtualizar) {
        Usuario usuarioAtualizado = usuarioService.atualizarParcialmente(id, dadosParaAtualizar);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/resetar-senha")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Void> resetarSenha(
            @PathVariable Long id,
            @RequestBody ResetarSenhaDTO dto
    ) {
        usuarioService.resetarSenha(id, dto.novaSenha());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/mudar-minha-senha")
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public ResponseEntity<Void> alterarMinhaSenha(
            @RequestBody AlterarMinhaSenhaDTO dto,
            Authentication authentication
    ) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();

        usuarioService.alterarMinhaSenha(usuarioAutenticado, dto.senhaAtual(), dto.novaSenha());
        return ResponseEntity.noContent().build();
    }
}