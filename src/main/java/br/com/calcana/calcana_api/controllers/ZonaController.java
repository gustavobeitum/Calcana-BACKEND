package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.exceptions.ResourceNotFoundException;
import br.com.calcana.calcana_api.model.Propriedade;
import br.com.calcana.calcana_api.model.Zona;
import br.com.calcana.calcana_api.repositories.PropriedadeRepository;
import br.com.calcana.calcana_api.repositories.ZonaRepository;
import br.com.calcana.calcana_api.services.ZonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/zonas")
public class ZonaController {

    @Autowired
    private ZonaRepository zonaRepository;

    @Autowired
    private PropriedadeRepository propriedadeRepository;

    @Autowired
    private ZonaService zonaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public List<Zona> listarTodas(){
        return zonaRepository.findAllByAtivoTrue();
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Zona> cadastrar(@RequestBody Zona novaZona) {
        Propriedade propriedade = propriedadeRepository.findById(novaZona.getPropriedade().getIdPropriedade())
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade com o ID informado não foi encontrado!"));
        novaZona.setPropriedade(propriedade);

        Zona zonaSalva = zonaRepository.save(novaZona);
        return ResponseEntity.status(201).body(zonaSalva);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public ResponseEntity<Zona> buscarPorId(@PathVariable Long id) {
        return zonaRepository.findById(id)
                .map(zonaEncontrada -> ResponseEntity.ok(zonaEncontrada))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Zona> atualizar(@PathVariable Long id, @RequestBody Zona dadosParaAtualizar) {
        return zonaRepository.findById(id)
                .map(zonaExistente -> {
                    Propriedade propriedade = propriedadeRepository.findById(dadosParaAtualizar.getPropriedade().getIdPropriedade())
                            .orElseThrow(() -> new ResourceNotFoundException("Propriedade com o ID informado não foi encontrado!"));

                    zonaExistente.setNome(dadosParaAtualizar.getNome());
                    zonaExistente.setPropriedade(propriedade);

                    Zona zonaAtualizada = zonaRepository.save(zonaExistente);

                    return ResponseEntity.ok(zonaAtualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Zona> atualizarParcialmente(@PathVariable Long id, @RequestBody Zona dadosParaAtualizar) {
        Zona zonaAtualizada = zonaService.atualizarParcialmente(id, dadosParaAtualizar);
        return ResponseEntity.ok(zonaAtualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        zonaService.desativarZonaEmCascata(id);
        return ResponseEntity.noContent().build();
    }
}
