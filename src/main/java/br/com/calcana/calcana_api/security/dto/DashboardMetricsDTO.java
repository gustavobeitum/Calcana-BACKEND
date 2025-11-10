package br.com.calcana.calcana_api.security.dto;

import java.time.LocalDate;

public record DashboardMetricsDTO(
        long totalAnalises,
        long analisesEsteAno,
        long fornecedoresAtivos,
        long propriedadesAtivas,
        Double mediaATR,
        LocalDate ultimaAnalise
) {}