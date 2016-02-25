package com.sopra.dao;

import java.sql.*;
import java.util.List;

/**
 * Classe permettant d'ajouter ou d'afficher des données issues de Google Analytics qui sont stockées dans la base de données
 * @author Brandon Gommard
 *
 */
public class ObjectifsManagement {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost:3306/bdgadget";


	/**
	 * fonction d'affichage du contenu de la table
	 * utilisé uniquement lors du développement pour réaliser des tests
	 * @param tableName
	 */
	public static void affichageContenu(String tableName, String user, String pass){
		System.out.println("Affichage du contenu");
		Connection conn = null;
		Statement stmt = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL,user,pass);

			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			String sql = "SELECT * FROM " + tableName;
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){
				System.out.print("\t" + rs.getInt("id"));
				System.out.print("\t" + rs.getString("date"));
				System.out.print("\t" + rs.getString("os"));
				System.out.print("\t" + rs.getString("vos"));
				System.out.print("\t" + rs.getString("device"));
				System.out.print("\t" + rs.getString("browser"));
				System.out.print("\t" + rs.getString("vbrowser"));
				System.out.println("\t" + rs.getInt("nbUsers"));
			}
			rs.close();
			stmt.close();
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se2){
			}// nothing we can do
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}
		}
		System.out.println("Goodbye!");
	}



	/**
	 * Fonction d'ajout de rows (réponse de GA concernant la dimension dimensionName) dans la table TABLE_NAME
	 * @param rows
	 * @param dimensionName
	 */
	public static void ajoutContenu(List<List<String>> rows , String dimensionName, String tableName, String user, String pass){
		Connection conn = null;
		try{
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,user,pass);


			try{
				System.out.println("Ajout du contenu pour la table...");
				String insertSQL = "INSERT INTO " 
							+ tableName + " (id, date, os, vos, device, browser, vbrowser, dimension, nbUsers) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement preparedStatement = conn.prepareStatement(insertSQL);

				Timestamp currentTimeStamp = getCurrentTimeStamp();
				if(rows != null){
					for(List<String> row : rows){
						preparedStatement.setString(1, null);
						preparedStatement.setTimestamp(2, currentTimeStamp);
						preparedStatement.setString(3, row.get(0));
						preparedStatement.setString(4, row.get(1));
						preparedStatement.setString(5, row.get(2));
						preparedStatement.setString(6, row.get(3));
						preparedStatement.setString(7, row.get(4));
						preparedStatement.setString(8, dimensionName);
						preparedStatement.setInt(9, Integer.parseInt(row.get(5)));

						preparedStatement.executeUpdate();
					}
				}else{
					System.out.println("Rien à ajouter");
				}

				//STEP 6: Clean-up environment
				conn.close();
			}catch(SQLException se){
				//Handle errors for JDBC
				se.printStackTrace();
			}catch(Exception e){
				//Handle errors for Class.forName
				e.printStackTrace();
			}

		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}

		finally{
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}
		}
		System.out.println("Requête de " + dimensionName + " ajouté dans la table " + tableName);
	}


	/**
	 * fonction utilitaire qui donne l'heure exacte en ms
	 * @return
	 */
	private static java.sql.Timestamp getCurrentTimeStamp() {

		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());

	}

}