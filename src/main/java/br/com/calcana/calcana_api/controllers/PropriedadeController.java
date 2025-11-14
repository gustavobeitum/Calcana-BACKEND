package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.services.PropriedadeService;
import br.com.calcana.calcana_api.model.Propriedade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/propriedades")
public class PropriedadeController {

    @Autowired
    private PropriedadeService propriedadeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public Page<Propriedade> listarTodas(
            @RequestParam(required = false, defaultValue = "ativos") String status,

            @RequestParam(required = false) String search,

            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return propriedadeService.listarTodos(status, search, pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Propriedade> cadastrar(@RequestBody Propriedade novaPropriedade) {
        Propriedade propriedadeSalva = propriedadeService.cadastrar(novaPropriedade);
        return ResponseEntity.status(201).body(propriedadeSalva);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public ResponseEntity<Propriedade> buscarPorId(@PathVariable Long id) {
        Propriedade propriedadeEncontrada = propriedadeService.buscarPorId(id);
        return ResponseEntity.ok(propriedadeEncontrada);
    }

    @GetMapping("/por-fornecedor/{fornecedorId}")
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public List<Propriedade> buscarPorFornecedor(@PathVariable Long fornecedorId) {
        return propriedadeService.buscarPorFornecedor(fornecedorId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Propriedade> atualizar(@PathVariable Long id, @RequestBody Propriedade dadosParaAtualizar) {
        Propriedade propriedadeAtualizada = propriedadeService.atualizar(id, dadosParaAtualizar);
        return ResponseEntity.ok(propriedadeAtualizada);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Propriedade> atualizarParcialmente(@PathVariable Long id, @RequestBody Propriedade dadosParaAtualizar) {
        Propriedade propriedadeAtualizada = propriedadeService.atualizarParcialmente(id, dadosParaAtualizar);
        return ResponseEntity.ok(propriedadeAtualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        propriedadeService.desativarPropriedade(id);
        return ResponseEntity.noContent().build();
    }
}