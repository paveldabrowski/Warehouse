package menu;

import java.util.Formatter;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class StockForOrder2 {
	protected JTable stockTable;
	private JScrollPane scroll;
	private String tableName = "productlibrary";
	private JFrame frame;



	public StockForOrder2() {

		stockTable = getDataFromDBLibrary(tableName);

	}
	
	private JTable getDataFromDBLibrary(String tableName) {
		try {
			Connection conn = DB.getConnection();
			Formatter format = new Formatter(); format.format("SELECT * FROM %s", tableName); 
			String formattedQuerry = format.toString();
			PreparedStatement statement = conn.prepareStatement(formattedQuerry);
			ResultSet result = statement.executeQuery();
			
			if(stockTable != null) {
	//			System.out.println("robi� null");
				stockTable = null;
			}
			stockTable = buildTableModel(result);
			stockTable.getTableHeader().setReorderingAllowed(false);
			stockTable.setAutoCreateRowSorter(true);
			
			stockTable.getModel().addTableModelListener(new TableModelListener() {
				
				@Override
				public void tableChanged(TableModelEvent e) { 
					if(e.getColumn() == 10 || e.getColumn() == 11) {
						System.out.println(stockTable.getModel().getColumnName(10));
						updateSellCell(stockTable, e);
					}
					
					stockTable = updateDBfromTableCell(stockTable, "productlibrary", e);
						
					}					
				}
			);
			
			cellConfiguration();
		    conn.close();
		    format.close();
		    
		    return stockTable;
			
	
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return stockTable;	
	
	
	
	}
	
	protected void cellConfiguration() {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JCheckBox.CENTER);
		
		
//		stockTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		stockTable.getColumnModel().getColumn(0).setMaxWidth(100);
		stockTable.getColumnModel().getColumn(0).setMinWidth(50);
		stockTable.getColumnModel().getColumn(0).setPreferredWidth(50);
		
		stockTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		stockTable.getColumnModel().getColumn(1).setMaxWidth(1000);
		stockTable.getColumnModel().getColumn(1).setMinWidth(40);
		stockTable.getColumnModel().getColumn(1).setPreferredWidth(40);
		
		stockTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		stockTable.getColumnModel().getColumn(2).setMaxWidth(1000);
		stockTable.getColumnModel().getColumn(2).setMinWidth(40);
		stockTable.getColumnModel().getColumn(2).setPreferredWidth(40);
		
//		stockTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
		stockTable.getColumnModel().getColumn(3).setMaxWidth(1000);
		stockTable.getColumnModel().getColumn(3).setMinWidth(190);
		stockTable.getColumnModel().getColumn(3).setPreferredWidth(190);
		
		stockTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
		stockTable.getColumnModel().getColumn(5).setMaxWidth(1000);
		stockTable.getColumnModel().getColumn(5).setMinWidth(50);
		stockTable.getColumnModel().getColumn(5).setPreferredWidth(140);
	
		stockTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
		stockTable.getColumnModel().getColumn(6).setMaxWidth(1000);
		stockTable.getColumnModel().getColumn(6).setMinWidth(50);
		stockTable.getColumnModel().getColumn(6).setPreferredWidth(70);
	
		stockTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
		stockTable.getColumnModel().getColumn(7).setMaxWidth(1000);
		stockTable.getColumnModel().getColumn(7).setMinWidth(50);
		stockTable.getColumnModel().getColumn(7).setPreferredWidth(70);
		
		stockTable.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);
		stockTable.getColumnModel().getColumn(8).setMaxWidth(1000);
		stockTable.getColumnModel().getColumn(8).setMinWidth(50);
		stockTable.getColumnModel().getColumn(8).setPreferredWidth(90);
		
		stockTable.getColumnModel().getColumn(10).setCellRenderer(centerRenderer);
		stockTable.getColumnModel().getColumn(10).setMaxWidth(1000);
		stockTable.getColumnModel().getColumn(10).setMinWidth(50);
		stockTable.getColumnModel().getColumn(10).setPreferredWidth(150);
		
		stockTable.getColumnModel().getColumn(11).setCellRenderer(centerRenderer);
		stockTable.getColumnModel().getColumn(11).setMaxWidth(1000);
		stockTable.getColumnModel().getColumn(11).setMinWidth(50);
		stockTable.getColumnModel().getColumn(11).setPreferredWidth(90);
		
		stockTable.getColumnModel().getColumn(12).setCellRenderer(centerRenderer);
		
		stockTable.getColumnModel().getColumn(13).setCellRenderer(centerRenderer);
		stockTable.getColumnModel().getColumn(13).setMaxWidth(1000);
		stockTable.getColumnModel().getColumn(13).setMinWidth(50);
		stockTable.getColumnModel().getColumn(13).setPreferredWidth(70);
		
		stockTable.getColumnModel().getColumn(14).setCellRenderer(centerRenderer);
		stockTable.getColumnModel().getColumn(14).setMaxWidth(1000);
		stockTable.getColumnModel().getColumn(14).setMinWidth(50);
		stockTable.getColumnModel().getColumn(14).setPreferredWidth(70);
		
		stockTable.getColumnModel().getColumn(15).setCellRenderer(centerRenderer);
		stockTable.getColumnModel().getColumn(15).setMaxWidth(1000);
		stockTable.getColumnModel().getColumn(15).setMinWidth(30);
		stockTable.getColumnModel().getColumn(15).setPreferredWidth(30);
		
	}

	public static JTable buildTableModel(ResultSet rs)
	        throws SQLException {
		
	    ResultSetMetaData metaData = rs.getMetaData();

	    // names of columns
	    Vector<String> columnNames = new Vector<String>();
	    
	    
	    int columnCount = metaData.getColumnCount();
	    
	    columnNames.add("Select");
	    columnNames.add("No.");//adding No. column
	    
	    for (int column = 1; column <= columnCount; column++) { //<= original
	        columnNames.add(metaData.getColumnName(column));
	    }	   
	    
	    // data of the table
	    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	    
	    int no = 1;
	    while (rs.next()) {
	        Vector<Object> vector = new Vector<Object>();
	       
	        vector.add(Boolean.FALSE);
	        vector.add(no++); // no.added to first column no. and variable no is increment
	        
	        
	        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) { // <= original
	            vector.add(rs.getObject(columnIndex));
	        }
	       	data.add(vector);
	    }
	    
	    DefaultTableModel model = new DefaultTableModel(data, columnNames) {

			
			@Override
			public boolean isCellEditable(int row, int column) {
			
				return column == 0 || column == 1 || column == 2 || column == 6 || column == 7 || column == 8 || column == 9 || column == 10 || column == 12 || column == 13 || column == 14 ? false : true;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				Class retVal = Object.class;

		        if(getRowCount() > 0)
		            retVal =  getValueAt(0, columnIndex).getClass();

		        return retVal;
			}
	    };
