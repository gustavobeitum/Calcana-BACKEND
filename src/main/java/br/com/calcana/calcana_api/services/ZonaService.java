package br.com.calcana.calcana_api.services;

import br.com.calcana.calcana_api.exceptions.ResourceNotFoundException;
import br.com.calcana.calcana_api.model.Propriedade;
import br.com.calcana.calcana_api.model.Talhao;
import br.com.calcana.calcana_api.model.Zona;
import br.com.calcana.calcana_api.repositories.PropriedadeRepository;
import br.com.calcana.calcana_api.repositories.TalhaoRepository;
import br.com.calcana.calcana_api.repositories.ZonaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZonaService {

    @Autowired
    private ZonaRepository zonaRepository;

    @Autowired
    private TalhaoRepository talhaoRepository;

    @Autowired
    private PropriedadeRepository propriedadeRepository;

    @Transactional
    public void desativarZonaEmCascata(Long id) {
        Zona zona = zonaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona não encontrada!"));

        List<Talhao> talhoesParaDesativar = talhaoRepository.findByZonaIdZonaAndAtivoTrue(id);
        for (Talhao talhao : talhoesParaDesativar) {
            talhao.setAtivo(false);
            talhaoRepository.save(talhao);
        }

        zona.setAtivo(false);
        zonaRepository.save(zona);
    }

    @Transactional
    public Zona atualizarParcialmente(Long id, Zona dadosParaAtualizar) {
        Zona zonaExistente = zonaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zona com ID " + id + " não encontrada!"));

        if (dadosParaAtualizar.getAtivo() != null && dadosParaAtualizar.getAtivo() == true) {
            Propriedade propriedadePai = zonaExistente.getPropriedade();
            if (propriedadePai.getAtivo() == false) {
                throw new RuntimeException("Não é possível ativar a Zona pois a Propriedade pai está inativa.");
            }
        }

        if (dadosParaAtualizar.getNome() != null) {
            zonaExistente.setNome(dadosParaAtualizar.getNome());
        }

        if (dadosParaAtualizar.getPropriedade() != null && dadosParaAtualizar.getPropriedade().getIdPropriedade() != null) {
            if (!dadosParaAtualizar.getPropriedade().getIdPropriedade().equals(zonaExistente.getPropriedade().getIdPropriedade())) {
                Propriedade novaPropriedade = propriedadeRepository.findById(dadosParaAtualizar.getPropriedade().getIdPropriedade())
                        .orElseThrow(() -> new ResourceNotFoundException("Propriedade com o ID informado não foi encontrada!"));
                if (novaPropriedade.getAtivo() == false) {
                    throw new RuntimeException("Não é possível mover a Zona para uma Propriedade inativa.");
                }
                zonaExistente.setPropriedade(novaPropriedade);
            }
        }

        if (dadosParaAtualizar.getAtivo() != null) {
            zonaExistente.setAtivo(dadosParaAtualizar.getAtivo());
        }

        return zonaRepository.save(zonaExistente);
    }
}