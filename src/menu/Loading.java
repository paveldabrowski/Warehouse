package menu;

import dbConnectionAndMethods.DB;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import user.User;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.*;

public class Loading extends JPanel {
	final double palletCapacity = 1589.76;
	final int palletWeight = 25;
	
	private JPanel panel;
	private JPanel panelNorth;
	private JPanel panelSouth;
	
	protected JTable tableNorth;
	protected JScrollPane scrollNorth;
	private JMenuBar menuBarNorthDown;
	
	private JButton addToTruck = new JButton("Add to truck");
	private JButton removeFromTruck = new JButton("Remove from truck");
	private JButton chooseTruckButton = new JButton("Choose truck");
	private JButton finalizeLoading = new JButton("Finalize loading");	
	
	private JLabel truckData = new JLabel(); 
	private JLabel chooseDriever = new JLabel(" Choose driver: "); 
	private JLabel capacityLabel = new JLabel(" Capacity: "); 
	private JLabel weightLabel = new JLabel(" Weight: "); 
	private JLabel maxCapacityLabel; 
	private JLabel currentCapacityLabel; 	
	private JLabel maxWeightLabel; 
	private JLabel currentWeightLabel; 	
	private JLabel currentPalletsLabel;
	private JLabel maxPalletsLabel;
	
	private JLabel palletsLabel = new JLabel(" Pallets: "); 	
	
	
	
	private JLabel betweenLabel1 = new JLabel(" / "); 
	private JLabel betweenLabel3 = new JLabel(" / "); 
	private JLabel betweenLabel4 = new JLabel(" / "); 
	private JLabel betweenLabel2 = new JLabel(" | "); 
	private JLabel betweenLabel5 = new JLabel(" | "); 
	private JLabel betweenLabel6 = new JLabel(" | "); 
	
	
	protected int truckID;
	protected String truckBrand;
	protected String truckModel;
	protected String truckYear;
	protected String truckRegistrationNumber;
	
	protected double truckCapacity = 0;
	protected double truckPermissibleLoad = 0;
	protected double truckMaxPallets = 0;
	protected double currentPallets = 0;
	
	protected User user;
	
	protected String whatOrder;
	protected String whatClient;
	private JTable tableSouth;
	private Vector<Vector<Object>> roadCardData;
	private JScrollPane scrollSouth;
	private JProgressBar capacityProgressBar;
	private JProgressBar permissibleLoadProgressBar;
	protected double currentCapacity = 0;
	protected double currentWeight = 0;
	private JComboBox<String> driversCombobox = new JComboBox<String>();
	private JPanel panelSouthDown;
	private JMenuBar menuBarSouthDown;
	private JPanel panelSouthDownRight;
	private int clientsInRoadCard;	
		
	public Loading() {		
		initComponents();
	}		
	
	public Loading(User user) {
		this.user = user;
		initComponents();
	}
	
	private void initComponents() {

		panel = this;
		panel.setLayout(new GridLayout(2,1,0,0));
		addToTruck.setForeground(Color.RED); 			 addToTruck.setFont(new Font("Tahoma", Font.PLAIN, 13));
		addToTruck.setIcon(new ImageIcon("img/Down.png"));
		removeFromTruck.setForeground(Color.RED); 			 removeFromTruck.setFont(new Font("Tahoma", Font.PLAIN, 13));
		removeFromTruck.setIcon(new ImageIcon("img/arrowUP.png"));
		chooseTruckButton.setForeground(Color.RED); 			 chooseTruckButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		chooseTruckButton.setIcon(new ImageIcon("img/Delivery.png"));
		finalizeLoading.setForeground(Color.RED); 			 finalizeLoading.setFont(new Font("Tahoma", Font.PLAIN, 13));
		finalizeLoading.setIcon(new ImageIcon("img/Apply.png"));
				
		initializeNorthPanel();
		
		initializeSouthPanel();
		
		actionsForComponents();
	}

