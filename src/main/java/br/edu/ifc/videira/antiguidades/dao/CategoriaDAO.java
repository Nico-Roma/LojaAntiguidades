package br.edu.ifc.videira.antiguidades.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.edu.ifc.videira.antiguidades.model.Categoria;

public class CategoriaDAO {

	/** READ: lista todas as categorias em ordem alfabética. */
	public List<Categoria> listar() throws SQLException {
		String sql = "SELECT id, nome FROM categoria ORDER BY nome";
		List<Categoria> lista = new ArrayList<>();
		try (Connection con = ConexaoDB.getConexao();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				lista.add(new Categoria(rs.getInt("id"), rs.getString("nome")));
			}
		}
		return lista;
	}
}
