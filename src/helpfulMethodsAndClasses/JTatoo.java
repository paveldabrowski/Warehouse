package helpfulMethodsAndClasses;
import java.awt.Rectangle;

import javax.swing.*;
public class JTatoo extends JFrame{
	
	public static void setLookAndFeel(String nazwa, JFrame frame) {
		 try {
	            //here you can put the selected theme class name in JTattoo
	            UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
	            SwingUtilities.updateComponentTreeUI(frame);

	        } catch (ClassNotFoundException ex) {
	            java.util.logging.Logger.getLogger(JTatoo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        } catch (InstantiationException ex) {
	            java.util.logging.Logger.getLogger(JTatoo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        } catch (IllegalAccessException ex) {
	            java.util.logging.Logger.getLogger(JTatoo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
	            java.util.logging.Logger.getLogger(JTatoo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        }
	}

	public static void main(String[] args) {
	    try {
            //here you can put the selected theme class name in JTattoo
            UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
//            SwingUtilities.updateComponentTreeUI(component);

        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JTatoo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JTatoo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JTatoo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JTatoo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
	    
	    
	    JTatoo dupa = new JTatoo();
	    dupa.setBounds(new Rectangle(400, 500));
	    dupa.setVisible(true);
	
	}

	}


