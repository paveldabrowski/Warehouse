package szablon;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import dbConnectionAndMethods.DB;
import menu.Menu;
import user.User;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.TreeMap;
import java.util.Vector;

public class Main extends JPanel {
	
	private JPanel panel;
	private JPanel panelNorth;
	protected User user;
	
	private JTable tableNorth;
	private JTable tableSouth;
	private JScrollPane scrollNorth;

	public Main() {
		
		initComponents();
	}
	
	public Main(User user) {
		this.user = user;
		
		initComponents();
	}
		
	private void initComponents() {
		panel = this;
		panel.setLayout(new GridLayout(2,1,0,0));
//		beginCompletion.setForeground(Color.GREEN); 			 beginCompletion.setFont(new Font("Tahoma", Font.PLAIN, 13));
		initializeNorthPanel();
	}
	
	private void initializeNorthPanel() {
		panelNorth = new JPanel();
		panelNorth.setLayout(new BorderLayout());
		
		tableNorth = createNorthTable();
		scrollNorth = new JScrollPane(tableNorth);
		
		panelNorth.add(scrollNorth, BorderLayout.CENTER);
		
		panel.add(panelNorth);
				
	}

	private JTable createNorthTable() {
		JTable tableNorth = new JTable();
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("no.");
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		try(Connection conn = DB.getConnection()){
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM roadCards");
			ResultSet rs = statement.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			for(int i = 1; i <= metaData.getColumnCount(); i++) {
				columnNames.add(metaData.getColumnName(i));
			}
			int j = 1;
			while (rs.next()) {
				Vector<Object> vector = new Vector<Object>();
				vector.add(j++);
				for(int i = 1; i <= metaData.getColumnCount(); i++) {
					vector.add(rs.getObject(i));
				}
				data.add(vector);
			}
			
			DefaultTableModel model = new DefaultTableModel(data, columnNames) {
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					Class retVal = Object.class;

			        if(getRowCount() > 0)
			            retVal =  getValueAt(0, columnIndex).getClass();

			        return retVal;
				}
			};
			
			tableNorth = new JTable(model);
			
			tableNorth.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
			tableNorth.setAutoCreateRowSorter(true);
			tableNorth.getTableHeader().setReorderingAllowed(false);			
			
			DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
			DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
			cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
			leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
			
			int no = tableNorth.getColumnModel().getColumnIndex("no.");			
			
			tableNorth.getColumnModel().getColumn(no).setMaxWidth(40);
			tableNorth.getColumnModel().getColumn(no).setCellRenderer(cellRenderer);
			
			return tableNorth;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNorth;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TreeMap<String,String> map = new TreeMap<String, String>();
					map.put("name", "Pawe³");
					map.put("lastName", "D¹browski");
					map.put("id", "4");
					map.put("login", "d");
					map.put("YWN", "20205999");
					map.put("password", "d");
					map.put("access", "admin");
					
					Menu menu = new Menu(map);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
}
