package helpfulMethodsAndClasses;

import javax.swing.*;

public class LookAndFeel {
	
	public static void makeLookAndFeel(String which, JComponent component ) {
		String lookName = "";
		if (which.equals("Metal"))
			lookName = "javax.swing.plaf.metal.MetalLookAndFeel";
		else if (which.equals("Metif"))
			lookName = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
		else if (which.equals("Windows"))
			lookName = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		try {
			UIManager.setLookAndFeel(lookName);
			SwingUtilities.updateComponentTreeUI(component);
		
		} catch (Exception e1) {
			System.out.println(e1.getMessage());
		}
		}
}