	private void actionsForComponents() {
		
		finalizeLoading.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				finalizeLoadingMethod();				
			}
		});
		
		removeFromTruck.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = tableSouth.getSelectedRow();
				if (selectedRow!= -1) {
					selectedRow = tableSouth.convertRowIndexToModel(selectedRow);
					int orderNorthColumn = tableNorth.getColumnModel().getColumnIndex("order_id");
					int orderSouthColumn = tableSouth.getColumnModel().getColumnIndex("order_id");
					int loadedColumn = tableNorth.getColumnModel().getColumnIndex("loaded");

					DefaultTableModel model = (DefaultTableModel)tableSouth.getModel();
					Vector<Object> vector = model.getDataVector().get(selectedRow);
					String orderToRemove = String.valueOf(vector.get(orderSouthColumn));
					
					model.removeRow(selectedRow);	
					if(tableSouth.getRowCount() > 0)
						tableSouth.setRowSelectionInterval(0, 0);
					
					countWeightVolumeAndPallets();					
							
					for(int i = 0; i < tableNorth.getRowCount(); i++) {					
						boolean loaded = (boolean) tableNorth.getValueAt(i, loadedColumn);						
						if(loaded) {
							String orderInNorthTable = String.valueOf(tableNorth.getValueAt(i, orderNorthColumn));						
							if(orderToRemove.equals(orderInNorthTable)) {												
								tableNorth.setValueAt(Boolean.FALSE, i, loadedColumn);
							}
						}
					}
					
				} else {
					JOptionPane.showMessageDialog(panel, "Select loaded order to remove.", "Order not selected", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
										
		chooseTruckButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseTruckButtonAction();
			}
		});
		
		addToTruck.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int selectedRow = tableNorth.getSelectedRow();				

				if (selectedRow != -1) {
					selectedRow = tableNorth.convertRowIndexToModel(selectedRow);
					int loadedColumn = tableNorth.getColumnModel().getColumnIndex("loaded");
					String order = String.valueOf(tableNorth.getValueAt(selectedRow, 3));
					if(((boolean) tableNorth.getValueAt(tableNorth.convertRowIndexToView(selectedRow), loadedColumn)) == Boolean.FALSE) {
						DefaultTableModel modelNorth = (DefaultTableModel) tableNorth.getModel();
						Vector<Object> selectedOrder = modelNorth.getDataVector().get(selectedRow);					
						Vector<Object> orderToLoad = new Vector<Object>();
						orderToLoad.add(1);
						for(int i = 1; i <= 11; i++) {						
							orderToLoad.add(selectedOrder.get(i));
						}
						
						
						DefaultTableModel modelSouth = (DefaultTableModel) tableSouth.getModel();
						modelSouth.addRow(orderToLoad);
																						
						countWeightVolumeAndPallets();				
												
						selectedRow = tableNorth.convertRowIndexToView(selectedRow);
						tableNorth.setValueAt(Boolean.TRUE, selectedRow, loadedColumn);
						
					}
				} else {
					JOptionPane.showMessageDialog(panel, "You have to choose which order you want to load on truck.", "Order not selected", JOptionPane.WARNING_MESSAGE);
				}			
			}
		});		
	}

	protected void finalizeLoadingMethod() {
		
		String choseDriver = driversCombobox.getSelectedItem().toString();
		String [] tmp = StringUtils.split(choseDriver);
		if(tmp.length>1)
			choseDriver = tmp[2];
				
		if(choseDriver.length() > 0) {
			int roadCardOrdersCount = tableSouth.getRowCount();			
			if (roadCardOrdersCount > 0) {
				if(currentWeight <= truckPermissibleLoad) {	
					
					HashMap<String, Integer> clientsInRoadCard = new HashMap<String, Integer>();
					double grossValue = 0;			
					String generatedRoadCardNumber = roadCardNumberGenerator();
					for (int i = 0; i < roadCardOrdersCount; i++) {
						String clientName = String.valueOf(tableSouth.getValueAt(i, 1));						
						clientsInRoadCard.put(clientName, 1);
//						
						try (Connection conn = DB.getConnection()) {
							PreparedStatement statement = conn.prepareStatement("INSERT INTO roadCardsOrders (order_id, card_id) VALUES (?,?)");
							int orderIDColumn = tableSouth.getColumnModel().getColumnIndex("order_id");
							String orderID = String.valueOf(tableSouth.getValueAt(i, orderIDColumn));
							statement.setString(1, orderID);
							statement.setString(2, generatedRoadCardNumber);
							statement.executeUpdate();
							System.out.println("Order to roadcard inserted.");
							
							PreparedStatement statement2 = conn.prepareStatement("UPDATE orders SET loaded = true WHERE order_id = ?");
							statement2.setString(1, orderID);
							statement2.executeUpdate();
							System.out.println("Order status updated");
						} catch (Exception e) {
							e.printStackTrace();
						}	
						int grossValueColumn = tableSouth.getColumnModel().getColumnIndex("gross_value");							
						grossValue += (double) tableSouth.getValueAt(i, grossValueColumn);
					}
					
					this.clientsInRoadCard = 0; 				
					clientsInRoadCard.forEach((k, v) -> this.clientsInRoadCard += clientsInRoadCard.get(k));
					
					Date calendar = Calendar.getInstance().getTime();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					String formattedDate = formatter.format(calendar);
					
					try (Connection conn = DB.getConnection()){
						PreparedStatement statement = conn.prepareStatement("INSERT INTO roadCards (card_id, date, clients, loader, driver, registration_number, weight, gross_value) VALUES (?,?,?,?,?,?,?,?)");
						statement.setString(1, generatedRoadCardNumber);
						statement.setString(2, String.valueOf(formattedDate));
						statement.setString(3, String.valueOf(this.clientsInRoadCard));
						statement.setString(4, String.valueOf(user.getYWN()));
						statement.setString(5, choseDriver);
						statement.setString(6, truckRegistrationNumber);
						statement.setString(7, String.valueOf(Math.round(currentWeight * 100.0) / 100.0));
						statement.setString(8, String.valueOf(Math.round(grossValue * 100.0) / 100.0));
						
						statement.executeUpdate();					
						System.out.println("New roadcard created.");
						
						
						
						JOptionPane.showMessageDialog(panel, "New road card created.", "Success", JOptionPane.INFORMATION_MESSAGE);
//		
						panel.remove(panelSouth);
						panelSouth.removeAll();						
						initializeSouthPanel();
						refreshNorthTable();
						
					} catch (Exception e) {					
						e.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(panel, "Current weight is greater than permissible load of this truck. Change truck or change loaded orders.", "too much weight", JOptionPane.ERROR_MESSAGE);
				}				
			} else {
				JOptionPane.showMessageDialog(panel, "At least one order must be loaded to finish loading.", "Order not loaded", JOptionPane.WARNING_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(panel, "Select driver to finish loading.", "Driver not selected", JOptionPane.WARNING_MESSAGE);
		}
		
	}

	private void refreshNorthTable() {
		panelNorth.remove(scrollNorth);
		createNorthTable();
		scrollNorth = new JScrollPane(tableNorth);
		panelNorth.add(scrollNorth, BorderLayout.CENTER);		
	}

	protected String roadCardNumberGenerator() {
		Random numberGenerator = new Random();
		int low2 = 100000000; int high2 = 999999999;
		int generatedNumber = numberGenerator.nextInt(high2-low2) + low2;
		
		Date date = new Date();
		
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate2 = formatter2.format(date);
		
		String generatedRoadCardNumber = "C/" + generatedNumber + "/" + formattedDate2;
		
		ArrayList<String> listOfOrders = new ArrayList<String>();
		try (Connection conn = DB.getConnection()) {
			
			PreparedStatement statement = conn.prepareStatement("SELECT card_id FROM roadCards");
			ResultSet rs = statement.executeQuery();
			while(rs.next())
				listOfOrders.add(rs.getString("card_id"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		if(!(listOfOrders.contains(generatedRoadCardNumber))) {
			return generatedRoadCardNumber;
		} else {
			System.out.println("Generated card number already exists, choosing another one");
			roadCardNumberGenerator();
		}
					
		return generatedRoadCardNumber;
	}
	
	protected void countWeightVolumeAndPallets() {
		int volumeColumn = tableSouth.getColumnModel().getColumnIndex("volume");
		int noColumn = tableSouth.getColumnModel().getColumnIndex("no.");
		int weightColumn = tableSouth.getColumnModel().getColumnIndex("weight");
		currentCapacity = 0;
		currentWeight = 0;
		currentPallets = 0;
		
		if (tableSouth.getRowCount() > 0 ) {			
			for(int i = 0; i < tableSouth.getRowCount(); i++) {
				tableSouth.setValueAt(i + 1, i, noColumn);
				double orderVolume = Double.parseDouble(String.valueOf(tableSouth.getValueAt(i, volumeColumn)));
				currentCapacity += orderVolume;
				currentPallets += orderVolume / palletCapacity;
				currentWeight += Double.parseDouble(String.valueOf(tableSouth.getValueAt(i, weightColumn)));				
			}
			currentWeight += Math.ceil(currentPallets) * palletWeight; // added weight of empty pallets
			capacityProgressBar.setValue((int) Math.round(currentCapacity));
			permissibleLoadProgressBar.setValue((int) Math.round(currentWeight));
			
			currentPalletsLabel.setText(String.valueOf(Math.round(currentPallets * 100.0) / 100.0));
            currentCapacityLabel.setText(String.valueOf(Math.round(currentCapacity * 100.0) / 100.0));
            currentWeightLabel.setText(String.valueOf(Math.round(currentWeight * 100.0) / 100.0));
            
        	if (currentCapacity > truckCapacity) {
				currentCapacityLabel.setForeground(Color.RED.brighter());
				JOptionPane.showMessageDialog(panel, "Current loaded volume is bigger than maximum truck capacity. Load more for your own risk.", "Maximum capacity reached", JOptionPane.WARNING_MESSAGE);
        	} else
        		currentCapacityLabel.setForeground(Color.WHITE);
			if (currentWeight > truckPermissibleLoad) {
				currentWeightLabel.setForeground(Color.RED.brighter());
				JOptionPane.showMessageDialog(panel, "Current loaded weight is bigger than truck permissible load. Weight of empty pallets included. Remove order or change truck for bigger to fit permissible load.", "Permissible load reached", JOptionPane.WARNING_MESSAGE);
			} else
	       		currentWeightLabel.setForeground(Color.WHITE);
			if (currentPallets > truckMaxPallets)
				currentPalletsLabel.setForeground(Color.ORANGE);
	       	else
	       		currentPalletsLabel.setForeground(Color.WHITE);
		} else {
			capacityProgressBar.setValue((int) Math.round(currentCapacity));
			permissibleLoadProgressBar.setValue((int) Math.round(currentWeight));
			currentPalletsLabel.setText(String.valueOf(Math.round(currentPallets * 100.0) / 100.0));
            currentCapacityLabel.setText(String.valueOf(Math.round(currentCapacity * 100.0) / 100.0));
            currentWeightLabel.setText(String.valueOf(Math.round(currentWeight * 100.0) / 100.0));
            currentCapacityLabel.setForeground(Color.WHITE);
            currentWeightLabel.setForeground(Color.WHITE);
            currentPalletsLabel.setForeground(Color.WHITE);            
		}		
	}

	protected void chooseTruckButtonAction() {
		JDialog dialog = new JDialog();				
		dialog.setLayout(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));				
				
		JButton ok = new JButton("OK");
		JButton cancel = new JButton("CANCEL");
						
		buttonPanel.add(ok);
		buttonPanel.add(cancel);
		
		dialog.add(buttonPanel, BorderLayout.SOUTH);
		
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("no.");
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		try(Connection conn = DB.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("SELECT truck_id, brand, model, year, registration_number, capacity, permissible_load, pallets FROM fleet");
			ResultSet rs = statement.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int no = 1;		
			for(int i = 1; i <= metaData.getColumnCount(); i++) {
				columnNames.add(metaData.getColumnName(i));
			}
			
			while(rs.next()) {
				Vector<Object> vector = new Vector<Object>();
				vector.add(no++);
				for(int j = 1; j <= metaData.getColumnCount(); j++) {
					vector.add(rs.getObject(j));
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
		
		JTable truckTable = new JTable(model);
		truckTable.getTableHeader().setReorderingAllowed(false);
		truckTable.setAutoCreateRowSorter(true);
		truckTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		truckTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		int visibleRows = 0;
		if(truckTable.getRowCount() < 20) {
			visibleRows = truckTable.getRowCount();
		} else 
			visibleRows = 20;
		
//		int visibleColumns = model.getColumnCount();
		visibleRows = Math.max(1, visibleRows);
//		visibleRows = Math.min(maxVisibleRows, visibleRows);
				
		Dimension size = truckTable.getPreferredScrollableViewportSize();
		size.height = truckTable.getRowHeight() * visibleRows;
		size.width = 1000;
		truckTable.setPreferredScrollableViewportSize(size);
		
		JScrollPane scrollPane = new JScrollPane(truckTable);
		
		dialog.add(scrollPane, BorderLayout.CENTER);
		dialog.pack();
		dialog.setResizable(false);
		dialog.setAlwaysOnTop(true);		
		ModalityType d = dialog.getModalityType();
		dialog.setModalityType(d.TOOLKIT_MODAL);
		
		int szer = Toolkit.getDefaultToolkit().getScreenSize().width;
		int wys = Toolkit.getDefaultToolkit().getScreenSize().height;
		int szerRamki = scrollPane.getSize().width;
		int wysRamki = scrollPane.getSize().height;

		dialog.setLocation((szer-szerRamki)/2,(wys-wysRamki)/2); // to centruje ramkê zawsze!!!			
		
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = truckTable.getSelectedRow();
				if (selectedRow != -1) {
					int truckIdColumn = truckTable.getColumnModel().getColumnIndex("truck_id");
					int truckbrandColumn = truckTable.getColumnModel().getColumnIndex("brand");
					int truckModelColumn = truckTable.getColumnModel().getColumnIndex("model");
					int truckYearColumn = truckTable.getColumnModel().getColumnIndex("year");
					int truckRegistrationNumberColumn = truckTable.getColumnModel().getColumnIndex("registration_number");
					int truckCapacityColumn = truckTable.getColumnModel().getColumnIndex("capacity");
					int truckPermissibleLoadColumn = truckTable.getColumnModel().getColumnIndex("permissible_load");
					int truckPalletsColumn = truckTable.getColumnModel().getColumnIndex("pallets");
					
					truckID = (int) truckTable.getValueAt(selectedRow, truckIdColumn);
					truckBrand = String.valueOf(truckTable.getValueAt(selectedRow, truckbrandColumn));
					truckModel = String.valueOf(truckTable.getValueAt(selectedRow, truckModelColumn));
					truckYear = String.valueOf(truckTable.getValueAt(selectedRow, truckYearColumn));
					truckRegistrationNumber = String.valueOf(truckTable.getValueAt(selectedRow, truckRegistrationNumberColumn));
					truckCapacity = (double) truckTable.getValueAt(selectedRow, truckCapacityColumn);
					truckPermissibleLoad = (double) truckTable.getValueAt(selectedRow, truckPermissibleLoadColumn);
					truckMaxPallets = (double) truckTable.getValueAt(selectedRow, truckPalletsColumn);	
					truckData.setText(" Selected truck : " + truckBrand + " | " + truckModel + " | " + truckYear + " | " + truckRegistrationNumber + " |");
					
					addToTruck.setEnabled(true);
					removeFromTruck.setEnabled(true);
					finalizeLoading.setEnabled(true);
//					chooseTruckButton.setEnabled(false);
					capacityProgressBar.setMaximum((int) Math.round(truckCapacity));
					permissibleLoadProgressBar.setMaximum((int) Math.round(truckPermissibleLoad));
					maxWeightLabel.setText(String.valueOf(truckPermissibleLoad));
					maxCapacityLabel.setText(String.valueOf(truckCapacity));
					maxPalletsLabel.setText(String.valueOf(truckMaxPallets));
					
					countWeightVolumeAndPallets();
					
					dialog.dispose();
				} else {
					JOptionPane.showMessageDialog(dialog, "Please select truck you want to load.", "Truck not selected", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();				
			}
		});
		
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);		
		dialog.setVisible(true);	
	}

	private void initializeNorthPanel() {
		panelNorth = new JPanel();
		panelNorth.setLayout(new BorderLayout());
		
		getDriversFromDB();
		
		menuBarNorthDown = new JMenuBar();
		menuBarNorthDown.add(addToTruck);
		menuBarNorthDown.add(removeFromTruck);
		menuBarNorthDown.add(chooseTruckButton);
		menuBarNorthDown.add(chooseDriever);
		
		menuBarNorthDown.add(driversCombobox);
		
		menuBarNorthDown.add(truckData);		
		
		createNorthTable();
		
		scrollNorth = new JScrollPane(tableNorth);		
		
		panelNorth.add(scrollNorth, BorderLayout.CENTER);
		panelNorth.add(menuBarNorthDown, BorderLayout.SOUTH);
		
		panel.add(panelNorth);
		
	}

	private void getDriversFromDB() {
		try (Connection conn = DB.getConnection()){
			driversCombobox = new JComboBox<String>();
			PreparedStatement statement = conn.prepareStatement("SELECT name, lastName, YWN FROM users WHERE access = 3");
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				driversCombobox.addItem(rs.getString("name") + " " + rs.getString("lastName") + " " + rs.getString("YWN"));
			}
			driversCombobox.addItem("");
			driversCombobox.setEditable(true);
			AutoCompleteDecorator.decorate(driversCombobox);
			driversCombobox.setSelectedItem("");
			Dimension size = driversCombobox.getPreferredSize();
			size.width = 200;
			driversCombobox.setMaximumSize(size);
			
		} catch (Exception e) {			
			e.printStackTrace();
		}		
	}

	protected void initializeSouthPanel() {
		
		maxCapacityLabel = new JLabel(); 
		currentCapacityLabel = new JLabel(); 	
		maxWeightLabel = new JLabel(); 
		currentWeightLabel = new JLabel(); 	
		currentPalletsLabel = new JLabel();
		maxPalletsLabel = new JLabel();
		
		addToTruck.setEnabled(false);
		removeFromTruck.setEnabled(false);
		finalizeLoading.setEnabled(false);
		
		driversCombobox.setSelectedItem("");
		truckData.setText("");
		
		panelSouth = new JPanel();
		panelSouth.setLayout(new BorderLayout());
		
		capacityProgressBar = new JProgressBar(JProgressBar.VERTICAL);
		capacityProgressBar.setBorderPainted(true);	
		capacityProgressBar.setStringPainted(true);
		capacityProgressBar.setValue(0);
		capacityProgressBar.setString("Capacity " + String.format("%.0f", capacityProgressBar.getPercentComplete()*100) + " %");
//		capacityProgressBar.setForeground(Color.lightGray.darker());
		capacityProgressBar.setFont(new Font(capacityProgressBar.getFont().getFamily(), Font.BOLD, 20));	
		
		capacityProgressBar.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				double percentComplete = capacityProgressBar.getPercentComplete() * 100;	
				capacityProgressBar.setString("Capacity " + String.format("%.0f", percentComplete) + " %");	
				
			}
		});
	
		permissibleLoadProgressBar = new JProgressBar(JProgressBar.VERTICAL);
		permissibleLoadProgressBar.setBorderPainted(true);	
		permissibleLoadProgressBar.setStringPainted(true);
		permissibleLoadProgressBar.setValue(0);
		permissibleLoadProgressBar.setString("Weight " + String.format("%.0f", permissibleLoadProgressBar.getPercentComplete()*100) + " %");
//		permissibleLoadProgressBar.setForeground(Color.lightGray.darker());
		permissibleLoadProgressBar.setFont(new Font(permissibleLoadProgressBar.getFont().getFamily(), Font.BOLD, 20));
		
		permissibleLoadProgressBar.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {				
				double percentComplete = permissibleLoadProgressBar.getPercentComplete() * 100;
				permissibleLoadProgressBar.setString("Weight " + String.format("%.0f", percentComplete) + " %");
				
			}
		});
		
		tableSouth = createSouthTable();
		scrollSouth = new JScrollPane(tableSouth);
		
		menuBarSouthDown = new JMenuBar();
		menuBarSouthDown.add(finalizeLoading);		
		
		panelSouthDown = new JPanel();
		panelSouthDown.setLayout(new BorderLayout());
		
		panelSouthDownRight = new JPanel();
		panelSouthDownRight.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelSouthDownRight.add(weightLabel);
		panelSouthDownRight.add(currentWeightLabel);
		panelSouthDownRight.add(betweenLabel1);
		panelSouthDownRight.add(maxWeightLabel);
		panelSouthDownRight.add(betweenLabel2);
		panelSouthDownRight.add(capacityLabel);
		panelSouthDownRight.add(currentCapacityLabel);
		panelSouthDownRight.add(betweenLabel3);
		panelSouthDownRight.add(maxCapacityLabel);
		panelSouthDownRight.add(betweenLabel5);		
		panelSouthDownRight.add(palletsLabel);
		panelSouthDownRight.add(currentPalletsLabel);
		panelSouthDownRight.add(betweenLabel4);
		panelSouthDownRight.add(maxPalletsLabel);
		panelSouthDownRight.add(betweenLabel6);
		
		panelSouthDown.add(menuBarSouthDown, BorderLayout.WEST);
		panelSouthDown.add(panelSouthDownRight, BorderLayout.EAST);
		
		panelSouth.add(panelSouthDown, BorderLayout.SOUTH);
		panelSouth.add(scrollSouth, BorderLayout.CENTER);
		panelSouth.add(capacityProgressBar, BorderLayout.EAST);
		panelSouth.add(permissibleLoadProgressBar, BorderLayout.WEST);
				
		panel.add(panelSouth);
		
		countWeightVolumeAndPallets();
	}
	
	private JTable createSouthTable() {
		Vector<String> columnNames = new Vector<String>();
		roadCardData = new Vector<Vector<Object>>();
		
		for(int i = 0; i < 12; i++) {
			if (!tableNorth.getColumnName(i).equals("percent_complete"))
				columnNames.add(tableNorth.getColumnName(i));			
		}
		
		DefaultTableModel model = new DefaultTableModel(roadCardData, columnNames) {
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				Class retVal = Object.class;

		        if(getRowCount() > 0)
		            retVal =  getValueAt(0, columnIndex).getClass();

		        return retVal;
			}
		};
		
		tableSouth = new JTable(model);
		tableSouth.setAutoCreateRowSorter(true);
		tableSouth.getTableHeader().setReorderingAllowed(false);	
		tableSouth.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
		
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
		cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		
		int no = tableSouth.getColumnModel().getColumnIndex("no.");
		int pcs = tableSouth.getColumnModel().getColumnIndex("pcs");
		int address = tableSouth.getColumnModel().getColumnIndex("address");
		int invoiceNumber = tableSouth.getColumnModel().getColumnIndex("invoice_number");
		tableSouth.getColumnModel().getColumn(no).setMaxWidth(40);
		tableSouth.getColumnModel().getColumn(no).setCellRenderer(cellRenderer);
		tableSouth.getColumnModel().getColumn(pcs).setMaxWidth(40);
		tableSouth.getColumnModel().getColumn(pcs).setCellRenderer(cellRenderer);
		tableSouth.getColumnModel().getColumn(address).setPreferredWidth(200);
		tableSouth.getColumnModel().getColumn(invoiceNumber).setPreferredWidth(100);
		tableSouth.getColumnModel().getColumn(invoiceNumber).setCellRenderer(cellRenderer);;
		
		tableSouth.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getType()==TableModelEvent.INSERT||e.getType()==TableModelEvent.DELETE) {
//	                currentPalletsLabel.setText(String.valueOf(Math.round(currentPallets * 100.0) / 100.0));
//	                currentCapacityLabel.setText(String.valueOf(Math.round(currentCapacity * 100.0) / 100.0));
//	                currentWeightLabel.setText(String.valueOf(Math.round(currentWeight * 100.0) / 100.0));	                	            
				}				
			}
		});
		
		return tableSouth;
	}

	private void createNorthTable() {
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<String> columnNamesNorth = new Vector<String>();
		columnNamesNorth.add("no.");
		
		try {
			Connection conn = DB.getConnection();
			PreparedStatement statement = conn.prepareStatement("SELECT c.name, CONCAT(c.street, ' ', c.zip_code, ' ', c.city, ' ', c.country) AS address, o.order_id, o.date, o.weight, o.volume, o.pcs, o.amount_of_products, o.value, o.gross_value, o.invoice_number, o.status, o.invoice, o.loaded"
					+ " FROM orders o JOIN clients c ON o.client_id = c.id WHERE loaded = false ORDER BY o.date ASC");
			ResultSet rs = statement.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			for(int i = 1; i <= metaData.getColumnCount(); i++) {
				columnNamesNorth.add(metaData.getColumnName(i));				
			}			
//			columnNamesNorth.add(2, "address");
			int k = 1;
			while(rs.next()) {
				Vector<Object> vector = new Vector<Object>();
				vector.add(k++);
				vector.add(rs.getString("name"));
				vector.add(rs.getString("address"));				
				vector.add(rs.getString("order_id"));
				vector.add(rs.getDate("date"));
				vector.add(rs.getDouble("weight"));
				vector.add(rs.getInt("volume"));
				vector.add(rs.getInt("pcs"));
				vector.add(rs.getInt("amount_of_products"));
				vector.add(Math.round(rs.getDouble("value") * 100.0) / 100.0);		
				vector.add(Math.round(rs.getDouble("gross_value") * 100.0) / 100.0);					
//				vector.add(rs.getDouble("percent_complete"));
				vector.add(rs.getString("invoice_number"));				
				vector.add(rs.getBoolean("status"));
				vector.add(rs.getBoolean("invoice"));
				vector.add(rs.getBoolean("loaded"));
											
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
		
		tableNorth = new JTable();
		tableNorth.setModel(defaultTableModel);
		tableNorth.setAutoCreateRowSorter(true);
		tableNorth.getTableHeader().setReorderingAllowed(false);
		
		
		int pcs = tableNorth.getColumnModel().getColumnIndex("pcs");
		int status = tableNorth.getColumnModel().getColumnIndex("status");
		int invoice = tableNorth.getColumnModel().getColumnIndex("invoice");
		int orderID = tableNorth.getColumnModel().getColumnIndex("order_id");
		int invoiceNumber = tableNorth.getColumnModel().getColumnIndex("invoice_number");		
		int loaded = tableNorth.getColumnModel().getColumnIndex("loaded");		
		int address = tableNorth.getColumnModel().getColumnIndex("address");		
		int date = tableNorth.getColumnModel().getColumnIndex("date");		
		int weight = tableNorth.getColumnModel().getColumnIndex("weight");		
		int volume = tableNorth.getColumnModel().getColumnIndex("volume");		
		int amountOfProducts = tableNorth.getColumnModel().getColumnIndex("amount_of_products");		
		
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
		cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		
		tableNorth.getColumnModel().getColumn(0).setMaxWidth(40);
		tableNorth.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);
		tableNorth.getColumnModel().getColumn(pcs).setMaxWidth(40);
		tableNorth.getColumnModel().getColumn(pcs).setCellRenderer(cellRenderer);
		tableNorth.getColumnModel().getColumn(date).setMaxWidth(80);
		tableNorth.getColumnModel().getColumn(date).setCellRenderer(cellRenderer);	
		tableNorth.getColumnModel().getColumn(weight).setMaxWidth(80);
		tableNorth.getColumnModel().getColumn(weight).setCellRenderer(cellRenderer);		
		tableNorth.getColumnModel().getColumn(volume).setMaxWidth(80);
		tableNorth.getColumnModel().getColumn(volume).setCellRenderer(cellRenderer);			
		tableNorth.getColumnModel().getColumn(status).setMaxWidth(50);
		tableNorth.getColumnModel().getColumn(invoice).setMaxWidth(50);	
		tableNorth.getColumnModel().getColumn(orderID).setMaxWidth(120);	
		tableNorth.getColumnModel().getColumn(orderID).setPreferredWidth(120);
		tableNorth.getColumnModel().getColumn(orderID).setCellRenderer(cellRenderer);
		tableNorth.getColumnModel().getColumn(invoiceNumber).setMaxWidth(140);	
		tableNorth.getColumnModel().getColumn(invoiceNumber).setPreferredWidth(140);
		tableNorth.getColumnModel().getColumn(address).setPreferredWidth(240);
		tableNorth.getColumnModel().getColumn(invoiceNumber).setCellRenderer(cellRenderer);
		tableNorth.getColumnModel().getColumn(loaded).setMaxWidth(50);		
		tableNorth.getColumnModel().getColumn(amountOfProducts).setPreferredWidth(100);
		
		tableNorth.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
		
		tableNorth.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedRow = tableNorth.getSelectedRow();
				if(selectedRow != -1) {
					int orderColumn = tableNorth.getColumnModel().getColumnIndex("order_id");
					int clientName = tableNorth.getColumnModel().getColumnIndex("name");
					whatOrder = String.valueOf(tableNorth.getValueAt(selectedRow, orderColumn));
					whatClient = String.valueOf(tableNorth.getValueAt(selectedRow, clientName));
					System.out.println(whatClient + " " + whatOrder);
				}				
			}
		});	
		
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
