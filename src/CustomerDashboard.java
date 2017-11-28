import Database.DbClient;
import Database.DbQuery;

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


public class CustomerDashboard{
	
	static JFrame frame;
	static JTextField actor_stock;
	static JTextField movie_info;
	
	static String user;

	public static JFrame createDashboard(String username) {
		//username is the distinct name of the user logged in at the moment.
		user = username;
		
    	 	 frame = new JFrame(username + "'s Dashboard");
         frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
         Dimension dim = new Dimension(800, 800);
       
         frame.getContentPane().setPreferredSize(dim);
         JPanel panel = new JPanel(new GridLayout(5,5,5,5));
         
         CustomerDashboard d = new CustomerDashboard();
         
         JButton deposit = new JButton("Deposit/Withdraw");
         deposit.addActionListener(d.new DepositListener());
         JButton buy = new JButton("Buy/Sell");
         buy.addActionListener(d.new BuyListener());
         JButton market_balance = new JButton("See Balance");
         market_balance.addActionListener(d.new BalanceListener());
         JButton transaction_history = new JButton("Transaction History");
         transaction_history.addActionListener(d.new TransactionListener());
         actor_stock = new JTextField("Type stock symbol Here to see current price");
         JButton go_1 = new JButton("Go");
         go_1.addActionListener(d.new Go1Listener());
         movie_info = new JTextField("Type name of movie to see information");
         JButton go_2 = new JButton("Go");
         go_2.addActionListener(d.new Go2Listener());
         
         panel.add(deposit);
         panel.add(buy);
         panel.add(market_balance);
         panel.add(transaction_history);
         panel.add(actor_stock);
         panel.add(go_1);
         panel.add(movie_info);
         panel.add(go_2);
         
         
         //4. Size the frame.
         frame.pack();
         frame.setContentPane(panel);
         
         frame.setVisible(true);
         
         return frame;
    }
	
	class DepositListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//go to deposit/withdraw page
			frame.setVisible(false);
			frame.dispose();
			
			DepositPage.createDepositPage(user);
		}
		
	}

	
	class BuyListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//go to buy/sell page
			
		}
		
	}

	
	class BalanceListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//show balance
			//get balance using username
			StringBuilder addEntry = new StringBuilder("SELECT M.balance ")
					.append("FROM Market_Account M ")
					.append("WHERE M.username = ").append(user);
			DbClient.getInstance().runQuery(new DbQuery(addEntry.toString()) {
				@Override
				public void onComplete(ResultSet result) {
					String balance = "";
					try {
						balance = result.getString(1);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String display_string = "Balance is " + balance + " dollars";
					JOptionPane.showMessageDialog(null, display_string, "Show Balance", 1);
				}
			});
			
			
			return;
		}
		
	}
	
	class TransactionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//deposit page
			
		}
		
	}
	
	class Go1Listener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//deposit page
			
		}
		
	}
	
	class Go2Listener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//deposit page
			
		}
		
	}
    
   
    
    }