package menu;

import com.google.gson.internal.LinkedTreeMap;
import dbConnectionAndMethods.DB;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.sql.*;
import java.util.*;

public class ManageOrders extends JPanel{

	protected Connection conn; 
	private JPanel panel;
	private JPanel southPanel;
	private JPanel northPanel;
	private JPanel northPanelUp;
	private JPanel southPanelUp;
	private JPanel southPanelDown;
	private JPanel southPanelSouth;
		
	private JComboBox<String> clientCombobox = new JComboBox<String>();
	private JComboBox<String> productCombobox = new JComboBox<String>();
	
	private JMenuBar menuBar;	
	private JMenuBar menuBarSouth;
	
	private JLabel chooseClient = new JLabel("Choose client: ");
	private JLabel searchClientLabel = new JLabel(" or search: ");
	private JLabel info = new JLabel(" Only markup and ordered amount can be edited directly from the table below. ");
	private JLabel orderParameters = new JLabel();
	private JLabel chooseProduct = new JLabel(" Choose product: ");
	private JLabel chooseAmount = new JLabel(" Choose amount: ");
	
	
	private JTextField searchClient = new JTextField();
	private JTextField setAmount = new JTextField();
	
//	private JButton getOrders = new JButton("Get Orders");
	private JButton showOrder = new JButton("Show order");
	private JButton deleteOrder = new JButton("Delete order");
	private JButton refreshOrder = new JButton("Refresh");	
	private JButton printOrder = new JButton("Print");	
	private JButton printOrders = new JButton("Print");	
	private JButton removeProduct = new JButton("Remove product");	
	private JButton addProduct = new JButton("Add new product");	
	private JButton add = new JButton("Add");	
		
	private Vector<String> columnNamesNorth;
	private Vector<Vector<Object>> allOrders;
	private HashMap<String, Object> ordersParametersMap;
	protected TreeMap<String, LinkedTreeMap<String, Vector<Vector<Object>>>> clientsOrdersMap = new TreeMap<String, LinkedTreeMap<String, Vector<Vector<Object>>>>();
	
	private JTable tableNorth;
	protected JTable tableSouth;
	
	private JScrollPane scrollNorth;
	protected JScrollPane scrollSouth;		
	private double oldAmount;
	
	private String whatClient;
	protected String whatOrder;

	public ManageOrders() {
		panel = this;
		panel.setLayout(new GridLayout(2,1,0,0));
		panel.setPreferredSize(new Dimension(200,500));
//		GridLayout lauout = (GridLayout) panel.getLayout();
//		lauout.preferredLayoutSize(panel);
//		
//		panel.setLayout(lauout);
		initComponents();
		actionsForButtons();
		
	}
		
	private void initComponents() {
		
		initializeNorthPanel();
	
		printOrder.setForeground(Color.GREEN); 		 printOrder.setFont(new Font("Tahoma", Font.PLAIN, 13));
		printOrder.setIcon(new ImageIcon("img\\Print.png"));
		
		addProduct.setForeground(Color.GREEN); 		 addProduct.setFont(new Font("Tahoma", Font.PLAIN, 13));
		addProduct.setIcon(new ImageIcon("img\\Create.png"));
		
		removeProduct.setForeground(Color.GREEN); 		 removeProduct.setFont(new Font("Tahoma", Font.PLAIN, 13));
		removeProduct.setIcon(new ImageIcon("img\\erase.png"));
		
		add.setForeground(Color.BLACK); 		 add.setFont(new Font("Tahoma", Font.BOLD, 13));
		add.setIcon(new ImageIcon("img\\Apply.png"));
		
		setAmount.setPreferredSize(new Dimension(40, setAmount.getPreferredSize().height));		
		setAmount.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if(!(e.getKeyChar()>= '0' && e.getKeyChar() <= '9'))
					e.consume();
			}
		});
	}
	
	private void initializeNorthPanel() {
		northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout(5, 0));
		northPanelUp = new JPanel();
		northPanelUp.setLayout(new FlowLayout());
		FlowLayout flowLayout = (FlowLayout) northPanelUp.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		
//		getOrdersFromFile();
		
