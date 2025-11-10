package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.model.*;
import br.com.calcana.calcana_api.repositories.*;
import br.com.calcana.calcana_api.services.AnaliseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

import java.util.List;

@RestController
@RequestMapping("/analises")
public class AnaliseController {

    @Autowired
    private AnaliseRepository analiseRepository;

    @Autowired
    private AnaliseService analiseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public List<Analises> listarTodas(
            @RequestParam(required = false) Long fornecedorId,
            @RequestParam(required = false) Long propriedadeId,
            @RequestParam(required = false) String talhao,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        return analiseService.listarTodasFiltradas(fornecedorId, propriedadeId, talhao, dataInicio, dataFim);
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Analises> cadastrar(@RequestBody Analises novaAnalise) {
        Analises analiseSalva = analiseService.calcularEsalvarAnalise(novaAnalise);
        return ResponseEntity.status(201).body(analiseSalva);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public ResponseEntity<Analises> buscarPorId(@PathVariable Long id) {
        Analises analise = analiseService.buscarPorId(id);
        return ResponseEntity.ok(analise);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!analiseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        analiseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}