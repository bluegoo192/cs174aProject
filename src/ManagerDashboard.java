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


public class ManagerDashboard{

	static JFrame frame;

	static JTextField customer_report;

	static String manager;

	static String print_string;

	private static JList market_list = new JList();
	private static JList stock_list = new JList();

	public static void set_market(Vector<String> input) {
		market_list = new JList(input);
	}
	public static void set_stock(Vector<String> input) {
		stock_list = new JList(input);
	}

	public static JFrame createDashboard(String managerID) {
		

		//username is the distinct name of the user logged in at the moment.
		manager = managerID;

		frame = new JFrame(managerID + "'s Dashboard");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Dimension dim = new Dimension(800, 800);

		frame.getContentPane().setPreferredSize(dim);
		JPanel panel = new JPanel(new GridLayout(5,5,5,5));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

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

	private class InterestListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// add appropriate amount of interest to all accounts
			//average daily balance


		}

	}
	private class StatementListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub

		}

	}
	private class ActivesListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {

			/*StringBuilder get_actives = new StringBuilder("SELECT M.username FROM ")
					.append("(SELECT COALESCE(B.NumShares) as num_b, B.MarketID FROM Buy_Stock B GROUP BY B.MarketID) as B1, ")
					.append("(SELECT COALESCE(S.NumShares) as num_s, S.MarketID FROM Sell_Stock S GROUP BY S.MarketID) as S1,")
					.append("Market_Account M ").append("WHERE M.AccountID = B1.MarketID ");*/

			StringBuilder get_actives = new StringBuilder("SELECT M.Username, SUM(B.NumShares+S.NumShares) FROM ")
					.append("Buy_Stock B, Sell_Stock S, Market_Account M ")
					.append("WHERE B.MarketID = M.AccountID and B.MarketID = S.MarketID GROUP BY M.username HAVING SUM(B.NumShares+S.NumShares) >= 1000");
			DbClient.getInstance().runQuery(new RetrievalQuery(get_actives.toString()) {
				@Override
				public void onComplete(ResultSet result) {

					try {
						if(!result.next()) {
							//check just sell_stock
							StringBuilder check_sell = new StringBuilder("SELECT M.Username, SUM(S.NumShares) FROM ")
									.append("Sell_Stock S, Market_Account M ")
									.append("WHERE S.MarketID = M.AccountID GROUP BY M.username HAVING SUM(S.NumShares) >= 1000");
							DbClient.getInstance().runQuery(new RetrievalQuery(check_sell.toString()) {

								@Override
								public void onComplete(ResultSet result) {
									// TODO Auto-generated method stub
									try {
										if(!result.next()) {
											//check just buy_stock
											StringBuilder check_buy = new StringBuilder("SELECT M.Username, SUM(B.NumShares) FROM ")
													.append("Buy_Stock B, Market_Account M ")
													.append("WHERE B.MarketID = M.AccountID GROUP BY M.username HAVING SUM(B.NumShares) >= 1000");
											DbClient.getInstance().runQuery(new RetrievalQuery(check_buy.toString()) {

												@Override
												public void onComplete(ResultSet result) {
													// TODO Auto-generated method stub
													try {
														if(!result.next()) {
															JOptionPane.showMessageDialog(null, "NO ACTIVE USERS", "Error Message", 0);
															return;
														}else {
															Vector<String> output_users = new Vector<String>();
															do {
																String curr_result;
																curr_result = result.getString(1);
																curr_result += ", ";
																curr_result += result.getString(2);
																output_users.add(curr_result);
															}while(result.next());
															//print as JList
															build_actives_frame(output_users);
															return;
														}
													} catch (SQLException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
												}

											});
										}else {
											Vector<String> output_users = new Vector<String>();
											do {
												String curr_result;
												curr_result = result.getString(1);
												curr_result += ", ";
												curr_result += result.getString(2);
												output_users.add(curr_result);
											}while(result.next());

											//print as JList
											build_actives_frame(output_users);
										}
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						});

											return;
					}else {
						//print out
						Vector<String> output_users = new Vector<String>();
						do {
							String curr_result;
							curr_result = result.getString(1);
							curr_result += ", ";
							curr_result += result.getString(2);
							output_users.add(curr_result);
						}while(result.next());

						//print as JList
						build_actives_frame(output_users);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	private void build_actives_frame(Vector<String> users) {
		JList user_list = new JList(users);

		JFrame frame1 = new JFrame("Active Users");
		frame1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Dimension d = new Dimension(800, 800);

		//create deposit list and label
		JLabel actives_label = new JLabel("Actives within the past month:");
		actives_label.setVerticalAlignment(JLabel.CENTER);

		user_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		user_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		user_list.setVisibleRowCount(-1);

		JScrollPane activeScroller = new JScrollPane(user_list);
		activeScroller.setPreferredSize(new Dimension(250, 80));

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

		panel.add(actives_label);
		panel.add(activeScroller);
		panel.add(backButton);

		frame1.setContentPane(panel);
		frame1.pack();



		frame1.setVisible(true);
	}

}

private class DTERListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

}
private class Go1Listener implements ActionListener{

	JFrame frame1;
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// List all accounts associated with a certain customer and their balances
		String user = customer_report.getText();
		System.out.println(user);

		//get market balance
		StringBuilder market_balance = new StringBuilder("SELECT M.AccountID, M.Balance ")
				.append("FROM Market_Account M ").append("WHERE M.Username = '")
				.append(user).append("'");
		//query and create JList
		DbClient.getInstance().runQuery(new RetrievalQuery(market_balance.toString()) {
			@Override
			public void onComplete(ResultSet result) {
				Vector<String> market_list = new Vector<String>();
				try {
					if(!result.next()) {
						market_list.add("NO MARKET ACCOUNTS");
						ManagerDashboard.set_market(market_list);
						get_stock_list(user);
						return;
					}
				}catch(SQLException e1) {
					e1.printStackTrace();
				}

				try {
					do {

						String curr_result;
						curr_result = "Market Account: ";
						curr_result += result.getString(1);
						curr_result += ", ";
						curr_result += result.getString(2);
						System.out.println(curr_result);
						market_list.add(curr_result);
					}while(result.next());
					ManagerDashboard.set_market(market_list);
					get_stock_list(user);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void get_stock_list(String user) {

		//get stock balance
		StringBuilder stock_balance = new StringBuilder("SELECT S.AccountID, S.StockBalance ")
				.append("FROM stock_account S ").append("WHERE S.Username = '")
				.append(user).append("'");
		//query and create JList
		DbClient.getInstance().runQuery(new RetrievalQuery(stock_balance.toString()) {
			@Override
			public void onComplete(ResultSet result) {
				Vector<String> stock_list = new Vector<String>();
				try {
					if(!result.next()) {
						stock_list.add("NO STOCK ACCOUNTS");
						ManagerDashboard.set_stock(stock_list);
						create_frame(user);
						return;
					}
				}catch(SQLException e1) {
					e1.printStackTrace();
				}

				try {
					do {
						String curr_result;
						curr_result = "Stock Account: ";
						curr_result += result.getString(1);
						curr_result += ", ";
						curr_result += result.getString(2);
						System.out.println(curr_result);
						stock_list.add(curr_result);
					}while(result.next());
					ManagerDashboard.set_stock(stock_list);
					create_frame(user);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});


	}

	private void create_frame(String user) {
		//create the frame and add the lists
		frame1 = new JFrame("Customer Report");
		frame1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JPanel panel = new JPanel(new GridLayout(4,4,4,4));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel title = new JLabel("Customer Report for " + user);

		//create market list and label
		JLabel market_label = new JLabel("Market Accounts:");
		market_label.setVerticalAlignment(JLabel.CENTER);

		market_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		market_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		market_list.setVisibleRowCount(-1);

		JScrollPane marketScroller = new JScrollPane(market_list);
		marketScroller.setPreferredSize(new Dimension(250, 80));

		//create stock list and label
		//create deposit list and label
		JLabel stock_label = new JLabel("Stock Accounts:");
		stock_label.setVerticalAlignment(JLabel.CENTER);

		stock_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		stock_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		stock_list.setVisibleRowCount(-1);

		JScrollPane stockScroller = new JScrollPane(stock_list);
		stockScroller.setPreferredSize(new Dimension(250, 80));

		JButton backButton = new JButton("Back to Manager Dash");
		backButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				frame1.setVisible(false);
				frame1.dispose();

			}


		});

		panel.add(title);
		panel.add(market_label);
		panel.add(marketScroller);
		panel.add(stock_label);
		panel.add(stockScroller);
		panel.add(backButton);

		frame1.setContentPane(panel);
		frame1.pack();
		frame1.setVisible(true);

		//display
	}



}
private class DeleteListener implements ActionListener{

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