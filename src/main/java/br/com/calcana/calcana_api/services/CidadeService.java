package br.com.calcana.calcana_api.services;

import br.com.calcana.calcana_api.exceptions.ResourceNotFoundException;
import br.com.calcana.calcana_api.model.Cidade;
import br.com.calcana.calcana_api.repositories.CidadeRepository;
import br.com.calcana.calcana_api.repositories.PropriedadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CidadeService {

    @Autowired
    private CidadeRepository cidadeRepository;

    @Autowired
    private PropriedadeRepository propriedadeRepository;

    public List<Cidade> listarTodas() {
        return cidadeRepository.findAll();
    }

    public Cidade buscarPorId(Long id) {
        return cidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cidade com ID " + id + " não encontrada!"));
    }

    @Transactional
    public Cidade cadastrar(Cidade novaCidade) {
        return cidadeRepository.save(novaCidade);
    }

    @Transactional
    public Cidade atualizar(Long id, Cidade dadosParaAtualizar) {
        Cidade cidadeExistente = buscarPorId(id);

        cidadeExistente.setNome(dadosParaAtualizar.getNome());
        cidadeExistente.setUf(dadosParaAtualizar.getUf());

        return cidadeRepository.save(cidadeExistente);
    }

    @Transactional
    public Cidade atualizarParcialmente(Long id, Cidade dadosParaAtualizar) {
        Cidade cidadeExistente = buscarPorId(id);

        if (dadosParaAtualizar.getNome() != null) {
            cidadeExistente.setNome(dadosParaAtualizar.getNome());
        }
        if (dadosParaAtualizar.getUf() != null) {
            cidadeExistente.setUf(dadosParaAtualizar.getUf());
        }

        return cidadeRepository.save(cidadeExistente);
    }

    @Transactional
    public void deletar(Long id) {
        if (!cidadeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cidade com ID " + id + " não encontrada!");
        }

        boolean cidadeEmUso = propriedadeRepository.existsByCidadeIdCidade(id);
        if (cidadeEmUso) {
            throw new ResourceNotFoundException("Não é possível excluir: A cidade está sendo usada por uma ou mais propriedades.");
        }

        cidadeRepository.deleteById(id);
    }
}