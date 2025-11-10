package br.com.calcana.calcana_api.security.dto;

import java.time.LocalDate;

public record AtividadeRecenteDTO(
        String id,
        String tipo,
        String descricao,
        LocalDate data,
        Double atr,
        String usuario
) {}
