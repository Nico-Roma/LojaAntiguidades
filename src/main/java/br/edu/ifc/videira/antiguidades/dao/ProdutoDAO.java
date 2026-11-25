package br.edu.ifc.videira.antiguidades.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.ifc.videira.antiguidades.model.Produto;

/**
 * Operações CRUD da tabela produto. Todas as consultas usam PreparedStatement
 * (protege contra SQL Injection).
 */
public class ProdutoDAO {

	private static final String SELECT_BASE = "SELECT p.id, p.nome, p.categoria_id, c.nome AS categoria_nome, "
			+ "p.descricao, p.ano_fabricacao, p.estado, p.preco, p.estoque, p.data_cadastro "
			+ "FROM produto p JOIN categoria c ON c.id = p.categoria_id ";

	/** CREATE: insere um produto e devolve o id gerado. */
	public int inserir(Produto p) throws SQLException {
		String sql = "INSERT INTO produto (nome, categoria_id, descricao, ano_fabricacao, estado, preco, estoque) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection con = ConexaoDB.getConexao();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			preencher(ps, p);
			ps.executeUpdate();
			try (ResultSet chaves = ps.getGeneratedKeys()) {
				return chaves.next() ? chaves.getInt(1) : 0;
			}
		}
	}

	/** READ: lista produtos, com filtro opcional por texto (nome/descrição) e categoria. */
	public List<Produto> listar(String busca, Integer categoriaId) throws SQLException {
		StringBuilder sql = new StringBuilder(SELECT_BASE).append("WHERE 1=1 ");
		List<Object> parametros = new ArrayList<>();

		if (busca != null && !busca.isBlank()) {
			sql.append("AND (p.nome LIKE ? OR p.descricao LIKE ?) ");
			String termo = "%" + busca.trim() + "%";
			parametros.add(termo);
			parametros.add(termo);
		}
		if (categoriaId != null) {
			sql.append("AND p.categoria_id = ? ");
			parametros.add(categoriaId);
		}
		sql.append("ORDER BY p.nome");

		List<Produto> lista = new ArrayList<>();
		try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
			for (int i = 0; i < parametros.size(); i++) {
				ps.setObject(i + 1, parametros.get(i));
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					lista.add(mapear(rs));
				}
			}
		}
		return lista;
	}

	/** READ: busca um produto pelo id (ou null se não existir). */
	public Produto buscarPorId(int id) throws SQLException {
		String sql = SELECT_BASE + "WHERE p.id = ?";
		try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapear(rs) : null;
			}
		}
	}

	/** UPDATE: atualiza os dados de um produto existente. */
	public boolean atualizar(Produto p) throws SQLException {
		String sql = "UPDATE produto SET nome = ?, categoria_id = ?, descricao = ?, ano_fabricacao = ?, "
				+ "estado = ?, preco = ?, estoque = ? WHERE id = ?";
		try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
			preencher(ps, p);
			ps.setInt(8, p.getId());
			return ps.executeUpdate() > 0;
		}
	}

	/** DELETE: remove um produto pelo id. */
	public boolean excluir(int id) throws SQLException {
		String sql = "DELETE FROM produto WHERE id = ?";
		try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		}
	}

	private void preencher(PreparedStatement ps, Produto p) throws SQLException {
		ps.setString(1, p.getNome());
		ps.setInt(2, p.getCategoriaId());
		ps.setString(3, p.getDescricao());
		if (p.getAnoFabricacao() == null) {
			ps.setNull(4, Types.SMALLINT);
		} else {
			ps.setInt(4, p.getAnoFabricacao());
		}
		ps.setString(5, p.getEstado());
		ps.setBigDecimal(6, p.getPreco());
		ps.setInt(7, p.getEstoque());
	}

	private Produto mapear(ResultSet rs) throws SQLException {
		Produto p = new Produto();
		p.setId(rs.getInt("id"));
		p.setNome(rs.getString("nome"));
		p.setCategoriaId(rs.getInt("categoria_id"));
		p.setCategoriaNome(rs.getString("categoria_nome"));
		p.setDescricao(rs.getString("descricao"));
		int ano = rs.getInt("ano_fabricacao");
		p.setAnoFabricacao(rs.wasNull() ? null : ano);
		p.setEstado(rs.getString("estado"));
		p.setPreco(rs.getBigDecimal("preco"));
		p.setEstoque(rs.getInt("estoque"));
		p.setDataCadastro(rs.getTimestamp("data_cadastro"));
		return p;
	}
}
