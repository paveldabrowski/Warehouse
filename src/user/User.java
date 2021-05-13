package user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

import dbConnectionAndMethods.DB;

public class User {
	private String name;
	private String lastName;
	private int id;
	private int ywn;
	private ArrayList<Integer> ywnNumbersFromDB;
	private int access;
	
	public User() {
		ywn = ywnGenerator();
	}
	
	public User(TreeMap<String,String> map) {
		super();
		this.name = map.get("name");
		this.lastName = map.get("lastname");
		this.id = Integer.parseInt(map.get("id"));
		this.ywn = Integer.parseInt(map.get("ywn"));
		this.access = Integer.parseInt(map.get("access"));

	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public int getId() {
		return id;
	}
	public int getYWN() {
		return ywn;
	}
	public int getAccess() {
		return access;
	}
	
	/**
	 * Creating random number for new user. If number exits in database method creates another one.
	 * @return generated number
	 */
	public int ywnGenerator() {
		Random ywnGenerator = new Random();
		int low2 = 20000000; int high2 = 29999999;
		int ywnNumber = ywnGenerator.nextInt(high2-low2) + low2;
		ywnNumbersFromDB = new ArrayList<Integer>();
		try(Connection conn = DB.getConnection()){
			PreparedStatement statement = conn.prepareStatement("SELECT `YWN` FROM users");
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				ywnNumbersFromDB.add(rs.getInt("YWN"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!ywnNumbersFromDB.contains(ywnNumber)) {
			return ywnNumber;
		} else {
			ywnGenerator();
		}
		return ywnNumber;
	}	
}
