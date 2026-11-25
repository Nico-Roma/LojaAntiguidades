package br.edu.ifc.videira.antiguidades.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fábrica de conexões com o MariaDB.
 * Ajuste URL, USUARIO e SENHA conforme o seu ambiente (XAMPP padrão: root sem senha).
 */
public class ConexaoDB {

	private static final String URL = "jdbc:mysql://localhost:3306/loja_antiguidades"
			+ "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8";
	private static final String USUARIO = "root";
	private static final String SENHA = "";

	private ConexaoDB() {
	}

	public static Connection getConexao() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new SQLException("Driver JDBC não encontrado.", e);
		}
		return DriverManager.getConnection(URL, USUARIO, SENHA);
	}
}
