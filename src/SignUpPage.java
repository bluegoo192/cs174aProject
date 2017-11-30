import Database.DbClient;
import Database.DbQuery;
import Database.UpdateQuery;

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
			
			StringBuilder foreign_key_checks0 = new StringBuilder("SET FOREIGN_KEY_CHECKS=0");
			DbClient.getInstance().runQuery(new UpdateQuery(foreign_key_checks0.toString()) {
				@Override
				public void onComplete(int result) {
					System.out.println("changed foreign key constraints");
				}
			});
			
			StringBuilder truncate_customers = new StringBuilder("TRUNCATE TABLE Customers");
			DbClient.getInstance().runQuery(new UpdateQuery(truncate_customers.toString()) {
				@Override
				public void onComplete(int result) {
					System.out.println("Customers truncated");
				}
			});
			
			StringBuilder foreign_key_checks1 = new StringBuilder("SET FOREIGN_KEY_CHECKS=1");
			DbClient.getInstance().runQuery(new UpdateQuery(foreign_key_checks1.toString()) {
				@Override
				public void onComplete(int result) {
					System.out.println("changed foreign key constraints");
				}
			});
			
			// check that password and password confirm are the same
			if(! password.getText().equals(password_confirm.getText())){
				JOptionPane.showMessageDialog(null, "PASSWORD AND PASSWORD CONFIRM DO NOT MATCH", "Error Message", 0);
				return;
			}
			
			if(phone_number.getText().length() != 10 || !phone_number.getText().matches("[0-9]+")) {
				JOptionPane.showMessageDialog(null, "Phone number hould be length 10 and contain only digits", "Error Message", 0);
				return;
			}
			
			int reply = JOptionPane.showConfirmDialog(null, "Automatically create market account and deposit $1000? (must click yes to continue)", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
		    if (reply == JOptionPane.NO_OPTION)
		    {
		      return;
		    }
			
			//check that username is unique (SQL query here)
			
			//generate taxID
			
			//after confirming validity of the entered information, create new account in sql
			
			StringBuilder addEntry = new StringBuilder("INSERT INTO Customers VALUES (")
					.append("'").append(username.getText()).append("'").append(",")
					.append("'").append(state_list.getSelectedItem()).append("'").append(",")
					.append("'").append(email_address.getText()).append("'").append(",")
					.append("'").append(taxID.getText()).append("'").append(",")
					.append("'").append(phone_number.getText()).append("'").append(",")
					.append("'").append(password.getText()).append("'").append(")");

			DbClient.getInstance().runQuery(new UpdateQuery(addEntry.toString()) {
				@Override
				public void onComplete(int result) {
					System.out.println("Added "+username+" successfully.");
				}
			});
			String date = "2017-04-04";
			int accountid = (int) (Math.random()*(30000));
			String aID = Integer.toString(accountid);
			
			
			StringBuilder addMarketAccount = new StringBuilder("INSERT INTO Market_Account VALUES( ")
					.append("'").append(aID).append("'").append(",").append("1000").append(",").append("'").append(username.getText()).append("'")
					.append(",").append("1000").append(",").append("'").append(date).append("'")
					.append(")");
			DbClient.getInstance().runQuery(new UpdateQuery(addMarketAccount.toString()) {
				@Override
				public void onComplete(int result) {
					System.out.println("Account created successfully");
				}
			});
			

			
			//go to new page
			frame.setVisible(false);
			frame.dispose();
			
			CustomerDashboard.createDashboard(username.getText());
		}
		
	}
}
