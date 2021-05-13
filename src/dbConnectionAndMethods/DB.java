package dbConnectionAndMethods;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import dbConnectionAndMethods.*;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;



public class DB {
	
	/**
	 * Method connecting with database
	 * @return connection
	 * @throws Exception when there is connection problem
	 */
	public static Connection getConnection() throws Exception {
				
		Connection conn = null;
		try {
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/warehouse?autoReconnect=true&useSSL=false","root", "admin");
			if(conn != null) {

				System.out.println("Connected to database");
			}
			
		} catch (Exception e) {
			System.out.println("Connection error!");
			System.out.println(e.getMessage());
		}
		return conn;
	}
	
	/**
	 * Gets resultset from database and wift this set creating table model	
	 * @param rs resultset
	 * @return {@link DefaultTableModel}
	 */
	public static DefaultTableModel buildTableModel(ResultSet rs)
	        throws SQLException {
	    ResultSetMetaData metaData = rs.getMetaData();
	    Vector<String> columnNames = new Vector<String>();
	    int columnCount = metaData.getColumnCount();
	    
	    columnNames.add("No.");//adding No. column
	    
	    for (int column = 1; column <= 9; column++) { //<= original columnCount

	    		columnNames.add(metaData.getColumnName(column));

	    }

	    // data of the table
	    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	    
	    int no = 1;
	    while (rs.next()) {
	        Vector<Object> vector = new Vector<Object>();
	       
	        vector.add(no++); // no.added to first column no. and variable no is increment
	        
	        
	        for (int columnIndex = 1; columnIndex <= 9; columnIndex++) { // <= original columnCount
	          
	        	vector.add(rs.getObject(columnIndex));
	        }
	        data.add(vector);
	    }
	    DefaultTableModel defaultTableModel = new DefaultTableModel(data, columnNames) {
	    		    		
	    	@Override
	    	public Class<?> getColumnClass(int columnIndex) {
			
				Class retVal = Object.class;
	
		        if(getRowCount() > 0)
		            retVal =  getValueAt(0, columnIndex).getClass();
	
		        return retVal;
	    	}						
	    };
	    
	    return defaultTableModel;
	}
	
	/**
	 * Method updating data in database from particular cell in {@link JTable}
	 * @param table from data is
	 * @param databaseTableName which table in database have to be updated
	 * @param e {@link TableModelEvent} to get updated data 
	 */
	public static void updateDbFromTableCell(JTable table, String databaseTableName, TableModelEvent e) {
		
		String tableName = databaseTableName;
		int row = e.getFirstRow();
		int col = e.getColumn();
		String data = String.valueOf(table.getValueAt(row, col));	
		
		String primaryColumnValue = String.valueOf(table.getValueAt(row, 1));
		String columnName = table.getColumnName(col);
		String primaryColumnName = table.getColumnName(1);
		System.out.println("Primary column: " + primaryColumnName);
		System.out.println("Edited column: " + columnName);
		System.out.println("id: " + primaryColumnValue);
		System.out.println("row: " + row + ", col: " + col + ", " + data);
		
		try {
			Connection conn = DB.getConnection();
			Formatter format = new Formatter(); format.format("UPDATE `warehouse`.`%s` SET `%s` = '%s' WHERE (`%s` = '%s')", tableName, columnName, data, primaryColumnName, primaryColumnValue);
			String querry = format.toString();
			PreparedStatement statement = conn.prepareStatement(querry);
			statement.executeUpdate();
			conn.close();
			System.out.println("Database updated");
			format.close();
			
			
		} catch (Exception e1) {
			e1.printStackTrace();
		} 		
	}
	
