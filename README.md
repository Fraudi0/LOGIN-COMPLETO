# Login Seguro

Sistema de autenticação e autorização desenvolvido com Java 17, Spring Boot 3, Spring Security, Thymeleaf e MongoDB Atlas.

Os usuários e as sessões ficam armazenados no Atlas. As sessões usam o Spring Session, então os dados do login vão para a coleção `sessoes` no banco, e não apenas na memória do servidor.

## O que o sistema faz

- Cadastro de usuário com validação dos campos e senha gravada com hash BCrypt
- Login e logout gerenciados pelo Spring Security, com proteção CSRF nos formulários
- Três perfis de acesso: `ADMIN`, `GERENTE` e `USUARIO`
- Rotas e páginas liberadas de acordo com o perfil
- Tela de administração para trocar o perfil, ativar/desativar e remover contas
- Tema visual trocado por configuração, sem alterar o HTML

## Perfis e rotas

| Rota | Quem acessa |
| --- | --- |
| `/login`, `/cadastro` | público |
| `/painel` | qualquer usuário autenticado |
| `/gerencia/**` | `GERENTE` e `ADMIN` |
| `/admin/**` | somente `ADMIN` |

Quem tenta abrir uma rota sem permissão é redirecionado para a página `/acesso-negado`.

## Pré-requisitos

- JDK 17 ou superior
- Maven 3.9 ou superior
- Uma conta gratuita no MongoDB Atlas

Confira o que está instalado com:

```bash
java -version
mvn -version
```

## Configurar o MongoDB Atlas

1. Crie uma conta em https://cloud.mongodb.com e um cluster gratuito (tipo M0).

2. Em **Database Access**, crie um usuário de banco com permissão de leitura e escrita. Esse usuário não é o mesmo que você usa para entrar no site do Atlas. Anote a senha.

   Prefira uma senha apenas com letras e números. Caracteres como `@`, `#`, `/`, `:` e `%` precisam ser codificados na URI (`@` vira `%40`, por exemplo) e costumam causar erro de conexão.

3. Em **Network Access**, libere o seu IP. Durante os estudos, `0.0.0.0/0` evita problemas quando o IP da sua rede muda. Em produção isso não deve ser usado.

4. Em **Clusters → Connect → Drivers**, copie a string de conexão. Ela tem este formato:

```
mongodb+srv://USUARIO:SENHA@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0
```

Substitua `SENHA` pela senha do usuário de banco criado no passo 2.

## Configuração da conexão

A string de conexão não fica escrita no código. O arquivo `src/main/resources/application.properties` lê as credenciais de variáveis de ambiente:

```properties
spring.data.mongodb.uri=${MONGODB_URI}
spring.data.mongodb.database=${MONGODB_DATABASE:login_seguro}
```

O arquivo `.env.example` na raiz do projeto mostra todas as variáveis esperadas. Copie-o para `.env` e preencha com os seus dados:

```bash
cp .env.example .env
```

O `.env` está listado no `.gitignore`, então as suas credenciais não vão para o repositório.

Variáveis disponíveis:

| Variável | Para que serve | Valor padrão |
| --- | --- | --- |
| `MONGODB_URI` | String de conexão do Atlas | obrigatória |
| `MONGODB_DATABASE` | Nome do banco | `login_seguro` |
| `ADMIN_EMAIL` | E-mail do administrador criado na primeira execução | `admin@exemplo.com` |
| `ADMIN_SENHA` | Senha desse administrador | `Admin@12345` |
| `APP_TEMA` | Tema visual carregado | `padrao` |

## Executar localmente

As variáveis precisam estar definidas na **mesma janela de terminal** em que você roda o Maven. Se fechar o terminal, defina de novo.

### Windows (PowerShell)

```powershell
$env:MONGODB_URI = 'mongodb+srv://usuario:senha@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0'
$env:MONGODB_DATABASE = 'login_seguro'
$env:ADMIN_EMAIL = 'admin@exemplo.com'
$env:ADMIN_SENHA = 'Admin@12345'

mvn spring-boot:run
```

Use aspas simples. Com aspas duplas, o `&` da URI pode ser interpretado como operador pelo PowerShell e quebrar o comando.

### Linux e macOS

```bash
export MONGODB_URI="mongodb+srv://usuario:senha@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
export MONGODB_DATABASE="login_seguro"
export ADMIN_EMAIL="admin@exemplo.com"
export ADMIN_SENHA="Admin@12345"

mvn spring-boot:run
```

Com a aplicação no ar, abra http://localhost:8080.

Na primeira execução o sistema cria sozinho o usuário administrador definido em `ADMIN_EMAIL` e `ADMIN_SENHA`. Procure no log a linha:

