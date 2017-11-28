import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

import javax.swing.*;



public class SignUpPage {

	static JFrame frame;
	static String[] states = { "AK","AL","AZ","AR","CA","CO","CT","DE","FL","GA","HI","ID","IL","IN",
			"IA","KS","KY","LA","ME","MD","MA","MI","MN","MS","MO","MT","NE","NV","NH","NJ",
			"NM","NY","NC","ND","OH","OK","OR","PA","RI","SC","SD","TN","TX","UT","VT","VA",
			"WA","WV","WI","WY"};
	
	static JTextField username;
	static  JPasswordField password;
    static JPasswordField password_confirm;
    static JTextField name;
    static JComboBox state_list;
    static JTextField phone_number;
    static JTextField email_address;
    static JTextField taxID;
	
	public static void createSignUpPage() {
		frame = new JFrame("SignUp");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension d = new Dimension(800, 800);
      
        frame.getContentPane().setPreferredSize(d);
        JPanel panel = new JPanel(new GridLayout(5,5,5,5));
        
        JLabel username_label = new JLabel("Username:");
        JLabel password_label = new JLabel("Password:");
        JLabel confirm_password_label = new JLabel("Confirm Password:");
        JLabel name_label = new JLabel("Name:");
        JLabel state_label = new JLabel("State:");
        JLabel phone_number_label = new JLabel("Phone Number:");
        JLabel email_address_label = new JLabel("Email:");
        JLabel taxID_label = new JLabel("Tax ID:");

        
        username = new JTextField(20);
        password = new JPasswordField(20);
        password_confirm = new JPasswordField(20);
        name = new JTextField(20);
        state_list = new JComboBox(states);
        phone_number = new JTextField(10);
        email_address = new JTextField(254);
        taxID = new JTextField(6);
        
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
        panel.add(taxID_label);
        panel.add(taxID);       
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
			// check that password and password confirm are the same
			if(! password.getText().equals(password_confirm.getText())){
				JOptionPane.showMessageDialog(null, "PASSWORD AND PASSWORD CONFIRM DO NOT MATCH", "Error Message", 0);
				return;
			}
			
			if(phone_number.getText().length() != 10 || !phone_number.getText().matches("[0-9]+")) {
				JOptionPane.showMessageDialog(null, "Phone number hould be length 10 and contain only digits", "Error Message", 0);
				return;
			}
			
			//check that username is unique (SQL query here)
			
			
			//generate taxID
			
			//after confirming validity of the entered information, create new account in sql
			
			StringBuilder addEntry = new StringBuilder("INSERT INTO CUSTOMERS VALUES (")
					.append(username.getText()).append(",")
					.append(state_list.getSelectedItem()).append(",")
					.append(email_address.getText()).append(",")
					.append(taxID.getText()).append(",")
					.append(phone_number.getText()).append(",")
					.append(password.getText()).append(",");

			DbClient.getInstance().runQuery(new DbQuery(addEntry.toString()) {
				@Override
				public void onComplete(ResultSet result) {
					System.out.println("Added "+username+" successfully.");
				}
			});
			
			//go to new page
			frame.setVisible(false);
			frame.dispose();
			
			
		}
		
	}
}
