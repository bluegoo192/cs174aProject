import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


public class WelcomePage{
	
	static JFrame frame;

	public static JFrame createFrame() {
    	 	 frame = new JFrame("Welcome to Stars R Us Stock Exchange System");
         frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
         Dimension d = new Dimension(800, 800);
       
         frame.getContentPane().setPreferredSize(d);
         JPanel panel = new JPanel(new GridLayout(4,4,4,4));
         
         JLabel label1 = new JLabel("Welcome to Stars R Us Stock Exchange System");
         label1.setVerticalAlignment(JLabel.CENTER);
         label1.setHorizontalAlignment(JLabel.CENTER);
         //frame.getContentPane().add(label1);
         panel.add(label1);
         //create buttons
         Dimension button_size = new Dimension(20, 20);
         WelcomePage wel= new WelcomePage();
         JButton signUpButton = new JButton("Sign Up");
         signUpButton.addActionListener(wel.new SignUpListener());
         signUpButton.setPreferredSize(button_size);
         
         JButton logInButton = new JButton("Log In");
         logInButton.addActionListener(wel.new LogInListener());
         logInButton.setPreferredSize(button_size);
         
         panel.add( signUpButton);
         panel.add( logInButton);
         //4. Size the frame.
         frame.pack();
         frame.setContentPane(panel);
         
         frame.setVisible(true);
         
         return frame;
    }
    
    class SignUpListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//go to sign up page
			frame.setVisible(false);
			frame.dispose();
			//open new frame
			SignUpPage.createSignUpPage();
		}
    		
    }
    
    class LogInListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//go to log in page
			frame.setVisible(false);
			frame.dispose();
			//open new frame
			LogInPage.createLogInPage();
			
		}
    		
    }
    
    }