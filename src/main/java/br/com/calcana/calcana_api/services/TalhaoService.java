package br.com.calcana.calcana_api.services;

import br.com.calcana.calcana_api.exceptions.ResourceNotFoundException;
import br.com.calcana.calcana_api.model.Talhao;
import br.com.calcana.calcana_api.model.Zona;
import br.com.calcana.calcana_api.repositories.TalhaoRepository;
import br.com.calcana.calcana_api.repositories.ZonaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TalhaoService {

    @Autowired
    private TalhaoRepository talhaoRepository;

    @Autowired
    private ZonaRepository zonaRepository;

    @Transactional
    public Talhao atualizarParcialmente(Long id, Talhao dadosParaAtualizar) {
        Talhao talhaoExistente = talhaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Talhão com ID " + id + " não encontrado!"));

        if (dadosParaAtualizar.getAtivo() != null && dadosParaAtualizar.getAtivo() == true) {
            Zona zonaPai = talhaoExistente.getZona();
            if (zonaPai.getAtivo() == false) {
                throw new RuntimeException("Não é possível ativar o Talhão pois a Zona pai está inativa.");
            }
        }

        if (dadosParaAtualizar.getIdentificador() != null) {
            talhaoExistente.setIdentificador(dadosParaAtualizar.getIdentificador());
        }

        if (dadosParaAtualizar.getZona() != null && dadosParaAtualizar.getZona().getIdZona() != null) {
            if (!dadosParaAtualizar.getZona().getIdZona().equals(talhaoExistente.getZona().getIdZona())) {
                Zona novaZona = zonaRepository.findById(dadosParaAtualizar.getZona().getIdZona())
                        .orElseThrow(() -> new ResourceNotFoundException("Zona com o ID informado não foi encontrada!"));
                if (novaZona.getAtivo() == false) {
                    throw new RuntimeException("Não é possível mover o Talhão para uma Zona inativa.");
                }
                talhaoExistente.setZona(novaZona);
            }
        }

        if (dadosParaAtualizar.getAtivo() != null) {
            talhaoExistente.setAtivo(dadosParaAtualizar.getAtivo());
        }

        return talhaoRepository.save(talhaoExistente);
    }
}