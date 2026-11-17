# Sistema Oficina

Sistema de gerenciamento de oficina mecânica desenvolvido como projeto individual para a disciplina de Programação Orientada a Objetos (POO).

A aplicação permite organizar informações de clientes, ordens de serviço, produtos, serviços e recebimentos, centralizando operações importantes da rotina de uma oficina.

## Funcionalidades

- **Dashboard:** visualização geral das informações e indicadores do sistema.
- **Gestão de clientes:** cadastro e consulta de pessoas físicas e jurídicas.
- **Ordens de serviço:** criação, consulta e gerenciamento de ordens de serviço.
- **Produtos:** gerenciamento de pneus e peças associados às ordens de serviço.
- **Serviços:** registro e gerenciamento dos serviços realizados.
- **Recebimentos:** controle dos valores e pagamentos relacionados às ordens de serviço.
- **Controle financeiro:** acompanhamento dos valores recebidos e pendentes.

## Tecnologias utilizadas

### Backend
- Java 21
- Spring Boot 3.5.16
- JDBC
- MySQL
- Maven

### Frontend
- HTML5
- CSS3
- JavaScript

### Testes
- JUnit 5

## Conceitos de Programação Orientada a Objetos

O projeto aplica conceitos fundamentais da Programação Orientada a Objetos:

- **Abstração:** representação de elementos do domínio da oficina por meio de classes.
- **Encapsulamento:** controle do acesso aos atributos e das operações das classes.
- **Herança:** especialização de classes, como `PessoaFisica` e `PessoaJuridica`, derivadas de `Cliente`, e `Pneu` e `Peca`, derivadas de `Produto`.
- **Polimorfismo:** utilização de classes especializadas a partir de suas classes-base.

## Arquitetura e organização

O projeto utiliza uma organização em camadas, separando as responsabilidades da aplicação:

- **Models:** classes que representam as entidades e regras do domínio.
- **Controllers:** responsáveis por receber as requisições HTTP e encaminhá-las às operações correspondentes.
- **DAOs (Data Access Objects):** responsáveis pela comunicação com o banco de dados por meio de JDBC.
- **Conexão:** classe responsável por estabelecer a conexão com o MySQL.
- **Frontend:** arquivos HTML, CSS e JavaScript responsáveis pela interface e comunicação com a API.
- **Tests:** testes automatizados das classes e operações do sistema.

### Estrutura de diretórios

```text
SistemaOficina/
├── database/
│   └── SistemaOficina.sql
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/oficina/
│   │   │       ├── controller/
│   │   │       │   ├── ClienteController.java
│   │   │       │   └── OrdemServicoController.java
│   │   │       │
│   │   │       ├── dao/
│   │   │       │   ├── ClienteDAO.java
│   │   │       │   ├── OrdemServicoDAO.java
│   │   │       │   └── RecebimentoDAO.java
│   │   │       │
│   │   │       ├── Cliente.java
│   │   │       ├── Conexao.java
│   │   │       ├── ItemOrdemServico.java
│   │   │       ├── Main.java
│   │   │       ├── OrdemServico.java
│   │   │       ├── Peca.java
│   │   │       ├── PessoaFisica.java
│   │   │       ├── PessoaJuridica.java
│   │   │       ├── Pneu.java
│   │   │       ├── Produto.java
│   │   │       ├── Recebimento.java
│   │   │       ├── Servico.java
│   │   │       ├── SistemaOficinaApplication.java
│   │   │       └── ValidacaoCliente.java
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/
│   │       │       │   └── Style.css
│   │       │       ├── js/
│   │       │       │   ├── Adapters.js
│   │       │       │   ├── Api.js
│   │       │       │   ├── App.js
│   │       │       │   ├── Clientes.js
│   │       │       │   ├── Config.js
│   │       │       │   ├── Dashboard.js
│   │       │       │   ├── Demo.js
│   │       │       │   ├── Ordens.js
│   │       │       │   ├── Recebimentos.js
│   │       │       │   └── Ui.js
│   │       │       └── index.html
│   │       │
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│           └── br/com/oficina/
│               ├── dao/
│               │   ├── ClienteDAOTest.java
│               │   └── OrdemServicoDAOTest.java
│               ├── ClienteTest.java
│               ├── ItemOrdemServicoTest.java
│               └── OrdemServicoTest.java
│
├── .gitignore
├── pom.xml
└── README.md
```

## Banco de dados

O sistema utiliza MySQL para armazenar os dados da aplicação.

O script de criação do banco de dados está disponível em:

`database/SistemaOficina.sql`

Para executar o projeto, é necessário importar esse script no MySQL antes de iniciar a aplicação.

## Como executar o projeto

### 1. Pré-requisitos

Certifique-se de ter instalado:

- Java 21 ou superior.
- MySQL.
- Maven (ou utilize o Maven integrado à IDE).
- Uma IDE de sua preferência, como IntelliJ IDEA.

### 2. Clonar o repositório

```bash
git clone https://github.com/Gustavoegidio1/SistemaOficina.git
```

Entre na pasta do projeto:

```bash
cd SistemaOficina
```

### 3. Configurar o banco de dados

Abra o MySQL e execute o script:

`database/SistemaOficina.sql`

O projeto utiliza as seguintes variáveis de ambiente para a conexão com o banco:

| Variável | Valor padrão local |
|---|---|
| DB_URL | jdbc:mysql://localhost:3306/sistemaoficina |
| DB_USER | root |
| DB_PASSWORD | (vazio) |

Caso necessário, configure essas variáveis de acordo com as credenciais do seu MySQL.

### 4. Executar a aplicação

Abra o projeto na IDE e execute a classe:

`SistemaOficinaApplication.java`

Também é possível iniciar pelo terminal, na raiz do projeto:

```bash
mvn spring-boot:run
```

### 5. Acessar o sistema

Com a aplicação em execução, abra o navegador e acesse:

```text
http://localhost:8080
```

## Testes

O projeto possui testes automatizados utilizando JUnit 5, contemplando classes do domínio e operações de acesso a dados.

Para executar os testes pelo terminal:

```bash
mvn test
```

## Objetivo acadêmico

Este projeto foi desenvolvido individualmente com o objetivo de aplicar, na prática, os conceitos de Programação Orientada a Objetos, integração com banco de dados, desenvolvimento de APIs e construção de interfaces web.

Além de atender aos requisitos acadêmicos, a proposta foi desenvolver uma aplicação baseada em necessidades reais de gerenciamento de uma oficina mecânica.