//		getOrders.setForeground(Color.BLACK); 		 	 getOrders.setFont(new Font("Tahoma", Font.PLAIN, 13));
		showOrder.setForeground(Color.GREEN); 			 showOrder.setFont(new Font("Tahoma", Font.PLAIN, 13));
		deleteOrder.setForeground(Color.GREEN); 		 deleteOrder.setFont(new Font("Tahoma", Font.PLAIN, 13));
		refreshOrder.setForeground(Color.GREEN); 		 refreshOrder.setFont(new Font("Tahoma", Font.PLAIN, 13));
		printOrders.setForeground(Color.GREEN); 		 printOrders.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		refreshOrder.setIcon(new ImageIcon("img\\refresh.png"));
		deleteOrder.setIcon(new ImageIcon("img\\erase.png"));
		showOrder.setIcon(new ImageIcon("img\\iconfinde.png"));
		printOrders.setIcon(new ImageIcon("img\\Print.png"));
						
		clientCombobox = getClientsFromDb();
		clientCombobox.addItem("");
		clientCombobox.setSelectedItem("");	
		
		
		clientCombobox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					DefaultTableModel defaultTableModel = (DefaultTableModel) tableNorth.getModel();
					TableRowSorter<DefaultTableModel> tr = new TableRowSorter<DefaultTableModel>(defaultTableModel);
					tableNorth.setRowSorter(tr);
					tr.setRowFilter(RowFilter.regexFilter(e.getItem().toString()));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});		
		
		createNorthTable();
				
		menuBar = new JMenuBar();
		menuBar.add(showOrder);
		menuBar.add(deleteOrder);
		menuBar.add(refreshOrder);
		menuBar.add(printOrders);
				
		scrollNorth = new JScrollPane(tableNorth);
		
		searchClient.setPreferredSize(new Dimension(100, 20));
				
		northPanelUp.add(chooseClient);
		northPanelUp.add(clientCombobox);
		northPanelUp.add(searchClientLabel);
		northPanelUp.add(searchClient);
		
