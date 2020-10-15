package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;

import ui.GUIListenerFunctions;

public class MySQLManager {
	
	private Connection con;
	public MySQLManager() {
		loadDriver();
		con = connect();
		
	}

	private void loadDriver() {
		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
        	GUIListenerFunctions.print("Connection to Database: Failure -> " + e.getMessage());
        }
	}
	
	public Connection connect(String host, String port, String db, String user, String password) {
		Connection con = null;
		
		try {
			con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db + "?" + "user=" + user + "&password=" + password);
			GUIListenerFunctions.print("Connection to Database: Successful");
		} catch (SQLException e) {
			//e.printStackTrace();
			if(e.getMessage().contains("No suitable driver"))
				GUIListenerFunctions.print("Connection to Database: Failure -> Driver not found");
			else GUIListenerFunctions.print("Connection to Database: Failure -> Connection details may be wrong");
		}
		
		return con;
	}
	
	private Connection connect () {
		String host="", port="", db="", user="", pw="";
		try {
			JSONObject dbcfg = JSONManager.readJSON("./dist/cfg/db.cfg");
			host = (String) dbcfg.get("host"); 
			port = (String) dbcfg.get("port");
			db = (String) dbcfg.get("db");
			user = (String) dbcfg.get("user");
			pw = (String) dbcfg.get("pw");
		} catch (NullPointerException e) {
			GUIListenerFunctions.print(e.getMessage());
		}
		return connect (host, port, db, user, pw);
	}
	
	public void close () {
		try {
			con.close();
		} catch (SQLException e) {
			//Ignore
		}
	}
	
	public List<HashMap<String, String>> selectByIdentifier (String Table, String idcolumn, String idvalue) {
		String query = "SELECT * FROM " + Table + " WHERE " + idcolumn + " = " + idvalue + ";";
		return select (query);
	}
	
	public List<HashMap<String, String>> selectAll (String Table) {
		String query = "SELECT * FROM " + Table + ";";
		return select (query);
	}
	
	private List<HashMap<String, String>> select (String query) {
		List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		Connection selCon = connect(); //Different connection for select to avoid disrupting the sensor reading inserts into the database
		ResultSet rs = null;
		Statement stmt = null;
		try {
			stmt = selCon.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData md = rs.getMetaData();
			int columns = md.getColumnCount();
			while (rs.next()){
				HashMap<String, String> row = new HashMap<String, String>(columns);
				for(int i=1; i<=columns; ++i){           
					row.put(md.getColumnName(i),rs.getString(i));
				}
				result.add(row);
			}
		} catch (SQLException e) {
			GUIListenerFunctions.print("Failed to select from Database -> Query: " + query + ", Error: " + e.getMessage());
		} finally {
		    if (rs != null) {
		        try {
		            rs.close();
		        } catch (SQLException sqlEx) { } // ignore

		        rs = null;
		    }

		    if (stmt != null) {
		        try {
		            stmt.close();
		        } catch (SQLException sqlEx) { } // ignore

		        stmt = null;
		    }
		    
		    if (selCon != null) {
		    	try {
		    		selCon.close();
		    	} catch (SQLException sqlEx) { } // ignore
		    }
		}
	    
		return result;
	}
	
	
	public void insert (String Table, List<String> columns, List<String> values) throws SQLException {
		if (columns.size() == values.size()) {
			String query = "INSERT INTO " + Table + " (";
			String columnsNames="", values2cells="";
			
			for(int c = 0; c < columns.size(); c++) {
				columnsNames+=columns.get(c);
				values2cells+=values.get(c);
				if(c<columns.size()-1) {
					columnsNames+=",";
					values2cells+=",";
				}
			}
			query += columnsNames +") VALUES (" + values2cells + ");";
			//System.out.println(query);
			executeQuery(query);
		} else {
			throw new SQLException ("Insert into database failed: Number of Columns does not match the Number of Values");
		}
	}
	
	public void delete (String Table, String idcolumn, String idvalue) {
		String query = "DELETE FROM " + Table + " WHERE " + idcolumn + " = " + idvalue + ";";
		executeQuery(query);
	}
	
	
	private void executeQuery (String query) {
		ResultSet rs = null;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			
			if (stmt.execute(query)) {
		        rs = stmt.getResultSet();
		    }
		} catch (SQLException e) {
			GUIListenerFunctions.print("Failed to execute the query: " + query + ", Error: " + e.getMessage());
		} finally {
		    if (rs != null) {
		        try {
		            rs.close();
		        } catch (SQLException sqlEx) { } // ignore

		        rs = null;
		    }

		    if (stmt != null) {
		        try {
		            stmt.close();
		        } catch (SQLException sqlEx) { } // ignore

		        stmt = null;
		    }
		}
	}
	
}
