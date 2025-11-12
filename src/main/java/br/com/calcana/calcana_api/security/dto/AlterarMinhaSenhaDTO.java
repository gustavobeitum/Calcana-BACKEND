package br.com.calcana.calcana_api.security.dto;

public record AlterarMinhaSenhaDTO(
        String senhaAtual,
        String novaSenha
) {
}