//	    model.setDataVector(data, columnNames);
	    JTable table = new JTable(model);
	    table.getTableHeader().setReorderingAllowed(false);
	    return table;
	}
	
	public static JTable updateDBfromTableCell(JTable table, String databaseTableName, TableModelEvent e) {
		
		String tableName = databaseTableName;
		int row = e.getFirstRow();
		int col = e.getColumn();
		if(col != 0) {
			String data = String.valueOf(table.getValueAt(row, col));	
			int rowToDB = row + 1;
			int colToDB = col + 1;
			String primaryColumnValue = String.valueOf(table.getValueAt(row, 2));
			String columnName = table.getColumnName(col);
			String primaryColumnName = table.getColumnName(2);
			
			if(columnName.equals("purchase price (PLN)") || columnName.equals("markup (%)")) {
				if(data.equals("")) {
					return table;
				}
				
				if(!(data.matches("^\\d+$") || data.matches("^(\\d+).(\\d+)$"))) {
					JOptionPane.showMessageDialog(table, "Wrong number. Please use one of this number format:  '0' or '0.0'." , "Wrong number", JOptionPane.WARNING_MESSAGE);
					table.setValueAt(0, row, col);
					return table;
				}
			}
				
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
				return table;
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
		}
		return table;
			
		
	}
	
	protected static void updateSellCell(JTable table, TableModelEvent e) {
		
		int row = e.getFirstRow();
		int col = e.getColumn();
		String data = String.valueOf(table.getValueAt(row, col));
			
		if((data.matches("^\\d+$") || data.matches("^(\\d+).(\\d+)$"))) {
			
			try {
				double purchasePrice = Double.parseDouble(String.valueOf(table.getValueAt(row, 10)));
				double markup = Double.parseDouble(String.valueOf(table.getValueAt(row, 11)));
				double result = purchasePrice + (purchasePrice*markup/100);
				result = result*100;
				result = Math.round(result);
				result = result/100;
				String stringResult = String.valueOf(result);
				table.setValueAt(result, row, 12);
				
			} catch (NumberFormatException e1) {
				JOptionPane.showMessageDialog(table, "Wrong number. Please use one of this number format:  '0' or '0.0'." , "Wrong number", JOptionPane.WARNING_MESSAGE);
				table.setValueAt(0, row, col);
//				e1.printStackTrace();
			}
			
		}else {
			
			JOptionPane.showMessageDialog(table, "Wrong number. Please use one of this number format:  '0' or '0.0'." , "Wrong number", JOptionPane.WARNING_MESSAGE);
			table.setValueAt(0, row, col);
		}
	}
	
	public static void main(String[] args) {
		
	
		
		
		System.out.println("Before Runnable");
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
//				TreeMap<String,String> map = new TreeMap<String, String>();
//				map.put("name", "Pawe�");
//				map.put("lastName", "D�browski");
//				map.put("id", "4");
//				map.put("login", "d");
//				map.put("YWN", "20205999");
//				map.put("password", "d");
//				map.put("access", "admin");
//				
//				Menu menu = new Menu(map);
				
				new StockForOrder2();
			}
		});
		System.out.println("After Runnable");
	}
	
}
