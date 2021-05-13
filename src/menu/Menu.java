package menu;


import closabeltabbedpane.tabbedpane.ClosableTabbedPane;
import utilities.Clock;
import utilities.JTatoo;
import utilities.StaticMethods;
import net.miginfocom.swing.MigLayout;
import user.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

public class Menu {
	private JFrame frame = new JFrame();
	private User user;
	private JPanel northPanel = new JPanel();
	private JPanel southPanel = new JPanel();
	private JPanel eastPanel = new JPanel();
	private JPanel westPanel = new JPanel();
	private JPanel leftTopCorner = new JPanel();
	private JPanel westTop = new JPanel();
	private JPanel westSouth = new JPanel();	
	
	private JLabel titleLabel = new JLabel("<- - - YOUR WAREHOUSE MENU - - ->");
	private JLabel clock = Clock.clock;
	private JLabel userLabel = new JLabel();
	private JLabel ywnLabel = new JLabel(); 	
	
	private JButton manageStock = new JButton("Manage Stock");
	private JButton createOrder = new JButton("Create Order");
	private JButton manageOrders = new JButton("Manage Orders");
	private JButton completion = new JButton("Completion");
	private JButton loading = new JButton("Loading");
	private JButton manageRoadCards = new JButton("Manage Road Cards");
	private JButton manageClients = new JButton("Manage Clients");
	private JButton manageEmployees = new JButton("Manage Employees");
	private JButton manageDocuments = new JButton("Manage Documents");
	private JButton fleet = new JButton("Manage Fleet");
	private JButton exit = new JButton("Exit");
	
	private JDesktopPane desktopPane = new JDesktopPane();
	
	private ClosableTabbedPane closableTabbedPane = new ClosableTabbedPane();
	private JTabbedPane tabbedPane = new JTabbedPane();
	
	private ArrayList<JButton> buttonsList = new ArrayList<JButton>();
	private JPanel photoPanel;
	private JLabel photoLabel;
	protected JScrollPane scroll;
	
//	public Menu() {
//		initComponents();
//	}
	
	public Menu(TreeMap<String, String> map) {
		user = new User(map);
		
		initComponents();
		modifyButtonForByPermission(user.getAccess());		
	}
	
