package br.com.calcana.calcana_api.controllers;


import br.com.calcana.calcana_api.model.Cidade;
import br.com.calcana.calcana_api.repositories.CidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/cidades")
public class CidadeController {

    @Autowired
    private CidadeRepository repository;

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public List<Cidade> listarTodos(){return repository.findAll();}

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Cidade> cadastrar(@RequestBody Cidade novoCidade){
        Cidade cidadeSalvo = repository.save(novoCidade);
        return ResponseEntity.status(201).body(cidadeSalvo);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public ResponseEntity<Cidade> buscarPorId(@PathVariable Long id){
        return repository.findById(id)
                .map(cidadeEncontrado -> ResponseEntity.ok(cidadeEncontrado))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Cidade> atualizar(@PathVariable Long id, @RequestBody Cidade dadosParaAtualizar){
        return repository.findById(id)
                .map(cidadeExistente -> {
                    cidadeExistente.setNome(dadosParaAtualizar.getNome());
                    cidadeExistente.setUf(dadosParaAtualizar.getUf());

                    Cidade cidadeAtualizado = repository.save(cidadeExistente);

                    return ResponseEntity.ok(cidadeAtualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Cidade> atualizarParcialmente(@PathVariable Long id, @RequestBody Cidade dadosParaAtualizar) {
        return repository.findById(id)
                .map(cidadeExistente -> {
                    if (dadosParaAtualizar.getNome() != null) {
                        cidadeExistente.setNome(dadosParaAtualizar.getNome());
                    }
                    if (dadosParaAtualizar.getUf() != null) {
                        cidadeExistente.setUf(dadosParaAtualizar.getUf());
                    }

                    Cidade cidadeAtualizada = repository.save(cidadeExistente);
                    return ResponseEntity.ok(cidadeAtualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        if(!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
