package menu;

import com.google.gson.internal.LinkedTreeMap;
import db.DB;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;


public class CreateOrder extends JPanel {

	private JPanel panel;
	private JPanel northPanel;
	private JPanel southPanel;
	private JTable stockTable;
	private JTable orderTable;	
	
	private JScrollPane scrollNorth;
	private JScrollPane scrollSouth;

	private Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	private JMenuBar menuBarNorth;
	private JMenuBar menuBarSouth;
	
	private JButton addToOrderButton = new JButton("Add product to order");
	private JButton removeFromOrderButton = new JButton("Remove from order");
	private JButton reserveButton = new JButton("Reserve");
	private JButton backReservation = new JButton("Back reservation");
	private JButton saveButton = new JButton("Save order");
	
	private JLabel clientLabel = new JLabel(" Choose client: ");
	private JLabel items = new JLabel();
	private JLabel orderParameters = new JLabel();
	
	
	private JTextField amountTextField = new JTextField();
	
	protected int no = 1;
	private JButton refreshButton = new JButton("Refresh");
	
	protected JComboBox<String> clientCombobox = new JComboBox<String>();
	
	protected LinkedTreeMap<String, Vector<Vector<Object>>> ordersMap = new LinkedTreeMap<String, Vector<Vector<Object>>>();
	protected TreeMap<String, LinkedTreeMap<String, Vector<Vector<Object>>>> clientsOrdersMap = new TreeMap<String, LinkedTreeMap<String, Vector<Vector<Object>>>>();

	protected HashMap<String, Object> orderParametersMap;
	
	public CreateOrder() {
		
		initComponents();
		
	}
	
	private void initComponents() {
		panel = this;
		panel.setLayout(new GridLayout(2,1,0,0));
		getClientToComboBox(clientCombobox);
		buttonStatus();
		initializeNorthPanel();
		initializeSouthPanel();
		actionsForButtons();
	}

	private void buttonStatus() {
		addToOrderButton.setEnabled(true);
		removeFromOrderButton.setEnabled(true);
		reserveButton.setEnabled(false);
		backReservation.setEnabled(false);
		saveButton.setEnabled(false);
	}
	
	private void initializeNorthPanel() {
		northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		menuBarNorth = new JMenuBar();

		addToOrderButton.setIcon(new ImageIcon("img\\iconfinde.png"));
		removeFromOrderButton.setIcon(new ImageIcon("img\\arrowUp.png"));
		saveButton.setIcon(new ImageIcon("img\\Apply.png"));
		
		refreshButton.setForeground(Color.GREEN); 		 refreshButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		addToOrderButton.setForeground(Color.GREEN); 		 addToOrderButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		removeFromOrderButton.setForeground(Color.GREEN); 		 removeFromOrderButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		amountTextField.setMaximumSize(new Dimension(70, 50));

		menuBarNorth.add(addToOrderButton);
		menuBarNorth.add(removeFromOrderButton);
		menuBarNorth.add(clientLabel);
		menuBarNorth.add(clientCombobox);
					
		StockForOrder2 stock = new StockForOrder2();
		stockTable = stock.stockTable;
		stockTable.getTableHeader().setReorderingAllowed(false);
						
		scrollNorth = new JScrollPane(stockTable);
		
		scrollNorth.setMaximumSize(new Dimension(100,290));
		northPanel.add(scrollNorth, BorderLayout.CENTER);
		northPanel.add(menuBarNorth, BorderLayout.SOUTH);
					
		panel.add(northPanel);
	}

