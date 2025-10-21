package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.exceptions.ResourceNotFoundException;
import br.com.calcana.calcana_api.model.Talhao;
import br.com.calcana.calcana_api.model.Zona;
import br.com.calcana.calcana_api.repositories.TalhaoRepository;
import br.com.calcana.calcana_api.repositories.ZonaRepository;
import br.com.calcana.calcana_api.services.TalhaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/talhoes")
public class TalhaoController {

    @Autowired
    private TalhaoRepository talhaoRepository;

    @Autowired
    private TalhaoService talhaoService;

    @Autowired
    private ZonaRepository zonaRepository;

    @GetMapping
    public List<Talhao> listarTodos() {
        return talhaoRepository.findAllByAtivoTrue();
    }

    @PostMapping
    public ResponseEntity<Talhao> cadastrar(@RequestBody Talhao novoTalhao) {
        Zona zona = zonaRepository.findById(novoTalhao.getZona().getIdZona())
                .orElseThrow(() -> new ResourceNotFoundException("Zona com o ID informado não foi encontrada!"));
        novoTalhao.setZona(zona);
        Talhao talhaoSalvo = talhaoRepository.save(novoTalhao);
        return ResponseEntity.status(201).body(talhaoSalvo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Talhao> buscarPorId(@PathVariable Long id) {
        return talhaoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Talhao> atualizar(@PathVariable Long id, @RequestBody Talhao dadosParaAtualizar) {
        Zona zona = zonaRepository.findById(dadosParaAtualizar.getZona().getIdZona())
                .orElseThrow(() -> new ResourceNotFoundException("Zona com o ID informado não foi encontrada!"));

        return talhaoRepository.findById(id)
                .map(talhaoExistente -> {
                    talhaoExistente.setIdentificador(dadosParaAtualizar.getIdentificador());
                    talhaoExistente.setZona(zona);
                    Talhao talhaoAtualizado = talhaoRepository.save(talhaoExistente);
                    return ResponseEntity.ok(talhaoAtualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Talhao> atualizarParcialmente(@PathVariable Long id, @RequestBody Talhao dadosParaAtualizar) {
        Talhao talhaoAtualizado = talhaoService.atualizarParcialmente(id, dadosParaAtualizar);
        return ResponseEntity.ok(talhaoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!talhaoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Talhao talhao = talhaoRepository.findById(id).get();
        talhao.setAtivo(false);
        talhaoRepository.save(talhao);
        return ResponseEntity.noContent().build();
    }
}
