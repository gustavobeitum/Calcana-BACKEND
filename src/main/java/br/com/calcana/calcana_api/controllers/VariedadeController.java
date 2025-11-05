package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.model.Variedade;
import br.com.calcana.calcana_api.repositories.VariedadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/variedades")
public class VariedadeController {

    @Autowired
    private VariedadeRepository repository;

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public List<Variedade> listarTodas() {return repository.findAll();}

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Variedade> cadastrar(@RequestBody Variedade novaVariedade) {
        Variedade variedadeSalva = repository.save(novaVariedade);
        return ResponseEntity.status(201).body(variedadeSalva);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public ResponseEntity<Variedade> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Variedade> atualizar(@PathVariable Long id, @RequestBody Variedade dadosParaAtualizar) {
        return repository.findById(id)
                .map(variedadeExistente -> {
                    variedadeExistente.setNome(dadosParaAtualizar.getNome());
                    variedadeExistente.setDescricao(dadosParaAtualizar.getDescricao());
                    Variedade variedadeAtualizada = repository.save(variedadeExistente);
                    return ResponseEntity.ok(variedadeAtualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Variedade> atualizarParcialmente(@PathVariable Long id, @RequestBody Variedade dadosParaAtualizar) {
        return repository.findById(id)
                .map(variedadeExistente -> {
                    if (dadosParaAtualizar.getNome() != null) {
                        variedadeExistente.setNome(dadosParaAtualizar.getNome());
                    }
                    if (dadosParaAtualizar.getDescricao() != null) {
                        variedadeExistente.setDescricao(dadosParaAtualizar.getDescricao());
                    }
                    Variedade variedadeAtualizada = repository.save(variedadeExistente);
                    return ResponseEntity.ok(variedadeAtualizada);
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