	private void initializeSouthPanel() {
		southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
				
		orderTable = createTable(stockTable, data);		
	
		orderTable.getModel().addTableModelListener(this::tableChangedMethod);
		
		scrollSouth = new JScrollPane(orderTable);		
		
		reserveButton.setForeground(Color.RED); 		 reserveButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		saveButton.setForeground(Color.RED); 		 	 saveButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		backReservation.setForeground(Color.RED); 		 backReservation.setFont(new Font("Tahoma", Font.PLAIN, 13));		

		reserveButton.setIcon(new ImageIcon("img\\thumbsUp.png"));
		backReservation.setIcon(new ImageIcon("img\\thumbsDown.png"));		
		
		menuBarSouth = new JMenuBar();
		menuBarSouth.add(reserveButton);
		menuBarSouth.add(backReservation);
		menuBarSouth.add(saveButton);
		menuBarSouth.add(items);
		menuBarSouth.add(orderParameters);		
		
		southPanel.add(menuBarSouth, BorderLayout.SOUTH);
		southPanel.add(scrollSouth, BorderLayout.CENTER);
		panel.add(southPanel);		
	}
	
	private void actionsForButtons() {
		
		addToOrderButton.addActionListener(e -> {

			try {
				int row = stockTable.getSelectedRow();
				if(stockTable.getValueAt(row, 0) == Boolean.FALSE) {
					Vector<Object> vector = new Vector<>();
					vector.add(String.valueOf(no++));
					for(int i = 2; i < 13; i++) {
						 String cellData = String.valueOf(stockTable.getValueAt(row, i));
						 vector.add(cellData);

					}
					vector.add("0");
					data.add(vector);
					stockTable.setValueAt(Boolean.TRUE, row, 0);
					refreshOrder();
					reserveButton.setEnabled(true);
					orderParametersMap = countItemsAndValue();

				}
			} catch (IndexOutOfBoundsException e1) {
				JOptionPane.showMessageDialog(panel, "You have to choose which product you want to add to order. Please select one.", "No product selected", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});
		
		refreshButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshAll();
			}
		});
		
