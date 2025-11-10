package br.com.calcana.calcana_api.security.dto;

public record AnaliseMensalDTO(
        String mes,
        Long analises,
        Double atr
) {
    public AnaliseMensalDTO(Integer mesNumero, Long analises, Double atr) {
        this(mesNumero.toString(), analises, atr);
    }
}