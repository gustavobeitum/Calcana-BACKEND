package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.exceptions.ResourceNotFoundException;
import br.com.calcana.calcana_api.model.*;
import br.com.calcana.calcana_api.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analises")
public class AnaliseController {

    @Autowired
    private AnaliseRepository analiseRepository;

    @Autowired
    private TalhaoRepository talhaoRepository;

    @Autowired
    private VariedadeRepository variedadeRepository;

    @Autowired
    private CorteRepository corteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Analises> listarTodas() {
        return analiseRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Analises> cadastrar(@RequestBody Analises novaAnalise) {
        Talhao talhao = talhaoRepository.findById(novaAnalise.getTalhao().getIdTalhao())
                .filter(Talhao::getAtivo)
                .orElseThrow(() -> new ResourceNotFoundException("Talhão ativo com o ID informado não foi encontrado!"));

        Variedade variedade = variedadeRepository.findById(novaAnalise.getVariedade().getIdVariedade())
                .orElseThrow(() -> new ResourceNotFoundException("Variedade com o ID informado não foi encontrada!"));

        Corte corte = corteRepository.findById(novaAnalise.getCorte().getIdCorte())
                .orElseThrow(() -> new ResourceNotFoundException("Corte com o ID informado não foi encontrado!"));

        Usuario usuario = usuarioRepository.findById(novaAnalise.getUsuarioLancamento().getIdUsuario())
                .filter(Usuario::getAtivo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário ativo com o ID informado não foi encontrado!"));

        novaAnalise.setTalhao(talhao);
        novaAnalise.setVariedade(variedade);
        novaAnalise.setCorte(corte);
        novaAnalise.setUsuarioLancamento(usuario);
        // -----------------------------------------

        // --- Lógica de Cálculo (SERÁ ADICIONADA DEPOIS) ---
        // Aqui chamaremos um serviço para calcular os outros campos (ATR, POL, etc.)
        // Por agora, eles serão salvos como nulos ou com os valores que vierem no JSON.
        // ----------------------------------------------------

        Analises analiseSalva = analiseRepository.save(novaAnalise);
        return ResponseEntity.status(201).body(analiseSalva);
    }

    // Outros endpoints (GET/{id}, PUT, PATCH, DELETE) serão adicionados depois
}