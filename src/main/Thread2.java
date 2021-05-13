package main;

public class Thread2 extends Thread{
	
	/**
	 * Thread which invokes sign in dialog 
	 */
	@Override
	public void run() {
		Main.dialog = new SignIn2();
		Main.dialog.setVisible(true);
		
		
	while(Main.dialog.isEnabled()) {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {	
			e.printStackTrace();
		};
	}
		System.out.println("Thread finished");
	}
}