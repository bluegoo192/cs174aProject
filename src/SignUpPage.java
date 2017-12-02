import Database.DbClient;
import Database.DbQuery;
import Database.RetrievalQuery;
import Database.UpdateQuery;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    
   
    public void update_settings(boolean market_account, boolean stock_account, boolean deposit, boolean withdraw) {
    	
    }
	
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
					.append("'").append(password.getText()).append("'").append(",")
					.append("'").append(name.getText()).append("'").append(")");

			DbClient.getInstance().runQuery(new UpdateQuery(addEntry.toString()) {
				@Override
				public void onComplete(int result) {
					System.out.println("Added "+username+" successfully.");
					addAccount();
				}
			});
		
		}
		
		
		private void addAccount() {
			
			
			/*
			 * "CREATE TABLE IF NOT EXISTS Market_Account (" +
					"	AccountID CHAR(20)," +
					"	Balance REAL CHECK (Balance >= 0)," +
					"	Username CHAR(20) NOT NULL,\n" +
					"	old_ADB REAL," +  // old average daily balance (until the most recent balance change)
					"	last_changed DATE," +
					"	last_interest_accrual DATE," +
					"	Original_Monthly_Balance REAL,"+
					"	FOREIGN KEY(username) REFERENCES Customers(username)" +
					"ON DELETE CASCADE ON UPDATE CASCADE," +
					"	PRIMARY KEY (AccountID) )",
			 * 
			 */
			
			String account_id = Integer.toString(StarsRUs.global_mark);
			StarsRUs.global_mark += 1;
			StringBuilder addMarketAccount = new StringBuilder("INSERT INTO Market_Account VALUES( ")
					.append("'").append(account_id).append("'").append(",").append("1000").append(",").append("'").append(username.getText()).append("'")
					.append(",").append("1000").append(",").append("'").append(StarsRUs.global_date).append("'")
					.append(", '").append(StarsRUs.global_date).append("', 1000")
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
