package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.model.Perfil;
import br.com.calcana.calcana_api.services.PerfilService; // Importamos o Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/perfis")
public class PerfilController {

    @Autowired
    private PerfilService perfilService;

    @GetMapping
    public List<Perfil> listarTodos() {
        return perfilService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Perfil> buscarPorId(@PathVariable Long id) {
        Perfil perfil = perfilService.buscarPorId(id);
        return ResponseEntity.ok(perfil);
    }

    @PostMapping
    public ResponseEntity<Perfil> cadastrar(@RequestBody Perfil novoPerfil) {
        Perfil perfilSalvo = perfilService.cadastrar(novoPerfil);
        return ResponseEntity.status(201).body(perfilSalvo);
    }
}