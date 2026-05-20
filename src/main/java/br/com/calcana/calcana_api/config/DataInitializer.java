package br.com.calcana.calcana_api.config;

import br.com.calcana.calcana_api.model.*;
import br.com.calcana.calcana_api.repositories.*;
import br.com.calcana.calcana_api.services.AnaliseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired private PerfilRepository perfilRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CidadeRepository cidadeRepository;
    @Autowired private FornecedorRepository fornecedorRepository;
    @Autowired private PropriedadeRepository propriedadeRepository;
    @Autowired private AnaliseRepository analiseRepository;

    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AnaliseService analiseService;

    private final Random rand = new Random();

    @Override
    public void run(String... args) throws Exception {

        Perfil gestorPerfil = criarPerfilSeNaoExistir("GESTOR");
        Perfil operadorPerfil = criarPerfilSeNaoExistir("OPERADOR");

        criarUsuarioSeNaoExistir("Admin Gestor", "admin@calcana.com", "admin123", gestorPerfil);
        criarUsuarioSeNaoExistir("Admin Gestor 2", "admin2@calcana.com", "admin123", gestorPerfil);
        Usuario operador = criarUsuarioSeNaoExistir("Usuario Operador", "operador@calcana.com", "oper123", operadorPerfil);

        if (fornecedorRepository.count() > 0 || propriedadeRepository.count() > 0 || analiseRepository.count() > 0) {
            System.out.println(">>> Banco de dados já populado. Pulando inserção de dados de demonstração.");
            return;
        }

        System.out.println(">>> Populando banco com dados de demonstração...");

        List<Cidade> cidades = new ArrayList<>();
        cidades.add(criarCidadeSeNaoExistir("Assis", "SP"));
        cidades.add(criarCidadeSeNaoExistir("Tarumã", "SP"));
        cidades.add(criarCidadeSeNaoExistir("Cândido Mota", "SP"));
        cidades.add(criarCidadeSeNaoExistir("Paraguaçu Paulista", "SP"));
        cidades.add(criarCidadeSeNaoExistir("Maracaí", "SP"));
        cidades.add(criarCidadeSeNaoExistir("Palmital", "SP"));
        cidades.add(criarCidadeSeNaoExistir("Iepê", "SP"));
        cidades.add(criarCidadeSeNaoExistir("Rancharia", "SP"));
        cidades.add(criarCidadeSeNaoExistir("Presidente Prudente", "SP"));
        cidades.add(criarCidadeSeNaoExistir("Ourinhos", "SP"));

        List<Fornecedor> fornecedores = new ArrayList<>();
        fornecedores.add(criarFornecedorSeNaoExistir("Agroterenas S.A.", "contato@agroterenas.com.br"));
        fornecedores.add(criarFornecedorSeNaoExistir("Nova América S.A.", "contato@novaamerica.com.br"));
        fornecedores.add(criarFornecedorSeNaoExistir("João da Silva Sauro", "joao.sauro@email.com"));
        fornecedores.add(criarFornecedorSeNaoExistir("Maria Oliveira Santos", "maria.oliveira@email.com"));
        fornecedores.add(criarFornecedorSeNaoExistir("Fazenda Três Irmãos Ltda.", "f3irmaos@email.com"));
        fornecedores.add(criarFornecedorSeNaoExistir("Sítio Boa Esperança", "boa.esperanca@email.com"));
        fornecedores.add(criarFornecedorSeNaoExistir("Grupo Carvalho", "grupo.carvalho@email.com"));
        fornecedores.add(criarFornecedorSeNaoExistir("Agropecuária Santa Luzia", "santa.luzia@email.com"));
        fornecedores.add(criarFornecedorSeNaoExistir("Carlos Alberto Pereira", "carlos.pereira@email.com"));
        fornecedores.add(criarFornecedorSeNaoExistir("Usina Cocal - Unidade Paraguaçu", "cocal@cocal.com.br"));
        fornecedores.add(criarFornecedorSeNaoExistir("Fazenda São Francisco", "sao.francisco@email.com"));
        fornecedores.add(criarFornecedorSeNaoExistir("Grupo Andrade", "andrade@email.com"));
        fornecedores.add(criarFornecedorSeNaoExistir("Sítio Palmeiras", "palmeiras@email.com"));
        fornecedores.add(criarFornecedorSeNaoExistir("José Ferreira Neto", "jose.ferreira@email.com"));
        fornecedores.add(criarFornecedorSeNaoExistir("Fazenda Água Doce", "aguadoce@email.com"));
        fornecedores.add(criarFornecedorSeNaoExistir("Herdeiros de Antônio Nunes", "antonio.nunes@email.com"));
        fornecedores.add(criarFornecedorSeNaoExistir("Agro Sol Nascente", "sol.nascente@email.com"));
        fornecedores.add(criarFornecedorSeNaoExistir("Fazenda Vista Alegre", "vista.alegre@email.com"));
        fornecedores.add(criarFornecedorSeNaoExistir("Pedro Martins Aguiar", "pedro.aguiar@email.com"));
        fornecedores.add(criarFornecedorSeNaoExistir("Terra Roxa Agronegócios", "terraroxa@email.com"));

        List<Propriedade> propriedades = new ArrayList<>();
        String[] nomesPropriedades = {"Fazenda Santa Maria", "Sítio São José", "Fazenda Boa Vista", "Sítio Esperança", "Chácara Primavera", "Recanto das Águas"};

        for (int i = 0; i < fornecedores.size(); i++) {
            Fornecedor f = fornecedores.get(i);

            propriedades.add(criarPropriedadeSeNaoExistir(
                    nomesPropriedades[rand.nextInt(nomesPropriedades.length)] + " (Principal)",
                    f,
                    cidades.get(rand.nextInt(cidades.size()))
            ));

            propriedades.add(criarPropriedadeSeNaoExistir(
                    nomesPropriedades[rand.nextInt(nomesPropriedades.length)] + " (Secundária)",
                    f,
                    cidades.get(rand.nextInt(cidades.size()))
            ));
        }

        int ano = LocalDate.now().getYear();
        LocalDate dataInicio = LocalDate.of(ano, 1, 1);

        for (int i = 0; i < 35; i++) {
            Analises analise = new Analises();

            Propriedade propAleatoria = propriedades.get(rand.nextInt(propriedades.size()));

            LocalDate dataAleatoria = dataInicio.plusDays(rand.nextInt(330));

            analise.setNumeroAmostra(100 + i);
            analise.setDataAnalise(dataAleatoria);
            analise.setPropriedade(propAleatoria);
            analise.setUsuarioLancamento(operador);

            analise.setZona("Zona " + (rand.nextInt(5) + 1));
            analise.setTalhao("T-" + String.format("%02d", (rand.nextInt(20) + 1)));
            analise.setCorte(rand.nextInt(6) + 1);

            analise.setPbu(BigDecimal.valueOf(80.0 + rand.nextDouble() * 10.0));
            analise.setBrix(BigDecimal.valueOf(18.0 + rand.nextDouble() * 4.0));
            analise.setLeituraSacarimetrica(BigDecimal.valueOf(65.0 + rand.nextDouble() * 15.0));
            analise.setStatusEnvioEmail(false);

            analiseService.calcularEsalvarAnalise(analise);
        }

        System.out.println(">>> " + analiseRepository.count() + " análises de demonstração criadas com sucesso!");
    }


    private Perfil criarPerfilSeNaoExistir(String descricao) {
        Optional<Perfil> perfilOpt = perfilRepository.findByDescricaoIgnoreCase(descricao);
        if (perfilOpt.isPresent()) {
            return perfilOpt.get();
        } else {
            Perfil novoPerfil = new Perfil();
            novoPerfil.setDescricao(descricao.toUpperCase());
            return perfilRepository.save(novoPerfil);
        }
    }

    private Usuario criarUsuarioSeNaoExistir(String nome, String email, String senha, Perfil perfil) {
        Optional<Usuario> userOpt = usuarioRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            return userOpt.get();
        } else {
            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(nome);
            novoUsuario.setEmail(email);
            novoUsuario.setSenha(passwordEncoder.encode(senha));
            novoUsuario.setAtivo(true);
            novoUsuario.setPerfil(perfil);
            usuarioRepository.save(novoUsuario);
            System.out.println(">>> Usuário " + perfil.getDescricao() + " (" + email + ") criado com sucesso!");
            return novoUsuario;
        }
    }

    private Cidade criarCidadeSeNaoExistir(String nome, String uf) {
        Optional<Cidade> cidOpt = cidadeRepository.findByNomeIgnoreCase(nome);
        if (cidOpt.isPresent()) {
            return cidOpt.get();
        } else {
            Cidade novaCidade = new Cidade();
            novaCidade.setNome(nome);
            novaCidade.setUf(uf.toUpperCase());
            return cidadeRepository.save(novaCidade);
        }
    }

    private Fornecedor criarFornecedorSeNaoExistir(String nome, String email) {
        Optional<Fornecedor> fornOpt = fornecedorRepository.findByEmail(email);
        if (fornOpt.isPresent()) {
            return fornOpt.get();
        } else {
            Fornecedor novoFornecedor = new Fornecedor();
            novoFornecedor.setNome(nome);
            novoFornecedor.setEmail(email);
            novoFornecedor.setAtivo(true);
            return fornecedorRepository.save(novoFornecedor);
        }
    }

    private Propriedade criarPropriedadeSeNaoExistir(String nome, Fornecedor fornecedor, Cidade cidade) {
        Optional<Propriedade> propOpt = propriedadeRepository.findByNomeAndFornecedor(nome, fornecedor);
        if (propOpt.isPresent()) {
            return propOpt.get();
        } else {
            Propriedade novaPropriedade = new Propriedade();
            novaPropriedade.setNome(nome);
            novaPropriedade.setFornecedor(fornecedor);
            novaPropriedade.setCidade(cidade);
            novaPropriedade.setAtivo(true);
            return propriedadeRepository.save(novaPropriedade);
        }
    }
}