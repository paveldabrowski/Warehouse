package main;

import com.mysql.jdbc.Connection;
import db.DB;
import menu.Menu;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.TreeMap;

public class Main extends Menu {
	static SignIn dialog;
	static TreeMap<String, String> userDataMap;
	
	/**
	 * Invokes {@link Menu} constructor
	 * @param map with user data
	 */
	public Main(TreeMap<String, String> map) {
		super(map);		
	}	

	public static void main(String[] args) {
		DialogThread thread = new DialogThread();
		thread.start();		
		
		try {
			while (thread.isAlive()) {
				thread.join();
			}
		} catch (InterruptedException e) {		
			e.printStackTrace();
		}		
		String user = dialog.getUser(); 

		userDataMap = getUserFromDB(user);
		System.out.println(userDataMap);
		new Menu(userDataMap);
		
		dialog = null;
	}
	
	/**
	 * Method gets full information of user from database
	 * @param user login from user input from {@link SignIn} dialog
	 * @return map with all user information
	 */
	private static TreeMap<String, String> getUserFromDB(String user) {
		
		Connection connection;
		PreparedStatement statement;
		ResultSet result;
		ResultSetMetaData metaData;
		TreeMap<String, String> userData = new TreeMap<String, String>();
		try {
			connection = DB.getConnection();
			String query="select * from users where login=?";
			statement = connection.prepareStatement(query);
			statement.setNString(1, user);
	
			result = statement.executeQuery();
			
			metaData = statement.getMetaData(); 
			int colCount = metaData.getColumnCount();  

			while(result.next()){
				for(int i = 1; i <= colCount; i++) {
					userData.put(metaData.getColumnName(i), result.getString(i));
				}
			}
			connection.close();
			return userData;
		} catch (Exception e) {
			System.out.println(e);
		}		
		return userData;
		}
	}