```
Usuario administrador criado: admin@exemplo.com
```

Troque a senha padrão antes de usar o sistema em qualquer ambiente real.

### Rodar em outra porta

Se a porta 8080 já estiver ocupada por outro programa (Apache, XAMPP, outra aplicação):

```bash
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
```

### Gerar o executável

```bash
mvn clean package
java -jar target/login-seguro-1.0.0.jar
```

### Rodar os testes

```bash
mvn test
```

## Testar os três perfis

Apenas o administrador é criado automaticamente. Quem se cadastra pela tela entra como `USUARIO`. Para ver os três perfis funcionando:

1. Entre com a conta de administrador.
2. Cadastre duas contas novas pela tela de cadastro.
3. Logado como administrador, abra **Usuários** e promova uma dessas contas para Gerente.
4. Saia e entre com cada conta para comparar o que aparece no menu.

## Estrutura do projeto

```
src/main/java/com/exemplo/loginseguro
├── config        Spring Security, tema e carga inicial do administrador
├── controller    rotas e telas
├── dto           formulário de cadastro com as validações
├── model         entidade Usuario e enum Perfil
├── repository    acesso ao MongoDB
├── security      UserDetails e UserDetailsService
└── service       regras de cadastro e gestão de usuários

src/main/resources
├── application.properties       configuração da aplicação
├── static/css/base.css          estrutura e layout das telas
├── static/css/temas/*.css       cores e fontes de cada tema
└── templates                    páginas Thymeleaf
```

## Trocar o tema

As páginas carregam dois arquivos de CSS: o `base.css`, que cuida do layout, e o arquivo do tema, que contém apenas variáveis de cor e fonte. O tema é escolhido pela propriedade:

```properties
app.tema.nome=padrao
```

ou pela variável de ambiente `APP_TEMA`. Dois temas já vêm prontos: `padrao` (claro) e `escuro`.

Para criar um tema novo:

1. Copie `src/main/resources/static/css/temas/padrao.css`.
2. Altere os valores das variáveis.
3. Salve como `src/main/resources/static/css/temas/seunome.css`.
4. Configure `app.tema.nome=seunome`.

Nenhum template precisa ser alterado.

O título e o texto do rodapé também vêm da configuração:

```properties
app.tema.titulo=Login Seguro
app.tema.rodape=Projeto academico
```

## Organização do repositório (GitFlow)

O projeto segue o GitFlow:

| Branch | Função |
| --- | --- |
| `main` | versões estáveis, com tag de release |
| `develop` | integração do desenvolvimento |
| `feature/*` | uma branch por funcionalidade, criada a partir de `develop` |

As funcionalidades foram desenvolvidas nestas branches:

- `feature/estrutura-inicial`
- `feature/modelo-usuario`
- `feature/spring-security`
- `feature/telas-thymeleaf`
- `feature/administracao-usuarios`
- `feature/tema-configuravel`
- `feature/documentacao`

Cada uma foi integrada em `develop` com merge sem fast-forward (`--no-ff`), preservando o histórico. A versão estável foi publicada em `main` com a tag `v1.0.0`.

Para continuar o desenvolvimento:

```bash
git checkout develop
git checkout -b feature/nome-da-funcionalidade

# desenvolva e faça os commits

git checkout develop
git merge --no-ff feature/nome-da-funcionalidade
```

## Segurança aplicada

- Senhas com BCrypt de força 12, com salt diferente a cada cadastro
- Proteção CSRF ativa nos formulários
- Logout por POST, para não ser disparado por link
- Cookie de sessão com `HttpOnly` e `SameSite=Lax`
- Contas desativadas não conseguem autenticar
- O administrador não consegue desativar nem excluir a própria conta
- Mensagem de erro genérica no login, sem revelar se o e-mail existe
- Credenciais fora do código, lidas de variáveis de ambiente

## Problemas comuns

| Mensagem | Causa | Solução |
| --- | --- | --- |
| `Could not resolve placeholder 'MONGODB_URI'` | A variável não foi definida nessa janela do terminal | Defina as variáveis e rode o Maven na mesma janela |
| `The connection string is invalid` | A URI chegou vazia ou cortada | Confira com `echo $env:MONGODB_URI` (PowerShell) ou `echo $MONGODB_URI` (Linux/macOS) |
| `O caráter de E comercial (&) não é permitido` | Aspas duplas no PowerShell | Use aspas simples na definição da variável |
| `Port 8080 was already in use` | Outro programa ocupa a porta | Rode em outra porta com `--server.port=8081` |
| Timeout ao conectar | IP não liberado no Atlas | Adicione o seu IP em **Network Access** |
| `Authentication failed` | Senha errada ou com caractere especial | Confira o usuário em **Database Access** e evite caracteres especiais |
