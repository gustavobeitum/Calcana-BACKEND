package br.com.calcana.calcana_api.services;

import br.com.calcana.calcana_api.exceptions.ResourceNotFoundException;
import br.com.calcana.calcana_api.model.Fornecedor;
import br.com.calcana.calcana_api.model.Propriedade;
import br.com.calcana.calcana_api.repositories.FornecedorRepository;
import br.com.calcana.calcana_api.repositories.PropriedadeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import br.com.calcana.calcana_api.repositories.specifications.FornecedorSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

@Service
public class FornecedorService {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private PropriedadeRepository propriedadeRepository;

    @Autowired
    private PropriedadeService propriedadeService;

    public Page<Fornecedor> listarTodos(String status, String searchTerm, Pageable pageable) {

        Specification<Fornecedor> spec = null;

        if ("inativos".equalsIgnoreCase(status)) {
            spec = FornecedorSpecification.comStatus(false);
        } else if ("ativos".equalsIgnoreCase(status)) {
            spec = FornecedorSpecification.comStatus(true);
        }

        if (searchTerm != null && !searchTerm.isEmpty()) {
            Specification<Fornecedor> searchTermSpec = FornecedorSpecification.comTermoDeBusca(searchTerm);

            if (spec == null) {
                spec = searchTermSpec;
            } else {
                spec = spec.and(searchTermSpec);
            }
        }

        return fornecedorRepository.findAll(spec, pageable);
    }

    public Fornecedor buscarPorId(Long id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor com ID " + id + " não encontrado!"));
    }

    @Transactional
    public Fornecedor cadastrar(Fornecedor novoFornecedor) {
        validarNomeFornecedorUnico(novoFornecedor.getNome(), null);
        validarEmailFornecedorUnico(novoFornecedor.getEmail(), null);

        novoFornecedor.setAtivo(true);
        return fornecedorRepository.save(novoFornecedor);
    }

    @Transactional
    public Fornecedor atualizar(Long id, Fornecedor dadosParaAtualizar) {
        Fornecedor fornecedorExistente = buscarPorId(id);

        validarNomeFornecedorUnico(dadosParaAtualizar.getNome(), id);
        validarEmailFornecedorUnico(dadosParaAtualizar.getEmail(), id);

        fornecedorExistente.setNome(dadosParaAtualizar.getNome());
        fornecedorExistente.setEmail(dadosParaAtualizar.getEmail());

        return fornecedorRepository.save(fornecedorExistente);
    }

    @Transactional
    public Fornecedor atualizarParcialmente(Long id, Fornecedor dadosParaAtualizar) {
        Fornecedor fornecedorExistente = buscarPorId(id);

        if (dadosParaAtualizar.getNome() != null) {
            validarNomeFornecedorUnico(dadosParaAtualizar.getNome(), id);

            fornecedorExistente.setNome(dadosParaAtualizar.getNome());
        }
        if (dadosParaAtualizar.getEmail() != null) {
            validarEmailFornecedorUnico(dadosParaAtualizar.getEmail(), id);

            fornecedorExistente.setEmail(dadosParaAtualizar.getEmail());
        }
        if (dadosParaAtualizar.getAtivo() != null) {
            if (!dadosParaAtualizar.getAtivo()) {
                desativarFornecedorEmCascata(id);
            } else {
                fornecedorExistente.setAtivo(true);
            }
        }

        return fornecedorRepository.save(fornecedorExistente);
    }


    @Transactional
    public void desativarFornecedorEmCascata(Long id) {
        Fornecedor fornecedor = buscarPorId(id);

        List<Propriedade> propriedadesParaDesativar = propriedadeRepository.findByFornecedorIdFornecedorAndAtivoTrue(id);

        for (Propriedade propriedade : propriedadesParaDesativar) {
            propriedadeService.desativarPropriedade(propriedade.getIdPropriedade());
        }

        fornecedor.setAtivo(false);
        fornecedorRepository.save(fornecedor);
    }

    private void validarEmailFornecedorUnico(String email, Long id) {
        Optional<Fornecedor> fornecedorExistente;
        if (id == null) {
            fornecedorExistente = fornecedorRepository.findByEmail(email);
        } else {
            fornecedorExistente = fornecedorRepository.findByEmailAndIdFornecedorNot(email, id);
        }
        if (fornecedorExistente.isPresent()) {
            throw new RuntimeException("O e-mail '" + email + "' já está em uso por outro fornecedor.");
        }
    }

    private void validarNomeFornecedorUnico(String nome, Long id) {
        Optional<Fornecedor> fornecedorExistente;
        if (id == null) {
            fornecedorExistente = fornecedorRepository.findByNome(nome);
        } else {
            fornecedorExistente = fornecedorRepository.findByNomeAndIdFornecedorNot(nome, id);
        }
        if (fornecedorExistente.isPresent()) {
            throw new RuntimeException("O nome '" + nome + "' já está em uso por outro fornecedor.");
        }
    }
}