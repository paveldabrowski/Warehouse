package menu;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.BasicConfigurator;

import db.DB;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import user.User;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.TreeMap;
import java.util.Vector;

public class ManageRoadCards extends JPanel {
	
	private JPanel panel;
	private JPanel panelNorth;
	private JPanel panelSouth;
	protected User user;
	
	private JTable tableNorth;
	private JTable tableSouth;
	private JScrollPane scrollNorth;
	
	private JButton showOrdersInRoadCard = new JButton("Show Road Card content");
	private JButton printRoadCard = new JButton("Print Road Card");
	private JMenuBar menuBarNorth;
	private JScrollPane scrollSouth;

	public ManageRoadCards() {
		
		initComponents();
	}
	
	public ManageRoadCards(User user) {
		this.user = user;
		
		initComponents();
	}
		
	private void initComponents() {
		panel = this;
		panel.setLayout(new GridLayout(2,1,0,0));
		showOrdersInRoadCard.setForeground(Color.RED); 			 showOrdersInRoadCard.setFont(new Font("Tahoma", Font.PLAIN, 13));
		showOrdersInRoadCard.setIcon(new ImageIcon("img/Down.png"));
		
		printRoadCard.setForeground(Color.RED); 			 printRoadCard.setFont(new Font("Tahoma", Font.PLAIN, 13));
		printRoadCard.setIcon(new ImageIcon("img/Print.png"));
		
		initializeNorthPanel();
		actionsForComponents();		
	}
	
	private void actionsForComponents() {
		
		printRoadCard.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try (Connection conn = DB.getConnection()){
					PreparedStatement statement = conn.prepareStatement("TRUNCATE TABLE roadCardToReport");
					statement.executeUpdate();
					
					int selectedRow = tableNorth.getSelectedRow();
					if (selectedRow != -1) {
//						selectedRow = tableNorth.convertRowIndexToModel(selectedRow);
						int roadCardNumberColumn = tableNorth.getColumnModel().getColumnIndex("card_id");
						String roadCardNumber = String.valueOf(tableNorth.getValueAt(selectedRow, roadCardNumberColumn));
						System.out.println("selected row on print: " + selectedRow + " " + roadCardNumber);
						
						PreparedStatement statement2 = conn.prepareStatement("INSERT INTO roadCardToReport (roadCard_number, user) VALUES (?,?)");
						statement2.setString(1, roadCardNumber);
						statement2.setString(2, user.getName() + " " + user.getLastName());
						statement2.executeUpdate();
						
						BasicConfigurator.configure();
//						
						JasperReport jasperReport = null;
						
						String sourceFileName = "src/roadcardreport/roadcardreport.jrxml";
					    jasperReport = JasperCompileManager.compileReport(sourceFileName);			
//								String report = JasperCompileManager.compileReportToFile("src/invoice/Invoice2.jrxml");
						
						
						JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, conn);
						JasperPrintManager.printReport(jasperPrint, true);
														
						
					} else {
						JOptionPane.showMessageDialog(panel, "Select road card to print.", "Road card not selected", JOptionPane.ERROR_MESSAGE);
					}					
					
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				
			}
		});
		
		showOrdersInRoadCard.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showOrdersInRoadCardMethod();			
			}
		});		
	}

	protected void showOrdersInRoadCardMethod() {
		
		int selectedRow = tableNorth.getSelectedRow();
		if(selectedRow != -1) {			
			
			try {
				panel.remove(panelSouth);
				panelSouth.remove(scrollSouth);
			} catch(Exception e) {
				
			}
			
			int cardIdColumn = tableNorth.getColumnModel().getColumnIndex("card_id");
			String cardNumber = String.valueOf(tableNorth.getValueAt(selectedRow, cardIdColumn));
			System.out.println(cardNumber);
			
			Vector<String> columnNames = new Vector<String>();
			columnNames.add("no.");
			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			
			try(Connection conn = DB.getConnection()){
				PreparedStatement statement = conn.prepareStatement("SELECT c.name, CONCAT(c.street, ' ', c.zip_code, ' ', c.city, ' ', c.country) as address, "
						+ "r.order_id, o.date, o.weight, o.volume, o.pcs, o.amount_of_products, o.value, o.gross_value, o.invoice_number FROM roadCardsOrders r"
						+ " JOIN orders o ON r.order_id = o.order_id JOIN clients c ON o.client_id = c.id WHERE r.card_id = ? ORDER BY o.date ASC");
				statement.setString(1, cardNumber);
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
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			DefaultTableModel model = new DefaultTableModel(data, columnNames) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
				
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					Class retVal = Object.class;

			        if(getRowCount() > 0)
			            retVal =  getValueAt(0, columnIndex).getClass();

			        return retVal;
				}
			};
			
			tableSouth = new JTable(model);
			
			tableSouth.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
			tableSouth.setAutoCreateRowSorter(true);
			tableSouth.getTableHeader().setReorderingAllowed(false);			
			
			DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();			
			cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);		
			
			int no = tableSouth.getColumnModel().getColumnIndex("no.");			
			int address = tableSouth.getColumnModel().getColumnIndex("address");			
			int invoiceNumber = tableSouth.getColumnModel().getColumnIndex("invoice_number");			
			
			tableSouth.getColumnModel().getColumn(no).setMaxWidth(40);
			tableSouth.getColumnModel().getColumn(no).setCellRenderer(cellRenderer);
			tableSouth.getColumnModel().getColumn(invoiceNumber).setPreferredWidth(100);
			tableSouth.getColumnModel().getColumn(invoiceNumber).setCellRenderer(cellRenderer);
			tableSouth.getColumnModel().getColumn(address).setPreferredWidth(190);
//			tableSouth.getColumnModel().getColumn(address).setCellRenderer(cellRenderer);
			
			panelSouth = new JPanel();
			panelSouth.setLayout(new BorderLayout());
			
			scrollSouth = new JScrollPane(tableSouth);
			panelSouth.add(scrollSouth, BorderLayout.CENTER);
			
			panel.add(panelSouth);
			
			
		} else {
			JOptionPane.showMessageDialog(panel, "Select road card to inspect what is inside.", "Road card not selected", JOptionPane.WARNING_MESSAGE);
		}			
	}

	protected void initializeSouthPanel() {
		
	}

	private void initializeNorthPanel() {
		panelNorth = new JPanel();
		panelNorth.setLayout(new BorderLayout());
		
		tableNorth = createNorthTable();
		scrollNorth = new JScrollPane(tableNorth);
		
		menuBarNorth = new JMenuBar();
		menuBarNorth.add(showOrdersInRoadCard);
		menuBarNorth.add(printRoadCard);
		
		panelNorth.add(scrollNorth, BorderLayout.CENTER);
		panelNorth.add(menuBarNorth, BorderLayout.SOUTH);
		
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
				public boolean isCellEditable(int row, int column) {
					return false;
				}
				
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
					map.put("name", "Pawe�");
					map.put("lastName", "D�browski");
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
