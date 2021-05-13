package main;


import dbConnectionAndMethods.DB;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class SignIn2 extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5601895450135351693L;
	private JPanel contentPane;
	private JTextField loginTextField;
	private JTextField passwordTextField;
	protected String user;
	protected String password;
	boolean isUserOk = false;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SignIn2 frame = new SignIn2();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SignIn2() {
		
		
		setAlwaysOnTop(true);
		setBounds(new Rectangle(0, 0, 640, 425));
		setTitle("Logowanie do programu");
		
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
	
		setBounds(width/3, height/4, 640, 426);
		contentPane = new JPanel();
		contentPane.setBounds(new Rectangle(0, 0, 640, 425));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);		
		
		Image backgroundImage = new ImageIcon(this.getClass().getResource("/toDialog.jpg")).getImage();
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 626, 389);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JButton btnNewButton = new JButton("Cancel");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			System.out.println("Cancel");
			System.exit(0);		
			
			}
		});
		btnNewButton.setBounds(388, 255, 89, 23);
		panel.add(btnNewButton);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionForOkAndEnter(e);
				}
			});
		okButton.setBounds(174, 255, 89, 23);
		panel.add(okButton);
		
		JLabel signInLabel = new JLabel("SIGN IN");
		signInLabel.setBounds(288, 89, 65, 23);
		panel.add(signInLabel);
		signInLabel.setBackground(new Color(153, 0, 102));
		signInLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		signInLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		signInLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		loginTextField = new JTextField();
		loginTextField.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		loginTextField.setCaretColor(new Color(153, 0, 153));
		loginTextField.setBackground(SystemColor.controlShadow);
		loginTextField.setBounds(273, 168, 96, 20);
		panel.add(loginTextField);
		loginTextField.setColumns(10);
		
		loginTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					passwordTextField.requestFocus();
				}
			}
		});
		
		passwordTextField = new JPasswordField();
		passwordTextField.setColumns(10);
		passwordTextField.setCaretColor(new Color(153, 0, 153));
		passwordTextField.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		passwordTextField.setBackground(SystemColor.controlShadow);
		passwordTextField.setBounds(273, 194, 96, 20);
		passwordTextField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
					actionForOkAndEnter(e);
			}
		});
		panel.add(passwordTextField);
		
		JLabel userLabel = new JLabel("User");
		userLabel.setHorizontalAlignment(SwingConstants.CENTER);
		userLabel.setBounds(288, 154, 65, 14);
		panel.add(userLabel);
		
		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
		passwordLabel.setBounds(288, 211, 65, 14);
		panel.add(passwordLabel);
		
		JLabel labelForBackgroundImage = new JLabel("");
		labelForBackgroundImage.setBounds(0, 0, 626, 389);
		panel.add(labelForBackgroundImage);
		labelForBackgroundImage.setIcon(new ImageIcon(backgroundImage));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); /*-?|SingIn|pavel|c0|?*/
		SwingUtilities.updateComponentTreeUI(this);
	}
	
	/**
	 * Method is checking if typed user login and password is in database
	 * @param user - login
	 * @param password - password
	 * @return true if user and password exists in datable; false if not
	 */	
	public boolean isUserValid(String user, String password) {		
		
		try (Connection conn = DB.getConnection()){
			
			if (conn != null) {
				PreparedStatement statement = conn.prepareStatement("SELECT login, password FROM users");				
				ResultSet result = statement.executeQuery();
				HashMap<String,String> map = new HashMap<String,String>();
				while(result.next()) {
					map.put(result.getString("login"), result.getString("password"));
				}
				if (map.containsKey(user)) {

					String tmp = map.get(user);				
					if (password.equals(tmp)){
						System.out.println("Signed in ;-)");
						return true;
					}
					else {
						System.out.println("Incorrect user or password ");
						return false;
					}						
				}
				
				else {
					System.out.println("User doesn't exists");
					return false;
				}				
			}
		} catch (Exception e) {
			System.out.println("False from exception");
			e.printStackTrace();
		}
		System.out.println("False");
		return false;
		
	}
	/**
	 * Method is closing sign in window and disable it. While this window is disable another thread run the program menu  
	 */
	private void disposeWindow() {
		this.setEnabled(false);
		this.dispose();
	}
	
	/**
	 * Method collect data from user input and calls {@link #isUserValid(String, String)} method to check data is correct. If true dispose singIn window and run the program, 
	 * if not show message dialog with fail information
	 * @param e awt event to check which component calls this method
	 */
	 
	private void actionForOkAndEnter(AWTEvent e) {
		user = loginTextField.getText().toString();
		password = passwordTextField.getText().toString();
		isUserOk = isUserValid(user, password);
		if(isUserOk == true) {
			disposeWindow();			
		}
		
		if(isUserOk == false) {
			loginTextField.requestFocus();
			if (e.getSource() instanceof JButton) {	
				JOptionPane.showMessageDialog(contentPane, "Invalid user or password.");				
			}
			else if (e.getSource() instanceof JTextField) {
				JOptionPane.showMessageDialog(contentPane, "Invalid user or password.");				
			}
		}	
	}
	
	/**
	 * getter for user
	 * @return user data
	 */
	protected String getUser() {
		return user;
	}
}

