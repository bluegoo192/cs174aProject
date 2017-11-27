import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;



public class SignUpPage {

	static JFrame frame;
	static String[] states = { "AK","AL","AZ","AR","CA","CO","CT","DE","FL","GA","HI","ID","IL","IN",
			"IA","KS","KY","LA","ME","MD","MA","MI","MN","MS","MO","MT","NE","NV","NH","NJ",
			"NM","NY","NC","ND","OH","OK","OR","PA","RI","SC","SD","TN","TX","UT","VT","VA",
			"WA","WV","WI","WY"};
	
	public static void createSignUpPage() {
		frame = new JFrame("SignUp");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension d = new Dimension(800, 800);
      
        frame.getContentPane().setPreferredSize(d);
        JPanel panel = new JPanel(new GridLayout(4,4,4,4));
        
        JLabel username_label = new JLabel("Username:");
        JLabel password_label = new JLabel("Password:");
        JLabel confirm_password_label = new JLabel("Confirm Password:");
        JLabel name_label = new JLabel("Name:");
        JLabel state_label = new JLabel("State:");
        JLabel phone_number_label = new JLabel("Phone Number:");
        JLabel email_address_label = new JLabel("Email:");

        
        JTextField username = new JTextField(20);
        JPasswordField password = new JPasswordField(20);
        JPasswordField password_confirm = new JPasswordField(20);
        JTextField name = new JTextField(20);
        JComboBox state_list = new JComboBox(states);
        JTextField phone_number = new JTextField(10);
        JTextField email_address = new JTextField(30);
        
        
        panel.add(username_label);
        panel.add(username);
        panel.add(password_label);
        panel.add(password);
        panel.add(confirm_password_label);
        panel.add(password_confirm);
        panel.add(name_label);
        panel.add(name);
        panel.add(state_label);
        panel.add(state_list);
        panel.add(phone_number_label);
        panel.add(phone_number);
        panel.add(email_address_label);
        panel.add(email_address);
       
        SignUpPage signUp = new SignUpPage();
        
        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(signUp.new EnterListener());
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
