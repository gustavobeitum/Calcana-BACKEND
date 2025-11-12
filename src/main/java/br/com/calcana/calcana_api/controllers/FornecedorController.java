package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.model.Fornecedor;
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

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public List<Fornecedor> listarTodos(
            @RequestParam(required = false, defaultValue = "ativos") String status
    ) {
        return fornecedorService.listarTodos(status);
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Fornecedor> cadastrar(@RequestBody Fornecedor novoFornecedor) {
        Fornecedor fornecedorSalvo = fornecedorService.cadastrar(novoFornecedor);
        return ResponseEntity.status(201).body(fornecedorSalvo);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public ResponseEntity<Fornecedor> buscarPorId(@PathVariable Long id) {
        Fornecedor fornecedorEncontrado = fornecedorService.buscarPorId(id);
        return ResponseEntity.ok(fornecedorEncontrado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Fornecedor> atualizar(@PathVariable Long id, @RequestBody Fornecedor dadosParaAtualizar) {
        Fornecedor fornecedorAtualizado = fornecedorService.atualizar(id, dadosParaAtualizar);
        return ResponseEntity.ok(fornecedorAtualizado);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Fornecedor> atualizarParcialmente(@PathVariable Long id, @RequestBody Fornecedor dadosParaAtualizar) {
        Fornecedor fornecedorAtualizado = fornecedorService.atualizarParcialmente(id, dadosParaAtualizar);
        return ResponseEntity.ok(fornecedorAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        fornecedorService.desativarFornecedorEmCascata(id);
        return ResponseEntity.noContent().build();
    }
}