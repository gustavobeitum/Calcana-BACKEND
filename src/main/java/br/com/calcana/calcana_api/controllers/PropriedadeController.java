package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.services.PropriedadeService;
import br.com.calcana.calcana_api.exceptions.ResourceNotFoundException;
import br.com.calcana.calcana_api.model.Cidade;
import br.com.calcana.calcana_api.model.Fornecedor;
import br.com.calcana.calcana_api.model.Propriedade;
import br.com.calcana.calcana_api.repositories.CidadeRepository;
import br.com.calcana.calcana_api.repositories.FornecedorRepository;
import br.com.calcana.calcana_api.repositories.PropriedadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/propriedades")
public class PropriedadeController {

    @Autowired
    private PropriedadeService propriedadeService;

    @Autowired
    private PropriedadeRepository propriedadeRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private CidadeRepository cidadeRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public List<Propriedade> listarTodas() {
        return propriedadeRepository.findAllByAtivoTrue();
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Propriedade> cadastrar(@RequestBody Propriedade novaPropriedade) {
        Fornecedor fornecedor = fornecedorRepository.findById(novaPropriedade.getFornecedor().getIdFornecedor())
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor com o ID informado não foi encontrado!"));

        Cidade cidade = cidadeRepository.findById(novaPropriedade.getCidade().getIdCidade())
                .orElseThrow(() -> new ResourceNotFoundException("Cidade com o ID informado não foi encontrado!"));

        novaPropriedade.setFornecedor(fornecedor);
        novaPropriedade.setCidade(cidade);

        Propriedade propriedadeSalva = propriedadeRepository.save(novaPropriedade);
        return ResponseEntity.status(201).body(propriedadeSalva);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public ResponseEntity<Propriedade> buscarPorId(@PathVariable Long id) {
        return propriedadeRepository.findById(id)
                .map(propriedadeEncontrado -> ResponseEntity.ok(propriedadeEncontrado))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Propriedade> atualizar(@PathVariable Long id, @RequestBody Propriedade dadosParaAtualizar) {
        return propriedadeRepository.findById(id)
                .map(propriedadeExistente -> {

                    Fornecedor fornecedor = fornecedorRepository.findById(dadosParaAtualizar.getFornecedor().getIdFornecedor())
                            .orElseThrow(() -> new ResourceNotFoundException("Fornecedor com o ID informado não foi encontrado!"));

                    Cidade cidade = cidadeRepository.findById(dadosParaAtualizar.getCidade().getIdCidade())
                            .orElseThrow(() -> new ResourceNotFoundException("Cidade com o ID informado não foi encontrado!"));

                    propriedadeExistente.setNome(dadosParaAtualizar.getNome());
                    propriedadeExistente.setFornecedor(fornecedor);
                    propriedadeExistente.setCidade(cidade);

                    Propriedade propriedadeAtualizada = propriedadeRepository.save(propriedadeExistente);

                    return ResponseEntity.ok(propriedadeAtualizada);
                })
                .orElse(ResponseEntity.notFound().build());
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
