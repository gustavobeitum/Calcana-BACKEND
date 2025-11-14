package br.com.calcana.calcana_api.config;

import br.com.calcana.calcana_api.model.Perfil;
import br.com.calcana.calcana_api.model.Usuario;
import br.com.calcana.calcana_api.repositories.PerfilRepository;
import br.com.calcana.calcana_api.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        Perfil gestorPerfil = criarPerfilSeNaoExistir("GESTOR");
        Perfil operadorPerfil = criarPerfilSeNaoExistir("OPERADOR");

        criarUsuarioSeNaoExistir(
                "Admin Gestor",
                "admin@calcana.com",
                "admin123",
                gestorPerfil
        );

        criarUsuarioSeNaoExistir(
                "Admin Gestor 2",
                "admin2@calcana.com",
                "admin123",
                gestorPerfil
        );

        criarUsuarioSeNaoExistir(
                "Usuario Operador",
                "operador@calcana.com",
                "oper123",
                operadorPerfil
        );
    }

   private Perfil criarPerfilSeNaoExistir(String descricao) {
        Optional<Perfil> perfilOpt = perfilRepository.findAll().stream()
                .filter(p -> p.getDescricao().equalsIgnoreCase(descricao))
                .findFirst();

        if (perfilOpt.isPresent()) {
            return perfilOpt.get();
        } else {
            Perfil novoPerfil = new Perfil();
            novoPerfil.setDescricao(descricao.toUpperCase());
            return perfilRepository.save(novoPerfil);
        }
    }

    private void criarUsuarioSeNaoExistir(String nome, String email, String senha, Perfil perfil) {
        if (usuarioRepository.findByEmail(email).isEmpty()) {
            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(nome);
            novoUsuario.setEmail(email);
            novoUsuario.setSenha(passwordEncoder.encode(senha));
            novoUsuario.setAtivo(true);
            novoUsuario.setPerfil(perfil);

            usuarioRepository.save(novoUsuario);
            System.out.println(">>> Usuário " + perfil.getDescricao() + " (" + email + ") criado com sucesso!");
        }
    }
}