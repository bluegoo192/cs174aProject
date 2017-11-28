import Database.DbClient;
import Database.DbQuery;
import Database.RetrievalQuery;
import Database.UpdateQuery;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;


public class DepositPage {

	static JFrame frame;
	
	static String user;
	static String accountID;
	static int beginning_balance;
	
	static JTextField amount;
	
	
	public static void createDepositPage(String username) {
		user = username;
		//find account ID
		StringBuilder findAccountID = new StringBuilder("SELECT M.AccountID ")
				.append("FROM Market_Account M ").append("WHERE ")
				.append("M.username = ").append(user);

		DbClient.getInstance().runQuery(new RetrievalQuery(findAccountID.toString()) {
			@Override
			public void onComplete(ResultSet result) {
				try {
					accountID = result.getString(1);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		StringBuilder findBalance = new StringBuilder("SELECT M.Balance")
				.append("FROM Market_Account M ").append("WHERE ")
				.append("M.username = ").append(user);

		DbClient.getInstance().runQuery(new RetrievalQuery(findBalance.toString()) {
			@Override
			public void onComplete(ResultSet result) {
				try {
					beginning_balance = result.getInt(1);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		
		
		frame = new JFrame("Deposit/Withdraw from Market Account");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension d = new Dimension(800, 800);
      
        frame.getContentPane().setPreferredSize(d);
        JPanel panel = new JPanel(new GridLayout(4,4,4,4));
        JLabel label1 = new JLabel("Enter an amount to deposit/withdraw and click the appropriate button");
		
        DepositPage dp = new DepositPage();
		JButton deposit_button = new JButton("Deposit");
		deposit_button.addActionListener(dp.new DepositListener());
		JButton withdraw_button = new JButton("Withdraw");
		withdraw_button.addActionListener(dp.new WithdrawListener());
		amount = new JTextField();
		
		
		panel.add(label1);
		panel.add(amount);
		panel.add(deposit_button);
		panel.add(withdraw_button);
		
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
	class DepositListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			//update user market account
			int deposit_amount = Integer.parseInt(amount.getText());
			int set_amount = beginning_balance+deposit_amount;

			StringBuilder depositString = new StringBuilder("UPDATE Market_Account ")
					.append("SET balance = ").append(set_amount)
					.append("WHERE username =  ").append(user);

			DbClient.getInstance().runQuery(new UpdateQuery(depositString.toString()));
			
			//automatically generate deposit id
			int deposit_id = (int) (Math.random()* (3000));
			Date date = new Date(2017, 06, 07);
			
			//add to deposit table
			StringBuilder createDepositRow = new StringBuilder("INSERT INTO Deposit ")
					.append("(DepositID, AccountID, Username, Value, Date) ")
					.append("VALUES ( ").append(deposit_id).append(", ")
					.append(accountID).append(", ").append(user).append(", ")
					.append(amount).append(", ").append(date).append(")");
			DbClient.getInstance().runQuery(new UpdateQuery(createDepositRow.toString()));
			
			
			
			frame.setVisible(false);
			frame.dispose();
			
			CustomerDashboard.createDashboard(user);
		}
		
		
	}
	
	class WithdrawListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// update user market account
			//update user market account
			int withdraw_amount = Integer.parseInt(amount.getText());
			int set_amount = beginning_balance-withdraw_amount;

			StringBuilder depositString = new StringBuilder("UPDATE Market_Account ")
					.append("SET balance = ").append(set_amount)
					.append("WHERE username =  ").append(user);

			DbClient.getInstance().runQuery(new UpdateQuery(depositString.toString()));
			
			//automatically generate deposit id
			int withdraw_id = (int) (Math.random()* (3000));
			Date date = new Date(2017, 06, 07);
			
			//add to deposit table
			StringBuilder createDepositRow = new StringBuilder("INSERT INTO Withdraw ")
					.append("(DepositID, AccountID, Username, Value, Date) ")
					.append("VALUES ( ").append(withdraw_id).append(", ")
					.append(accountID).append(", ").append(user).append(", ")
					.append(amount).append(", ").append(date).append(")");
			DbClient.getInstance().runQuery(new UpdateQuery(createDepositRow.toString()));
			
			
			
			frame.setVisible(false);
			frame.dispose();
			
			CustomerDashboard.createDashboard(user);
		}
		
		
	}
}