	/**
	 * Method detect access of current user and make modules active or disable for particular access
	 * @param access
	 */
	private void modifyButtonForByPermission(int access) {
		switch(access) {
		case 1:
			break;
		case 2:
			break;
		case 3:
			for(JButton button : buttonsList)
				button.setEnabled(false);
			exit.setEnabled(true);
			break;
		case 4:
			for(JButton button : buttonsList) {
				String buttonText = button.getText();
				if(!buttonText.equals(completion.getText()) && !buttonText.equals(loading.getText()) && !buttonText.equals(exit.getText()) )
					button.setEnabled(false);			
			}
			
			break;
		default:
			for(JButton button : buttonsList)
				button.setEnabled(false);
			exit.setEnabled(true);
			break;
		}
	
	}
/**
 * This method initialize frame(program menu)
 */
	private void initComponents() {		
	    
		/*
		 * Initialize frame, clock, look
		 */
		
		frame.setVisible(true);
		ActionListener stoper = new Clock();
		Timer zegar = new Timer(1000, stoper);
		zegar.start();
		JTatoo.setLookAndFeel("Noire", frame);
		
		/**
		 * Frame configuration
		 */
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		
		frame.setTitle("Your Warehouse");
		frame.setBounds(0, 0, width, height-300);
		frame.setLayout(new BorderLayout());
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(3);

		
		/**
		 * Panels configuration
		 */
		northPanel.setBackground(Color.BLACK);
		northPanel.setLayout(new BorderLayout(0,0));
		northPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, new Color(255, 215, 0), new Color(240, 248, 255)));
		
		leftTopCorner.setBackground(Color.BLACK);
		leftTopCorner.setLayout(new GridLayout(0, 1, 0, 0));
		
		westPanel.setLayout(new BorderLayout(0,0));
		westPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, new Color(255, 215, 0), new Color(240, 248, 255)));
		westPanel.setBackground(Color.BLACK);
		

		southPanel.setBackground(Color.BLACK);
		
		westTop.setLayout(new MigLayout("", "[]", "[][][]"));
		westTop.setBackground(new Color(255, 215, 0));
		
		westSouth.setBackground(new Color(255, 215, 0));
		
		eastPanel.setBackground(new Color(255, 215, 0));
		
		desktopPane.setBackground(Color.BLACK);
		
		/**
		 * Labels configuration
		 */
		titleLabel.setForeground(new Color(255, 215, 0));
		titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		clock.setForeground(Color.WHITE);
		clock.setHorizontalAlignment(SwingConstants.RIGHT);
		clock.setPreferredSize(new Dimension(230, 14));	
	
		userLabel.setText("  " + user.getName() + " " + user.getLastName());
		userLabel.setPreferredSize(new Dimension(2, 14));
		userLabel.setForeground(Color.WHITE);
		userLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		ywnLabel.setText("  Your Wareshouse Number: " + user.getYWN());
		ywnLabel.setPreferredSize(new Dimension(230, 14));
		ywnLabel.setForeground(Color.WHITE);
		ywnLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		/**
		 * Buttons configuration
		 */
		buttonConfiguration();		
		
		/**
		 * Adding to panels
		 */
		northPanel.add(titleLabel, BorderLayout.CENTER);
		northPanel.add(clock, BorderLayout.EAST);
		northPanel.add(leftTopCorner, BorderLayout.WEST);
	
		leftTopCorner.add(ywnLabel);
		leftTopCorner.add(userLabel);
	
		westPanel.add(westTop, BorderLayout.NORTH);
		westPanel.add(westSouth, BorderLayout.SOUTH);		
		
		westTop.add(manageStock, "cell 0 0,alignx left,aligny top");
		westTop.add(createOrder, "cell 0 1,alignx left,aligny top");
		westTop.add(manageOrders, "cell 0 2,alignx left,aligny top");
		westTop.add(completion, "cell 0 3,alignx left,aligny top");
		westTop.add(manageDocuments, "cell 0 4,alignx left,aligny top");
		westTop.add(loading, "cell 0 5,alignx left,aligny top");
		westTop.add(manageRoadCards, "cell 0 6,alignx left,aligny top");
		westTop.add(manageClients, "cell 0 7,alignx left,aligny top");
		westTop.add(manageEmployees, "cell 0 8,alignx left,aligny top");
		westTop.add(fleet, "cell 0 9,alignx left,aligny top");
	
		westSouth.add(exit);
		
		/**
		 * Adding to desktop pane
		 */
		
		photoPanel = new JPanel();
		photoPanel.setLayout(new GridLayout(1,1,0,0));
		photoLabel = new JLabel();
		photoLabel.setIcon(new ImageIcon("hurt.jpg"));
		photoPanel.add(photoLabel);		
		
		/**
		 * Adding to frame
		 */
		
		BufferedImage image = createImage();


		JPanel photoPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {

				super.paintComponent(g);
				g.drawImage(image, 0, 0, null);
				g.setFont(new Font("Tahoma", Font.BOLD, 25));
				g.drawString("Welcome! Hope you enjoy work with Your Warehouse program... ", 300, 400);
			}
		};
		
		frame.getContentPane().add(northPanel, BorderLayout.NORTH);
		frame.getContentPane().add(eastPanel, BorderLayout.EAST);
		frame.getContentPane().add(westPanel, BorderLayout.WEST);
		frame.getContentPane().add(southPanel, BorderLayout.SOUTH);
		closableTabbedPane.addTab("Intro  ", photoPanel);
		frame.getContentPane().add(closableTabbedPane, BorderLayout.CENTER);	


