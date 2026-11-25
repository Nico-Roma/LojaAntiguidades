# Antiquário Melodia — CRUD de loja de antiguidades musicais

Aplicação web em **Java (Servlet + JSP/JSTL)**, **Tomcat 10.1+** e **MariaDB**, no mesmo padrão do projeto de referência
[Portfolio1](https://github.com/Karli-IFC-Videira/Portfolio1) (Maven, empacotamento WAR, namespace `jakarta.*`, driver `mysql-connector-j`).

A loja vende **vitrolas, discos e instrumentos**. O CRUD é sobre os **produtos** do acervo.

## Funcionalidades (CRUD)

| Operação | Onde |
|---|---|
| **C**reate | `+ Novo item` → formulário → `ProdutoDAO.inserir` |
| **R**ead | Lista com busca por texto, filtro por categoria e resumo de estoque → `ProdutoDAO.listar / buscarPorId` |
| **U**pdate | Botão `Editar` → formulário preenchido → `ProdutoDAO.atualizar` |
| **D**elete | Botão `Excluir` (com confirmação) → `ProdutoDAO.excluir` |

Extras: validação no servidor (com mensagens e dados preservados), `PreparedStatement` contra SQL Injection,
escape de HTML nas telas (XSS), padrão Post/Redirect/Get com mensagens de sucesso, layout responsivo.

## Estrutura

```
LojaAntiguidades/
├── pom.xml
├── banco/loja_antiguidades.sql          ← cria o banco, as tabelas e dados de exemplo
└── src/main
    ├── java/br/edu/ifc/videira/antiguidades
    │   ├── model/       Produto, Categoria
    │   ├── dao/         ConexaoDB, ProdutoDAO, CategoriaDAO
    │   └── controller/  ProdutoServlet  (@WebServlet("/produtos"))
    └── webapp
        ├── index.jsp
        ├── css/estilo.css               ← tema vintage (CSS puro)
        └── WEB-INF
            ├── web.xml
            └── views/ cabecalho.jspf, rodape.jspf, lista.jsp, formulario.jsp
```

## Banco de dados

Tabelas `categoria` (Vitrolas, Discos, Instrumentos) e `produto` (nome, categoria, descrição, ano, estado, preço, estoque),
ligadas por chave estrangeira.

## Como executar

1. **Ligue o MariaDB** (ex.: XAMPP) e importe `banco/loja_antiguidades.sql`
   (phpMyAdmin → Importar, ou `mariadb -u root -p < banco/loja_antiguidades.sql`).
2. Confira usuário e senha em `dao/ConexaoDB.java` (padrão: `root`, sem senha, porta 3306).
3. Gere o WAR: `mvn clean package` → `target/LojaAntiguidades.war`.
4. Copie o WAR para a pasta `webapps` do **Tomcat 10.1 ou superior** (necessário por causa do `jakarta.*`) e inicie o servidor.
   No Eclipse: *Run on Server* com Tomcat 10.1.
5. Acesse: <http://localhost:8080/LojaAntiguidades/>

## Requisitos

- JDK 21 ou superior (o pom usa `release 21`; o projeto de referência usa 23 — ajuste em `pom.xml` se quiser)
- Maven 3.9+
- Tomcat 10.1+
- MariaDB 10.4+