//		northPanelUp.add(getOrders);		
		
		northPanel.add(scrollNorth,BorderLayout.CENTER);
		northPanel.add(northPanelUp, BorderLayout.NORTH);
		northPanel.add(menuBar, BorderLayout.SOUTH);
				
		panel.add(northPanel);		
		
	}
	
	private void createNorthTable() {
		
		allOrders = new Vector<Vector<Object>>();
		
		columnNamesNorth = new Vector<String>();
		columnNamesNorth.add("No.");
		columnNamesNorth.add("client");
		columnNamesNorth.add("order");
		columnNamesNorth.add("date");
		columnNamesNorth.add("volume");
		columnNamesNorth.add("pcs");
		columnNamesNorth.add("amount of products");
		columnNamesNorth.add("value (PLN)");
		
		int no = 1;
		
		try {
			Connection conn = DB.getConnection();
//			Formatter format = new Formatter();
//			format.format("select c.name, o.order_id, o.date, o.volume, o.pcs, o.amount_of_products, o.value from orders o join clients c on o.client_id = c.id", args);
			String querry = "SELECT c.name, o.order_id, o.date, o.volume, o.pcs, o.amount_of_products, o.value FROM orders o JOIN clients c on o.client_id = c.id WHERE status = 0";
			PreparedStatement statement = conn.prepareStatement(querry);
			ResultSet rs = statement.executeQuery();
			int columnCount = rs.getMetaData().getColumnCount();
			
			while(rs.next()) {
				Vector<Object> vector = new Vector<Object>();
				vector.add(no++);
				for(int i=1; i <= columnCount; i++ ) {
					vector.add(rs.getObject(i));
				}
				allOrders.add(vector);
			}
			conn.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		DefaultTableModel defaultTableModel = new DefaultTableModel(allOrders, columnNamesNorth) {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				Class retVal = Object.class;

		        if(getRowCount() > 0)
		            retVal =  getValueAt(0, columnIndex).getClass();

		        return retVal;
			}
		};
		
		tableNorth = new JTable(defaultTableModel) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}			
		};
		
		tableNorth.setAutoCreateRowSorter(true);
		tableNorth.getColumnModel().getColumn(0).setMaxWidth(40);
		tableNorth.getTableHeader().setReorderingAllowed(false);
		
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
		cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		tableNorth.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);
		
		tableNorth.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		tableNorth.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				
			}
		});
				
	}

	private void initializeSouthPanel(String whatOrder) {
		
		menuBarSouth = new JMenuBar();
		
		menuBarSouth.add(addProduct);
		menuBarSouth.add(removeProduct);
		menuBarSouth.add(printOrder);
		menuBarSouth.add(orderParameters);
			
		tableSouth = createSouthTable();
		scrollSouth = new JScrollPane(tableSouth);
		
		ordersParametersMap = countItemsAndValue();
		
		southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		
		southPanelUp = new JPanel();
		southPanelUp.setLayout(new FlowLayout());
		FlowLayout flowLayout = (FlowLayout) southPanelUp.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
				
		southPanelDown = new JPanel();
		southPanelDown.setLayout(new BorderLayout());
		southPanelDown.add(menuBarSouth, BorderLayout.WEST);
					
		southPanelSouth = new JPanel();
		southPanelSouth.setLayout(new BorderLayout());
		
//		southPanelSouth.add(southPanelUp, BorderLayout.NORTH);
		southPanelSouth.add(southPanelDown, BorderLayout.WEST);
				
		southPanel.add(southPanelSouth, BorderLayout.SOUTH);
		
		southPanel.add(scrollSouth, BorderLayout.CENTER);
		panel.add(southPanel);
		
		menuBar.add(info);			
	}

	protected JTable createSouthTable() {
		try {
			southPanel.removeAll();
			panel.remove(southPanel);
			} catch (NullPointerException e2) {
				
			}						
			
			Vector<Vector<Object>> orderVector = new Vector<Vector<Object>>();
			try {
				Connection conn = DB.getConnection();
				PreparedStatement statement = conn.prepareStatement("SELECT lib.id, lib.name, lib.manufacturer, lib.`country of production`, lib.weight, lib.unit, lib.`volume (dm3)`, "
						+ "lib.barcode, p.purchase_price, p.`markup(%)`, p.sell_price, p.amount_ordered FROM productlist p JOIN productlibrary lib ON p.product_id=lib.id WHERE order_id=?");
				
				statement.setString(1, whatOrder);
				ResultSet rs = statement.executeQuery();
				int rsColumnCount = rs.getMetaData().getColumnCount();
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
			} catch (Exception e) {				
				e.printStackTrace();
			}
			
			System.out.println(orderVector);
			Vector<String> columnNames = new CreateOrder().getColumnsNames();
			
			DefaultTableModel defaultTableModel = new DefaultTableModel(orderVector, columnNames){
				@Override
				public boolean isCellEditable(int row, int column) {
					
					return column <= 9 || column == 11 ? false : true;				
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
			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
//			tableSouth.setDefaultRenderer(Integer.class, centerRenderer);
	
			tableSouth.getColumnModel().getColumn(0).setMaxWidth(40);
			tableSouth.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
			tableSouth.getColumnModel().getColumn(1).setMaxWidth(40);
			tableSouth.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
			tableSouth.getColumnModel().getColumn(12).setMaxWidth(50);			
			tableSouth.getColumnModel().getColumn(2).setPreferredWidth(190);;			
			
			tableSouth.setEnabled(true);
			tableSouth.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			tableSouth.getModel().addTableModelListener(new TableModelListener() {
				
				@Override
				public void tableChanged(TableModelEvent e) {
					tableChangedMethod(e);
				}
			});
			
			tableSouth.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent e) {
					tableSelectionMethod(e);
					
				}
			});
			
			return tableSouth;
	}

	protected void tableChangedMethod(TableModelEvent e) {
		int col = e.getColumn();
		if(col == 10) {
			updateSellCell(tableSouth, e);
			ordersParametersMap = countItemsAndValue();
			try {
				int row = e.getFirstRow();
				double purchasePrice = Double.parseDouble(String.valueOf(tableSouth.getValueAt(row, 9)));
				double markup = Double.parseDouble(String.valueOf(tableSouth.getValueAt(row, 10)));
				double sellPrice = Double.parseDouble(String.valueOf(tableSouth.getValueAt(row, 11)));
				int product_id = (int) tableSouth.getValueAt(row, 1);
				
				Connection conn = DB.getConnection();
				PreparedStatement statement = conn.prepareStatement("UPDATE productlist SET purchase_price = ?, `markup(%)` = ?, sell_price = ? WHERE product_id = ? AND order_id = ?");
				statement.setString(1, String.valueOf(purchasePrice));
				statement.setString(2, String.valueOf(markup));
				statement.setString(3, String.valueOf(sellPrice));
				statement.setString(4, String.valueOf(product_id));
				statement.setString(5, String.valueOf(whatOrder));
				statement.executeUpdate();
				
				updateOrdersTableInDB(ordersParametersMap);				
								
				refreshNorthTable();
				
				conn.close();
				
			} catch (Exception e1) {
				
				e1.printStackTrace();
			}
		
		}else if(col == 12) {
			
			int row = e.getFirstRow();
			String dataFromCell = String.valueOf(tableSouth.getValueAt(row, col));
									
			if((dataFromCell.matches("^\\d+$") || dataFromCell.matches("^(\\d+).(\\d+)$"))) {
				HashMap<String, Double> dataToUpdate = new HashMap<String, Double>();
				
				double dataFromSellDouble = Double.parseDouble(dataFromCell);
				String id = String.valueOf(tableSouth.getValueAt(row, 1));
				dataToUpdate.put(id, dataFromSellDouble);
				dataFromCell = updateReservedInStock(dataToUpdate, e);
				System.out.println("Data from the cell: " + dataFromCell);
				
				ordersParametersMap = countItemsAndValue();
				
				try {
					Connection conn = DB.getConnection();
					PreparedStatement statement = conn.prepareStatement("UPDATE productlist SET amount_ordered = ? WHERE product_id = ? AND order_id = ?");
					statement.setString(1, dataFromCell);
					statement.setString(2, id);
					statement.setString(3, whatOrder);
					statement.executeUpdate();
					refreshSouthTable();
					ordersParametersMap = countItemsAndValue();
										
					PreparedStatement statement2 = conn.prepareStatement("UPDATE orders SET volume = ?, pcs = ?, amount_of_products = ?, value = ? WHERE order_id = ?");
					statement2.setString(1, String.valueOf(ordersParametersMap.get("volume")));
					statement2.setString(2, String.valueOf(ordersParametersMap.get("pcs")));
					statement2.setString(3, String.valueOf(ordersParametersMap.get("amount_of_products")));
					statement2.setString(4, String.valueOf(ordersParametersMap.get("value")));
					statement2.setString(5, whatOrder);
					statement2.executeUpdate();
					conn.close();			
					refreshNorthTable();
//					refreshSouthTable();
					
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}				
				
			} else {
				JOptionPane.showMessageDialog(panel, "Wrong number. Please use one of this number format:  '0' or '0.0'." , "Wrong number", JOptionPane.WARNING_MESSAGE);
				refreshSouthTable();
			}
		} else {
//			DB.updateDBfromTableCell(tableSouth, "productlibrary", e);
		}
		
	}

	protected void tableSelectionMethod(ListSelectionEvent e) {
		int row = tableSouth.getSelectedRow();
		if(row != -1) {
			oldAmount = Double.parseDouble(String.valueOf(tableSouth.getValueAt(row, 12)));
			System.out.println("Old amount = " + oldAmount);
		}
	}
	
	protected String updateReservedInStock(HashMap<String, Double> dataToUpdate, TableModelEvent e) {
		int row = e.getFirstRow();
		int col = e.getColumn();
		
		try {
			Connection conn = DB.getConnection();
			PreparedStatement statement = conn.prepareStatement("SELECT amount, reserved FROM productlibrary WHERE id = ?");
			Set<String> keySet = dataToUpdate.keySet();
			ArrayList<String> id = new ArrayList<String>(keySet); 
			System.out.println("Id from keySet: " + id);
			statement.setString(1, id.get(0));
			ResultSet rs = statement.executeQuery();
			
			double amountFromDb = 0;
			double reservedFromDb = 0;
			while(rs.next()) {
				amountFromDb = rs.getDouble(1);
				reservedFromDb = rs.getDouble(2);
			}
			double newReserved = dataToUpdate.get(id.get(0));
			if (newReserved > oldAmount) {
				System.out.println("New value is greater than old value");
				double difference = newReserved - oldAmount;
				if(difference <= amountFromDb) {
					System.out.println("There is enough amount of this product and change is possible.");
					double newAmountToDB = amountFromDb - difference;
					double newReservedToDB = reservedFromDb + difference;
					
					PreparedStatement statement2 = conn.prepareStatement("UPDATE productlibrary SET amount = ?, reserved = ? WHERE id = ?");
					statement2.setString(1, String.valueOf(newAmountToDB));
					statement2.setString(2, String.valueOf(newReservedToDB));
					statement2.setString(3, id.get(0));
					statement2.executeUpdate();
					System.out.println("Amount in stock and reserved updated.");				
					conn.close();
					return String.valueOf(newReserved);
					
				} else if (difference > amountFromDb && amountFromDb != 0) {
					JOptionPane.showMessageDialog(panel, "There is not enough amount of this product in stock. Set maximum availible.", "Not enough in stock", JOptionPane.WARNING_MESSAGE);
					double newAmountToDB = amountFromDb - amountFromDb;
					double newReservedToDB = reservedFromDb + amountFromDb;
					PreparedStatement statement2 = conn.prepareStatement("UPDATE productlibrary SET amount = ?, reserved = ? WHERE id = ?");
					statement2.setString(1, String.valueOf(newAmountToDB));
					statement2.setString(2, String.valueOf(newReservedToDB));
					statement2.setString(3, id.get(0));
					statement2.executeUpdate();
					System.out.println("Amount in stock and reserved updated with maximum from stock.");	
					conn.close(); 
//					refreshSouthTable();
					
					return String.valueOf(oldAmount + amountFromDb);
				} else if(difference > amountFromDb && amountFromDb == 0) {
					System.out.println("The stock for this product eqals 0");
					JOptionPane.showMessageDialog(panel, "The stock for this product eqals 0. Setting old value.", "Empty stock", JOptionPane.ERROR_MESSAGE);
					conn.close();
					tableSouth.setValueAt(oldAmount, row, 12);
					return String.valueOf(oldAmount);
				}
				
			} else if (newReserved < oldAmount){
				System.out.println("New value is smaller than old value");
				double difference = oldAmount - newReserved;
				double newAmountToDB = amountFromDb + difference;
				double newReservedToDB = reservedFromDb - difference;
				
				PreparedStatement statement2 = conn.prepareStatement("UPDATE productlibrary SET amount = ?, reserved = ? WHERE id = ?");
				statement2.setString(1, String.valueOf(newAmountToDB));
				statement2.setString(2, String.valueOf(newReservedToDB));
				statement2.setString(3, id.get(0));
				statement2.executeUpdate();
				System.out.println("Amount in stock and reserved updated.");
				conn.close();
				return String.valueOf(newReserved);
			}
				conn.close();
		} catch (Exception e2) {	
			e2.printStackTrace();
			return String.valueOf(oldAmount);
			
		}
		return String.valueOf(oldAmount);		
	}

	private void actionsForButtons() {
		
		add.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String item = (String) productCombobox.getSelectedItem();
				String data = setAmount.getText();
				String [] splitItem;
				String [] id;
				String productId = "";
				
				if(item.length() > 2 && data.length() > 0) {
					splitItem = item.split("\\|");
					id = splitItem[splitItem.length-1].split("\\s+");
					System.out.println(item.length() + " " + data.length());
					productId = id[2]; 			
				}
				
				System.out.println("Product id: " + productId);
				ArrayList<String> productIDList = new ArrayList<String>();
				for(int i = 0; i < tableSouth.getRowCount(); i++) {
					productIDList.add(String.valueOf(tableSouth.getValueAt(i, 1)));
				}
				System.out.println("array list: " + productIDList);
				
				if(!(productIDList.contains(productId.toString())) && item != "") {
					if((data.matches("^\\d+$") || data.matches("^(\\d+).(\\d+)$"))) {
						double amountOredered = Double.parseDouble(setAmount.getText());
						
						try {
							Connection conn = DB.getConnection();
							PreparedStatement statement = conn.prepareStatement("SELECT `purchase price (PLN)`, `markup (%)`, `selling price(no tax)`, amount, reserved FROM productlibrary WHERE id = ?");
							statement.setString(1, productId);
							ResultSet rs = statement.executeQuery();
							double purchasePrise = 0;
							double markup = 0;
							double sellPrise = 0;
							double amountFromStock = 0;
							double reserved = 0;
							while(rs.next()) {
								purchasePrise = rs.getDouble("purchase price (PLN)");
								markup = rs.getDouble("markup (%)");
								sellPrise = rs.getDouble("selling price(no tax)");
								amountFromStock = rs.getDouble("amount");
								reserved = rs.getDouble("reserved");
							}
							if(amountOredered <= amountFromStock) {
								PreparedStatement statement2 = conn.prepareStatement("INSERT INTO productlist (order_id, product_id, `purchase_price`, `markup(%)`, `sell_price`, amount_ordered) VALUES (?,?,?,?,?,?)");
								statement2.setString(1, whatOrder);
								statement2.setString(2, productId);
								statement2.setString(3, String.valueOf(purchasePrise));
								statement2.setString(4, String.valueOf(markup));
								statement2.setString(5, String.valueOf(sellPrise));
								statement2.setString(6, String.valueOf(amountOredered));								
								statement2.executeUpdate();
								System.out.println("Productlist updated");
								
								amountFromStock = amountFromStock - amountOredered;
								reserved = reserved + amountOredered;
								
								PreparedStatement statement3 = conn.prepareStatement("UPDATE productlibrary SET amount = ?, reserved = ? WHERE id = ?");
								statement3.setString(1, String.valueOf(amountFromStock));
								statement3.setString(2, String.valueOf(reserved));
								statement3.setString(3, String.valueOf(productId));
								statement3.executeUpdate();
								System.out.println("Reserved and stock updated");
								
								refreshSouthTable();
								ordersParametersMap = countItemsAndValue();
								updateOrdersTableInDB(ordersParametersMap);
								refreshNorthTable();
								initializeAddingProductPanel();
								
								System.out.println("Product added to order: " + whatOrder);
								
								conn.close();
							} else if(amountFromStock == 0) {
								JOptionPane.showMessageDialog(panel, "This product amount in stock equals 0.", "Empty stock", JOptionPane.ERROR_MESSAGE);
								conn.close();
							} else {
								JOptionPane.showMessageDialog(panel, "There is not enough amount of this product in stock. Set maximum availible", "Not enough in stock", JOptionPane.WARNING_MESSAGE);
						
								PreparedStatement statement2 = conn.prepareStatement("INSERT INTO productlist (order_id, product_id, `purchase_price`, `markup(%)`, `sell_price`, amount_odererd) VALUES (?,?,?,?,?,?)");
								statement2.setString(1, whatOrder);
								statement2.setString(2, productId);
								statement2.setString(3, String.valueOf(purchasePrise));
								statement2.setString(4, String.valueOf(markup));
								statement2.setString(5, String.valueOf(sellPrise));
								statement2.setString(6, String.valueOf(amountOredered));								
								statement2.executeUpdate();
								System.out.println("Productlist updated");
								
								amountFromStock = amountFromStock - amountFromStock;
								reserved = reserved + amountFromStock;
								
								PreparedStatement statement3 = conn.prepareStatement("UPDATE productlibrary SET amount = ?, reserved = ? WHERE id = ?");
								statement3.setString(1, String.valueOf(amountFromStock));
								statement3.setString(2, String.valueOf(reserved));
								statement3.setString(3, String.valueOf(productId));
								statement3.executeUpdate();
								System.out.println("Reserved and stock updated");
								
								refreshSouthTable();
								ordersParametersMap = countItemsAndValue();
								updateOrdersTableInDB(ordersParametersMap);
								refreshNorthTable();
								initializeAddingProductPanel();
								conn.close();
							}													
							conn.close();
						} catch (Exception e1) {
							
							e1.printStackTrace();
						}
						
						
					} else {
						JOptionPane.showMessageDialog(panel, "Wrong number use Integer.", "Wrong number", JOptionPane.WARNING_MESSAGE);
					}
				}else if(productIDList.contains(productId.toString())) {
					JOptionPane.showMessageDialog(panel, "Order contains that product.", "Product already in", JOptionPane.WARNING_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(panel, "Select product to add.", "Product not selected", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		
		addProduct.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				initializeAddingProductPanel();
				
			}
		});
		
		removeProduct.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int row = tableSouth.getSelectedRow();
				if(row != -1) {
					int choise = JOptionPane.showConfirmDialog(panel, "Are you sure you want to remove this product?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
					if(choise == 0) {
						try {
							Connection conn = DB.getConnection();
							PreparedStatement statement = conn.prepareStatement("DELETE FROM productlist WHERE order_id = ? AND product_id = ?");
							statement.setString(1, whatOrder);
							statement.setString(2, String.valueOf(tableSouth.getValueAt(row, 1)));
							statement.executeUpdate();
							
							HashMap<String, Double> dataToUpdate = new HashMap<String, Double>();
							dataToUpdate.put(String.valueOf(tableSouth.getValueAt(row, 1)), Double.parseDouble(String.valueOf(tableSouth.getValueAt(row, 12))));	
							DB.updateReservedInStock(dataToUpdate);
							refreshSouthTable();						
							ordersParametersMap = countItemsAndValue();
							updateOrdersTableInDB(ordersParametersMap);
							refreshNorthTable();
							
							System.out.println("Product removed from order: " + whatOrder + " for: " + whatClient);
							conn.close();
						} catch (Exception e1) {
							
							e1.printStackTrace();
						}
					} 
				} else {
					JOptionPane.showMessageDialog(panel, "Select product to remove", "Product not selected", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		
		deleteOrder.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = tableNorth.getSelectedRow();
				if(selectedRow != -1) {
					int choise = JOptionPane.showConfirmDialog(panel, "Are you sure to delete order?", "Congirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(choise == 0) {
						String orderKey = String.valueOf(tableNorth.getValueAt(selectedRow, 2));
						try {
							Connection conn = DB.getConnection();
							PreparedStatement statement = conn.prepareStatement("SELECT product_id, amount_ordered FROM productlist WHERE order_id = ?");
							statement.setString(1, orderKey);
							ResultSet rs = statement.executeQuery();
							HashMap<String, Double> dataToUpdate = new HashMap<String, Double>();
							
							while(rs.next()) {
								dataToUpdate.put(rs.getString(1), rs.getDouble(2));
							}
							System.out.println(dataToUpdate);
													
							DB.updateReservedInStock(dataToUpdate);
							
							PreparedStatement statement2 = conn.prepareStatement("DELETE FROM productlist WHERE order_id = ?");
							PreparedStatement statement3 = conn.prepareStatement("DELETE FROM orders WHERE order_id = ?");
							statement2.setString(1, orderKey);
							statement3.setString(1, orderKey);
							statement2.executeUpdate();
							statement3.executeUpdate();
							System.out.println("Order deleted");
							
							try {											
								panel.remove(southPanel);							
								panel.revalidate();
								panel.repaint();
								southPanel.removeAll();
								
							} catch (Exception e2){
								
							}							
							refreshNorthTable();						
							conn.close();
						} catch (Exception e1) {							
							e1.printStackTrace();
						}										
					}else {
						
					}
					
				} else {
					JOptionPane.showMessageDialog(panel, "Select order you want to delete", "Order not selected", JOptionPane.WARNING_MESSAGE);
				}						
			}	
		});
		
		printOrder.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					tableSouth.print();
				} catch (PrinterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		printOrders.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					tableNorth.print();
				} catch (PrinterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});

		searchClient.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				clientCombobox.setSelectedItem("");
				
				try {
					DefaultTableModel defaultTableModel = (DefaultTableModel) tableNorth.getModel();
					TableRowSorter<DefaultTableModel> tr = new TableRowSorter<DefaultTableModel>(defaultTableModel);
					tableNorth.setRowSorter(tr);
					tr.setRowFilter(RowFilter.regexFilter(searchClient.getText().substring(0, 1).toUpperCase() + searchClient.getText().substring(1)));
				} catch (IndexOutOfBoundsException e1) {
					// TODO Auto-generated catch block
					e1.getMessage();
				} catch(Exception e2) {
					e2.getStackTrace();
				}
			}
		});
				
		showOrder.addActionListener(new ActionListener() {
					

			@Override
			public void actionPerformed(ActionEvent e) {
				
				int row = tableNorth.getSelectedRow();		
				if(row != -1) {
					whatOrder = String.valueOf(tableNorth.getValueAt(row, 2));
					whatClient = String.valueOf(tableNorth.getValueAt(row, 1));
					initializeSouthPanel(whatOrder);
				}else {
					JOptionPane.showMessageDialog(panel, "Select order to inspect.", "Order not selected", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
			
		refreshOrder.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshNorthTable();				
			}
		});
		
	}
	
	protected void initializeAddingProductPanel() {
		try {
			southPanelSouth.remove(southPanelUp);
			southPanelUp.removeAll();
			productCombobox.removeAllItems();
		} catch (Exception e1) { }
		
		productCombobox = getProductsToCombobox();
		
		southPanelUp.add(chooseProduct);
		southPanelUp.add(productCombobox);
		southPanelUp.add(chooseAmount);
		southPanelUp.add(setAmount);
		southPanelUp.add(add);
		
		southPanelUp.scrollRectToVisible(southPanelUp.getVisibleRect());
		southPanelUp.setAutoscrolls(true);
		southPanelSouth.add(southPanelUp, BorderLayout.NORTH);
		
		
	}

	protected JComboBox<String> getProductsToCombobox() {
		JComboBox<String> productCombobox = new JComboBox<String>();
		
		productCombobox.setEditable( true );
		AutoCompleteDecorator.decorate(productCombobox);
		
		try {
			Connection conn = DB.getConnection();
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM productlibrary");
			ResultSet rs = statement.executeQuery();
			int no = 1;			
			ResultSetMetaData metaData = rs.getMetaData();
			ArrayList<String> columnNames = new ArrayList<String>();
			for(int i = 1; i < metaData.getColumnCount(); i++) {
				columnNames.add(metaData.getColumnName(i));
			}
			System.out.println(columnNames);
			
			while(rs.next()) {

				String formatted = String.format("%s %s %s %s | unit: %s | barcode: %s | price: %s | stock: %s | id: %s |", /*String.valueOf(no++),  	*/				rs.getString("name"),    						 
						rs.getString("manufacturer"),				rs.getString("country of production"),		rs.getString("weight"), 					
						rs.getString("unit"), 						rs.getString("barcode"), 					rs.getString("selling price(no tax)"),     
						rs.getString("amount"),					    rs.getString("id"));

				productCombobox.addItem(formatted);
			}
			productCombobox.addItem("");
			productCombobox.setSelectedItem("");	
			conn.close();
			return productCombobox;
			
		} catch (Exception e) {			
			e.printStackTrace();
		}		
		
		return null;
	}

	protected void updateOrdersTableInDB(HashMap<String, Object> ordersParametersMap) {
		
		try {
			Connection conn = DB.getConnection();			
			PreparedStatement statement = conn.prepareStatement("UPDATE orders SET volume = ?, pcs = ?, amount_of_products = ?, value = ? WHERE order_id = ?");
			statement.setString(1, String.valueOf(ordersParametersMap.get("volume")));
			statement.setString(2, String.valueOf(ordersParametersMap.get("pcs")));
			statement.setString(3, String.valueOf(ordersParametersMap.get("amount_of_products")));
			statement.setString(4, String.valueOf(ordersParametersMap.get("value")));
			statement.setString(5, whatOrder);
			statement.executeUpdate();
			conn.close();
			System.out.println("Orders table updated in DB.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected void refreshNorthTable() {
		
		
		createNorthTable();
		
		scrollNorth = new JScrollPane(tableNorth);
		
		northPanel.add(scrollNorth, BorderLayout.CENTER);
		
	}

	private JComboBox<String> getClientsFromDb() {
		Vector<String> vector = new Vector<String>();
		JComboBox<String> cliComboBox = new JComboBox<String>();
		
		try {
			Connection conn = DB.getConnection();
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM clients");
			ResultSet rs = statement.executeQuery();
			
			while(rs.next()) {
				vector.add(rs.getString("name"));	
			}
			Collections.sort(vector);
			
			int i = 1;
			for(String x: vector) {
//				cliComboBox.addItem(""+ i++ +". " + x);
				cliComboBox.addItem(x);
			}
					
			conn.close();
			
			vector.clear();
			return cliComboBox;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cliComboBox;
	}
	
	protected void refreshSouthTable() {
		
		try {
			southPanel.remove(scrollSouth);
			southPanelSouth.remove(southPanelUp);
		} catch (Exception e1) {
			
		}
		
		Vector<Vector<Object>> orderVector = new Vector<Vector<Object>>();
		try {
			Connection conn = DB.getConnection();
			PreparedStatement statement = conn.prepareStatement("SELECT lib.id, lib.name, lib.manufacturer, lib.`country of production`, lib.weight, lib.unit, lib.`volume (dm3)`, "
					+ "lib.barcode, p.purchase_price, p.`markup(%)`, p.sell_price, p.amount_ordered FROM productlist p JOIN productlibrary lib ON p.product_id=lib.id WHERE order_id=?");
			
			statement.setString(1, whatOrder);
			ResultSet rs = statement.executeQuery();
			int rsColumnCount = rs.getMetaData().getColumnCount();
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
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		System.out.println(orderVector);
		Vector<String> columnNames = new CreateOrder().getColumnsNames();
		
		DefaultTableModel defaultTableModel = new DefaultTableModel(orderVector, columnNames);
		tableSouth = new JTable(defaultTableModel) {
			@Override
			public boolean isCellEditable(int row, int column) {
				
				return column <= 9 || column == 11 ? false : true;				}
		};
		
		tableSouth.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				tableChangedMethod(e);
			}
		});
		
		tableSouth.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				tableSelectionMethod(e);
				
			}
		});

		tableSouth.setEnabled(true);
		tableSouth.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		tableSouth.getColumnModel().getColumn(0).setMaxWidth(40);
		tableSouth.getColumnModel().getColumn(1).setMaxWidth(40);
		tableSouth.getColumnModel().getColumn(12).setMaxWidth(50);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
		tableSouth.setDefaultRenderer(Integer.class, centerRenderer);
						
		scrollSouth = new JScrollPane(tableSouth);
		
		southPanel.add(scrollSouth, BorderLayout.CENTER);
		southPanel.revalidate();
		southPanel.repaint();
	}
	
	protected JTable getTableNorth() {
		return tableNorth;
	}
	
	private HashMap<String, Object> countItemsAndValue() {
		double orderValue = 0;
		int rowCount = tableSouth.getRowCount();
		double orderVolume = 0;
		int pcs = 0;
		
		for(int i=0; i < rowCount; i++) {
			double rowValue = (Double.parseDouble(String.valueOf(tableSouth.getValueAt(i, 11)))) * (Double.parseDouble(String.valueOf(tableSouth.getValueAt(i, 12))));
			orderValue += rowValue;
			double rowVolume = (Double.parseDouble(String.valueOf(tableSouth.getValueAt(i, 7)))) * (Double.parseDouble(String.valueOf(tableSouth.getValueAt(i, 12))));
			orderVolume += rowVolume;
			double rowPcs = Double.parseDouble(String.valueOf(tableSouth.getValueAt(i, 12)));
			pcs += rowPcs;
		}
		String strDouble = String.format("%.2f", orderValue);
		
		orderParameters.setText(" | Order id: " + whatOrder + " | Client: " + whatClient + " | Items: " + rowCount + " | Order volume: " + orderVolume + " | Pcs: " + pcs + " | Order value: " + strDouble + " PLN |");
		
		HashMap<String, Object> orderParametersMap = new HashMap<String, Object>();
		double roundedValue = Math.round(orderValue * 100.0) / 100.0;
		orderParametersMap.put("amount_of_products", rowCount);
		orderParametersMap.put("volume", orderVolume);
		orderParametersMap.put("value", roundedValue);
		orderParametersMap.put("pcs", pcs);
		
		return orderParametersMap; 
	}
	
	protected void updateSellCell(JTable table, TableModelEvent e) {
		
		int row = e.getFirstRow();
		int col = e.getColumn();
		String data = String.valueOf(table.getValueAt(row, col));
			
		if((data.matches("^\\d+$") || data.matches("^(\\d+).(\\d+)$"))) {
			
			try {
				double purchasePrice = Double.parseDouble(String.valueOf(table.getValueAt(row, 9)));
				double markup = Double.parseDouble(String.valueOf(table.getValueAt(row, 10)));
				double result = purchasePrice + (purchasePrice*markup/100);
				result = result*100;
				result = Math.round(result);
				result = result/100;
				String stringResult = String.valueOf(result);
				table.setValueAt(result, row, 11);
				
			} catch (NumberFormatException e1) {
				JOptionPane.showMessageDialog(table, "Wrong number. Please use one of this number format:  '0' or '0.0'." , "Wrong number", JOptionPane.WARNING_MESSAGE);
//				table.setValueAt("0.0", row, col);
//				e1.printStackTrace();
				refreshSouthTable();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			
		}else {
			
			JOptionPane.showMessageDialog(table, "Wrong number. Please use one of this number format:  '0' or '0.0'." , "Wrong number", JOptionPane.WARNING_MESSAGE);
//			table.setValueAt("0.0", row, col);
			refreshSouthTable();
		}
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
