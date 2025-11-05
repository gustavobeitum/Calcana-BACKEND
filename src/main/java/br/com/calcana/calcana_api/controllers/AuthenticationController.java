package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.model.Usuario;
import br.com.calcana.calcana_api.security.TokenService;
import br.com.calcana.calcana_api.security.dto.LoginRequestDTO;
import br.com.calcana.calcana_api.security.dto.LoginResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());

        Authentication auth = authenticationManager.authenticate(usernamePassword);

        Usuario usuarioAutenticado = (Usuario) auth.getPrincipal();

        String token = tokenService.gerarToken(usuarioAutenticado);

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}