# ⚙️ Calcana API — Backend

API REST do **Sistema Calcana**, desenvolvido como projeto de extensão universitária em parceria com a **Assocana** (Associação Rural dos Fornecedores e Plantadores de Cana do Vale do Paranapanema). O sistema centraliza e moderniza a gestão de análises de ATR (Açúcar Total Recuperável) de propriedades rurais, substituindo processos manuais por uma solução digital completa.

---

## 📋 Sobre o Projeto

O Calcana API fornece todos os serviços necessários para o sistema: autenticação segura via JWT, cadastro de produtores e propriedades, lançamento de análises laboratoriais, geração de relatórios em PDF/Excel e envio automático por e-mail, além de um dashboard com métricas consolidadas para a gestão da associação.

---

## 🚀 Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.5.6 | Framework principal |
| Spring Data JPA | — | Persistência e repositórios |
| Spring Security | — | Autenticação e autorização |
| Spring Mail | — | Envio de e-mails via SMTP |
| MySQL | — | Banco de dados relacional |
| Lombok | — | Redução de boilerplate |
| JWT (Auth0) | — | Tokens de autenticação |
| iText 7 | 8.0.4 | Geração de relatórios PDF |
| Apache POI | 5.2.5 | Geração de planilhas Excel |
| Maven | 3.9 | Gerenciamento de dependências |

---

## 📁 Estrutura do Projeto

```
src/main/java/br/com/calcana/calcana_api/
├── config/             # Inicialização de dados (DataInitializer)
├── controllers/        # Endpoints REST
│   ├── AnaliseController
│   ├── AuthenticationController
│   ├── CidadeController
│   ├── DashboardController
│   ├── FornecedorController
│   ├── PerfilController
│   ├── PropriedadeController
│   ├── ReportController
│   └── UsuarioController
├── exceptions/         # Tratamento global de erros
├── model/              # Entidades JPA
│   ├── Analises
│   ├── Cidade
│   ├── Fornecedor
│   ├── Perfil
│   ├── Propriedade
│   └── Usuario
├── repositories/       # Repositórios Spring Data + Specifications
├── security/           # JWT, SecurityConfig, filtros e DTOs de auth
├── services/           # Regras de negócio
│   ├── AnaliseService
│   ├── DashboardService
│   ├── EmailService
│   ├── FornecedorService
│   ├── PropriedadeService
│   ├── ReportService
│   └── UsuarioService
└── CalcanaApiApplication.java
```

---

## ⚙️ Como Rodar Localmente

**Pré-requisitos:** Java 21, Maven e MySQL.

**1. Clone o repositório**
```bash
git clone https://github.com/gustavobeitum/Calcana-BACKEND.git
cd calcana-api
```

**2. Configure o banco de dados**

Crie o banco no MySQL:
```sql
CREATE DATABASE assocanaDB;
```

**3. Configure o `application.properties`**

Edite `src/main/resources/application.properties` com suas credenciais:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/assocanaDB?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA

api.security.token.secret=SUA_CHAVE_SECRETA_JWT

# Configuração de e-mail (Brevo/SendGrid ou outro SMTP)
spring.mail.host=smtp-relay.brevo.com
spring.mail.port=587
spring.mail.username=SEU_EMAIL_SMTP
spring.mail.password=SUA_SENHA_SMTP
calcana.email.remetente=seu@email.com
```

> ⚠️ **Nunca suba credenciais reais para o repositório.** Utilize variáveis de ambiente em produção.

**4. Execute a aplicação**
```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

---

## 🔐 Autenticação

A API utiliza **JWT (JSON Web Token)** para autenticação stateless. O token deve ser enviado no header de todas as requisições protegidas:

```
Authorization: Bearer <token>
```

O sistema possui dois perfis de acesso: **OPERADOR** e **ADMIN**, com endpoints protegidos de acordo com o papel do usuário.

---

## 📡 Principais Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/auth/login` | Autenticação e geração de token JWT |
| `GET` | `/analises` | Listar análises (com filtros) |
| `POST` | `/analises` | Registrar nova análise de ATR |
| `GET` | `/fornecedores` | Listar fornecedores/produtores |
| `POST` | `/fornecedores` | Cadastrar fornecedor |
| `GET` | `/propriedades` | Listar propriedades rurais |
| `POST` | `/propriedades` | Cadastrar propriedade |
| `GET` | `/dashboard` | Métricas e estatísticas consolidadas |
| `POST` | `/relatorio/enviar` | Gerar e enviar relatório por e-mail |
| `GET` | `/usuarios` | Gerenciar usuários (ADMIN) |

---

## 📊 Modelo de Análise de ATR

Cada análise registrada contém os seguintes dados técnicos:

| Campo | Descrição |
|---|---|
| `numeroAmostra` | Número da amostra |
| `dataAnalise` | Data da coleta |
| `brix` | Teor de sólidos solúveis |
| `leituraSacarimetrica` | Leitura sacarimétrica |
| `polCaldo` / `polCana` | Pol do caldo e da cana |
| `fibra` | Teor de fibra |
| `pureza` | Pureza do caldo |
| `arCana` / `arCaldo` | Açúcares redutores |
| `atr` | Açúcar Total Recuperável |

---

## 📧 Envio de Relatórios

O sistema gera relatórios em **PDF** (iText 7) e **Excel** (Apache POI) e os envia automaticamente por e-mail via **SMTP** (configurado com Brevo). O histórico de envios é registrado por análise, incluindo data e hora do último envio.

---

## 🔗 Repositório do Frontend

A interface web que consome esta API está disponível em:
👉 [calcana-frontend](https://github.com/gustavobeitum/Calcana-FRONTEND)

---

## 🎓 Projeto de Extensão

Este sistema foi desenvolvido como **projeto de extensão universitária**, com o objetivo de aplicar conhecimentos acadêmicos em uma solução real para a comunidade agrícola da região, beneficiando produtores rurais e a associação Assocana.
