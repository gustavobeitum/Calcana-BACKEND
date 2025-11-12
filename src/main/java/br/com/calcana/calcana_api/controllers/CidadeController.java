package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.model.Cidade;
import br.com.calcana.calcana_api.services.CidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cidades")
public class CidadeController {

    @Autowired
    private CidadeService cidadeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public List<Cidade> listarTodos() {
        return cidadeService.listarTodas();
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Cidade> cadastrar(@RequestBody Cidade novaCidade) {
        Cidade cidadeSalva = cidadeService.cadastrar(novaCidade);
        return ResponseEntity.status(201).body(cidadeSalva);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public ResponseEntity<Cidade> buscarPorId(@PathVariable Long id) {
        Cidade cidadeEncontrada = cidadeService.buscarPorId(id);
        return ResponseEntity.ok(cidadeEncontrada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Cidade> atualizar(@PathVariable Long id, @RequestBody Cidade dadosParaAtualizar) {
        Cidade cidadeAtualizada = cidadeService.atualizar(id, dadosParaAtualizar);
        return ResponseEntity.ok(cidadeAtualizada);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Cidade> atualizarParcialmente(@PathVariable Long id, @RequestBody Cidade dadosParaAtualizar) {
        Cidade cidadeAtualizada = cidadeService.atualizarParcialmente(id, dadosParaAtualizar);
        return ResponseEntity.ok(cidadeAtualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cidadeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}