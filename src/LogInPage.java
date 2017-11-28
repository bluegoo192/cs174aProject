import Database.DbClient;
import Database.DbQuery;
import Database.RetrievalQuery;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

public class LogInPage {

	static JFrame frame;
	static boolean isManager;
	
	static JTextField username;
	static JPasswordField password;
	
	public static void createLogInPage(boolean manage) {
		isManager = manage;
		
		frame = new JFrame("Log In");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension d = new Dimension(800, 800);
      
        frame.getContentPane().setPreferredSize(d);
        JPanel panel = new JPanel(new GridLayout(4,4,4,4));
        
        JLabel username_label = new JLabel("Username:");
        JLabel password_label = new JLabel("Password:");
        
        username = new JTextField(20);
        password = new JPasswordField(20);
        
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
			
			//check if username and password are good
			if(isManager) {
				//check manager list
				StringBuilder checkManagerList = new StringBuilder("SELECT C.username ")
						.append("FROM Manager ")
						.append("WHERE M.username = ").append(username.getText())
						.append(" AND M.password = ").append(password.getText());
				DbClient.getInstance().runQuery(new RetrievalQuery(checkManagerList.toString()) {
					@Override
					public void onComplete(ResultSet result) {
						try {
							if(!result.next()) {
								//there are no results
								JOptionPane.showMessageDialog(null, "No users match username/password set", "Error Message", 0);
								return;
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}else {
				//check customers list
				StringBuilder checkManagerList = new StringBuilder("SELECT C.username ")
						.append("FROM CUSTOMERS C ")
						.append("WHERE C.username = ").append(username.getText())
						.append(" AND C.password = ").append(password.getText());
				DbClient.getInstance().runQuery(new RetrievalQuery(checkManagerList.toString()) {
					@Override
					public void onComplete(ResultSet result){
						try {
							if(!result.next()) {
								//there are no results
								JOptionPane.showMessageDialog(null, "No managers match username/password set", "Error Message", 0);
								return;
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
			
			
			//go to new page
			frame.setVisible(false);
			frame.dispose();
			
			if(isManager) {
				//open manager dashboard
			}else {
				//open user dashboard
			}
			
			
		}
		
	}
}