	/**
	 * Method add new product to library
	 * @param name 
	 * @param manufacturer
	 * @param countryOfProduction
	 * @param weight
	 * @param unit
	 * @param barcore
	 * @param volume
	 * @param parentComponent
	 * @param vat
	 */
	public static void addProductToLibrary(String name,String manufacturer,String countryOfProduction, String weight, String unit, String barcore, String volume, JComponent parentComponent, String vat ){
		
		PreparedStatement ps = null;
		Connection connection = null;
		try {
			connection=DB.getConnection();
			String query="insert into productlibrary(`name`,`manufacturer`,`country of production`,`weight`,`unit`,`volume (dm3)`,`barcode`,`amount`, `vat`) values (?,?,?,?,?,?,?,?,?)";
			ps=connection.prepareStatement(query);
			ps.setString(1, name);
			ps.setString(2, manufacturer);
			ps.setString(3, countryOfProduction);
			ps.setString(4, weight);
			ps.setString(5, unit);
			ps.setString(6, volume);
			ps.setString(7, barcore);
			ps.setString(8, "0");
			ps.setString(9, vat);
			
			
			System.out.println(ps);
			ps.executeUpdate();
			connection.close();
			System.out.println("Product added to library");
		} catch (MySQLIntegrityConstraintViolationException  e) {
			System.out.println("Duplicate entry in DB.java");
			
			JOptionPane.showMessageDialog(parentComponent, e.getMessage() + ".\n Each product must have unique barcode." , "Duplicate entry", JOptionPane.ERROR_MESSAGE);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Method removes selected row from {@link JTable}
	 * @param tableName database table name from which row is selected removed
	 * @param idValue id of row to detect which row is selected to removed
	 * @param parent component to show message dialog
	 */
	public static void removeRowFromDB(String tableName, String idValue, JComponent parent) {
		try {
			Connection conn = getConnection();
			
			Formatter format2 = new Formatter(); format2.format("SELECT COUNT(*) FROM %s", tableName); 
			String formattedQuerry = format2.toString();
			PreparedStatement statement2 = conn.prepareStatement(formattedQuerry);
			ResultSet result = statement2.executeQuery();
			result.next();
			int rowCountBeforeDelete = result.getInt(1);
//			System.out.println("Before Delete: " + rowCountBeforeDelete);
			
			
			Formatter format = new Formatter();
			format.format("DELETE FROM `warehouse`.`%s` WHERE `id`=%s AND amount=0", tableName, idValue);
			String querry = format.toString();
			PreparedStatement statement = conn.clientPrepareStatement(querry);
			
			statement.executeUpdate();
			
			
			PreparedStatement statement3 = conn.prepareStatement(formattedQuerry);
			ResultSet result2 = statement2.executeQuery();
			result2.next();
			int rowCountAfterDelete = result2.getInt(1);
//			System.out.println("After Delete: " + rowCountAfterDelete);
			
			if(rowCountAfterDelete < rowCountBeforeDelete ) {
				System.out.println("Product removed");
				JOptionPane.showMessageDialog(parent, "Product successfully removed from library.", "Remove successfull", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(parent, "Product isn't removed from library, because amout in stock is bigger than 0.", "Remove error", JOptionPane.WARNING_MESSAGE);
			}
			
			
			conn.close();
			
		} catch (Exception e) {
			System.out.println("Product not romoved");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Method is updating reserved amount of products in database
	 * @param data contains id's and values to change
	 */
	public static void updateReservedInStock(HashMap<String, Double> data) {
		try {
			Connection conn = DB.getConnection();
			HashMap<String, Double> dataToUpdate = data;
			Set<String> set = dataToUpdate.keySet();
			PreparedStatement statement;
			for(String id: set) {
				Formatter format = new Formatter(); format.format("SELECT amount,reserved FROM productlibrary WHERE id=%s", id);
				String formatedQuerry = format.toString();
				statement = conn.prepareStatement(formatedQuerry);
				ResultSet rs = statement.executeQuery();
				double[] dbData = new double[2];
				while(rs.next()) {
					System.out.println("id: " + id + " amount: " + rs.getString("amount") + " reserved: " + rs.getString("reserved"));
					
					dbData[0] = Double.parseDouble(rs.getString("amount"));
					dbData[1] = Double.parseDouble(rs.getString("reserved"));
				}
				double toDecrease = dataToUpdate.get(id);
				dbData[0] = dbData[0] + toDecrease;
				dbData[1] = dbData[1] - toDecrease;
				
				
				Formatter format2 = new Formatter(); format2.format("UPDATE productlibrary SET amount=%s, reserved=%s WHERE id=%s", String.valueOf(dbData[0]), String.valueOf(dbData[1]), id);
				String formattedQuerry2 = format2.toString();
				
				statement = conn.prepareStatement(formattedQuerry2);
				statement.executeUpdate();
				System.out.println("Database updated reserved is back to stock");			
		}
			conn.close();
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}
			

}


