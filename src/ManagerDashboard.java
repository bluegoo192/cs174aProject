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
		customer_report = new JTextField("Type username of customer here. This field can be used to generate either the Monthly Statment or the Customer Report");
		JButton go_1 = new JButton("Customer Report");
		go_1.addActionListener(d.new Go1Listener());
		JButton delete_trans = new JButton("Delete Transactions");
		delete_trans.addActionListener(d.new DeleteListener());

		panel.add(add_interest);
		panel.add(actives);
		panel.add(dter);

		panel.add(customer_report);
		panel.add(go_1);
		panel.add(monthly_statement);
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
			// average daily balance
			RetrievalQuery getUsers = new RetrievalQuery("SELECT Username FROM Customers") {
				@Override
				public void onComplete(ResultSet result) {
					try {
						while (result.next()) {
							DbClient.getInstance().accrueInterest(result.getString("Username"));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			};
			DbClient.getInstance().runQuery(getUsers);
		}

	}
	private class StatementListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("in action performed");
			// Generate Monthly statement

			/*
			 * 
			StringBuilder combine = new StringBuilder("SELECT M.Username, nested_shares.sum_shares FROM  (SELECT marketIDs.MarketID, SUM(COALESCE(Buy_Stock.NumShares, 0) +")
					.append(" COALESCE(Sell_Stock.NumShares, 0)) as sum_shares, Buy_Stock.MarketID as ID ")
					.append("FROM( SELECT DISTINCT(MarketID) FROM (SELECT MarketID FROM Buy_Stock WHERE archived=0 UNION SELECT MarketID FROM Sell_Stock) tmp ")
					.append(") marketIDs")
					.append(" LEFT JOIN Sell_Stock ON Sell_Stock.MarketID = marketIDs.MarketID ")
					.append("LEFT JOIN Buy_Stock ON Buy_Stock.MarketID = marketIDs.MarketID")
					.append(" GROUP BY Buy_Stock.MarketID HAVING sum_shares >= 1000) as nested_shares, Market_Account M")
					.append(" WHERE nested_shares.ID = M.AccountID");
			 */

			StringBuilder get_profits = new StringBuilder("SELECT S.Profit FROM Sell_Stock S,  Market_Account MA WHERE ")
					.append("MA.Username = '").append(customer_report.getText()).append("' AND S.MarketID = MA.AccountID");		

			System.out.println(get_profits.toString());
			DbClient.getInstance().runQuery(new RetrievalQuery(get_profits.toString()) {

				@Override
				public void onComplete(ResultSet result) {
					// TODO Auto-generated method stub
					Vector<String> first_query = new Vector<String>();
					String profits ;
					try {
						if(!result.next()) {
							profits = "NO PROFITS RECORDED FOR THIS MONTH FOR THIS CUSTOMER";
						}else {
							int total = 0;
							do {
								total += result.getDouble(1);
							}while(result.next());

							profits = Integer.toString(total);

						}

						//get Accrue Interest
						StringBuilder get_interest = new StringBuilder("SELECT AI.MoneyAdded FROM Accrue_Interest AI,  Market_Account MA WHERE ")
								.append("MA.Username = '").append(customer_report.getText()).append("' AND AI.AccountID = MA.AccountID");	
						DbClient.getInstance().runQuery(new RetrievalQuery(get_interest.toString()) {

							@Override
							public void onComplete(ResultSet result) {
								try {
									if(result.next()) {
										//not 0
										int total = 0;
										do {
											total += result.getDouble(1);
										}while(result.next());
										//first_query.add(first_query.get(0) + total);
									}

									first_query.add("PROFITS SO FAR: " + profits);

									get_commision(first_query);
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}


							}
						});

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});


		}

		private void get_commision(Vector<String> first_query) {
			System.out.println("in get_commission");
			//get commision information
			StringBuilder combine = new StringBuilder("SELECT (buy.buy_count + sell.sell_count) FROM (SELECT COUNT(*) as buy_count FROM Buy_Stock B, Market_Account M WHERE B.archived = 0 AND B.MarketID = M.AccountID AND M.Username = '")
					.append(customer_report.getText()).append("') as buy, (SELECT COUNT(*) as sell_count FROM Sell_Stock S, Market_Account M WHERE S.MarketID = M.AccountID AND M.Username = '") 
					.append(customer_report.getText()).append("') as sell");
			DbClient.getInstance().runQuery(new RetrievalQuery(combine.toString()) {

				@Override
				public void onComplete(ResultSet result) {
					// TODO Auto-generated method stub
					String commission = "";
					try {
						if(!result.next()) {
							commission = "NO COMMISSIONS FOR THIS CUSTOMER";

						}else {
							commission = "Total Commission:$";
							commission += (20*result.getInt(1));
						}

						get_transactions(first_query, commission);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


				}


			});

		}

		private void get_transactions(Vector<String> first_query, String commission) {
			//get deposit list

			System.out.println("in transaction");
			//get buy list
			StringBuilder get_buy_list = new StringBuilder("SELECT  B.stock_symbol, B.NumShares, B.Date, B.price")
					.append(" FROM Buy_Stock B, Market_Account M ").append("WHERE M.Username = '")
					.append(customer_report.getText()).append("' AND B.MarketID = M.AccountID AND archived = 0");
			DbClient.getInstance().runQuery(new RetrievalQuery(get_buy_list.toString()) {
				@Override
				public void onComplete(ResultSet result) {
					Vector<String> buy_list = new Vector<String>();
					try {
						if(!result.next()) {
							buy_list.add("YOU HAVE NOT BOUGHT ANY STOCKS THIS MONTH");
							initialize_sell(first_query, commission, buy_list);
							return;
						}
					}catch(SQLException e1) {
						e1.printStackTrace();
					}

					try {
						do{
							String curr_result;
							curr_result = result.getString(1);
							curr_result += ", ";
							curr_result += result.getString(2);
							curr_result += ", ";
							curr_result += result.getString(3);
							curr_result += ", ";
							curr_result += result.getString(4);
							buy_list.add(curr_result);
						}while(result.next()) ;
						initialize_sell(first_query, commission, buy_list);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

		}

		private void initialize_sell(Vector<String> first_query, String commission, Vector<String> buy_list) {
			System.out.println("in initialize sell");
			//get sell list
			StringBuilder get_sell_list = new StringBuilder("SELECT  S.stock_symbol, S.NumShares, S.Date, S.Selling_Price, S.Profit")
					.append(" FROM Sell_Stock S, Market_Account M ").append("WHERE M.Username = '")  
					.append(customer_report.getText()).append("' AND S.MarketID = M.AccountID");
			DbClient.getInstance().runQuery(new RetrievalQuery(get_sell_list.toString()) {
				@Override
				public void onComplete(ResultSet result) {
					Vector<String> sell_list = new Vector<String>();
					try {
						if(!result.next()) {
							sell_list.add("YOU HAVE NOT SOLD ANY STOCKS THIS MONTH");
							get_info(first_query, commission, buy_list, sell_list);
							return;
						}else {
							do{
								String curr_result;
								curr_result = result.getString(1);
								curr_result += ", ";
								curr_result += result.getString(2);
								curr_result += ", ";
								curr_result += result.getString(3);
								curr_result += ", ";
								curr_result += result.getString(4);
								curr_result += ", ";
								curr_result += result.getString(5);

								sell_list.add(curr_result);
							}while(result.next()) ;
							get_info(first_query, commission, buy_list, sell_list);
						}
					}catch(SQLException e1) {
						e1.printStackTrace();
					}

				}
			});
		}

		private void get_info(Vector<String> first_query, String commission, Vector<String> buy_list, Vector<String> sell_list) {
			System.out.println("in get info");
			StringBuilder get_info = new StringBuilder(" SELECT C.Name, C.Username, C.Email, M.balance, M.Original_Monthly_Balance FROM Customers C, Market_Account M ")
					.append("WHERE M.Username = C.Username AND C.Username = '").append(customer_report.getText()).append("'");
			DbClient.getInstance().runQuery(new RetrievalQuery(get_info.toString()) {
				@Override
				public void onComplete(ResultSet result) {
					Vector<String> info = new Vector<String>();
					try {
						if(!result.next()) {
							info.add("NO CUSTOMER INFO");
							return;
						}else {
							info.add(result.getString(1).toString());
							info.add(result.getString(2).toString());
							info.add(result.getString(3).toString());
							info.add("Current Balance: " + result.getString(4).toString());
							info.add("Beginning of the Month Balance: " + result.getString(5).toString());
							build_frame(first_query, commission, buy_list, sell_list, info);
						}
					}catch(SQLException e1) {
						e1.printStackTrace();
					}


				}

			});

		}

		public void build_frame(Vector<String> first_query, String commission, Vector<String> buy, Vector<String> sell, Vector<String> info) {
		

			JFrame frame2 = new JFrame("MONTHLY REPORT");
			frame2.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			Dimension d = new Dimension(800, 800);


			JLabel info_label = new JLabel("CUSTOMER INFO");
			info_label.setVerticalAlignment(JLabel.CENTER);
			JList info_list = new JList(info);

			info_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			info_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			info_list.setVisibleRowCount(-1);

			JScrollPane infoScroller = new JScrollPane(info_list);
			infoScroller.setPreferredSize(new Dimension(250, 80));

			JLabel trans_label = new JLabel("PROFIT INFORMATION (from selling and interest)");
			trans_label.setVerticalAlignment(JLabel.CENTER);
			JList trans_list = new JList(first_query);

			trans_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			trans_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			trans_list.setVisibleRowCount(-1);

			JScrollPane transScroller = new JScrollPane(trans_list);
			transScroller.setPreferredSize(new Dimension(250, 80));

			JLabel comm = new JLabel(commission);
			comm.setVerticalAlignment(JLabel.CENTER);

			//create buy list and label
			JLabel buy_label = new JLabel("Buy (stock symbol, number of stocks, date, price):");
			buy_label.setVerticalAlignment(JLabel.CENTER);
			JList buy_list = new JList(buy);

			buy_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			buy_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			buy_list.setVisibleRowCount(-1);

			JScrollPane  buyScroller = new JScrollPane( buy_list);
			buyScroller.setPreferredSize(new Dimension(250, 80));

			//create sell list and label
			JLabel sell_label = new JLabel("Sell (stock symbol, number of stocks, date, price, profit):");
			sell_label.setVerticalAlignment(JLabel.CENTER);
			JList sell_list = new JList(sell);

			sell_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			sell_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			sell_list.setVisibleRowCount(-1);

			JScrollPane sellScroller = new JScrollPane(sell_list);
			sellScroller.setPreferredSize(new Dimension(250, 80));

			JButton backButton = new JButton("Back to Dash");
			backButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					frame2.setVisible(false);
					frame2.dispose();
				}

			});

			frame2.getContentPane().setPreferredSize(d);
			JPanel panel = new JPanel(new GridLayout(4,4,4,4));
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));



			panel.add(info_label);
			panel.add(infoScroller);
			panel.add(trans_label);
			panel.add(transScroller);
			panel.add(comm);
			panel.add(buy_label);
			panel.add(buy_list);
			panel.add(buyScroller);
			panel.add(sell_label);
			panel.add(sellScroller);
			panel.add(backButton);

			frame2.setContentPane(panel);

			frame2.pack();
			frame2.setVisible(true);

		}

	}


	private class ActivesListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {

			/*StringBuilder combine = new StringBuilder("SELECT M.Username, nested_shares.sum_shares FROM  (SELECT marketIDs.MarketID, SUM(COALESCE(Buy_Stock.NumShares, 0) +")
					.append(" COALESCE(Sell_Stock.NumShares, 0)) as sum_shares, Buy_Stock.MarketID as ID ")
					.append("FROM( SELECT DISTINCT(MarketID) FROM (SELECT MarketID FROM Buy_Stock WHERE archived=0 UNION SELECT MarketID FROM Sell_Stock) tmp ")
					.append(") marketIDs")
					.append(" LEFT JOIN Sell_Stock ON Sell_Stock.MarketID = marketIDs.MarketID ")
					.append("LEFT JOIN Buy_Stock ON Buy_Stock.MarketID = marketIDs.MarketID")
					.append(" GROUP BY Buy_Stock.MarketID HAVING sum_shares >= 1000) as nested_shares, Market_Account M")
					.append(" WHERE nested_shares.ID = M.AccountID");
					
					*StringBuilder get_profits = new StringBuilder("SELECT S.Profit FROM Sell_Stock S,  Market_Account MA WHERE ")
					.append("MA.Username = '").append(customer_report.getText()).append("' AND S.MarketID = MA.AccountID");		

			System.out.println(get_profits.toString());
			DbClient.getInstance().runQuery(new RetrievalQuery(get_profits.toString()) {


						//get Accrue Interest
						StringBuilder get_interest = new StringBuilder("SELECT AI.MoneyAdded FROM Accrue_Interest AI,  Market_Account MA WHERE ")
								.append("MA.Username = '").append(customer_report.getText()).append("' AND AI.AccountID = MA.AccountID");	
						DbClient.getInstance().runQuery(new RetrievalQuery(get_interest.toString()) {
					*/
			StringBuilder get_num_buy = new StringBuilder("SELECT M.Username FROM Buy_Stock BS, Sell_Stock SS, Market_Account M WHERE ")
					.append("BS.MarketID = SS.MarketID AND M.AccountID = BS.MarketID GROUP BY M.Username HAVING (SUM(BS.numShares) + SUM(SS.numShares))> 1000");
			DbClient.getInstance().runQuery(new RetrievalQuery(get_num_buy.toString()) {
				@Override
				public void onComplete(ResultSet result) {
					try {
						if(!result.next()) {
							JOptionPane.showMessageDialog(null, "NO ACTIVE CUSTOMERS", "Customer Message", 1);
							return;
						}else {
							Vector<String> output_string = new Vector<String>();
							do {
								String curr_result;
								curr_result = result.getString(1);
								System.out.println(curr_result);
								output_string.add(curr_result);
							}while(result.next());
							build_actives_frame(output_string);
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
			user_list.setLayoutOrientation(JList.VERTICAL_WRAP);
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

			//join buy_stock and sell_stock
			StringBuilder combine = new StringBuilder("SELECT M.Username, nested_query.total_earnings, C.State, C.TaxID FROM (SELECT marketIDs.MarketID, SUM(COALESCE(Sell_Stock.Profit, 0)")
					.append("+ COALESCE(Accrue_Interest.MoneyAdded, 0)) as total_earnings ")
					.append("FROM( SELECT DISTINCT(MarketID) FROM (SELECT MarketID FROM Sell_Stock UNION SELECT AccountID FROM Accrue_Interest) tmp ")
					.append(") marketIDs ")
					.append("LEFT JOIN Sell_Stock ON Sell_Stock.MarketID = marketIDs.MarketID ")
					.append("LEFT JOIN Accrue_Interest ON Accrue_Interest.AccountID = marketIDs.MarketID")
					.append(" GROUP BY Sell_Stock.MarketID HAVING total_earnings >= 10000) as nested_query, Market_Account M, Customers C ")
					.append("WHERE C.Username = M.Username AND nested_query.MarketID = M.AccountID");
			DbClient.getInstance().runQuery(new RetrievalQuery(combine.toString()) {
				@Override
				public void onComplete(ResultSet result) {
					Vector<String> dter_list = new Vector<String>();
					try {
						if(!result.next()) {
							dter_list.add("NO CUSTOMERS MUST BE REPORTED THIS MONTH");
							build_dter_frame(dter_list);
							return;
						}

					}catch(SQLException e1) {
						e1.printStackTrace();
					}

					try {
						do {
							String curr_result;
							curr_result = result.getString(1);
							curr_result += ", ";
							curr_result += result.getString(2);
							curr_result += ", ";
							curr_result += result.getString(3);
							curr_result += ", ";
							curr_result += result.getString(4);
							dter_list.add(curr_result);
						}while(result.next());
						build_dter_frame(dter_list);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});


		}


		private void build_dter_frame(Vector<String> users) {
			JList user_list = new JList(users);

			JFrame frame1 = new JFrame("Government Drug & Tax Evasion Report");
			frame1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			Dimension d = new Dimension(800, 800);

			//create deposit list and label
			JLabel actives_label = new JLabel("Customers to be reported this month: ");
			actives_label.setVerticalAlignment(JLabel.CENTER);

			user_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			user_list.setLayoutOrientation(JList.VERTICAL_WRAP);
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
	//also, this function updates the original monthly balance used to generate the monthly statement
	private class DeleteListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub

			int reply = JOptionPane.showConfirmDialog(null, "Are you sure? (Clicking yes will irretrivalbly delete all of the recorded transactions)", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.NO_OPTION)
			{
				return;
			}

			StringBuilder update_starting_balance = new StringBuilder("UPDATE Market_Account SET Original_Monthly_Balance = Balance");
			DbClient.getInstance().runQuery(new UpdateQuery(update_starting_balance.toString()) {
				public void onComplete(int numRowsAffected) {
					System.out.println("updated original monthly balance");
				}
			});

			//delete everything in buy stock
			StringBuilder delete_stock_buys = new StringBuilder("UPDATE Buy_Stock SET archived = 1;");
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