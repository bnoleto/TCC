package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConexaoDB {
	
	String host = "jdbc:mariadb://";
	String usuario;
	String senha;

	Connection con;
	
	public ConexaoDB(String host, String usuario, String senha) {
		this.host += host;
		this.usuario = usuario;
		this.senha = senha;
		
		try {
            con = DriverManager.getConnection(this.host, this.usuario, this.senha);
            
            System.out.println("Conexão realizada com sucesso! " + this.host);
        } catch (Exception e) {
        	
        	System.out.println(e.getMessage());
        }
	}
	
	public Map<String,ArrayList<String>> getTabela() throws SQLException{


		Statement st = con.createStatement();
		
		ResultSet resultado = st.executeQuery("SELECT * FROM tb_dados;");
		
		Map<String,ArrayList<String>> tabela = new HashMap<String,ArrayList<String>>();

		while (resultado.next()) {
			
			ArrayList<String> item = new ArrayList<String>();
			for(int j = 0; j < 6; j++) {
				
				item.add(resultado.getString(j+1));

			}
			tabela.put(item.get(0), item);
		}
		
		return tabela;

	}
	
	public void reconectar() {
		
		try {
            con = DriverManager.getConnection(this.host, this.usuario, this.senha);
            
            System.out.println("Conexão realizada com sucesso!");
        } catch (Exception e) {
        	
        	System.out.println(e.getMessage());
        }
	}
	
	public void encerrar_conexao() {
		try {
			this.con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
