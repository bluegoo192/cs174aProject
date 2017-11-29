import Database.DbClient;
import Database.DbQuery;
import Database.RetrievalQuery;
import Database.UpdateQuery;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;


public class ManagerDashboard{
	
	static JFrame frame;
	
	static JTextField customer_report;
	
	static String manager;
	
	static String print_string;

	public static JFrame createDashboard(String managerID) {
		//username is the distinct name of the user logged in at the moment.
		manager = managerID;
		
    	 	 frame = new JFrame(managerID + "'s Dashboard");
         frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
         Dimension dim = new Dimension(800, 800);
       
         frame.getContentPane().setPreferredSize(dim);
         JPanel panel = new JPanel(new GridLayout(5,5,5,5));
         
         ManagerDashboard d = new ManagerDashboard();
         
         JButton add_interest = new JButton("Add Interest");
         add_interest.addActionListener(d.new InterestListener());
         JButton monthly_statement = new JButton("Generate Monthly Statement");
         monthly_statement.addActionListener(d.new StatementListener());
         JButton actives = new JButton("List Active Customers");
         actives.addActionListener(d.new ActivesListener());
         JButton dter = new JButton("Generate DTER");
         dter.addActionListener(d.new DTERListener());
         customer_report = new JTextField("Type username of customer here");
         JButton go_1 = new JButton("Go");
         go_1.addActionListener(d.new Go1Listener());
         JButton delete_trans = new JButton("Delete Transactions");
         delete_trans.addActionListener(d.new DeleteListener());
         
         panel.add(add_interest);
         panel.add(monthly_statement);
         panel.add(actives);
         panel.add(customer_report);
         panel.add(go_1);
         panel.add(delete_trans);
         
         
         //4. Size the frame.
         frame.pack();
         frame.setContentPane(panel);
         
         frame.setVisible(true);
         
         return frame;
    }
	
	class InterestListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// add appropriate amount of interest to all accounts
			//average daily balance
			
			
		}
		
	}
	class StatementListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	class ActivesListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class DTERListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	class Go1Listener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// List all accounts associated with a certain customer and their balances
			String user = customer_report.getText();
			
			print_string = "This customer's balances are: ";
			
			StringBuilder get_balances = new StringBuilder("SELECT M.balance ")
					.append("FROM Market_Account M ")
					.append("WHERE M.username = ").append("'").append(user)
					.append("'");
			DbClient.getInstance().runQuery(new RetrievalQuery(get_balances.toString()) {
				@Override
				public void onComplete(ResultSet result) {
					String balance = "";
					String get_balances = "";
					try {
						while(result.next()) {
							String print_string = "";
							balance = result.getString(1);
							get_balances += balance;
							get_balances += ", ";
						}
						if(get_balances.equals("")) {
							System.out.println("no balances");
							ManagerDashboard.print_string = "THERE ARE NO ACCOUNTS ASSOCIATED WITH THIS USERNAME";
							return;
						}
						
						display_string = print_string;
						
						ManagerDashboard.print_string += display_string;
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(print_string.equals("THERE ARE NO ACCOUNTS ASSOCIATED WITH THIS USERNAME")){
				JOptionPane.showMessageDialog(null, print_string, "Show Balances", 0);
				return;
			}
			
			StringBuilder get_stock_balances = new StringBuilder("SELECT S.StockBalance ")
					.append("FROM stock_account S ")
					.append("WHERE S.username = ").append("'").append(user)
					.append("'");
			DbClient.getInstance().runQuery(new RetrievalQuery(get_stock_balances.toString()) {
				@Override
				public void onComplete(ResultSet result) {
					String balance = "";
					String get_balances = "";
					try {
						while(result.next()) {
							String print_string = "";
							balance = result.getString(1);
							get_balances += balance;
							get_balances += ", ";
						}
						
						if(get_balances.equals("")) {
							ManagerDashboard.print_string = "THERE ARE NO ACCOUNTS ASSOCIATED WITH THIS USERNAME";
							return;
						}
						display_string = print_string;
						ManagerDashboard.print_string += display_string;
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(print_string.equals("THERE ARE NO ACCOUNTS ASSOCIATED WITH THIS USERNAME")){
				JOptionPane.showMessageDialog(null, print_string, "Show Balances", 0);
				return;
			}
			
			
			JOptionPane.showMessageDialog(null, print_string, "Show Balances", 1);
		}
		
	}
	class DeleteListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
			int reply = JOptionPane.showConfirmDialog(null, "Are you sure? (Clicking yes will irretrivalbly delete all of the recorded transactions)", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
		    if (reply == JOptionPane.NO_OPTION)
		    {
		      return;
		    }
			
			//delete everything in buy stock
			StringBuilder delete_stock_buys = new StringBuilder("TRUNCATE TABLE Buy_Stock");
			DbClient.getInstance().runQuery(new UpdateQuery(delete_stock_buys.toString()) {
				@Override
				public void onComplete(int numRowsAffected) {
					System.out.println("Buy Table deleted");
				}
			});
			
			StringBuilder delete_stock_sells = new StringBuilder("TRUNCATE TABLE Sell_Stock");
			DbClient.getInstance().runQuery(new UpdateQuery(delete_stock_sells.toString()) {
				@Override
				public void onComplete(int numRowsAffected) {
					System.out.println("Sell Table deleted");
				}
			});
			
			StringBuilder delete_deposit = new StringBuilder("TRUNCATE TABLE Deposit");
			DbClient.getInstance().runQuery(new UpdateQuery(delete_deposit.toString()) {
				@Override
				public void onComplete(int numRowsAffected) {
					System.out.println("Deposit Table deleted");
				}
			});
			
			StringBuilder delete_withdraw = new StringBuilder("TRUNCATE TABLE Withdraw");
			DbClient.getInstance().runQuery(new UpdateQuery(delete_withdraw.toString()) {
				@Override
				public void onComplete(int numRowsAffected) {
					System.out.println("Withdraw Table deleted");
				}
			});
			
			
			
		}
		
	}
	
    
   
    
    }