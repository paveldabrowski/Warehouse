package menu;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.BasicConfigurator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import dbConnectionAndMethods.DB;
import menu.Menu;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public class ManageDocuments extends JPanel {
	
	private JPanel panel;
	private JPanel panelNorth;
	private JPanel panelSouth;
	
	private JTable tableNorth;
	private JTable tableSouth;
	
	private JScrollPane scrollNorth;

	private JButton deleteOrder = new JButton("Delete order");
	private JButton showOrder = new JButton("Show order");
	private JButton print = new JButton("Print invoice");
	private JButton prepareInvoices = new JButton("Prepare invoices");
	
	private JMenuBar menuBarNorth;
	protected JScrollPane scrollSouth;
	protected String whatOrder;
	protected String whatClient;
	
	private JLabel clientLabel = new JLabel();
	private JLabel orderLabel = new JLabel();

	public ManageDocuments() {
		panel = this;
		panel.setLayout(new GridLayout(2,1,0,0));
		deleteOrder.setForeground(Color.GREEN); 			 deleteOrder.setFont(new Font("Tahoma", Font.PLAIN, 13));
		deleteOrder.setIcon(new ImageIcon("img/Delete.png"));
		showOrder.setForeground(Color.GREEN); 			 showOrder.setFont(new Font("Tahoma", Font.PLAIN, 13));
		showOrder.setIcon(new ImageIcon("img/Down.png"));
		print.setForeground(Color.GREEN); 			 print.setFont(new Font("Tahoma", Font.PLAIN, 13));
		print.setIcon(new ImageIcon("img/Print.png"));
		prepareInvoices.setForeground(Color.GREEN); 			 prepareInvoices.setFont(new Font("Tahoma", Font.PLAIN, 13));
		prepareInvoices.setIcon(new ImageIcon("img/Accounting.png"));
		
		
		initComponents();
	}	
	
	private void initComponents() {
		initializeNorthPanel();
		actionsForComponents();
	}

	private void initializeNorthPanel() {
		panelNorth = new JPanel();
		panelNorth.setLayout(new BorderLayout());
						
		createNorthTable();
		
		menuBarNorth = new JMenuBar();
		menuBarNorth.add(showOrder);
//		menuBarNorth.add(deleteOrder);
		menuBarNorth.add(prepareInvoices);
		menuBarNorth.add(print);
		menuBarNorth.add(clientLabel);
		menuBarNorth.add(orderLabel);
		
		scrollNorth = new JScrollPane(tableNorth);
		
		panelNorth.add(scrollNorth, BorderLayout.CENTER);
		panelNorth.add(menuBarNorth, BorderLayout.SOUTH);
		
		panel.add(panelNorth);
	}
	
	private void actionsForComponents() {
		
		print.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				printInvoiceMethod();
			}
		});
		
		prepareInvoices.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				prepareInvoicesMethod();
				
			}
		});
				
		showOrder.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				showOrderMethod();				
			}
		});
	}

	protected void printInvoiceMethod() {
		
		int selectedRow = tableNorth.getSelectedRow();
		int status = tableNorth.getColumnModel().getColumnIndex("status");
		int invoice = tableNorth.getColumnModel().getColumnIndex("invoice");
		if(selectedRow != -1) {
			boolean statusBoolean = (boolean) tableNorth.getValueAt(selectedRow, status);
			boolean invoiceBoolean = (boolean) tableNorth.getValueAt(selectedRow, invoice);
 			if(statusBoolean && invoiceBoolean) {
				try {
					Connection conn = DB.getConnection();
					PreparedStatement statement = conn.prepareStatement("TRUNCATE TABLE invoice");
					statement.executeUpdate();
					statement = conn.prepareStatement("TRUNCATE TABLE dataToInvoice");
					statement.executeUpdate();
					
					PreparedStatement statement2 = conn.prepareStatement("INSERT INTO invoice (`Name of product`, `Product id`, Quantity, Unit, `Net price`, `Net value`, `VAT %`, `VAT value`, `Gross value`)" 
					+ "SELECT CONCAT(l.name, ' ', l.manufacturer, ' ', l.`country of production`), p.product_id, p.amount_completed, l.unit, p.sell_price, p.row_price, l.vat, p.vat_value, p.row_gross_price FROM productlist p JOIN productlibrary l ON p.product_id = l.id WHERE order_id = ?");
					statement2.setString(1, whatOrder);
					statement2.executeUpdate();
					
					PreparedStatement statement3 = conn.prepareStatement("SELECT id FROM clients WHERE name = ?");
					statement3.setString(1, whatClient);
					ResultSet rs = statement3.executeQuery();
					
					int clientID = 0;
					while(rs.next()) {
						clientID = rs.getInt("id");
					}
					
					PreparedStatement statement4 = conn.prepareStatement("INSERT INTO dataToInvoice (order_id, client_id, date, amount_of_products, value, gross_value, vat_value, invoice_number)"
							+ " SELECT order_id, client_id, date, amount_of_products, value, gross_value, vat_value, invoice_number FROM orders WHERE client_id = ? AND order_id = ?");
					statement4.setString(1, String.valueOf(clientID));
					statement4.setString(2, String.valueOf(whatOrder));
					statement4.executeUpdate();
										
					BasicConfigurator.configure();
//			
					JasperReport jasperReport = null;
					
					String sourceFileName = "src/invoice/Invoice3.jrxml";
				    jasperReport = JasperCompileManager.compileReport(sourceFileName);			
//					String report = JasperCompileManager.compileReportToFile("src/invoice/Invoice2.jrxml");
					
					
					JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, conn);
					JasperPrintManager.printReport(jasperPrint, true);
					
					conn.close();
				} catch (Exception e) {				
					e.printStackTrace();
				}
 			} else {
 				JOptionPane.showMessageDialog(panel, "Order isn't completed or invoice isn't prepared.", "Wrong status", JOptionPane.ERROR_MESSAGE);
 			}			
		} else {
			JOptionPane.showMessageDialog(panel, "Select order to print invoice.", "Order not selected", JOptionPane.WARNING_MESSAGE);
		}		
	}

	protected void prepareInvoicesMethod() {
		boolean flag = true;
		int status = tableNorth.getColumnModel().getColumnIndex("status");
		int invoice = tableNorth.getColumnModel().getColumnIndex("invoice");		
		
		for(int i = 0; i < tableNorth.getRowCount(); i++) {
			if((boolean) tableNorth.getValueAt(i, status) == Boolean.TRUE) {
				
				if((boolean) tableNorth.getValueAt(i, invoice) == Boolean.FALSE) {
					String orderId = String.valueOf(tableNorth.getValueAt(i, 2));
					
					double orderGrossValue = 0;	
					double invoiceVatValue = 0;
					double weight = 0;
					String invoiceNumber = invoiceNumberGenerator();
					
					try {
						Connection conn = DB.getConnection();
						PreparedStatement statement = conn.prepareStatement("SELECT p.product_id, p.sell_price, p.amount_completed, l.vat, l.weight FROM productlist p JOIN productlibrary l ON p.product_id = l.id WHERE p.order_id = ? ");
						statement.setString(1, orderId);
						ResultSet rs = statement.executeQuery();		
						
						while(rs.next()) {
							
							System.out.println("id: " + rs.getInt("product_id") + "| sell_price: " + rs.getDouble("sell_price") + "| amount_completed: "  
									+ rs.getInt("amount_completed") + "| pr_vat: " + rs.getInt("vat"));
							
							int productId = rs.getInt("product_id");
							double sellPrice = rs.getDouble("sell_price");
							int amountCompleted = rs.getInt("amount_completed");
							int vat = rs.getInt("vat");
							double productWeight = rs.getDouble("weight");
							
							double netRowPrice = sellPrice * amountCompleted; 		netRowPrice = Math.round(netRowPrice * 100.0) / 100.0;
							double grossPrice = sellPrice * (100 + vat) / 100;		grossPrice = Math.round(grossPrice * 100.0) / 100.0;
							double grossRowPrice = amountCompleted * grossPrice;	grossRowPrice = Math.round(grossRowPrice * 100.0) / 100.0;	
							orderGrossValue += grossRowPrice;
							double vatValue = grossRowPrice - netRowPrice;			vatValue = Math.round(vatValue * 100.0) / 100.0;
							invoiceVatValue += vatValue;
							double rowWeight = productWeight * amountCompleted;		rowWeight = Math.round(rowWeight * 100.0) / 100.0;
							weight += rowWeight;						
							
							PreparedStatement statement2 = conn.prepareStatement("UPDATE productlist SET gross_price = ?, row_price = ?, row_gross_price = ?, vat_value = ? WHERE order_id = ? AND product_id = ?");
							statement2.setString(1, String.valueOf(grossPrice));
							statement2.setString(2, String.valueOf(netRowPrice));
							statement2.setString(3, String.valueOf(grossRowPrice));
							statement2.setString(4, String.valueOf(vatValue));
							statement2.setString(5, orderId);
							statement2.setString(6, String.valueOf(productId));
							statement2.executeUpdate();
							
							System.out.println("Product list updated");			
							
							PreparedStatement statement3 = conn.prepareStatement("SELECT reserved FROM productlibrary WHERE id = ?");
							statement3.setString(1, String.valueOf(productId));
							ResultSet rs2 = statement3.executeQuery();
							
							double reservedFromDB = 0;
							while (rs2.next()) {
								reservedFromDB = rs2.getDouble("reserved");
							}
							
							reservedFromDB = reservedFromDB - amountCompleted;
							reservedFromDB = Math.round(reservedFromDB * 100.0) / 100.0;
							
							PreparedStatement statement4 = conn.prepareStatement("UPDATE productlibrary SET reserved = ? WHERE id = ?");
							statement4.setString(1, String.valueOf(reservedFromDB));
							statement4.setString(2, String.valueOf(productId));
							statement4.executeUpdate();
							
							System.out.println("Reserved updated in product library");
						}
						
						
						orderGrossValue = Math.round(orderGrossValue * 100.0) / 100.0;
						invoiceVatValue = Math.round(invoiceVatValue * 100.0) / 100.0;						
						weight = Math.round(weight * 100.0) / 100.0;						
						
						PreparedStatement statement5 = conn.prepareStatement("SELECT amount_ordered, amount_completed, gross_price FROM productlist WHERE order_id = ?");
						statement5.setString(1, orderId);
						ResultSet rs2 = statement5.executeQuery();
						
						double valueOrdered = 0;
						double valueCompleted = 0;
						
						while(rs2.next()) {
							valueOrdered += rs2.getDouble("amount_ordered") * rs2.getDouble("gross_price");
							valueCompleted += rs2.getDouble("amount_completed") * rs2.getDouble("gross_price");
						}
						double percentComplete = (valueCompleted * 100) / valueOrdered;
										
						percentComplete = Math.round(percentComplete * 100.0) / 100.0; 
						System.out.println("ord: " + valueOrdered);
						System.out.println("com: " + valueCompleted);
						System.out.println("perc: :" + percentComplete);													
						
						PreparedStatement statement2 = conn.prepareStatement("UPDATE orders SET gross_value = ?, invoice_number = ?, vat_value = ?, invoice = true, percent_complete = ?, weight = ? WHERE order_id = ?");
						statement2.setString(1, String.valueOf(orderGrossValue));
						statement2.setString(2, String.valueOf(invoiceNumber));
						statement2.setString(3, String.valueOf(invoiceVatValue));
						statement2.setString(4, String.valueOf(percentComplete));
						statement2.setString(5, String.valueOf(weight));
						
						statement2.setString(6, orderId);
						statement2.executeUpdate();
						
						System.out.println("Orders updated");								
						conn.close();
						
						refreshTableNorth();
						
					} catch (Exception e1) {					
						e1.printStackTrace();
					}
					
					
				} else {
					System.out.println("invoice already prepared");
				}
			} else {
				flag = false;
				System.out.println("order not completed");
			}
				
		}
		if(flag == false) {
			JOptionPane.showMessageDialog(panel, "One or more orders aren't complete. Prepare invoice isn't possible.", "Order not completed", JOptionPane.WARNING_MESSAGE);
		}
	}	

	protected void refreshTableNorth() {
		
		try {
			panelNorth.remove(scrollNorth);
		} catch (Exception e) {		}
		
		createNorthTable();
		
		scrollNorth = new JScrollPane(tableNorth);
		panelNorth.add(scrollNorth, BorderLayout.CENTER);		
	}

	protected String invoiceNumberGenerator() {
		Random numberGenerator = new Random();
		int low2 = 100000000; int high2 = 999999999;
		int generatedNumber = numberGenerator.nextInt(high2-low2) + low2;
		
		Date date = new Date();
		
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate2 = formatter2.format(date);
		
		String generatedInvoiceNumber = "F" + generatedNumber + "/" + formattedDate2;
		
		ArrayList<String> listOfOrders = new ArrayList<String>();
		try {
			Connection conn = DB.getConnection();
			PreparedStatement statement = conn.prepareStatement("select invoice_number from orders");
			ResultSet rs = statement.executeQuery();
			while(rs.next())
				listOfOrders.add(rs.getString("invoice_number"));
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		if(!(listOfOrders.contains(generatedInvoiceNumber))) {
			return generatedInvoiceNumber;
		} else {
			System.out.println("Generated invoice number already exists, choosing another one");
			invoiceNumberGenerator();
		}
					
		return generatedInvoiceNumber;
	}

	protected void showOrderMethod() {
		int selectedRow = tableNorth.getSelectedRow();
		if(selectedRow != -1) {
			try {
				panel.remove(panelSouth);
			} catch (Exception e1) {}
			
			panelSouth = new JPanel();
			panelSouth.setLayout(new BorderLayout());
							
			Vector<Vector<Object>> orderVector = new Vector<Vector<Object>>();
			Vector<String> columnNames = new Vector<String>();
			columnNames.add("no.");
			try {
				Connection conn = DB.getConnection();
				PreparedStatement statement = conn.prepareStatement("SELECT lib.id, lib.name, lib.manufacturer, lib.`country of production`, lib.weight, lib.unit, lib.`volume (dm3)`, "
						+ "lib.barcode, p.purchase_price, p.`markup(%)`, p.sell_price, p.amount_ordered, p.amount_completed FROM productlist p JOIN productlibrary lib ON p.product_id=lib.id WHERE order_id=?");
				
				statement.setString(1, whatOrder);
				ResultSet rs = statement.executeQuery();
				ResultSetMetaData metaData = rs.getMetaData();				
				
				int rsColumnCount = metaData.getColumnCount();
				
				for(int i = 1; i <= rsColumnCount; i++) {
					
					columnNames.add(metaData.getColumnName(i));
				}								
				
				int no = 1;
				while(rs.next()) {
					Vector<Object> vector = new Vector<Object>();
					vector.add(no++);
					for(int i = 1; i <= rsColumnCount; i++) {
						vector.add(rs.getObject(i));
					}
					orderVector.add(vector);
				}
				conn.close();
			} catch (Exception e2) {				
				e2.printStackTrace();
			}		
			
			DefaultTableModel defaultTableModel = new DefaultTableModel(orderVector, columnNames){
				@Override
				public boolean isCellEditable(int row, int column) {
					
					return false;				
				}
				
				@Override
				public Class<?> getColumnClass(int column) {
					Class retVal = Object.class;
	
			        if(getRowCount() > 0)
			            retVal =  getValueAt(0, column).getClass();
	
			        return retVal;
				}			
			};		
			
			tableSouth = new JTable(defaultTableModel) ;
			tableSouth.getTableHeader().setReorderingAllowed(false);
			tableSouth.setAutoCreateRowSorter(true);
			tableSouth.setEnabled(true);
			tableSouth.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
			
			int no = tableSouth.getColumnModel().getColumnIndex("no.");
			int id = tableSouth.getColumnModel().getColumnIndex("id");
			int unit = tableSouth.getColumnModel().getColumnIndex("unit");
			int name = tableSouth.getColumnModel().getColumnIndex("name");			
			int weight = tableSouth.getColumnModel().getColumnIndex("weight");			
			int amountCompleted = tableSouth.getColumnModel().getColumnIndex("amount_completed");			
						
			tableSouth.getColumnModel().getColumn(no).setMaxWidth(40);
			tableSouth.getColumnModel().getColumn(no).setCellRenderer(centerRenderer);
			tableSouth.getColumnModel().getColumn(id).setMaxWidth(40);
			tableSouth.getColumnModel().getColumn(id).setCellRenderer(centerRenderer);
			tableSouth.getColumnModel().getColumn(unit).setMaxWidth(50);
			tableSouth.getColumnModel().getColumn(unit).setCellRenderer(centerRenderer);
			tableSouth.getColumnModel().getColumn(name).setPreferredWidth(190);
			tableSouth.getColumnModel().getColumn(weight).setMaxWidth(50);
			tableSouth.getColumnModel().getColumn(amountCompleted).setPreferredWidth(90);
			
			clientLabel.setText(" client: " + whatClient);
			orderLabel.setText(" | order: " + whatOrder);
			
			scrollSouth = new JScrollPane(tableSouth);
			panelSouth.add(scrollSouth, BorderLayout.CENTER);
			
			panel.add(panelSouth);
		} else {
			JOptionPane.showMessageDialog(panel, "You must select order which you want to inspect.", "Order not selected", JOptionPane.WARNING_MESSAGE);
		}
	}

	private void createNorthTable() {
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<String> columnNamesNorth = new Vector<String>();
		columnNamesNorth.add("no.");
		
		try {
			Connection conn = DB.getConnection();
			PreparedStatement statement = conn.prepareStatement("SELECT c.name, o.order_id, o.date, o.volume, o.pcs, o.amount_of_products, o.value, o.gross_value, o.percent_complete, o.invoice_number, o.status, o.invoice"
					+ " FROM orders o JOIN clients c ON o.client_id = c.id");
			ResultSet rs = statement.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			for(int i = 1; i <= metaData.getColumnCount(); i++) {
				columnNamesNorth.add(metaData.getColumnName(i));				
			}			
			int k = 1;
			while(rs.next()) {
				Vector<Object> vector = new Vector<Object>();
				vector.add(k++);
				vector.add(rs.getString("name"));
				vector.add(rs.getString("order_id"));
				vector.add(rs.getDate("date"));
				vector.add(rs.getInt("volume"));
				vector.add(rs.getInt("pcs"));
				vector.add(rs.getInt("amount_of_products"));
				vector.add(Math.round(rs.getDouble("value") * 100.0) / 100.0);		
				vector.add(Math.round(rs.getDouble("gross_value") * 100.0) / 100.0);					
				vector.add(rs.getDouble("percent_complete"));
				vector.add(rs.getString("invoice_number"));				
				vector.add(rs.getBoolean("status"));
				vector.add(rs.getBoolean("invoice"));
											
				data.add(vector);
			}						
			conn.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}		
				
		DefaultTableModel defaultTableModel = new DefaultTableModel(data, columnNamesNorth){
			
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
			
			@Override
			public Class<?> getColumnClass(int column) {
				Class retVal = Object.class;

		        if(getRowCount() > 0)
		            retVal =  getValueAt(0, column).getClass();

		        return retVal;
			}
		};
		
		tableNorth = new JTable(defaultTableModel);
		tableNorth.setAutoCreateRowSorter(true);
		tableNorth.getTableHeader().setReorderingAllowed(false);
		
		
		int pcs = tableNorth.getColumnModel().getColumnIndex("pcs");
		int status = tableNorth.getColumnModel().getColumnIndex("status");
		int invoice = tableNorth.getColumnModel().getColumnIndex("invoice");
		int orderID = tableNorth.getColumnModel().getColumnIndex("order_id");
		int invoiceNumber = tableNorth.getColumnModel().getColumnIndex("invoice_number");		
		
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
		cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		
		tableNorth.getColumnModel().getColumn(0).setMaxWidth(40);
		tableNorth.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);
		tableNorth.getColumnModel().getColumn(pcs).setMaxWidth(40);
		tableNorth.getColumnModel().getColumn(pcs).setCellRenderer(cellRenderer);;
		tableNorth.getColumnModel().getColumn(status).setMaxWidth(50);
		tableNorth.getColumnModel().getColumn(invoice).setMaxWidth(50);	
		tableNorth.getColumnModel().getColumn(orderID).setMaxWidth(120);	
		tableNorth.getColumnModel().getColumn(orderID).setPreferredWidth(120);
		tableNorth.getColumnModel().getColumn(orderID).setCellRenderer(cellRenderer);
		tableNorth.getColumnModel().getColumn(invoiceNumber).setMaxWidth(140);	
		tableNorth.getColumnModel().getColumn(invoiceNumber).setPreferredWidth(140);
		tableNorth.getColumnModel().getColumn(invoiceNumber).setCellRenderer(cellRenderer);
		
		
		tableNorth.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
		
		tableNorth.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedRow = tableNorth.getSelectedRow();
				if(selectedRow != -1) {
					whatOrder = String.valueOf(tableNorth.getValueAt(selectedRow, 2));
					whatClient = String.valueOf(tableNorth.getValueAt(selectedRow, 1));
				}
				
			}
		});
	}
	
	protected JTable getTableNorth() {
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
					map.put("access", "1");
					
					Menu menu = new Menu(map);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			
		});

	}
}
