import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class LogInPage {

	static JFrame frame;
	
	public static void createLogInPage() {
		frame = new JFrame("Log In");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension d = new Dimension(800, 800);
      
        frame.getContentPane().setPreferredSize(d);
        JPanel panel = new JPanel(new GridLayout(4,4,4,4));
        
        JLabel username_label = new JLabel("Username:");
        JLabel password_label = new JLabel("Password:");
        
        JTextField username = new JTextField(20);
        JPasswordField password = new JPasswordField(20);
        
        panel.add(username_label);
        panel.add(username);
        panel.add(password_label);
        panel.add(password);
       
        LogInPage logIn = new LogInPage();
        
        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(logIn.new EnterListener());
        panel.add(enterButton);
        
        frame.setContentPane(panel);
        frame.pack();
        
        frame.setVisible(true);
        
	}
	
	class EnterListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// check that username and password are good
			
			//go to new page
			frame.setVisible(false);
			frame.dispose();
			
		}
		
	}
}
