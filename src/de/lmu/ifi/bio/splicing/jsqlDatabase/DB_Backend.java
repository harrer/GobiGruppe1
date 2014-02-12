package de.lmu.ifi.bio.splicing.jsqlDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public class DB_Backend {

	/** The name of the MySQL account to use (or empty for anonymous) */
	private final String userName = "gobi1";//"root";

	/** The password for the MySQL account (or empty for anonymous) */
	private final String password = new String(new char[]{69,103,101,55,70,107,105,82,83,109,117,66,77});//"root";

	/** The name of the computer running MySQL */
	private final String serverName = "mysql2-ext.bio.ifi.lmu.de";//"localhost";

	/** The port of the MySQL server (default is 3306) */
	private final int portNumber = 3306;

	/** The name of the database we are testing with (this default is installed with MySQL) */
	private final String dbName = "gobi1";//"test";
	
	/** The name of the table we are testing with */
	private final String tableName = "JDBC_TEST";
	
	private Connection connection;
	
	public DB_Backend(){
		try {
			this.connection = getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);

		conn = DriverManager.getConnection("jdbc:mysql://"
				+ this.serverName + ":" + this.portNumber + "/" + this.dbName,
				connectionProps);

		return conn;
	}

	/**
	 * Run a SQL command which does not return a recordset:
	 * CREATE/INSERT/UPDATE/DELETE/DROP/etc.
	 * 
	 * @throws SQLException If something goes wrong
	 */
	public boolean executeUpdate(Connection conn, String command) throws SQLException {
	    Statement stmt = null;
	    try {
	        stmt = conn.createStatement();
	        stmt.executeUpdate(command); // This will throw a SQLException if it fails
	        return true;
	    } finally {

	    	// This will run whether we throw an exception or not
	        if (stmt != null) { stmt.close(); }
	    }
	}
	
	public void run() throws SQLException {

		// Connect to MySQL
		Connection conn = null;
		try {
			conn = this.getConnection();
			System.out.println("Connected to database");
		} catch (SQLException e) {
			System.out.println("ERROR: Could not connect to the database");
			e.printStackTrace();
			return;
		}

		// Create a table
		try {
		    String createString =
			        "CREATE TABLE " + this.tableName + " ( " +
			        "ID INTEGER NOT NULL, " +
			        "NAME varchar(40) NOT NULL, " +
			        "STREET varchar(40) NOT NULL, " +
			        "CITY varchar(20) NOT NULL, " +
			        "STATE char(2) NOT NULL, " +
			        "ZIP char(5), " +
			        "PRIMARY KEY (ID))";
			this.executeUpdate(conn, createString);
			System.out.println("Created a table");
	    } catch (SQLException e) {
			System.out.println("ERROR: Could not create the table");
			e.printStackTrace();
			return;
		}
		
		//insert
		String insert = "insert into "+tableName+" values(1,'john','main','LA','CA','90705')";
		try {
			this.executeUpdate(conn, insert);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Inserted into table");
		
		// Drop the table
		try {
		    String dropString = "DROP TABLE " + this.tableName;
			this.executeUpdate(conn, dropString);
			System.out.println("Dropped the table");
	    } catch (SQLException e) {
			System.out.println("ERROR: Could not drop the table");
			e.printStackTrace();
			return;
		}
	}
	
	public Object[][] select(String select, boolean[] columns) throws SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		Statement stmt = null;
		ArrayList<Object> row = new ArrayList<>(columns.length);
		ArrayList<Object[]> list = new ArrayList<>();
		try {
	        stmt = conn.createStatement();
	        ResultSet rs = stmt.executeQuery(select);
	        while (rs.next()) {
	        	for (int i = 0; i < columns.length; i++) {
					if(columns[i]){
						row.add(rs.getObject(i));
					}
				}
	        	list.add(row.toArray());
	        }
	    } catch (SQLException e ) {
	        e.printStackTrace();
	        return null;
	    } finally {
	        if (stmt != null) { stmt.close(); }
	    }
		return list.toArray(new Object[][]{});
	}

	/**
	 * Connect to the DB and do some stuff
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		DB_Backend app = new DB_Backend();
		app.run();
	}
}