import Database.DbClient;
import Database.DbQuery;
import Database.RetrievalQuery;
import Database.UpdateQuery;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;


public class CustomerDashboard{
	
	static JFrame frame;
	static JTextField actor_stock;
	static JTextField movie_info;
	
	private static String user;
	
	//used for deposit/withdraw listener
	private static String market_accountID;
	private static String stock_accountID = "";
	
	
	//setter functions
	public static void set_stock_account(String input) {
		stock_accountID = input;
		}
	public static void set_market_account(String input) {
		market_accountID = input;
	}
	public static void set_user(String input) {
		user = input;
	}
	
	public static String get_stock_account() {
		return stock_accountID;
	}
	public static String get_market_account() {
		return market_accountID;
	}
	public static String getUser() {
		return user;
	}
	
	

	public static void createDashboard(String username) {
		
		//initialize the global variables
		user = username;
		initialize_accounts();
		
    	 	
    }
	
	private static void initialize_accounts() {
		//get stock ID
		StringBuilder find_stock_account = new StringBuilder("SELECT S.AccountID ")
				.append("FROM stock_account S ").append("WHERE S.Username = ")
				.append("'").append(user).append("'");
		DbClient.getInstance().runQuery(new RetrievalQuery(find_stock_account.toString()) {
			@Override
			public void onComplete(ResultSet result) {
				System.out.println(result.toString());
				try {
					if(!result.next()) {
						//no stock account
						//create new stock account
						CustomerDashboard.set_stock_account("");
						initialize_market_account();
						return;
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				try {
					String stock_id = result.getString(1);
					CustomerDashboard.set_stock_account(stock_id);
					initialize_market_account();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		
	}
	
	private static void initialize_market_account() {
		StringBuilder findAccountID = new StringBuilder("SELECT M.AccountID ")
				.append("FROM Market_Account M ").append("WHERE ")
				.append("M.username = ").append("'").append(user)
				.append("'");

		DbClient.getInstance().runQuery(new RetrievalQuery(findAccountID.toString()) {
			@Override
			public void onComplete(ResultSet result) {
				
				try {
					if(!result.next()) {
						set_market_account("");
						build_frame();
						return;
					}
				} catch (HeadlessException | SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					set_market_account(result.getString(1));
					build_frame();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	private static void build_frame() {
		 frame = new JFrame(user + "'s Dashboard");
         frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
         Dimension dim = new Dimension(800, 800);
       
         frame.getContentPane().setPreferredSize(dim);
         JPanel panel = new JPanel(new GridLayout(5,5,5,5));
         panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
         
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
         
	}
	
	private class DepositListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//go to deposit/withdraw page

			
			if(get_market_account() == "") {
				JOptionPane.showMessageDialog(null, "no accounts shown for given username", "Error with Deposit/Withdraw", 0);
				return;
			}
			
			frame.setVisible(false);
			frame.dispose();
			
			DepositPage.createDepositPage();
		}
		
	}

	
	private class BuyListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//go to buy/sell page
			
			frame.setVisible(false);
			frame.dispose();
			
			new BuyStocksPage().createStocksPage();
			
		}
		
	}

	
	private class BalanceListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//show balance
			//get balance using username
			StringBuilder addEntry = new StringBuilder("SELECT M.balance ")
					.append("FROM Market_Account M ")
					.append("WHERE M.username = ").append("'").append(user)
					.append("'");
			DbClient.getInstance().runQuery(new RetrievalQuery(addEntry.toString()) {
				@Override
				public void onComplete(ResultSet result) {
					String balance = "";
					
					try {
						if(!result.next()) {
							JOptionPane.showMessageDialog(null, "no accounts shown for given username", "Show Balance", 1);
							return;
						}
					} catch (HeadlessException | SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
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
	
	private class TransactionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//deposit page
			
			TransactionHistoryPage.createHistoryPage();
			
		}
		
	}
	
	private class Go1Listener implements ActionListener{
		private Vector<String> actor_info;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//gets information about the entered stock
			
			//get stock
			String stock = actor_stock.getText();
			
			
			if(stock.length() != 3 || !stock.matches("[a-zA-Z]+")) {
				JOptionPane.showMessageDialog(null, "Stock Symbol must be of size 3 and can only contain letters", "Stock Error", 0);
				return;
			}
			
			//stock is a 3 letter string
			//query to find stock
			
			StringBuilder addEntry = new StringBuilder("SELECT A.current_stock_price, A.Name, A.Birth ")
					.append("FROM Actor_Stock A ")
					.append("WHERE A.stock_symbol = ").append("'").append(CustomerDashboard.actor_stock.getText())
					.append("'");
			DbClient.getInstance().runQuery(new RetrievalQuery(addEntry.toString()) {
				@Override
				public void onComplete(ResultSet result) {
					String price = "";
					
					try {
						if(!result.next()) {
							JOptionPane.showMessageDialog(null, "no stocks under this symbol", "Error in Stocks", 0);
							return;
						}else {
							
							Vector<String> output_string = new Vector<String>();
							do {
								String curr_result;
								curr_result = result.getString(1);
								curr_result += ", ";
								curr_result += result.getString(2);
								curr_result += ", ";
								curr_result += result.getString(3);
								output_string.add(curr_result);
							}while(result.next());
							
							actor_info = output_string;
							
							StringBuilder movie_info = new StringBuilder("SELECT M.Title, M.Year, C.Role, C.Total_Value  ")
									.append("FROM Movie M, MovieContract C ")
									.append("WHERE C.stock_symbol = '").append(CustomerDashboard.actor_stock.getText())
									.append("' AND M.MovieID = C.MovieID");
							DbClient.getInstance().runQuery(new RetrievalQuery(movie_info.toString() ) {

								@Override
								public void onComplete(ResultSet result) {
									// TODO Auto-generated method stub
									Vector<String> output_string = new Vector<String>();
									try {
										if(!result.next()) {
											output_string.add("THIS ACTOR CURRENTLY HAS NO MOVIE CONTRACTS");
											build_actor_frame(output_string);
											return;
										}else {
											
											do {
												String curr_result;
												curr_result = result.getString(1);
												curr_result += ", ";
												curr_result += result.getString(2);
												curr_result += ", ";
												curr_result += result.getString(3);
												output_string.add(curr_result);
											}while(result.next());
											build_actor_frame(output_string);
											return;
										}
									} catch (SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								
							});
						}
					} catch (HeadlessException | SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
		}
		
		private void build_actor_frame(Vector<String> actor_movie_info) {
			
			JList movie_list = new JList(actor_movie_info);
			JList actor_list = new JList(actor_info);

			JFrame frame1 = new JFrame("Active Users");
			frame1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			Dimension d = new Dimension(800, 800);

			//create actor list
			//create movie list and label
			JLabel actor_label = new JLabel("Current Actor Info");
			actor_label.setVerticalAlignment(JLabel.CENTER);

			actor_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			actor_list.setLayoutOrientation(JList.VERTICAL_WRAP);
			actor_list.setVisibleRowCount(-1);

			JScrollPane actorScroller = new JScrollPane(actor_list);
			actorScroller.setPreferredSize(new Dimension(250, 80));
			
			//create movie list and label
			JLabel movie_label = new JLabel("Movie Contract Info");
			movie_label.setVerticalAlignment(JLabel.CENTER);

			movie_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			movie_list.setLayoutOrientation(JList.VERTICAL_WRAP);
			movie_list.setVisibleRowCount(-1);

			JScrollPane movieScroller = new JScrollPane(movie_list);
			movieScroller.setPreferredSize(new Dimension(250, 80));

			TransactionHistoryPage t = new TransactionHistoryPage();
			JButton backButton = new JButton("Back to Dash");
			backButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					frame1.setVisible(false);
					frame1.dispose();
				}

			});

			JPanel panel = new JPanel(new GridLayout(4,4,4,4));
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

			panel.add(actor_label);
			panel.add(actorScroller);
			panel.add(movie_label);
			panel.add(movieScroller);
			panel.add(backButton);

			frame1.setContentPane(panel);
			frame1.pack();

			frame1.setVisible(true);

			
		}
		
	}
	
	private class Go2Listener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//deposit page
			
		}
		
	}
	
	//get next accountID
    
   
    
    }