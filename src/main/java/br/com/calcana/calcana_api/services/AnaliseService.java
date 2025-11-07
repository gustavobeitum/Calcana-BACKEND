package br.com.calcana.calcana_api.services;

import br.com.calcana.calcana_api.exceptions.ResourceNotFoundException;
import br.com.calcana.calcana_api.model.*;
import br.com.calcana.calcana_api.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class AnaliseService {

    @Autowired private AnaliseRepository analiseRepository;
    @Autowired private PropriedadeRepository propriedadeRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @Transactional
    public Analises calcularEsalvarAnalise(Analises novaAnalise) {
        Propriedade propriedade = propriedadeRepository.findById(novaAnalise.getPropriedade().getIdPropriedade())
                .filter(Propriedade::getAtivo)
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade ativa com o ID informado não foi encontrada!"));

        Usuario usuario = usuarioRepository.findById(novaAnalise.getUsuarioLancamento().getIdUsuario())
                .filter(Usuario::getAtivo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário ativo com o ID informado não foi encontrado!"));

        novaAnalise.setPropriedade(propriedade);
        novaAnalise.setUsuarioLancamento(usuario);
        calcularValoresDerivados(novaAnalise);

        return analiseRepository.save(novaAnalise);
    }

    public Analises buscarPorId(Long id) {
        return analiseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Análise com ID " + id + " não encontrada!"));
    }

    @Transactional
    public Analises atualizarAnalise(Long id, Analises dadosParaAtualizar) {
        Analises analiseExistente = buscarPorId(id);

        Propriedade propriedade = propriedadeRepository.findById(dadosParaAtualizar.getPropriedade().getIdPropriedade())
                .filter(Propriedade::getAtivo)
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade ativa não encontrada!"));

        Usuario usuario = usuarioRepository.findById(dadosParaAtualizar.getUsuarioLancamento().getIdUsuario())
                .filter(Usuario::getAtivo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário ativo não encontrado!"));

        analiseExistente.setNumeroAmostra(dadosParaAtualizar.getNumeroAmostra());
        analiseExistente.setDataAnalise(dadosParaAtualizar.getDataAnalise());
        analiseExistente.setPropriedade(propriedade);
        analiseExistente.setUsuarioLancamento(usuario);
        analiseExistente.setZona(dadosParaAtualizar.getZona());
        analiseExistente.setTalhao(dadosParaAtualizar.getTalhao());
        analiseExistente.setCorte(dadosParaAtualizar.getCorte());
        analiseExistente.setPbu(dadosParaAtualizar.getPbu());
        analiseExistente.setBrix(dadosParaAtualizar.getBrix());
        analiseExistente.setLeituraSacarimetrica(dadosParaAtualizar.getLeituraSacarimetrica());
        analiseExistente.setStatusEnvioEmail(dadosParaAtualizar.getStatusEnvioEmail());
        analiseExistente.setDataEnvioEmail(dadosParaAtualizar.getDataEnvioEmail());

        calcularValoresDerivados(analiseExistente);

        return analiseRepository.save(analiseExistente);
    }

    @Transactional
    public Analises atualizarParcialmenteAnalise(Long id, Analises dadosParaAtualizar) {
        Analises analiseExistente = buscarPorId(id);
        boolean recalcular = false;

        if (dadosParaAtualizar.getNumeroAmostra() != null) {
            analiseExistente.setNumeroAmostra(dadosParaAtualizar.getNumeroAmostra());
        }
        if (dadosParaAtualizar.getDataAnalise() != null) {
            analiseExistente.setDataAnalise(dadosParaAtualizar.getDataAnalise());
        }

        if (dadosParaAtualizar.getZona() != null) {
            analiseExistente.setZona(dadosParaAtualizar.getZona());
        }
        if (dadosParaAtualizar.getTalhao() != null) {
            analiseExistente.setTalhao(dadosParaAtualizar.getTalhao());
        }
        if (dadosParaAtualizar.getCorte() != null) {
            analiseExistente.setCorte(dadosParaAtualizar.getCorte());
        }

        if (dadosParaAtualizar.getStatusEnvioEmail() != null) {
            analiseExistente.setStatusEnvioEmail(dadosParaAtualizar.getStatusEnvioEmail());
        }
        if (dadosParaAtualizar.getDataEnvioEmail() != null) {
            analiseExistente.setDataEnvioEmail(dadosParaAtualizar.getDataEnvioEmail());
        }

        if (dadosParaAtualizar.getPbu() != null) {
            analiseExistente.setPbu(dadosParaAtualizar.getPbu());
            recalcular = true;
        }
        if (dadosParaAtualizar.getBrix() != null) {
            analiseExistente.setBrix(dadosParaAtualizar.getBrix());
            recalcular = true;
        }
        if (dadosParaAtualizar.getLeituraSacarimetrica() != null) {
            analiseExistente.setLeituraSacarimetrica(dadosParaAtualizar.getLeituraSacarimetrica());
            recalcular = true;
        }

        if (dadosParaAtualizar.getPropriedade() != null && dadosParaAtualizar.getPropriedade().getIdPropriedade() != null) {
            Propriedade propriedade = propriedadeRepository.findById(dadosParaAtualizar.getPropriedade().getIdPropriedade())
                    .filter(Propriedade::getAtivo).orElseThrow(() -> new ResourceNotFoundException("Propriedade ativa não encontrada!"));
            analiseExistente.setPropriedade(propriedade);
        }
        if (dadosParaAtualizar.getUsuarioLancamento() != null && dadosParaAtualizar.getUsuarioLancamento().getIdUsuario() != null) {
            Usuario usuario = usuarioRepository.findById(dadosParaAtualizar.getUsuarioLancamento().getIdUsuario())
                    .filter(Usuario::getAtivo).orElseThrow(() -> new ResourceNotFoundException("Usuário ativo não encontrado!"));
            analiseExistente.setUsuarioLancamento(usuario);
        }

        if (recalcular) {
            calcularValoresDerivados(analiseExistente);
        }

        return analiseRepository.save(analiseExistente);
    }

    private void calcularValoresDerivados(Analises analise) {
        BigDecimal brix = analise.getBrix();
        BigDecimal leituraSac = analise.getLeituraSacarimetrica();
        BigDecimal pbu = analise.getPbu();

        if (brix == null || leituraSac == null || pbu == null) {
            analise.setPolCaldo(null);
            analise.setPureza(null);
            analise.setArCana(null);
            analise.setFibra(null);
            analise.setPolCana(null);
            analise.setArCana(null);
            analise.setAtr(null);
            analise.setLeituraSacarimetricaCorrigida(null);
            return;
        }

        BigDecimal s = BigDecimal.valueOf(1.00621).multiply(leituraSac)
                .add(BigDecimal.valueOf(0.05117));
        BigDecimal tempS = BigDecimal.valueOf(0.2605)
                .subtract(BigDecimal.valueOf(0.0009882).multiply(brix));
        s = s.multiply(tempS).setScale(2, RoundingMode.HALF_UP);
        analise.setPolCaldo(s);

        BigDecimal q = BigDecimal.valueOf(100).multiply(s)
                .divide(brix, 2, RoundingMode.HALF_UP);
        analise.setPureza(q);

        BigDecimal ar = BigDecimal.valueOf(3.641)
                .subtract(BigDecimal.valueOf(0.0343).multiply(q))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal f = BigDecimal.valueOf(0.08).multiply(pbu)
                .add(BigDecimal.valueOf(0.876))
                //pq arredonda para 4 e depois para 2
                .setScale(4, RoundingMode.HALF_UP);
        analise.setFibra(f.setScale(2, RoundingMode.HALF_UP));

        BigDecimal c = BigDecimal.valueOf(1.0313)
                .subtract(BigDecimal.valueOf(0.00575).multiply(f))
                .setScale(4, RoundingMode.HALF_UP);

        BigDecimal pc = BigDecimal.ONE.subtract(BigDecimal.valueOf(0.01).multiply(f));
        pc = s.multiply(pc).multiply(c).setScale(2, RoundingMode.HALF_UP);
        analise.setPolCana(pc);

        BigDecimal arc = BigDecimal.ONE.subtract(BigDecimal.valueOf(0.01).multiply(f));
        arc = ar.multiply(arc).multiply(c).setScale(2, RoundingMode.HALF_UP);
        analise.setArCana(arc);

        BigDecimal atr = BigDecimal.valueOf(9.6316).multiply(pc)
                .add(BigDecimal.valueOf(9.15).multiply(arc))
                .setScale(2, RoundingMode.HALF_UP);
        analise.setAtr(atr);

        //verificar conta de leitura sacarimetrica corrigida
        if (analise.getLeituraSacarimetrica() != null) {
            BigDecimal leituraCorrigidaPlaceholder = analise.getLeituraSacarimetrica().add(BigDecimal.ONE).setScale(2, RoundingMode.HALF_UP);
            analise.setLeituraSacarimetricaCorrigida(leituraCorrigidaPlaceholder);
        } else {
            analise.setLeituraSacarimetricaCorrigida(null);
        }
    }
}