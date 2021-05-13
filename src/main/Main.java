package main;

import com.mysql.jdbc.Connection;
import dbConnectionAndMethods.DB;
import menu.Menu;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.TreeMap;

public class Main extends Menu {
	static SignIn2 dialog;
	static TreeMap<String, String> userDataMap;
	
	/**
	 * Invokes {@link Menu} constructor
	 * @param map with user data
	 */
	public Main(TreeMap<String, String> map) {
		super(map);		
	}	
	
	/**
	 * This method run the program
	 * @param args
	 */
	public static void main(String[] args) {
		Thread2 thread = new Thread2();
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
	 * @param user login from user input from {@link SignIn2} dialog
	 * @return map with all user information
	 */
	private static TreeMap<String, String> getUserFromDB(String user) {
		
		Connection connection;
		PreparedStatement statement = null;
		ResultSet result=null;
		ResultSetMetaData metaData = null;
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

