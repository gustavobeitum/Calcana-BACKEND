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

        if (usuarioRepository.findByEmail("admin@calcana.com").isEmpty()) {

            Usuario admin = new Usuario();
            admin.setNome("Admin Gestor");
            admin.setEmail("admin@calcana.com");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setAtivo(true);
            admin.setPerfil(gestorPerfil);

            usuarioRepository.save(admin);

            System.out.println(">>> Usuário GESTOR padrão criado com sucesso!");
        }
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
}