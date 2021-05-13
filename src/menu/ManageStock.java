package menu;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import dbConnectionAndMethods.DB;

public class ManageStock extends JPanel {
	

	protected JPanel panel;
	protected JPanel southPanel;
	protected JScrollPane scroll;
	private ArrayList<JComponent> textFieldList;
	private JPanel southPanelUP;
	private String tableName = "productlibrary";
	private JPanel northPanel;
	private JTable tableProductLibrary;
	
	/*
	 * Stock objects
	 */
	private JTable stockTable;
	private JButton increaseAmount;
	private JButton removeAmount;
	private JTextField increaseAmountTextField;
	private JTextField removeAmountTextField;
	
	
	
	
	public ManageStock() {
//		super();
		panel = this;
		
		initComponents();
		
	
	}
	void initComponents(){
		
		panel.setBounds(300, 300, 300, 200);
		panel.setVisible(true);
		panel.setBorder(new EtchedBorder(EtchedBorder.RAISED, new Color(255, 215, 0), new Color(240, 248, 255)));
		panel.setLayout(new BorderLayout());
		for(int i = 0; i < 24; i++) {
			vatComboBox.addItem(i);
		}
		vatComboBox.setSelectedItem(23);
		
		actionsForComponents();
		
		initializeMainButtons();
		
		try {
			panel.removeAll();
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
		
		initializeMainButtons();
		
		Stock stockObject = new Stock();
		stockTable = stockObject.stockTable;
		scroll = new JScrollPane(stockTable); 
		panel.add(scroll, BorderLayout.CENTER);
		
		
		
		inilializeStockButtons();
	
	}
	
	private void actionsForComponents() {
		
		productLibrary.addActionListener(new ActionListener() {
			
			

			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					panel.removeAll();
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
				
				initializeMainButtons();
				
				ProductLibrary productLibrary = new ProductLibrary();
				tableProductLibrary = productLibrary.table;
				scroll = new JScrollPane(tableProductLibrary); 
				panel.add(scroll, BorderLayout.CENTER);
				
				tableProductLibrary.getModel().addTableModelListener(new TableModelListener() {
					
					@Override
					public void tableChanged(TableModelEvent e) { 
						if(e.getColumn()!= 9 && e.getColumn() != 5) {
							DB.updateDbFromTableCell(tableProductLibrary, "productlibrary", e);
						} else if(e.getColumn() == 5) {
							int selectedRow = e.getFirstRow();
							String valueFromTable = String.valueOf(tableProductLibrary.getValueAt(selectedRow, 5));
							if(valueFromTable.matches("^\\d+$") || valueFromTable.matches("^(\\d+).(\\d+)$")) {
								DB.updateDbFromTableCell(tableProductLibrary, "productlibrary", e);
							} else {
								JOptionPane.showMessageDialog(panel, "You can use Integer or double only.", "Wrong number", JOptionPane.ERROR_MESSAGE);
								refreshMethod();
							}
						} else {
							int selectedRow = e.getFirstRow();
							String valueFromTable = String.valueOf(tableProductLibrary.getValueAt(selectedRow, 9));
							if(valueFromTable.matches("^\\d+$")) {
								DB.updateDbFromTableCell(tableProductLibrary, "productlibrary", e);
							} else {
								JOptionPane.showMessageDialog(panel, "You can only use Integer", "Wrong number", JOptionPane.ERROR_MESSAGE);
								refreshMethod();
							}
						}
						
					}					
				});
				
				initializeLibraryButtons();
				
				
			}
		});
		
		stock.addActionListener(new ActionListener() {
			
			

			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					panel.removeAll();
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
				
				initializeMainButtons();
				
				Stock stockObject = new Stock();
				stockTable = stockObject.stockTable;
				scroll = new JScrollPane(stockTable); 
				panel.add(scroll, BorderLayout.CENTER);
				
				inilializeStockButtons();
				
				
			}
		});
		
		
		okAddingButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				okAddingButtonAction();
				
			}
		});
		
		removeButton.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (tableProductLibrary.isEditing())
			    tableProductLibrary.getCellEditor().stopCellEditing();
			
			try {
				int col = tableProductLibrary.getSelectedColumn();
				int row = tableProductLibrary.getSelectedRow();
				System.out.println("" + col + row );
				String idValue = String.valueOf(tableProductLibrary.getValueAt(row, 1));
				
				DB.removeRowFromDB(tableName, idValue, scroll);
				refreshMethod();
			} catch (IndexOutOfBoundsException e1) {
				
				JOptionPane.showMessageDialog(scroll, "You have to choose product to remove.", "Product is not selected.", JOptionPane.WARNING_MESSAGE);
			}
		}
	});
		
		barcodeTextField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if(!(e.getKeyChar()>= '0' && e.getKeyChar() <= '9'))
					e.consume();
			
			}				
		});
		
		volumeTextField.addKeyListener(new KeyAdapter() {
		
			@Override
			public void keyTyped(KeyEvent e) {
				if(!(e.getKeyChar()>= '0' && e.getKeyChar() <= '9' || e.getKeyChar() == '.'))
					e.consume();
			
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()== KeyEvent.VK_ENTER)
					okAddingButtonAction();
			}
			
		});
		
		weightTextField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if(!(e.getKeyChar()>= '0' && e.getKeyChar() <= '9' || e.getKeyChar() == '.'))
					e.consume();
			}
		});
		
		
	}
	
	private void initializeMainButtons(){
		
		northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		JPanel southPanel = new JPanel();
		JPanel southPanelUP = new JPanel();
		southPanel.setLayout(new BorderLayout());
		southPanelUP.setLayout(new FlowLayout()); 
		
		
		stock.setForeground(Color.RED); 		 stock.setFont(new Font("Tahoma", Font.PLAIN, 13));
		stock.setIcon(new ImageIcon("img/Home.png"));
		productLibrary.setForeground(Color.RED); productLibrary.setFont(new Font("Tahoma", Font.PLAIN, 13));
		productLibrary.setIcon(new ImageIcon("img/Table.png"));
		okAddingButton.setIcon(new ImageIcon("img/Apply.png"));
		
		southPanel = new JPanel();
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(stock); 
		menuBar.add(productLibrary); 
		
		
		northPanel.add(menuBar, BorderLayout.WEST);
		
		panel.add(northPanel, BorderLayout.NORTH);
		
		
		
	}
	
	private void inilializeStockButtons() {
		increaseAmount = new JButton("Increase amount");		 increaseAmount.setForeground(Color.RED); 		 increaseAmount.setFont(new Font("Tahoma", Font.PLAIN, 13));
		removeAmount = new JButton("Decrease amount");			 removeAmount.setForeground(Color.RED); 		 removeAmount.setFont(new Font("Tahoma", Font.PLAIN, 13));
		JButton refreshButton = new JButton("Refresh");			 refreshButton.setForeground(Color.RED); 		 refreshButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		JButton calculateButton = new JButton("Calculate");		 calculateButton.setForeground(Color.RED); 		 calculateButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		increaseAmount.setIcon(new ImageIcon("img/arrowUp.png"));
		removeAmount.setIcon(new ImageIcon("img/Down.png"));
		refreshButton.setIcon(new ImageIcon("img/refresh.png"));
		calculateButton.setIcon(new ImageIcon("img/Calculator.png"));
		
		calculateButton.setToolTipText("All rows in the table will be calculated with purchase prise and markup to sell prise.");
		
		JButton printButton = new JButton("Print"); 			  printButton.setForeground(Color.RED); 		 printButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		printButton.setIcon(new ImageIcon("img/Print.png"));
		
		JLabel message = new JLabel("Some columns in the table below may be editet.       ");	
		northPanel.add(message, BorderLayout.EAST);
		
		JMenuBar menuBarBottom = new JMenuBar();
		menuBarBottom.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		
		
		increaseAmountTextField = new JTextField();
		increaseAmountTextField.setPreferredSize(new Dimension(100,14));
		increaseAmountTextField.setText("0.0 or 0");
		increaseAmountTextField.addFocusListener(new FocusAdapter() {
		
		
		
			
			@Override
			public void focusGained(FocusEvent e) {
				increaseAmountTextField.setText("");
				
			}
		});
		
		
		removeAmountTextField = new JTextField();
		removeAmountTextField.setPreferredSize(new Dimension(100,14));
		removeAmountTextField.setText("0.0 or 0");
		removeAmountTextField.addFocusListener(new FocusAdapter() {
			
			
			@Override
			public void focusGained(FocusEvent e) {
				removeAmountTextField.setText("");
				
			}
		});
	
		
		if(menuBarBottom.getComponentCount()<3) {
			menuBarBottom.add(increaseAmount);
			menuBarBottom.add(increaseAmountTextField);
			
			menuBarBottom.add(removeAmount);
			menuBarBottom.add(removeAmountTextField);
			
			menuBarBottom.add(calculateButton);
			menuBarBottom.add(printButton);
			
			
			menuBarBottom.add(refreshButton);
		}
		southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		southPanel.add(menuBarBottom, BorderLayout.WEST);
		panel.add(southPanel, BorderLayout.SOUTH);
		
		increaseAmount.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (stockTable.isEditing())
				    stockTable.getCellEditor().stopCellEditing();
						
					try {
							int row = stockTable.getSelectedRow();
							System.out.println(stockTable.getValueAt(row, 12));
							double oldAmount =  Double.parseDouble(String.valueOf(stockTable.getValueAt(row, 12)));
							String unit = String.valueOf(stockTable.getValueAt(row, 6));
										
							if(unit.equals("pcs")) {

								if(increaseAmountTextField.getText().toString().matches("^\\d+$")) {
								double newAmount = oldAmount + Double.parseDouble(increaseAmountTextField.getText());								
								stockTable.setValueAt(newAmount, row, 12);
								
								refreshStockMethod();
								} else {
									JOptionPane.showMessageDialog(scroll, "Unit of this product is 'pcs'. Use integer to increase.", "Wrong number", JOptionPane.WARNING_MESSAGE);
									increaseAmountTextField.requestFocus();
								}
							}
							if (unit.equals("kg")) {
								if(increaseAmountTextField.getText().toString().matches("^\\d+$") || increaseAmountTextField.getText().toString().matches("^(\\d+).(\\d+)$")) {
									double newAmount = oldAmount + Double.parseDouble(increaseAmountTextField.getText());									
									stockTable.setValueAt(newAmount, row, 12);
									refreshStockMethod();
								} else {
									JOptionPane.showMessageDialog(scroll, "Wrong number. Please use one of this number format:  '0' or '0.0'." , "Wrong number", JOptionPane.WARNING_MESSAGE);
								}
							}				
					} catch (IndexOutOfBoundsException e1) {
						JOptionPane.showMessageDialog(scroll, "You have to select in data table which amount of product you want to increase.", "Product not selected", JOptionPane.WARNING_MESSAGE);
					}	
			}
		});
		
		removeAmount.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (stockTable.isEditing())
				    stockTable.getCellEditor().stopCellEditing();
						
					try {
							int row = stockTable.getSelectedRow();
							System.out.println(stockTable.getValueAt(row, 12));
							double oldAmount =  Double.parseDouble(String.valueOf(stockTable.getValueAt(row, 12)));
							String unit = String.valueOf(stockTable.getValueAt(row, 6));
										
							if(unit.equals("pcs")) {

								if(removeAmountTextField.getText().toString().matches("^\\d+$")) {
									double amountToRemove = Double.parseDouble(removeAmountTextField.getText());
									if(amountToRemove <= oldAmount) {
										double newAmount = oldAmount - amountToRemove;										
										stockTable.setValueAt(newAmount, row, 12);
										
									}else {
										JOptionPane.showMessageDialog(scroll, "You can't remove more than there is in stock.", "Stock remove warning", JOptionPane.WARNING_MESSAGE);
										removeAmountTextField.requestFocus();
									}
									
								refreshStockMethod();
								} else {
									JOptionPane.showMessageDialog(scroll, "Unit of this product is 'pcs'. Use integer to decrease.", "Wrong number", JOptionPane.WARNING_MESSAGE);
									removeAmountTextField.requestFocus();
								}
							}
							if (unit.equals("kg")) {
								if(removeAmountTextField.getText().toString().matches("^\\d+$") || removeAmountTextField.getText().toString().matches("^(\\d+).(\\d+)$")) {
									double amountToRemove = Double.parseDouble(removeAmountTextField.getText());
									if(amountToRemove <= oldAmount) {
										double newAmount = oldAmount - amountToRemove;										
										stockTable.setValueAt(newAmount, row, 12);
										refreshStockMethod();
									} else {
										JOptionPane.showMessageDialog(scroll, "You can't remove more than there is in stock.", "Stock remove warning", JOptionPane.WARNING_MESSAGE);
										removeAmountTextField.requestFocus();
									}
									
								} else {
									JOptionPane.showMessageDialog(scroll, "Wrong number. Please use one of this number format:  '0' or '0.0'." , "Wrong number", JOptionPane.WARNING_MESSAGE);
								}
							}				
					} catch (IndexOutOfBoundsException e1) {
						JOptionPane.showMessageDialog(scroll, "You have to select in data table which amount of product you want to increase.", "Product not selected", JOptionPane.WARNING_MESSAGE);
					}
				
			}
		});
	
		refreshButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshStockMethod();
				
			}
		});
	
		calculateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < stockTable.getRowCount(); i++) {
			
						double purchasePrice = Double.parseDouble((String) stockTable.getValueAt(i, 9));
						double markup = Double.parseDouble((String) stockTable.getValueAt(i, 10));
						double result = purchasePrice + (purchasePrice*markup/100);
						result = result*100;
						result = Math.round(result);
						result = result/100;						
						stockTable.setValueAt(result, i, 11);
				}
				
			}
		});
	
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					stockTable.print();
				} catch (PrinterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
	}
	
	private void initializeLibraryButtons() {
		
		JButton addButton = new JButton("Add Product");			 addButton.setForeground(Color.RED); 			 addButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		addButton.setIcon(new ImageIcon("img/Create.png"));
		removeButton.setForeground(Color.RED); 		 			 removeButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		removeButton.setIcon(new ImageIcon("img/Remove.png"));
		JButton refreshButton = new JButton("Refresh");			 refreshButton.setForeground(Color.RED); 		 refreshButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		refreshButton.setIcon(new ImageIcon("img/refresh.png"));
		JButton printButton = new JButton("Print");				 printButton.setForeground(Color.RED); 		 	printButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		printButton.setIcon(new ImageIcon("img/Print.png"));
		
		JLabel message = new JLabel("Some columns in the table below may be editet.       ");	
		northPanel.add(message, BorderLayout.EAST);
		
		
		JMenuBar menuBarBottom = new JMenuBar();
		menuBarBottom.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		 
		
		if(menuBarBottom.getComponentCount()<4) {
			menuBarBottom.add(addButton);
			menuBarBottom.add(removeButton);
			menuBarBottom.add(printButton);
			menuBarBottom.add(refreshButton);
		}
		southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		southPanel.add(menuBarBottom, BorderLayout.WEST);
		panel.add(southPanel, BorderLayout.SOUTH);
		
		
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
					
				initializeLabelsAntTextFields();
				nameTextField.requestFocus();
			}
				
			
		});
		
		refreshButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				refreshMethod();
				
			}
		});
		
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					tableProductLibrary.print();
				} catch (PrinterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
	}
	
	private void initializeLabelsAntTextFields() {
		
	
		southPanelUP = new JPanel();
		FlowLayout flowLayout = (FlowLayout) southPanelUP.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		
		if (southPanelUP.getComponentCount() < 13) {
		
		southPanelUP.add(nameLabel);
		southPanelUP.add(nameTextField);
		southPanelUP.add(menufacturerLabel);
		southPanelUP.add(manufacturerTextField);
		southPanelUP.add(countyOfProductionLabel);
		southPanelUP.add(countryOfProductionTextField);
		southPanelUP.add(weightLabel);
		southPanelUP.add(weightTextField);
		southPanelUP.add(unitLabel);
		southPanelUP.add(unitComboBox);
		southPanelUP.add(barcode);
		southPanelUP.add(barcodeTextField);
		southPanelUP.add(volumeLabel);
		southPanelUP.add(volumeTextField);
		southPanelUP.add(vatLabel);		
		southPanelUP.add(vatComboBox);
		
		southPanelUP.add(okAddingButton);
		
		
		}
		
		try {
			textFieldList.clear();
			System.out.println("Textfield list cleared");
		} catch (Exception e1) {
			
			e1.getMessage();
		}
		
		textFieldList = new ArrayList<JComponent>();
		textFieldList.add(nameTextField);	
		textFieldList.add(manufacturerTextField);		
		textFieldList.add(countryOfProductionTextField);	
		textFieldList.add(weightTextField);
		textFieldList.add(barcodeTextField);
		textFieldList.add(volumeTextField);
		
//		okAddingButton.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				
//				okAddingButtonAction();
//				
//			}
//		});
		
		
		southPanel.add(southPanelUP, BorderLayout.NORTH);
		
			
	}
	
	protected void refreshMethod() {
		try {
			panel.remove(scroll);
			System.out.println("Scroll removed");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.getMessage();
		}
		if (scroll!= null) {
			System.out.println("Refresh making scrool = null");
			scroll = null;
		}
		ProductLibrary productLibrary = new ProductLibrary();
		tableProductLibrary = productLibrary.table;
		tableProductLibrary.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scroll = new JScrollPane(productLibrary.table); 
		panel.add(scroll, BorderLayout.CENTER);
		tableProductLibrary.setRowSelectionInterval(tableProductLibrary.getRowCount()-1, tableProductLibrary.getRowCount()-1);
		tableProductLibrary.scrollRectToVisible(tableProductLibrary.getCellRect(tableProductLibrary.getRowCount()-1, tableProductLibrary.getColumnCount()-1, true));
		
		tableProductLibrary.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) { 
				if(e.getColumn()!= 9) {
					DB.updateDbFromTableCell(tableProductLibrary, "productlibrary", e);
				} else if(e.getColumn() == 5) {
					int selectedRow = e.getFirstRow();
					String valueFromTable = String.valueOf(tableProductLibrary.getValueAt(selectedRow, 5));
					if(valueFromTable.matches("^\\d+$") || valueFromTable.matches("^(\\d+).(\\d+)$")) {
						DB.updateDbFromTableCell(tableProductLibrary, "productlibrary", e);
					} else {
						JOptionPane.showMessageDialog(panel, "You can use Integer or double only.", "Wrong number", JOptionPane.ERROR_MESSAGE);
						refreshMethod();
					}
				} else {
					int selectedRow = e.getFirstRow();
					String valueFromTable = String.valueOf(tableProductLibrary.getValueAt(selectedRow, 9));
					if(valueFromTable.matches("^\\d+$")) {
						DB.updateDbFromTableCell(tableProductLibrary, "productlibrary", e);
					} else {
						JOptionPane.showMessageDialog(panel, "You can only use Integer.", "Wrong number", JOptionPane.ERROR_MESSAGE);
						refreshMethod();
					}
				}
				
			}					
		});
	}
	
	protected void refreshStockMethod() {
		int isRowSelected = stockTable.getSelectedRow();
		int isColumnSelected = stockTable.getSelectedColumn();
		try {
			panel.remove(scroll);
			System.out.println("Scroll removed");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.getMessage();
		}
		if (scroll!= null) {
			System.out.println("Refresh making scrool = null");
			scroll = null;
		}
		Stock stockObject = new Stock();
		stockTable = stockObject.stockTable;
		scroll = new JScrollPane(stockTable); 
		panel.add(scroll, BorderLayout.CENTER);
		
		
		System.out.println(isRowSelected);
		if(isRowSelected != -1) {
			stockTable.setRowSelectionInterval(isRowSelected, isRowSelected);
			stockTable.scrollRectToVisible(stockTable.getCellRect(isRowSelected, isColumnSelected, true));
		}
		
	}
	
	protected void okAddingButtonAction() {
		Boolean flag = false;
				
		for(JComponent t: textFieldList) {
			String z = ((JTextField) t).getText();
			if (z.length() == 0){
				System.out.println("There is a blank field");
				flag = true;
				JOptionPane.showConfirmDialog(scroll, "You have to fill all text fields. Product has to be full described.", "Adding Issue", JOptionPane.CLOSED_OPTION, JOptionPane.WARNING_MESSAGE);
				t.requestFocus();
				break;
							
			}
		}
		
		if (flag == false && !barcodeTextField.getText().toString().matches("^\\d+$"))
		{
			flag = true;
			JOptionPane.showMessageDialog(scroll, "Barcode must be a digit sequence. ", "Invalid barcode", JOptionPane.WARNING_MESSAGE);
			barcodeTextField.requestFocus(true);
		}
		
		if((volumeTextField.getText().toString().matches("^\\d+$") || volumeTextField.getText().toString().matches("^(\\d+).(\\d+)$")) && Double.parseDouble(volumeTextField.getText()) < 1650) {
		
		
			if(flag == false) {
				String capitalizedName = nameTextField.getText().substring(0,1).toUpperCase() + nameTextField.getText().substring(1);
				String capitalizedManufacturer = manufacturerTextField.getText().substring(0,1).toUpperCase() + manufacturerTextField.getText().substring(1);
				DB.addProductToLibrary(capitalizedName, capitalizedManufacturer, countryOfProductionTextField.getText().toUpperCase(), 
						weightTextField.getText(), unitComboBox.getSelectedItem().toString(), barcodeTextField.getText().toString(), volumeTextField.getText().toString(), scroll, vatComboBox.getSelectedItem().toString());
				
				refreshMethod();
			}
		} else if (volumeTextField.getText() == "") {
			JOptionPane.showMessageDialog(scroll, "Volume must be the number one of this example: '1' or '0.4'. Values between 0 - 1650 dm3. ", "Invalid volume", JOptionPane.WARNING_MESSAGE);
			volumeTextField.requestFocus();
		} else {
			JOptionPane.showMessageDialog(scroll, "Volume must be the number one of this example: '1' or '0.4'. Values between 0 - 1650 dm3. ", "Invalid volume", JOptionPane.WARNING_MESSAGE);
			volumeTextField.requestFocus();
		}
				
	}
	
	/*
	 * Product library configuration
	 */
	private JLabel nameLabel = new JLabel("Name: ");
	private JLabel menufacturerLabel = new JLabel("Manufacturer: ");
	private JLabel countyOfProductionLabel = new JLabel("Country of production: ");
	private JLabel weightLabel = new JLabel("Weight (kg): ");
	private JLabel unitLabel = new JLabel("Unit: ");
	private JLabel barcode = new JLabel("Barcode: ");
	private JLabel volumeLabel = new JLabel("Volume: ");
	private JLabel vatLabel = new JLabel("Vat: ");
	
	
	private JButton okAddingButton = new JButton("Add product");
	
	
	
	private JTextField nameTextField = new JTextField(10);
	private JTextField manufacturerTextField = new JTextField(10);
	private JTextField countryOfProductionTextField = new JTextField(10);
	private JTextField weightTextField = new JTextField(5);
	
	private JComboBox<String> unitComboBox = new JComboBox<String>(new String[] {"pcs", "kg"});
	private JComboBox<Integer> vatComboBox = new JComboBox<Integer>();
		
	private JTextField barcodeTextField = new JTextField(10);
	private JTextField volumeTextField = new JTextField(10);
	
	private	JButton removeButton = new JButton("Remove product");
	private JButton stock = new JButton("Stock"); 
	private JButton productLibrary = new JButton("Product Library"); 
	
		public static void main(String[] args) {
			TreeMap<String,String> map = new TreeMap<String, String>();
			map.put("name", "Paweł");
			map.put("lastName", "Dąbrowski");
			map.put("id", "4");
			map.put("login", "d");
			map.put("YWN", "20205999");
			map.put("password", "d");
			map.put("access", "1");
			
			Menu menu = new Menu(map);
}}

