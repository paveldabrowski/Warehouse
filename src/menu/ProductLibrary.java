package menu;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Formatter;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.mysql.jdbc.Connection;

import db.DB;

public class ProductLibrary {
	
	
	
	protected JTable table;
	
	private String tableName = "productlibrary";
	
	
	public ProductLibrary() {

		table = getDataFromDBLibrary2(tableName);
	
	}
	
	protected JTable getDataFromDBLibrary(String tableName) {
		try {
			Connection conn = DB.getConnection();
			Formatter format = new Formatter(); format.format("SELECT * FROM %s", tableName); 
			String formattedQuerry = format.toString();
			PreparedStatement statement = conn.prepareStatement(formattedQuerry);
			ResultSet result = statement.executeQuery();
			
			if(table != null) {
	//			System.out.println("robię null");
				table = null;
			}
			table = new JTable(DB.buildTableModel(result)) {
				
				@Override
				public boolean isCellEditable(int row, int column) {
				
					return column == 0 || column == 1 || column == 6 || column == 7 || column == 11 ? false : true;
				}					
			};
		
			table.setAutoCreateRowSorter(true);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.getTableHeader().setReorderingAllowed(false);
			
			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(JLabel.CENTER);
			
			table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
			table.getColumnModel().getColumn(0).setMaxWidth(100);
			table.getColumnModel().getColumn(0).setMinWidth(50);
			table.getColumnModel().getColumn(0).setPreferredWidth(50);
			
			table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
			table.getColumnModel().getColumn(1).setMaxWidth(1000);
			table.getColumnModel().getColumn(1).setMinWidth(50);
			table.getColumnModel().getColumn(1).setPreferredWidth(70);

			
			for(int i = 0; i < table.getColumnCount(); i++) {
				table.getColumnModel().getColumn(i).sizeWidthToFit();
			}
			
			
			
		    conn.close();
		    format.close();
		    
		    return table;
			
	
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return table;	
	
	
	
	}
	
	protected JTable getDataFromDBLibrary2(String tableName) {
		try {
			Connection conn = DB.getConnection();
//			Formatter format = new Formatter(); format.format("SELECT name, manufacturer, `country of production`, weight, unit, `volume (dm3)`, barcode, `purchase price`, `markup (%)`, `selling price(no tax)`, amount, reserved FROM %s", tableName); 
//			String formattedQuerry = format.toString();
//			PreparedStatement statement = conn.prepareStatement(formattedQuerry);
			PreparedStatement statement = conn.prepareStatement("SELECT id, name, manufacturer, `country of production`, weight, unit, `volume (dm3)`, barcode, vat FROM productlibrary");
//			statement.setString(1, "productlibrary");
			ResultSet result = statement.executeQuery();
			
			if(table != null) {
	//			System.out.println("robię null");
				table = null;
			}
			table = new JTable(DB.buildTableModel(result)) {
				
				@Override
				public boolean isCellEditable(int row, int column) {
				
					return column == 0 || column == 1 || column == 6 || column == 7 || column == 11 ? false : true;
				}
					
			};
		
			table.setAutoCreateRowSorter(true);
			table.getTableHeader().setReorderingAllowed(false);
			
//			table.getModel().addTableModelListener(new TableModelListener() {
//				
//				@Override
//				public void tableChanged(TableModelEvent e) { 
//			
//						table = DB.updateDBfromTableCell(table, "productlibrary", e);
//					}
//					
//				}
//			);
			
			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(JLabel.CENTER);
			
			table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
			table.getColumnModel().getColumn(0).setMaxWidth(100);
			table.getColumnModel().getColumn(0).setMinWidth(50);
			table.getColumnModel().getColumn(0).setPreferredWidth(50);
			
			table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
			table.getColumnModel().getColumn(1).setMaxWidth(1000);
			table.getColumnModel().getColumn(1).setMinWidth(50);
			table.getColumnModel().getColumn(1).setPreferredWidth(70);
			
			table.getColumnModel().getColumn(9).setCellRenderer(centerRenderer);
			table.getColumnModel().getColumn(9).setMaxWidth(1000);
			table.getColumnModel().getColumn(9).setMinWidth(50);
			table.getColumnModel().getColumn(9).setPreferredWidth(70);

			
			for(int i = 0; i < table.getColumnCount(); i++) {
				table.getColumnModel().getColumn(i).sizeWidthToFit();
			}
			
			
			
		    conn.close();
//		    format.close();
		    
		    return table;
			
	
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return table;	
	
	
	
	}
}
