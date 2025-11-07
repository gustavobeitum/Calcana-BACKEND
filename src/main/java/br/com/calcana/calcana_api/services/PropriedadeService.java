package br.com.calcana.calcana_api.services;

import br.com.calcana.calcana_api.exceptions.ResourceNotFoundException;
import br.com.calcana.calcana_api.model.Propriedade;
import br.com.calcana.calcana_api.model.Cidade;
import br.com.calcana.calcana_api.model.Fornecedor;
import br.com.calcana.calcana_api.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropriedadeService {

    @Autowired
    private PropriedadeRepository propriedadeRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private CidadeRepository cidadeRepository;

    @Transactional
    public void desativarPropriedade(Long id) {
        Propriedade propriedade = propriedadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade com ID " + id + " não encontrada!"));

        propriedade.setAtivo(false);
        propriedadeRepository.save(propriedade);
    }

    @Transactional
    public Propriedade atualizarParcialmente(Long id, Propriedade dadosParaAtualizar) {
        Propriedade propriedadeExistente = propriedadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade com ID " + id + " não encontrada!"));

        if (dadosParaAtualizar.getAtivo() != null && dadosParaAtualizar.getAtivo() == true) {
            Fornecedor fornecedorPai = propriedadeExistente.getFornecedor();
            if (fornecedorPai.getAtivo() == false) {
                throw new RuntimeException("Não é possível ativar a Propriedade pois o Fornecedor pai está inativo.");
            }
        }

        if (dadosParaAtualizar.getNome() != null) {
            propriedadeExistente.setNome(dadosParaAtualizar.getNome());
        }

        if (dadosParaAtualizar.getFornecedor() != null && dadosParaAtualizar.getFornecedor().getIdFornecedor() != null) {
            if (!dadosParaAtualizar.getFornecedor().getIdFornecedor().equals(propriedadeExistente.getFornecedor().getIdFornecedor())) {
                Fornecedor novoFornecedor = fornecedorRepository.findById(dadosParaAtualizar.getFornecedor().getIdFornecedor())
                        .orElseThrow(() -> new ResourceNotFoundException("Fornecedor com o ID informado não foi encontrado!"));
                if (novoFornecedor.getAtivo() == false) {
                    throw new RuntimeException("Não é possível mover a Propriedade para um Fornecedor inativo.");
                }
                propriedadeExistente.setFornecedor(novoFornecedor);
            }
        }

        if (dadosParaAtualizar.getCidade() != null && dadosParaAtualizar.getCidade().getIdCidade() != null) {
            if (!dadosParaAtualizar.getCidade().getIdCidade().equals(propriedadeExistente.getCidade().getIdCidade())) {
                Cidade novaCidade = cidadeRepository.findById(dadosParaAtualizar.getCidade().getIdCidade())
                        .orElseThrow(() -> new ResourceNotFoundException("Cidade com o ID informado não foi encontrada!"));
                propriedadeExistente.setCidade(novaCidade);
            }
        }

        if (dadosParaAtualizar.getAtivo() != null) {
            propriedadeExistente.setAtivo(dadosParaAtualizar.getAtivo());
        }

        return propriedadeRepository.save(propriedadeExistente);
    }
}