package br.com.calcana.calcana_api.services;

import br.com.calcana.calcana_api.security.dto.AnaliseMensalDTO;
import br.com.calcana.calcana_api.security.dto.AtividadeRecenteDTO;
import br.com.calcana.calcana_api.security.dto.DashboardMetricsDTO;
import br.com.calcana.calcana_api.model.Analises;
import br.com.calcana.calcana_api.repositories.AnaliseRepository;
import br.com.calcana.calcana_api.repositories.FornecedorRepository;
import br.com.calcana.calcana_api.repositories.PropriedadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private AnaliseRepository analiseRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private PropriedadeRepository propriedadeRepository;

    private static final Locale PT_BR = new Locale("pt", "BR");
    private static final String[] MESES_ABREVIADOS = {
            "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
            "Jul", "Ago", "Set", "Out", "Nov", "Dez"
    };

    public DashboardMetricsDTO getMetrics() {
        long totalAnalises = analiseRepository.count();
        long analisesEsteAno = analiseRepository.findContagemAnalisesEsteAno();
        long fornecedoresAtivos = fornecedorRepository.countByAtivoTrue();
        long propriedadesAtivas = propriedadeRepository.countByAtivoTrue();
        Double mediaATR = analiseRepository.findMediaATR();
        LocalDate ultimaAnalise = analiseRepository.findUltimaDataAnalise().orElse(null);

        return new DashboardMetricsDTO(
                totalAnalises,
                analisesEsteAno,
                fornecedoresAtivos,
                propriedadesAtivas,
                mediaATR,
                ultimaAnalise
        );
    }

    public List<AnaliseMensalDTO> getAnalisesMensais() {
        List<AnaliseMensalDTO> dadosDoRepo = analiseRepository.findAnalisesMensaisDoAno();

        return dadosDoRepo.stream()
                .map(dto -> new AnaliseMensalDTO(
                        MESES_ABREVIADOS[Integer.parseInt(dto.mes()) - 1],
                        dto.analises(),
                        dto.atr() != null ? dto.atr().doubleValue() : null
                ))
                .collect(Collectors.toList());
    }

    public List<AtividadeRecenteDTO> getAtividadesRecentes() {
        List<Analises> analisesRecentes = analiseRepository.findTop5ByOrderByDataAnaliseDesc();

        return analisesRecentes.stream()
                .map(analise -> new AtividadeRecenteDTO(
                        analise.getIdAnalise().toString(),
                        "analise",
                        "Análise " + analise.getTalhao() + " - " + analise.getPropriedade().getNome(),
                        analise.getDataAnalise(),
                        analise.getAtr() != null ? analise.getAtr().doubleValue() : null,
                        analise.getUsuarioLancamento().getNome()
                ))
                .collect(Collectors.toList());
    }
}