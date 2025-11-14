package br.com.calcana.calcana_api.services;

import br.com.calcana.calcana_api.exceptions.ResourceNotFoundException;
import br.com.calcana.calcana_api.model.Propriedade;
import br.com.calcana.calcana_api.model.Cidade;
import br.com.calcana.calcana_api.model.Fornecedor;
import br.com.calcana.calcana_api.repositories.*;
import br.com.calcana.calcana_api.repositories.specifications.PropriedadeSpecification;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class PropriedadeService {

    @Autowired
    private PropriedadeRepository propriedadeRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private CidadeRepository cidadeRepository;

    public Propriedade buscarPorId(Long id) {
        return propriedadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade com ID " + id + " não encontrada!"));
    }

    public Page<Propriedade> listarTodos(String status, String searchTerm, Pageable pageable) {

        Specification<Propriedade> spec = null;

        if ("inativos".equalsIgnoreCase(status)) {
            spec = PropriedadeSpecification.comStatus(false);
        } else if ("ativos".equalsIgnoreCase(status)) {
            spec = PropriedadeSpecification.comStatus(true);
        }
        if (searchTerm != null && !searchTerm.isEmpty()) {
            Specification<Propriedade> searchTermSpec = PropriedadeSpecification.comTermoDeBusca(searchTerm);

            if (spec == null) {
                spec = searchTermSpec;
            } else {
                spec = spec.and(searchTermSpec);
            }
        }

        return propriedadeRepository.findAll(spec, pageable);
    }

    public List<Propriedade> buscarPorFornecedor(Long fornecedorId) {
        if (!fornecedorRepository.existsById(fornecedorId)) {
            throw new ResourceNotFoundException("Fornecedor com ID " + fornecedorId + " não encontrado!");
        }
        return propriedadeRepository.findByFornecedorIdFornecedorAndAtivoTrue(fornecedorId);
    }

    @Transactional
    public Propriedade cadastrar(Propriedade novaPropriedade) {
        Fornecedor fornecedor = fornecedorRepository.findById(novaPropriedade.getFornecedor().getIdFornecedor())
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor com o ID " + novaPropriedade.getFornecedor().getIdFornecedor() + " não foi encontrado!"));

        if (!fornecedor.getAtivo()) {
            throw new RuntimeException("Não é possível cadastrar: O Fornecedor '" + fornecedor.getNome() + "' está inativo.");
        }

        Cidade cidade = cidadeRepository.findById(novaPropriedade.getCidade().getIdCidade())
                .orElseThrow(() -> new ResourceNotFoundException("Cidade com o ID " + novaPropriedade.getCidade().getIdCidade() + " não foi encontrada!"));

        novaPropriedade.setFornecedor(fornecedor);
        novaPropriedade.setCidade(cidade);
        novaPropriedade.setAtivo(true);

        return propriedadeRepository.save(novaPropriedade);
    }

    @Transactional
    public Propriedade atualizar(Long id, Propriedade dadosParaAtualizar) {
        Propriedade propriedadeExistente = buscarPorId(id);

        Fornecedor fornecedor = fornecedorRepository.findById(dadosParaAtualizar.getFornecedor().getIdFornecedor())
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor com o ID " + dadosParaAtualizar.getFornecedor().getIdFornecedor() + " não foi encontrado!"));

        if (!fornecedor.getAtivo()) {
            throw new RuntimeException("Não é possível atualizar: O Fornecedor '" + fornecedor.getNome() + "' está inativo.");
        }

        Cidade cidade = cidadeRepository.findById(dadosParaAtualizar.getCidade().getIdCidade())
                .orElseThrow(() -> new ResourceNotFoundException("Cidade com o ID " + dadosParaAtualizar.getCidade().getIdCidade() + " não foi encontrada!"));

        propriedadeExistente.setNome(dadosParaAtualizar.getNome());
        propriedadeExistente.setFornecedor(fornecedor);
        propriedadeExistente.setCidade(cidade);

        return propriedadeRepository.save(propriedadeExistente);
    }

    @Transactional
    public void desativarPropriedade(Long id) {
        Propriedade propriedade = buscarPorId(id);
        propriedade.setAtivo(false);
        propriedadeRepository.save(propriedade);
    }

    @Transactional
    public Propriedade atualizarParcialmente(Long id, Propriedade dadosParaAtualizar) {
        Propriedade propriedadeExistente = this.buscarPorId(id);

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