//		frame.getContentPane().add(photoPanel, BorderLayout.CENTER);		
		SwingUtilities.updateComponentTreeUI(frame);
		
	}
	
	BufferedImage createImage() {
		File file = new File("img/wall2.jpg");
		try {
			BufferedImage image = ImageIO.read(file);
			return image;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Buttons Configuration
	 */	
	private void buttonConfiguration() {
		buttonsList.add(manageStock);
		buttonsList.add(createOrder);
		buttonsList.add(manageOrders);		
		buttonsList.add(completion);
		buttonsList.add(manageDocuments);
		buttonsList.add(loading);
		buttonsList.add(manageRoadCards);
		buttonsList.add(manageClients);
		buttonsList.add(manageEmployees);
		buttonsList.add(fleet);
		buttonsList.add(exit);
		
		StaticMethods.setButtonsFont(buttonsList, "Tahoma", Font.BOLD, 13);
		StaticMethods.setButtonsSize(buttonsList, 200, 30);

		
		manageStock.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean flag = false;
				int match = 0;
				for(int i=0; i < closableTabbedPane.getTabCount(); i++) {
					if(closableTabbedPane.getTabTitleAt(i).equals(((JButton)e.getSource()).getText())) {
						flag = true;
						match = i;
					}

					if(closableTabbedPane.getTabTitleAt(i).equals("Intro")){
						closableTabbedPane.removeTabAt(i);
					}
				}
				if (flag == true) {
					closableTabbedPane.removeTabAt(match);
					
					JPanel manageStockPanel = new ManageStock();
					scroll = new JScrollPane(manageStockPanel);
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, scroll, null);	
					closableTabbedPane.setSelectedComponent(scroll);
					flag = false;
				} else {
					JPanel manageStockPanel = new ManageStock();
					scroll = new JScrollPane(manageStockPanel);
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, scroll, null);	
					closableTabbedPane.setSelectedComponent(scroll);
				}				
			}
		});
		
		createOrder.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean flag = false;
				int match = 0;
				for(int i=0; i < closableTabbedPane.getTabCount(); i++) {
					if(closableTabbedPane.getTabTitleAt(i).equals(((JButton)e.getSource()).getText())) {
						flag = true;
						match = i;
					}

					if(closableTabbedPane.getTabTitleAt(i).equals("Intro")){
						closableTabbedPane.removeTabAt(i);
					}
				}
				if (flag == true) {
					closableTabbedPane.removeTabAt(match);
					
					CreateOrder createOrder = new CreateOrder();
//					scroll = new JScrollPane(createOrder);
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, createOrder, null);	
					closableTabbedPane.setSelectedComponent(createOrder);
					flag = false;
				} else {
					CreateOrder createOrder = new CreateOrder();
//					scroll = new JScrollPane(createOrder);
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, createOrder, null);	
					closableTabbedPane.setSelectedComponent(createOrder);
				}		
			}
		});
		
		manageOrders.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean flag = false;
				int match = 0;
				for(int i=0; i < closableTabbedPane.getTabCount(); i++) {
					if(closableTabbedPane.getTabTitleAt(i).equals(((JButton)e.getSource()).getText())) {
						flag = true;
						match = i;
					}

					if(closableTabbedPane.getTabTitleAt(i).equals("Intro")){
						closableTabbedPane.removeTabAt(i);
					}
				}
				if (flag == true) {
					closableTabbedPane.removeTabAt(match);
					
					ManageOrders manageOrders = new ManageOrders();
					scroll = new JScrollPane(manageOrders);
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, scroll, null);	
					closableTabbedPane.setSelectedComponent(scroll);
					flag = false;
				} else {
					ManageOrders manageOrders = new ManageOrders();
					scroll = new JScrollPane(manageOrders);
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, scroll, null);	
					closableTabbedPane.setSelectedComponent(scroll);
				}		
			}
		});
		
		completion.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean flag = false;
				int match = 0;
				for(int i=0; i < closableTabbedPane.getTabCount(); i++) {
					if(closableTabbedPane.getTabTitleAt(i).equals(((JButton)e.getSource()).getText())) {
						flag = true;
						match = i;
					}

					if(closableTabbedPane.getTabTitleAt(i).equals("Intro")){
						closableTabbedPane.removeTabAt(i);
					}
				}
				if (flag == true) {
					closableTabbedPane.removeTabAt(match);
					
					Completion completion = new Completion();
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, completion, null);	
					closableTabbedPane.setSelectedComponent(completion);
					flag = false;
				} else {
					Completion completion = new Completion();
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, completion, null);	
					closableTabbedPane.setSelectedComponent(completion);
				}		
			}
		});
		
		manageDocuments.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean flag = false;
				int match = 0;
				for(int i=0; i < closableTabbedPane.getTabCount(); i++) {
					if(closableTabbedPane.getTabTitleAt(i).equals(((JButton)e.getSource()).getText())) {
						flag = true;
						match = i;
					}

					if(closableTabbedPane.getTabTitleAt(i).equals("Intro")){
						closableTabbedPane.removeTabAt(i);
					}
				}
				if (flag == true) {
					closableTabbedPane.removeTabAt(match);
					
					ManageDocuments manageDocuments = new ManageDocuments();
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, manageDocuments, null);	
					closableTabbedPane.setSelectedComponent(manageDocuments);
					flag = false;
				} else {
					ManageDocuments manageDocuments = new ManageDocuments();
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, manageDocuments, null);	
					closableTabbedPane.setSelectedComponent(manageDocuments);
				}			
			}
		});
		
		loading.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean flag = false;
				int match = 0;
				for(int i=0; i < closableTabbedPane.getTabCount(); i++) {
					if(closableTabbedPane.getTabTitleAt(i).equals(((JButton)e.getSource()).getText())) {
						flag = true;
						match = i;
					}

					if(closableTabbedPane.getTabTitleAt(i).equals("Intro")){
						closableTabbedPane.removeTabAt(i);
					}
				}
				if (flag == true) {
					closableTabbedPane.removeTabAt(match);
					
					Loading loading = new Loading(user);
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, loading, null);	
					closableTabbedPane.setSelectedComponent(loading);
					flag = false;
				} else {
					Loading loading = new Loading(user);
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, loading, null);	
					closableTabbedPane.setSelectedComponent(loading);
				}				
			}
		});
		
		manageRoadCards.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean flag = false;
				int match = 0;
				for(int i=0; i < closableTabbedPane.getTabCount(); i++) {
					if(closableTabbedPane.getTabTitleAt(i).equals(((JButton)e.getSource()).getText())) {
						flag = true;
						match = i;
					}

					if(closableTabbedPane.getTabTitleAt(i).equals("Intro")){
						closableTabbedPane.removeTabAt(i);
					}
				}
				if (flag == true) {
					closableTabbedPane.removeTabAt(match);
					
					ManageRoadCards manageRoadCards = new ManageRoadCards(user);
					scroll = new JScrollPane(manageRoadCards);
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, manageRoadCards, null);	
					closableTabbedPane.setSelectedComponent(manageRoadCards);
					flag = false;
				} else {
					ManageRoadCards manageRoadCards = new ManageRoadCards(user);
					scroll = new JScrollPane(manageRoadCards);
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, manageRoadCards, null);	
					closableTabbedPane.setSelectedComponent(manageRoadCards);
				}	
				
			}
		});
		
		manageClients.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean flag = false;
				int match = 0;
				for(int i=0; i < closableTabbedPane.getTabCount(); i++) {
					if(closableTabbedPane.getTabTitleAt(i).equals(((JButton)e.getSource()).getText())) {
						flag = true;
						match = i;
					}

					if(closableTabbedPane.getTabTitleAt(i).equals("Intro")){
						closableTabbedPane.removeTabAt(i);
					}
				}
				if (flag == true) {
					closableTabbedPane.removeTabAt(match);
					
					ManageClients manageClients = new ManageClients();
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, manageClients, null);	
					closableTabbedPane.setSelectedComponent(manageClients);
					flag = false;
				} else {
					ManageClients manageClients = new ManageClients();
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, manageClients, null);	
					closableTabbedPane.setSelectedComponent(manageClients);
				}				
			}
		});
		
		
		
		manageEmployees.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean flag = false;
				int match = 0;
				for(int i=0; i < closableTabbedPane.getTabCount(); i++) {
					if(closableTabbedPane.getTabTitleAt(i).equals(((JButton)e.getSource()).getText())) {
						flag = true;
						match = i;
					}

					if(closableTabbedPane.getTabTitleAt(i).equals("Intro")){
						closableTabbedPane.removeTabAt(i);
					}
				}
				if (flag == true) {
					closableTabbedPane.removeTabAt(match);
					
					ManageEmployees manageEmployees = new ManageEmployees(user);
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, manageEmployees, null);	
					closableTabbedPane.setSelectedComponent(manageEmployees);
					flag = false;
				} else {
					ManageEmployees manageEmployees = new ManageEmployees(user);
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, manageEmployees, null);	
					closableTabbedPane.setSelectedComponent(manageEmployees);
				}			
			}
		});
		
		fleet.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean flag = false;
				int match = 0;
				for(int i=0; i < closableTabbedPane.getTabCount(); i++) {
					if(closableTabbedPane.getTabTitleAt(i).equals(((JButton)e.getSource()).getText())) {
						flag = true;
						match = i;
					}

					if(closableTabbedPane.getTabTitleAt(i).equals("Intro")){
						closableTabbedPane.removeTabAt(i);
					}
				}
				if (flag == true) {
					closableTabbedPane.removeTabAt(match);
					
					ManageFleet manageFleet = new ManageFleet();
					scroll = new JScrollPane(manageFleet);
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, scroll, null);	
					closableTabbedPane.setSelectedComponent(scroll);
					flag = false;
				} else {
					ManageFleet manageFleet = new ManageFleet();
					scroll = new JScrollPane(manageFleet);
					closableTabbedPane.addTab(((JButton)e.getSource()).getText() + "    ", null, scroll, null);	
					closableTabbedPane.setSelectedComponent(scroll);
				}			
			}
		});
		
		exit.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				final int decison = JOptionPane.showConfirmDialog(frame, "Are you sure to exit? ", "Exit dialog", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (decison == JOptionPane.OK_OPTION)
					System.exit(0);
			}
		});
	
}

	public static void main(String[] args) {
		TreeMap<String,String> map = new TreeMap<String, String>();
		map.put("name", "Pawe�");
		map.put("lastName", "D�browski");
		map.put("id", "4");
		map.put("login", "d");
		map.put("YWN", "20205999");
		map.put("password", "d");
		map.put("access", "1");
		
		Menu menu = new Menu(map);
		
	}
	
}
