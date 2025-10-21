package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.model.Perfil;
import br.com.calcana.calcana_api.repositories.PerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/perfis")
public class PerfilController {

    @Autowired
    private PerfilRepository repository;

    @GetMapping
    public List<Perfil> listarTodos() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<Perfil> cadastrar(@RequestBody Perfil novoPerfil) {
        Perfil perfilSalvo = repository.save(novoPerfil);
        return ResponseEntity.status(201).body(perfilSalvo);
    }
}