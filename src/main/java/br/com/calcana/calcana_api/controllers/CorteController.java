package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.model.Corte;
import br.com.calcana.calcana_api.repositories.CorteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cortes")
public class CorteController {

    @Autowired
    private CorteRepository repository;

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public List<Corte> listarTodos() {
        return repository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Corte> cadastrar(@RequestBody Corte novoCorte) {
        Corte corteSalvo = repository.save(novoCorte);
        return ResponseEntity.status(201).body(corteSalvo);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public ResponseEntity<Corte> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Corte> atualizar(@PathVariable Long id, @RequestBody Corte dadosParaAtualizar) {
        return repository.findById(id)
                .map(corteExistente -> {
                    corteExistente.setDescricao(dadosParaAtualizar.getDescricao());
                    Corte corteAtualizado = repository.save(corteExistente);
                    return ResponseEntity.ok(corteAtualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}