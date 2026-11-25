package br.edu.ifc.videira.antiguidades.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import br.edu.ifc.videira.antiguidades.dao.CategoriaDAO;
import br.edu.ifc.videira.antiguidades.dao.ProdutoDAO;
import br.edu.ifc.videira.antiguidades.model.Produto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controlador do CRUD de produtos.
 *
 * GET  /produtos                 -> READ   (lista, com busca e filtro)
 * GET  /produtos?acao=novo       -> formulário de cadastro
 * GET  /produtos?acao=editar&id= -> formulário de edição
 * POST /produtos?acao=salvar     -> CREATE (sem id) ou UPDATE (com id)
 * POST /produtos?acao=excluir    -> DELETE
 */
@WebServlet("/produtos")
public class ProdutoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final List<String> ESTADOS = List.of("Excelente", "Muito bom", "Bom", "Regular", "Para restauro");
	private static final String VIEW_LISTA = "/WEB-INF/views/lista.jsp";
	private static final String VIEW_FORM = "/WEB-INF/views/formulario.jsp";

	private final ProdutoDAO produtoDAO = new ProdutoDAO();
	private final CategoriaDAO categoriaDAO = new CategoriaDAO();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String acao = req.getParameter("acao");
		if (acao == null) {
			acao = "listar";
		}
		try {
			switch (acao) {
			case "novo" -> mostrarFormulario(req, resp, new Produto());
			case "editar" -> editar(req, resp);
			default -> listar(req, resp);
			}
		} catch (SQLException e) {
			erroBanco(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String acao = req.getParameter("acao");
		try {
			if ("excluir".equals(acao)) {
				excluir(req, resp);
			} else if ("salvar".equals(acao)) {
				salvar(req, resp);
			} else {
				resp.sendRedirect(req.getContextPath() + "/produtos");
			}
		} catch (SQLException e) {
			erroBanco(req, resp, e);
		}
	}

	// ---------------------------------------------------------------- READ
	private void listar(HttpServletRequest req, HttpServletResponse resp)
			throws SQLException, ServletException, IOException {
		String busca = req.getParameter("busca");
		Integer categoriaId = parseInteiro(req.getParameter("categoria"));

		List<Produto> produtos = produtoDAO.listar(busca, categoriaId);

		int totalPecas = 0;
		BigDecimal valorEstoque = BigDecimal.ZERO;
		for (Produto p : produtos) {
			totalPecas += p.getEstoque();
			valorEstoque = valorEstoque.add(p.getPreco().multiply(BigDecimal.valueOf(p.getEstoque())));
		}

		req.setAttribute("produtos", produtos);
		req.setAttribute("categorias", categoriaDAO.listar());
		req.setAttribute("busca", busca == null ? "" : busca.trim());
		req.setAttribute("categoriaSelecionada", categoriaId);
		req.setAttribute("totalPecas", totalPecas);
		req.setAttribute("valorEstoque", valorEstoque);
		moverMensagemDaSessao(req);
		req.getRequestDispatcher(VIEW_LISTA).forward(req, resp);
	}

	private void editar(HttpServletRequest req, HttpServletResponse resp)
			throws SQLException, ServletException, IOException {
		Integer id = parseInteiro(req.getParameter("id"));
		Produto p = id == null ? null : produtoDAO.buscarPorId(id);
		if (p == null) {
			mensagem(req, "erro", "Produto não encontrado.");
			resp.sendRedirect(req.getContextPath() + "/produtos");
			return;
		}
		mostrarFormulario(req, resp, p);
	}

	private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp, Produto p)
			throws SQLException, ServletException, IOException {
		req.setAttribute("produto", p);
		req.setAttribute("categorias", categoriaDAO.listar());
		req.setAttribute("estados", ESTADOS);
		req.getRequestDispatcher(VIEW_FORM).forward(req, resp);
	}

	// ------------------------------------------------------- CREATE / UPDATE
	private void salvar(HttpServletRequest req, HttpServletResponse resp)
			throws SQLException, ServletException, IOException {
		Produto p = new Produto();
		Integer id = parseInteiro(req.getParameter("id"));
		p.setId(id == null ? 0 : id);
		p.setNome(texto(req.getParameter("nome")));
		p.setDescricao(texto(req.getParameter("descricao")));
		p.setEstado(texto(req.getParameter("estado")));

		List<String> erros = new ArrayList<>();

		if (p.getNome().isEmpty()) {
			erros.add("Informe o nome do produto.");
		} else if (p.getNome().length() > 120) {
			erros.add("O nome pode ter no máximo 120 caracteres.");
		}

		Integer categoriaId = parseInteiro(req.getParameter("categoriaId"));
		if (categoriaId == null) {
			erros.add("Escolha uma categoria.");
		} else {
			p.setCategoriaId(categoriaId);
		}

		String anoTxt = texto(req.getParameter("anoFabricacao"));
		if (!anoTxt.isEmpty()) {
			Integer ano = parseInteiro(anoTxt);
			int anoAtual = Year.now().getValue();
			if (ano == null || ano < 1700 || ano > anoAtual) {
				erros.add("O ano de fabricação deve estar entre 1700 e " + anoAtual + ".");
			} else {
				p.setAnoFabricacao(ano);
			}
		}

		if (!ESTADOS.contains(p.getEstado())) {
			erros.add("Escolha o estado de conservação.");
		}

		try {
			BigDecimal preco = new BigDecimal(texto(req.getParameter("preco")).replace(",", "."));
			if (preco.signum() < 0 || preco.compareTo(new BigDecimal("99999999.99")) > 0) {
				erros.add("O preço deve ser um valor positivo.");
			} else {
				p.setPreco(preco.setScale(2, java.math.RoundingMode.HALF_UP));
			}
		} catch (NumberFormatException e) {
			erros.add("Informe um preço válido (ex.: 1250,00).");
		}

		Integer estoque = parseInteiro(req.getParameter("estoque"));
		if (estoque == null || estoque < 0) {
			erros.add("O estoque deve ser um número inteiro maior ou igual a zero.");
		} else {
			p.setEstoque(estoque);
		}

		if (!erros.isEmpty()) {
			req.setAttribute("erros", erros);
			mostrarFormulario(req, resp, p);
			return;
		}

		if (p.getId() == 0) {
			produtoDAO.inserir(p);
			mensagem(req, "sucesso", "Produto \"" + p.getNome() + "\" cadastrado com sucesso!");
		} else if (produtoDAO.atualizar(p)) {
			mensagem(req, "sucesso", "Produto \"" + p.getNome() + "\" atualizado com sucesso!");
		} else {
			mensagem(req, "erro", "Produto não encontrado para atualização.");
		}
		resp.sendRedirect(req.getContextPath() + "/produtos");
	}

	// ---------------------------------------------------------------- DELETE
	private void excluir(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
		Integer id = parseInteiro(req.getParameter("id"));
		if (id != null && produtoDAO.excluir(id)) {
			mensagem(req, "sucesso", "Produto excluído com sucesso.");
		} else {
			mensagem(req, "erro", "Não foi possível excluir: produto não encontrado.");
		}
		resp.sendRedirect(req.getContextPath() + "/produtos");
	}

	// --------------------------------------------------------------- auxiliares
	private static String texto(String valor) {
		return valor == null ? "" : valor.trim();
	}

	private static Integer parseInteiro(String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}
		try {
			return Integer.valueOf(valor.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** Guarda uma mensagem na sessão para exibir após o redirecionamento (padrão PRG). */
	private static void mensagem(HttpServletRequest req, String tipo, String texto) {
		HttpSession sessao = req.getSession();
		sessao.setAttribute("msgTipo", tipo);
		sessao.setAttribute("msgTexto", texto);
	}

	private static void moverMensagemDaSessao(HttpServletRequest req) {
		HttpSession sessao = req.getSession(false);
		if (sessao != null && sessao.getAttribute("msgTexto") != null) {
			req.setAttribute("msgTipo", sessao.getAttribute("msgTipo"));
			req.setAttribute("msgTexto", sessao.getAttribute("msgTexto"));
			sessao.removeAttribute("msgTipo");
			sessao.removeAttribute("msgTexto");
		}
	}

	private void erroBanco(HttpServletRequest req, HttpServletResponse resp, SQLException e)
			throws ServletException, IOException {
		log("Erro de banco de dados", e);
		req.setAttribute("erroBanco", "Não foi possível acessar o banco de dados. "
				+ "Verifique se o MariaDB está ligado e se o script banco/loja_antiguidades.sql foi importado.");
		req.setAttribute("produtos", new ArrayList<Produto>());
		req.setAttribute("categorias", new ArrayList<>());
		req.setAttribute("busca", "");
		req.setAttribute("totalPecas", 0);
		req.setAttribute("valorEstoque", BigDecimal.ZERO);
		req.getRequestDispatcher(VIEW_LISTA).forward(req, resp);
	}
}