		removeFromOrderButton.addActionListener(e -> {

			try {
				int row = orderTable.getSelectedRow();
				String id = String.valueOf(orderTable.getValueAt(row, 1));
				DefaultTableModel model = ((DefaultTableModel) orderTable.getModel());
				for(int i = 0; i < orderTable.getRowCount(); i++) {
					String idInData = String.valueOf(data.get(i).get(1));
					System.out.println(idInData);
					if (idInData.equals(id)) {
						data.remove(i);
						System.out.println("Row removed from data");
						break;
					}
				}

				for (int i = 0; i < stockTable.getRowCount(); i++) {
					if(stockTable.getValueAt(i, 0)==Boolean.TRUE) {
						System.out.println("Found Selected");
						String idInStock = String.valueOf(stockTable.getValueAt(i, 2));
						if(id.equals(idInStock)) {
							stockTable.setValueAt(Boolean.FALSE, i, 0);
							System.out.println("Stock row checkbox changed to false");
							break;
						}
					}
				}

				refreshOrder();
				if (orderTable.getRowCount() == 0) {
					reserveButton.setEnabled(false);
				}
				orderParametersMap = countItemsAndValue();

			} catch (IndexOutOfBoundsException e1) {
				JOptionPane.showMessageDialog(panel, "You have to choose which product you want to remove from order.", "No product selected", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});
		
		reserveButton.addActionListener(e -> {
			for(int i = 0; i < orderTable.getRowCount(); i++) {
				String idInOrder = String.valueOf(orderTable.getValueAt(i, 1));
				double amountInOrder = Double.parseDouble((String) orderTable.getValueAt(i, 12));

				for (int j = 0; j < stockTable.getRowCount(); j++) {
					if(stockTable.getValueAt(j, 0).equals(Boolean.TRUE)) {
						String idInStock = String.valueOf(stockTable.getValueAt(j, 2));
						if(idInOrder.equals(idInStock)) {
							double amountInStock = Double.parseDouble(String.valueOf(stockTable.getValueAt(j, 13)));
							double differense = amountInStock - amountInOrder;
							double amountReseved = Double.parseDouble(String.valueOf(stockTable.getValueAt(j, 14)));
							double sumOfReseved = amountReseved + amountInOrder;
							stockTable.setValueAt(String.valueOf(differense), j, 13);
							stockTable.setValueAt(String.valueOf(sumOfReseved), j, 14);
							reserveButton.setEnabled(false);
							backReservation.setEnabled(true);
							saveButton.setEnabled(true);
						}
					}
				}
			}
			removeFromOrderButton.setEnabled(false);
			addToOrderButton.setEnabled(false);
			orderTable.setEnabled(false);
		});

		backReservation.addActionListener(e -> {
			backAllReservationMethod();
			reserveButton.setEnabled(true);
			backReservation.setEnabled(false);
			addToOrderButton.setEnabled(true);
			removeFromOrderButton.setEnabled(true);
			orderTable.setEnabled(true);
			saveButton.setEnabled(false);
		});
		
		saveButton.addActionListener(e -> {
			String clientName = clientCombobox.getSelectedItem().toString();
			System.out.println("Client: " + clientName);

			if(!clientName.equals("")) {
				int choice = JOptionPane.showConfirmDialog(orderTable, "Are you sure you want to save order? " +
						"After that you won't be able to make changes. ", "Confirm", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if(choice == 0) {
					Date date = new Date();
					System.out.println("date: " + date);
					SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
					String formattedDate = formatter.format(date);

					SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
					String formattedDate2 = formatter2.format(date);

					int generatedOrderNumber = orderNumberGenerator(formattedDate);
					String orderKey = "" + generatedOrderNumber + "." + formattedDate;
					String clientID = "";

					try {
						Connection conn = DB.getConnection();
						PreparedStatement statement = conn.prepareStatement("SELECT id FROM clients WHERE name=?");
						statement.setString(1, clientName);
						ResultSet rs = statement.executeQuery();

						while(rs.next())
							clientID = rs.getString("id");
						System.out.println("Client id: " + clientID);

						for(int i = 0;i < orderTable.getRowCount(); i++) {
							int amountColumn = orderTable.getColumnModel().getColumnIndex("amount");
								String productID = String.valueOf(orderTable.getValueAt(i, 1));
								String amountOrdered = String.valueOf(orderTable.getValueAt(i, 12));
								String purchasePrice = String.valueOf(orderTable.getValueAt(i, 9));
								String markup = String.valueOf(orderTable.getValueAt(i, 10));
								String sellPrice = String.valueOf(orderTable.getValueAt(i, 11));

								PreparedStatement statement3 = conn.prepareStatement("INSERT INTO productlist(order_id, product_id, amount_ordered, purchase_price, `markup(%)`, sell_price) VALUES (?,?,?,?,?,?)");
								statement3.setString(1, orderKey);
								statement3.setString(2, productID);
								statement3.setString(3, amountOrdered);
								statement3.setString(4, purchasePrice);
								statement3.setString(5, markup);
								statement3.setString(6, sellPrice);
								statement3.executeUpdate();
						}

						PreparedStatement statement2 = conn.prepareStatement("INSERT INTO orders (client_id, order_id, date, volume, pcs, amount_of_products, value, weight) VALUES (?,?,?,?,?,?,?,?)");
						statement2.setString(1, clientID);
						statement2.setString(2, orderKey);
						statement2.setString(3, formattedDate2);
						statement2.setString(4, String.valueOf(orderParametersMap.get("volume")));
						statement2.setString(5, String.valueOf(orderParametersMap.get("pcs")));
						statement2.setString(6, String.valueOf(orderParametersMap.get("amount_of_products")));
						statement2.setString(7, String.valueOf(orderParametersMap.get("value")));
						statement2.setString(8, String.valueOf(orderParametersMap.get("weight")));

						statement2.executeUpdate();
						JOptionPane.showMessageDialog(panel, "Order saved!", "Success",
								JOptionPane.INFORMATION_MESSAGE);
						conn.close();
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					data.clear();
					ordersMap.clear();
					clientsOrdersMap.clear();
					refreshOrder();
					unselectAll();
					buttonStatus();
				}
			} else {
				JOptionPane.showMessageDialog(orderTable, "You must choose the client.", "Client not selected", JOptionPane.INFORMATION_MESSAGE);
				clientCombobox.requestFocus();
			}
		});
	}
	
	private void tableChangedMethod(TableModelEvent e) {
		if(e.getColumn()== 9 || e.getColumn()== 10) {
			updateSellCell(orderTable, e);
			}
		
		if(e.getColumn() != 12 && e.getColumn() != 0) {
			
		} else if (e.getColumn() == 12) {
			
			int row = orderTable.getSelectedRow();
			String stringAmountInOrder = (String) orderTable.getValueAt(row, 12);
			String unit = (String) orderTable.getValueAt(row, 6);
			
			if(!(stringAmountInOrder.matches("^\\d+$") || stringAmountInOrder.matches("^(\\d+).(\\d+)$"))) {
				
				orderTable.getModel().setValueAt("0", row, 12);
				JOptionPane.showMessageDialog(orderTable, "Wrong number. Please use one of this number format:  '0' or '0.0'." , "Wrong number", JOptionPane.WARNING_MESSAGE);
			}
									
			if(unit.equals("pcs") && stringAmountInOrder.matches("^\\d+$") && !(stringAmountInOrder.equals("0") || stringAmountInOrder.equals("0.0"))) {

				double amountInOrder = Double.parseDouble((String) orderTable.getValueAt(row, 12));
				String idInOrder = String.valueOf(orderTable.getValueAt(row, 1));
				
				for (int i = 0; i < stockTable.getRowCount(); i++) {
					if(stockTable.getValueAt(i, 0)==Boolean.TRUE) {
						System.out.println("Found Selected");
						String idInStock = String.valueOf(stockTable.getValueAt(i, 2));
						if(idInOrder.equals(idInStock)) {
							double amountInStock = Double.parseDouble(String.valueOf(stockTable.getValueAt(i, 13)));
							if(amountInOrder <= amountInStock) {
								/*
								 * action
								 */
							} else {
								JOptionPane.showMessageDialog(orderTable, "There is not enough amount of this product in stock. Added max amount.", "Not enough amount in stock", JOptionPane.INFORMATION_MESSAGE);
								orderTable.getModel().setValueAt(String.valueOf((int)amountInStock), row, 12);
							}
							break;
						}
					}
				}
				System.out.println("pcs data ok");
			} else if (unit.equals("pcs") && !(stringAmountInOrder.matches("^\\d+$")) && !(stringAmountInOrder.equals("0")) && !(stringAmountInOrder.equals("0.0") )) {
				System.out.println("Jestem tu");
				orderTable.getModel().setValueAt("0", row, 12);
				JOptionPane.showMessageDialog(orderTable, "Unit of this product is 'pcs'. Use integer." , "Wrong number", JOptionPane.WARNING_MESSAGE);
			}
			
			if(unit.equals("kg") && (stringAmountInOrder.matches("^\\d+$") || stringAmountInOrder.matches("^(\\d+).(\\d+)$")) && !(stringAmountInOrder.equals("0")) && !(stringAmountInOrder.equals("0.0"))){
				/*
				 * action
				 */
				
				double amountInOrder = Double.parseDouble((String) orderTable.getValueAt(row, 12));
				String idInOrder = String.valueOf(orderTable.getValueAt(row, 1));
				
				for (int i = 0; i < stockTable.getRowCount(); i++) {
					if(stockTable.getValueAt(i, 0)==Boolean.TRUE) {
						System.out.println("Found Selected");
						String idInStock = String.valueOf(stockTable.getValueAt(i, 2));
						if(idInOrder.equals(idInStock)) {
							double amountInStock = Double.parseDouble((String) stockTable.getValueAt(i, 13));
							if(amountInOrder <= amountInStock) {
								/*
								 * action
								 */
							} else {
								JOptionPane.showMessageDialog(orderTable, "There is not enough amount of this product in stock. Added max amount.", "Not enough amount in stock", JOptionPane.INFORMATION_MESSAGE);
								orderTable.getModel().setValueAt(String.valueOf(amountInStock), row, 12);
							}
							break;
						}
					}
				}
				System.out.println("kg data ok");
			}
		}
	orderParametersMap = countItemsAndValue();	
	}
	
	private void refreshAll() {
		panel.removeAll();		
		initializeNorthPanel();
		initializeSouthPanel();
	}
	
	private void refreshOrder() {
		panel.remove(southPanel);
		initializeSouthPanel();
		for(int i = 0; i < orderTable.getRowCount(); i++) {
			orderTable.setValueAt(i+1, i, 0);
		}		
	}
	
 	private JTable createTable(JTable table, Vector<Vector<Object>> data) {
 		DefaultTableModel defaultTableModel = buildTableModel(table, data);
		JTable orderTable = new JTable(defaultTableModel);
		orderTable.getTableHeader().setReorderingAllowed(false);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		orderTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		orderTable.getColumnModel().getColumn(0).setMaxWidth(100);
		orderTable.getColumnModel().getColumn(0).setMinWidth(50);
		orderTable.getColumnModel().getColumn(0).setPreferredWidth(50);
		
		orderTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		orderTable.getColumnModel().getColumn(1).setMaxWidth(1000);
		orderTable.getColumnModel().getColumn(1).setMinWidth(50);
		orderTable.getColumnModel().getColumn(1).setPreferredWidth(70);

		orderTable.getColumnModel().getColumn(2).setMaxWidth(1000);
		orderTable.getColumnModel().getColumn(2).setMinWidth(150);
		orderTable.getColumnModel().getColumn(2).setPreferredWidth(197);

		orderTable.getColumnModel().getColumn(3).setMaxWidth(1000);
		orderTable.getColumnModel().getColumn(3).setMinWidth(50);
		orderTable.getColumnModel().getColumn(3).setPreferredWidth(126);
		
		orderTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
		orderTable.getColumnModel().getColumn(4).setMaxWidth(1000);
		orderTable.getColumnModel().getColumn(4).setMinWidth(50);
		orderTable.getColumnModel().getColumn(4).setPreferredWidth(140);
	
		orderTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
		orderTable.getColumnModel().getColumn(5).setMaxWidth(1000);
		orderTable.getColumnModel().getColumn(5).setMinWidth(50);
		orderTable.getColumnModel().getColumn(5).setPreferredWidth(70);
	
		orderTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
		orderTable.getColumnModel().getColumn(6).setMaxWidth(1000);
		orderTable.getColumnModel().getColumn(6).setMinWidth(50);
		orderTable.getColumnModel().getColumn(6).setPreferredWidth(70);
		
		orderTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
		orderTable.getColumnModel().getColumn(7).setMaxWidth(1000);
		orderTable.getColumnModel().getColumn(7).setMinWidth(50);
		orderTable.getColumnModel().getColumn(7).setPreferredWidth(90);

		orderTable.getColumnModel().getColumn(8).setMaxWidth(1000);
		orderTable.getColumnModel().getColumn(8).setMinWidth(50);
		orderTable.getColumnModel().getColumn(8).setPreferredWidth(125);
		
		orderTable.getColumnModel().getColumn(9).setCellRenderer(centerRenderer);
		orderTable.getColumnModel().getColumn(9).setMaxWidth(1000);
		orderTable.getColumnModel().getColumn(9).setMinWidth(50);
		orderTable.getColumnModel().getColumn(9).setPreferredWidth(125);
		
		orderTable.getColumnModel().getColumn(10).setCellRenderer(centerRenderer);
		orderTable.getColumnModel().getColumn(10).setMaxWidth(1000);
		orderTable.getColumnModel().getColumn(10).setMinWidth(50);
		orderTable.getColumnModel().getColumn(10).setPreferredWidth(90);
		
		orderTable.getColumnModel().getColumn(11).setCellRenderer(centerRenderer);
		
		orderTable.getColumnModel().getColumn(12).setCellRenderer(centerRenderer);
		orderTable.getColumnModel().getColumn(12).setMaxWidth(1000);
		orderTable.getColumnModel().getColumn(12).setMinWidth(50);
		orderTable.getColumnModel().getColumn(12).setPreferredWidth(140);
		
		return orderTable;
	}

	private DefaultTableModel buildTableModel(JTable table, Vector<Vector<Object>> data){
	    // names of columns
	    Vector<Object> columnNames = new Vector<Object>();
	    for(int i = 1; i < table.getColumnCount() - 2; i++) {
	    	String columnName = table.getColumnName(i);
	    	columnNames.add(columnName);
	    }
	    
	    DefaultTableModel defaultTableModel = new DefaultTableModel(data, columnNames) {
	    					
			@Override
			public boolean isCellEditable(int row, int column) {
			
				return column == 0 || column == 1 || column == 6 || column == 7 || column == 8 || column == 9 ||
						column == 11 ? false : true;
			}
			
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
	
	protected void updateSellCell(JTable table, TableModelEvent e) {
	
		int row = e.getFirstRow();
		int col = e.getColumn();
		String data = (String) table.getValueAt(row, col);
			
		if((data.matches("^\\d+$") || data.matches("^(\\d+).(\\d+)$"))) {
			try {
				double purchasePrice = Double.parseDouble((String) table.getValueAt(row, 9));
				double markup = Double.parseDouble((String) table.getValueAt(row, 10));
				double result = purchasePrice + (purchasePrice*markup/100);
				result = result*100;
				result = Math.round(result);
				result = result/100;
				String stringResult = String.valueOf(result);
				table.setValueAt(stringResult, row, 11);
				
			} catch (NumberFormatException e1) {
				JOptionPane.showMessageDialog(table, "Wrong number. Please use one of this number format:  '0' or '0.0'." , "Wrong number", JOptionPane.WARNING_MESSAGE);
				table.setValueAt("0.0", row, col);
			}
		}else {
			JOptionPane.showMessageDialog(table, "Wrong number. Please use one of this number format:  '0' or '0.0'." , "Wrong number", JOptionPane.WARNING_MESSAGE);
			table.setValueAt("0.0", row, col);
		}
	}
	
	private void getClientToComboBox (JComboBox<String> combo) {
		
		try {
			Connection conn = DB.getConnection();
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM clients");
			ResultSet resultSet = statement.executeQuery();
			combo.setEditable(true);
			combo.setMaximumSize(new Dimension(200, 40));
			while(resultSet.next()) {
				String item = resultSet.getString("name");
				combo.addItem(item);
			}
			clientCombobox.setSelectedItem("");
			
			AutoCompleteDecorator.decorate(combo);
			
			conn.close();
		} catch (Exception e) {		
			e.printStackTrace();
		}
	}
	
	protected void backAllReservationMethod() {
		for(int i = 0; i < orderTable.getRowCount(); i++) {
			String idInOrder = String.valueOf(orderTable.getValueAt(i, 1));
			double amountInOrder = Double.parseDouble((String) orderTable.getValueAt(i, 12));
			
			for (int j = 0; j < stockTable.getRowCount(); j++) {
				if(stockTable.getValueAt(j, 0).equals(Boolean.TRUE)) {
					String idInStock = String.valueOf(stockTable.getValueAt(j, 2));
					if(idInOrder.equals(idInStock)) {
						double amountReseved = Double.parseDouble((String) stockTable.getValueAt(j, 14));
						double amountInStock = Double.parseDouble((String) stockTable.getValueAt(j, 13));
						double difference = amountReseved - amountInOrder;
						double sumOfStock = amountInStock + amountInOrder;
						stockTable.setValueAt(String.valueOf(difference), j, 14);
						stockTable.setValueAt(String.valueOf(sumOfStock), j, 13);
					}
				}
			}
		}
	}

	protected void unselectAll() {
		for(int i = 0; i < stockTable.getRowCount(); i++) {
			if(stockTable.getValueAt(i, 0) == Boolean.TRUE) {
				stockTable.setValueAt(Boolean.FALSE, i, 0);
			}
		}
	}

	protected int orderNumberGenerator(String date) {
		Random numberGenerator = new Random();
		int low2 = 10000; int high2 = 90000;
		int generatedNumber = numberGenerator.nextInt(high2-low2) + low2;
		ResultSet rs;
		ArrayList<String> listOfOrders = new ArrayList<>();
		try {
			Connection conn = DB.getConnection();
			PreparedStatement statement = conn.prepareStatement("select order_id from orders");
			rs = statement.executeQuery();
			while(rs.next())
				listOfOrders.add(rs.getString("order_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		if(!(listOfOrders.contains("" + generatedNumber + "." + date))) {
			return generatedNumber;
		} else {
			System.out.println("Generated order number already exists, choosing another one");
			orderNumberGenerator(date);
		}
					
		return generatedNumber;
	}
	private HashMap<String, Object> countItemsAndValue() {
		double orderValue = 0;
		int rowCount = orderTable.getModel().getRowCount();
		double orderVolume = 0;
		int pcs = 0;
		double weight = 0;
		
		for(int i = 0; i < rowCount; i++) {
			double rowValue = (Double.parseDouble((String) orderTable.getValueAt(i, 11))) * (Double.parseDouble(String.valueOf(orderTable.getValueAt(i, 12))));
			orderValue += rowValue;
			double rowVolume = (Double.parseDouble((String) orderTable.getValueAt(i, 7))) *  (Double.parseDouble(String.valueOf(orderTable.getValueAt(i, 12))));
			orderVolume += rowVolume;
			double rowPcs = Double.parseDouble((String) orderTable.getValueAt(i, 12));
			pcs += rowPcs;
			int weightColumn = orderTable.getColumnModel().getColumnIndex("weight");
			double rowWeight = Double.parseDouble(String.valueOf(orderTable.getValueAt(i, weightColumn))) *  (Double.parseDouble(String.valueOf(orderTable.getValueAt(i, 12))));
			weight += rowWeight;
		}
		String strDouble = String.format("%.2f", orderValue);
		orderValue = Math.round(orderValue * 100.0) / 100.0;
		weight = Math.round(weight * 100.0) / 100.0;
		this.orderParameters.setText(" | Items: " + rowCount + " | Order weight: " + weight + " | Order volume: " + orderVolume + " | Pcs: " + pcs + " | Order value: " + strDouble + " PLN |");
		
		HashMap<String, Object> orderParametersMap = new HashMap<String, Object>();
		orderParametersMap.put("amount_of_products", rowCount);
		orderParametersMap.put("volume", orderVolume);
		orderParametersMap.put("value", orderValue);
		orderParametersMap.put("pcs", pcs);
		orderParametersMap.put("weight", weight);
		
		return orderParametersMap; 		
	}
	
	protected Vector<String> getColumnsNames () {
		Vector<String> vector = new Vector<String>();
		for (int i = 0; i < orderTable.getColumnCount(); i++) {
			
			vector.add(orderTable.getColumnName(i));
		}
		return vector;
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				TreeMap<String,String> map = new TreeMap<>();
				map.put("name", "Pawe�");
				map.put("lastName", "D�browski");
				map.put("id", "4");
				map.put("login", "d");
				map.put("YWN", "20205999");
				map.put("password", "d");
				map.put("access", "1");

				Menu menu = new Menu(map);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
