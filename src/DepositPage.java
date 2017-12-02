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
	
	private static String user;
	private static String account;
	static int beginning_balance;
	
	static JTextField amount;
	
	
	public static void createDepositPage() {
		user = CustomerDashboard.getUser();
		//find account ID
		account = CustomerDashboard.get_market_account();
		
		
		StringBuilder findBalance = new StringBuilder("SELECT M.Balance ")
				.append("FROM Market_Account M ").append("WHERE ")
				.append("M.username = ").append("'").append(user)
				.append("'");

		DbClient.getInstance().runQuery(new RetrievalQuery(findBalance.toString()) {
			@Override
			public void onComplete(ResultSet result) {
				try {
					if(!result.next()) {
						return;
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
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
	
	private class DepositListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			//update user market account
			int deposit_amount = Integer.parseInt(amount.getText());
			try {
				DbClient.getInstance().adjustMarketAccountBalance(user, deposit_amount);
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.print("Deposit failed, please try again");
			}
			
			//automatically generate deposit id
			String depID = Integer.toString(StarsRUs.global_deposit);
			StarsRUs.global_deposit += 1;
			
			//add to deposit table
			StringBuilder createDepositRow = new StringBuilder("INSERT INTO Deposit ")
					.append("(DepositID, AccountID, Username, Value, Date, OriginalBalance) ")
					.append("VALUES ( ").append("'").append(depID).append("'").append(", ")
					.append("'").append(account).append("'").append(", ").append("'").append(user).append("'").append(", ")
					.append(deposit_amount).append(", ").append("'").append(StarsRUs.global_date).append("'").append(",").append(beginning_balance).append(")");
			DbClient.getInstance().runQuery(new UpdateQuery(createDepositRow.toString()));
			
			
			
			frame.setVisible(false);
			frame.dispose();
			
			CustomerDashboard.createDashboard(user);
		}
		
		
	}
	
	private class WithdrawListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// update user market account
			//update user market account
			int withdraw_amount = Integer.parseInt(amount.getText());
			int set_amount = beginning_balance-withdraw_amount;
			
			if(set_amount <0) {
				JOptionPane.showMessageDialog(null, "Cannot have balance below $0", "Error with Deposit/Withdraw", 0);
				frame.setVisible(false);
				frame.dispose();
				CustomerDashboard.createDashboard(user);
				return;
			}

			try {
				DbClient.getInstance().adjustMarketAccountBalance(user, withdraw_amount);
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.print("Deposit failed, please try again");
			}
			
			//automatically generate withdraw id
			String withdraw_id = Integer.toString(StarsRUs.global_withdraw);
			StarsRUs.global_withdraw += 1;
			
			//add to withdraw table
			StringBuilder createWithdrawRow = new StringBuilder("INSERT INTO Withdraw ")
					.append("(WithdrawID, AccountID, Username, Value, Date, OriginalBalance) ")
					.append("VALUES ( ").append("'").append(withdraw_id).append("'").append(", ")
					.append("'").append(account).append("'").append(", ").append("'").append(user).append("'").append(", ")
					.append(withdraw_amount).append(", ").append("'").append(StarsRUs.global_date).append("'").append(",").append(beginning_balance).append(")");
			DbClient.getInstance().runQuery(new UpdateQuery(createWithdrawRow.toString()));
			
			
			
			frame.setVisible(false);
			frame.dispose();
			
			CustomerDashboard.createDashboard(user);
		}
		
		
	}
}
