package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.model.Fornecedor;
import br.com.calcana.calcana_api.repositories.FornecedorRepository;
import br.com.calcana.calcana_api.services.FornecedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/fornecedores")
public class FornecedorController {

    @Autowired
    private FornecedorService fornecedorService;

    @Autowired
    private FornecedorRepository repository;

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public List<Fornecedor> listarTodos() {
        return repository.findAllByAtivoTrue();
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Fornecedor> cadastrar(@RequestBody Fornecedor novoFornecedor) {
        Fornecedor fornecedorSalvo = repository.save(novoFornecedor);
        return ResponseEntity.status(201).body(fornecedorSalvo);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public ResponseEntity<Fornecedor> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(fornecedorEncontrado -> ResponseEntity.ok(fornecedorEncontrado))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Fornecedor> atualizar(@PathVariable Long id, @RequestBody Fornecedor dadosParaAtualizar) {
        return repository.findById(id)
                .map(fornecedorExistente -> {
                    fornecedorExistente.setNome(dadosParaAtualizar.getNome());
                    fornecedorExistente.setEmail(dadosParaAtualizar.getEmail());

                    Fornecedor fornecedorAtualizado = repository.save(fornecedorExistente);

                    return ResponseEntity.ok(fornecedorAtualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Fornecedor> atualizarParcialmente(@PathVariable Long id, @RequestBody Fornecedor dadosParaAtualizar) {
        return repository.findById(id)
                .map(fornecedorExistente -> {
                    if (dadosParaAtualizar.getNome() != null) {
                        fornecedorExistente.setNome(dadosParaAtualizar.getNome());
                    }
                    if (dadosParaAtualizar.getEmail() != null) {
                        fornecedorExistente.setEmail(dadosParaAtualizar.getEmail());
                    }
                    if (dadosParaAtualizar.getAtivo() != null) {
                        fornecedorExistente.setAtivo(dadosParaAtualizar.getAtivo());
                    }
                    Fornecedor fornecedorAtualizado = repository.save(fornecedorExistente);
                    return ResponseEntity.ok(fornecedorAtualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        fornecedorService.desativarFornecedorEmCascata(id);
        return ResponseEntity.noContent().build();
    }
}