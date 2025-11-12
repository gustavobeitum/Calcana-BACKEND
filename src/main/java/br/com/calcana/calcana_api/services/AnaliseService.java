package br.com.calcana.calcana_api.services;

import br.com.calcana.calcana_api.exceptions.ResourceNotFoundException;
import br.com.calcana.calcana_api.model.*;
import br.com.calcana.calcana_api.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import br.com.calcana.calcana_api.repositories.specifications.AnaliseSpecification;
import java.time.LocalDate;
import java.util.List;

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
        analiseExistente.setObservacoes(dadosParaAtualizar.getObservacoes());

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

        if (dadosParaAtualizar.getObservacoes() != null) {
            analiseExistente.setObservacoes(dadosParaAtualizar.getObservacoes());
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

    public List<Analises> listarTodasFiltradas(Long fornecedorId, Long propriedadeId, String talhao, LocalDate dataInicio, LocalDate dataFim) {
        Specification<Analises> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        if (fornecedorId != null) {
            spec = spec.and(AnaliseSpecification.comFornecedorId(fornecedorId));
        }
        if (propriedadeId != null) {
            spec = spec.and(AnaliseSpecification.comPropriedadeId(propriedadeId));
        }
        if (talhao != null && !talhao.isEmpty()) {
            spec = spec.and(AnaliseSpecification.comTalhao(talhao));
        }
        if (dataInicio != null) {
            spec = spec.and(AnaliseSpecification.comDataInicio(dataInicio));
        }
        if (dataFim != null) {
            spec = spec.and(AnaliseSpecification.comDataFim(dataFim));
        }

        return analiseRepository.findAll(spec);
    }

    @Transactional
    public void deletar(Long id) {
        Analises analiseParaDeletar = buscarPorId(id);

        // Poderíamos verificar aqui se a análise já foi enviada por e-mail e impedir a exclusão, por exemplo.
        // if (analiseParaDeletar.getStatusEnvioEmail()) {
        //    throw new RuntimeException("Não é possível excluir uma análise que já foi enviada.");
        // }

        analiseRepository.delete(analiseParaDeletar);
    }

    private void calcularValoresDerivados(Analises analise) {
        BigDecimal brix = analise.getBrix();
        BigDecimal leituraSac = analise.getLeituraSacarimetrica();
        BigDecimal pbu = analise.getPbu();

        if (brix == null || leituraSac == null || pbu == null) {
            analise.setPolCaldo(null);
            analise.setPureza(null);
            analise.setArCana(null);
            analise.setArCaldo(null);
            analise.setFibra(null);
            analise.setPolCana(null);
            analise.setArCana(null);
            analise.setAtr(null);
            analise.setLeituraSacarimetricaCorrigida(null);
            return;
        }

        int precisaoIntermediaria = 10;
        RoundingMode arredondamento = RoundingMode.HALF_UP;

        // S = (1,00621 * L.Sac + 0,05117) * (0,2605 - 0,0009882 * B)
        BigDecimal parte1 = (new BigDecimal("1.00621")).multiply(leituraSac)
                .add(new BigDecimal("0.05117"));
        BigDecimal parte2 = (new BigDecimal("0.2605"))
                .subtract((new BigDecimal("0.0009882")).multiply(brix));

        BigDecimal s_polCaldo = parte1.multiply(parte2);

        // Salvar S (Pol do Caldo) - Precisão 2
        analise.setPolCaldo(s_polCaldo.setScale(2, arredondamento));


        // Q = 100 * S / B
        BigDecimal q_pureza = (new BigDecimal("100")).multiply(s_polCaldo)
                .divide(brix, precisaoIntermediaria, arredondamento);

        // Salvar Q (Pureza) - Precisão 2
        analise.setPureza(q_pureza.setScale(2, arredondamento));


        // AR = 3,641 - 0,0343 * Q (Açúcares Redutores do Caldo)
        BigDecimal ar_acucaresRedutoresCaldo = (new BigDecimal("3.641"))
                .subtract((new BigDecimal("0.0343")).multiply(q_pureza));

        // Salvar AR (Ar Caldo) - Precisão 2
        analise.setArCaldo(ar_acucaresRedutoresCaldo.setScale(2, arredondamento));


        // F = 0,08 * PBU + 0,876
        BigDecimal f_fibra = (new BigDecimal("0.08")).multiply(pbu)
                .add(new BigDecimal("0.876"));

        // Salvar F (Fibra) - Precisão 2
        analise.setFibra(f_fibra.setScale(2, arredondamento));


        // C = 1,0313 - 0,00575 * F
        BigDecimal c_coeficiente = (new BigDecimal("1.0313"))
                .subtract((new BigDecimal("0.00575")).multiply(f_fibra));

        // Precisão 4
        c_coeficiente = c_coeficiente.setScale(4, arredondamento);


        // PC = S * (1 - 0,01 * F) * C
        BigDecimal pc_polCana = s_polCaldo.multiply(
                (BigDecimal.ONE.subtract((new BigDecimal("0.01")).multiply(f_fibra)))
        ).multiply(c_coeficiente);

        // Salvar PC (Pol da Cana) - Precisão 2
        analise.setPolCana(pc_polCana.setScale(2, arredondamento));


        // ARC = AR * (1 - 0,01 * F) * C
        BigDecimal arc_arCana = ar_acucaresRedutoresCaldo.multiply(
                (BigDecimal.ONE.subtract((new BigDecimal("0.01")).multiply(f_fibra)))
        ).multiply(c_coeficiente);

        // Salvar ARC (AR da Cana) - Precisão 2
        analise.setArCana(arc_arCana.setScale(2, arredondamento));


        // ATR = (9,6316 * PC) + (9,15 * ARC)
        BigDecimal atr_final = (new BigDecimal("9.6316")).multiply(pc_polCana)
                .add((new BigDecimal("9.15")).multiply(arc_arCana));

        // Salvar ATR - Precisão 2
        analise.setAtr(atr_final.setScale(2, arredondamento));


        //Leitura Sacarimétrica Corrigida a fórmula para este campo não foi encontrada.
        // Estamos a definir como null para evitar salvar o dado placeholder.
        analise.setLeituraSacarimetricaCorrigida(null);
    }
}