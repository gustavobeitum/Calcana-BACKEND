package br.com.calcana.calcana_api.security.dto;

public record EnviarRelatorioDTO(
        String emailDestino,
        String assunto,
        String mensagem
) {
}