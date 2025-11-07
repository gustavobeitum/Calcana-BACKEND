package br.com.calcana.calcana_api.services;

import br.com.calcana.calcana_api.exceptions.ResourceNotFoundException;
import br.com.calcana.calcana_api.model.Fornecedor;
import br.com.calcana.calcana_api.model.Propriedade;
import br.com.calcana.calcana_api.repositories.FornecedorRepository;
import br.com.calcana.calcana_api.repositories.PropriedadeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FornecedorService {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private PropriedadeRepository propriedadeRepository;

    @Autowired
    private PropriedadeService propriedadeService;

    @Transactional
    public void desativarFornecedorEmCascata(Long id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor com ID " + id + " não encontrado!"));

        List<Propriedade> propriedadesParaDesativar = propriedadeRepository.findByFornecedorIdFornecedorAndAtivoTrue(id);

        for (Propriedade propriedade : propriedadesParaDesativar) {
            propriedadeService.desativarPropriedade(propriedade.getIdPropriedade());
        }

        fornecedor.setAtivo(false);
        fornecedorRepository.save(fornecedor);
    }
}