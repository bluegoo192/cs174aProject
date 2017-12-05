import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.Exchanger;

import javax.swing.*;

import Database.DbClient;
import Database.DbQuery;
import Database.RetrievalQuery;
import Database.UpdateQuery;
import com.mysql.cj.x.protobuf.MysqlxCrud;

public class BuyStocksPage {

	static JFrame frame;

	private static String user;
	private static String stock_id;
	private String marketID;
	private HashMap<String, Integer> stockQuantityMap = new HashMap<>();

	JButton buyButton;
	JButton sellButton;
	JButton backButton;
	TextField stockSymbolField = new TextField("Enter stock to buy");
	TextField quantityField = new TextField("Enter quantity");
	JComboBox<String> stocksComboBox;


	static JTextField amount;

	public BuyStocksPage() {


		buyButton = new JButton("Buy");
		buyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Integer quantity = Integer.parseInt(quantityField.getText());
					buyStock(stockSymbolField.getText(), quantity);
				} catch (Exception ex) {
					quantityField.setText("Must be a number!");
				}
			}
		});
		sellButton = new JButton("Sell");
		sellButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Integer quantity = Integer.parseInt(quantityField.getText());
					sellStock2(stockSymbolField.getText(), quantity);
				} catch (Exception ex) {
					quantityField.setText("Must be a number!");
				}
			}
		});
		backButton = new JButton("Back");
		stocksComboBox = new JComboBox<>();
	}


	public void createStocksPage() {
		user = CustomerDashboard.getUser();
		build_frame();
		RetrievalQuery getStocks = new RetrievalQuery("SELECT stock_symbol, StockBalance FROM stock_account WHERE Username = '"+user+"' ") {
			@Override
			public void onComplete(ResultSet result) {
				try {
					int results = 0;
					stocksComboBox.removeAll();
					while (result.next()) {
						results++;
						stocksComboBox.addItem(result.getString("stock_symbol")+" ("+result.getDouble("StockBalance")+")");
						stockQuantityMap.put(result.getString("stock_symbol"), result.getInt("StockBalance"));
					}
					if (results == 0) {
						sellButton.setText("Sell (no stocks owned)");
						sellButton.setEnabled(false);
					}
				} catch (SQLException e) {
					System.out.println("Failed to get current stocks");
					e.printStackTrace();
				}
			}
		};
		DbClient.getInstance().runQuery(getStocks);
	}

	private void build_frame() {
		BuyStocksPage.frame = new JFrame("Buy/Sell Stocks");
		BuyStocksPage.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Dimension d = new Dimension(800, 800);

		BuyStocksPage.frame.getContentPane().setPreferredSize(d);
		JPanel panel = new JPanel(new GridLayout(4,4,4,4));
		panel.add(stockSymbolField);
		panel.add(quantityField);
		panel.add(buyButton);
		panel.add(stocksComboBox);
		panel.add(sellButton);

		JButton backButton = new JButton("Back to Dash");
		backButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				CustomerDashboard.createDashboard(user);

				BuyStocksPage.frame.setVisible(false);
				BuyStocksPage.frame.dispose();

			}


		});

		panel.add(backButton);



		BuyStocksPage.frame.setContentPane(panel);
		BuyStocksPage.frame.pack();
		BuyStocksPage.frame.setVisible(true);
	}

	private void buyStock(String symbol, int quantity) {
		//			statement.setString(3, symbol);
		//			statement.setString(4, );
		System.out.println(symbol);
		DbQuery mainQuery = new RetrievalQuery("SELECT S.curr_buy_id AS curr_buy_id, S.curr_stock_account_id AS curr_stock_id, MA.AccountID AS marketID, MA.Balance AS balance, A.current_stock_price AS price FROM " +
				"Settings S, Market_Account MA, Actor_Stock A WHERE S.setting_id = 1 AND A.stock_symbol = '" + symbol + "' " +
				"AND MA.Username = '"+user+"'") {
			@Override
			public void onComplete(ResultSet result) {
				final Integer currentBuyId;
				final String marketId;
				final Integer currentStockId;
				final Double balance;
				final Double price;
				try {
					if (result.next()) {
						System.out.println(symbol);
						currentBuyId = result.getInt("curr_buy_id");
						marketId = result.getString("marketID");
						currentStockId = result.getInt("curr_stock_id");
						balance = result.getDouble("balance");
						price = result.getDouble("price");
						if (balance < (price * quantity) + DbClient.getInstance().commission) {
							System.err.println("Can't afford purchase");
							JOptionPane.showMessageDialog(null, "Can't afford purchase", "Error with Buy/Sell", 0);
							return;
						}
						System.out.println("sf" + currentBuyId+"  "+marketId);
					} else {
						currentBuyId = 1;
						marketId = "";
						currentStockId = 1;
						balance = 0.0;
						price = 100.0;
						System.out.println(symbol);
					}
				} catch (SQLException e) {
					e.printStackTrace();
					return;
				}
				DbQuery getStockAccountQuery = new RetrievalQuery("SELECT * FROM stock_account WHERE Username = '"+user+"' AND stock_symbol = '"+symbol+"'") {
					@Override
					public void onComplete(ResultSet result2) {
						try {
							System.out.println("SUBMITTING TO BUY_STOCK");
							// Update Buy_Stock and stock_account
							PreparedStatement statement = DbClient.getInstance().getMainConnection().prepareStatement(
									"INSERT INTO Buy_Stock VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
							statement.setString(1, Integer.toString(StarsRUs.global_buy+1));
							statement.setInt(2, quantity); 
							statement.setString(3, symbol);
							statement.setString(4, marketId);
							statement.setString(5, user+":"+symbol);
							statement.setDate(6, DbClient.getInstance().TODAY);
							statement.setDouble(7, DbClient.getInstance().commission);
							statement.setInt(8, quantity);
							statement.setDouble(9, price);
							statement.setInt(10, 0);
							StarsRUs.global_buy++;
							if (result2.next()) { // if they already have a stock account
								//statement.setString(5, result2.getString("AccountID"));
								DbClient.getInstance().runQuery(new UpdateQuery(statement) {
									public void onComplete(int numChanged) {
										System.out.println("BUYING QUERY SUCCESSFULL");
									}
								});
								PreparedStatement updateAccount = DbClient.getInstance().getMainConnection().prepareStatement(
										"UPDATE stock_account SET StockBalance = StockBalance + ? WHERE AccountID = ?"
										);
								updateAccount.setDouble(1, quantity);
								updateAccount.setString(2, result2.getString("AccountID"));
								DbClient.getInstance().runQuery(new UpdateQuery(updateAccount));
							} else { // if they don't, make one first
								//add to Buy_Stock account when done
								PreparedStatement createAccount = DbClient.getInstance().getMainConnection().prepareStatement(
										"INSERT INTO stock_account VALUES (?,?,?,?)");
								createAccount.setString(1, user+":"+symbol);
								createAccount.setDouble(2, quantity);
								createAccount.setString(3, user);
								createAccount.setString(4, symbol);
								//statement.setString(5, Integer.toString(currentStockId+1));
								UpdateQuery createAccountQuery = new UpdateQuery(createAccount) {
									@Override
									public void onComplete(int numRowsAffected) {

										DbClient.getInstance().runQuery(new UpdateQuery(statement));

									}
								};
								DbClient.getInstance().runQuery(createAccountQuery);
							}
							// Update balance
							DbClient.getInstance().adjustMarketAccountBalance(marketId, (long) -((quantity * price)+DbClient.getInstance().commission));
						} catch (SQLException e) {
							e.printStackTrace();
							return;
						}
					}
				};
				DbClient.getInstance().runQuery(getStockAccountQuery);
			}
		};
		DbClient.getInstance().runQuery(mainQuery);
	}

	private void sellStock(String symbol, final int quantity) {
		DbQuery getStockOwnership = new RetrievalQuery("SELECT * FROM Buy_Stock B, Actor_Stock A, Market_Account M" +
				" WHERE B.numStillOwned > 0  AND B.MarketID = M.AccountID AND M.Username = '"+user+"'" +
				" AND B.stock_symbol = '"+symbol+"' AND A.stock_symbol = '"+symbol+"'") {
			@Override
			public void onComplete(ResultSet result) {
				System.out.println(this.getQuery());
				int q = quantity; // so we can edit from within inner class
				double commission = DbClient.getInstance().commission;
				try {
					while (result.next() && q > 0) {
						StarsRUs.global_sell++;
						int currentSellId = StarsRUs.global_sell;

						int numOwned = result.getInt("numStillOwned");
						double price = result.getDouble("price");
						PreparedStatement sell = DbClient.getInstance().getMainConnection().prepareStatement(
								"INSERT INTO Sell_Stock VALUE (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
						sell.setString(1, Integer.toString(currentSellId)); // SellID
						sell.setString(3, symbol); // stock_symbol
						sell.setString(4, result.getString("MarketID")); // MarketID
						sell.setString(5, result.getString("StockID")); // StockID
						sell.setString(6, StarsRUs.global_date); // Date
						sell.setDouble(7, result.getDouble("price")); // OriginalBuyingPrice
						sell.setDouble(8, result.getDouble("current_stock_price")); // Selling_Price
						sell.setDouble(9, result.getDouble("current_stock_price") - price); // Profit
						sell.setDouble(10, DbClient.getInstance().commission); // Commmission
						if (result.getInt("numStillOwned") > q) {
							// SELL Q
							q = 0; // update q
							sell.setInt(2, q); // NumShares
							DbClient.getInstance().adjustMarketAccountBalance(result.getString("MarketID"), (long) (commission + (q * result.getDouble("current_stock_price"))));
						} else {
							// SELL NUMBER OWNED
							q -= numOwned; // update q
							sell.setInt(2, numOwned); // NumShares
							DbClient.getInstance().adjustMarketAccountBalance(result.getString("MarketID"), (long) (commission + (numOwned * result.getDouble("current_stock_price"))));
						}
						DbClient.getInstance().runQuery(new UpdateQuery(sell));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		};
		DbClient.getInstance().runQuery(getStockOwnership);
	}

	private void sellStock2(String symbol, final int quantity) {
		System.out.println("in stocks 2");
		//first, check that there are buy_stock entries for this, and that there are enough stocks left to sell
		StringBuilder check_buy_stocks = new StringBuilder("SELECT B.BuyID, B.numStillOwned, B.price, M.AccountID, S.AccountID, A.current_stock_price FROM Buy_Stock B, Market_Account M, stock_account S, Actor_Stock A ")
				.append("WHERE B.stock_symbol = '").append(symbol).append("'").append(" AND B.MarketID = M.AccountID AND ")
				.append("M.Username = '").append(user).append("'").append(" AND S.Username = '").append(user).append("'")
				.append(" AND A.stock_symbol = '").append(symbol).append("'").append(" AND S.stock_symbol = '").append(symbol).append("'");
		System.out.println(check_buy_stocks.toString());
		//run query
		DbClient.getInstance().runQuery(new RetrievalQuery(check_buy_stocks.toString()) {

			@Override
			public void onComplete(ResultSet result) {
				// TODO Auto-generated method stub
				try {
					if(!result.next()) {
						JOptionPane.showMessageDialog(null, "YOU DO NOT OWN ANY STOCKS OF THIS SYMBOL", "Error Message", 0);
						return;
					}else {
						String MarketID = result.getString(4);
						String StockID = result.getString(5);
						Double curr_price = result.getDouble(6);
						//it returned something
						int test_num = quantity;
						Vector<String> buy_ids_to_use = new Vector<String>();
						Vector<Double> buy_id_prices = new Vector<Double>();
						Vector<Integer> buy_num_shares = new Vector<Integer>();
						double profit = 0;
						//going through each value twice???
						do {
							//calculate profit here as well
							int curr = result.getInt(2);
							test_num -= curr;
							buy_ids_to_use.add(result.getString(1));
							buy_id_prices.add(result.getDouble(3));
							buy_num_shares.add(result.getInt(2));
							profit += curr_price*(curr) - (result.getInt(3)*curr);
						}while(result.next());
						if(test_num > 0) {
							JOptionPane.showMessageDialog(null, "ATTEMPT TO SELL MORE STOCKS THAN OWNED RESULTS IN FAILURE", "Error Message", 0);
							return;
						}
						//they put in stocks they own, and they have enough to sell
						//move onto adding to sell_stocks
						add_to_sell_stocks(MarketID, StockID, buy_ids_to_use, buy_id_prices, buy_num_shares, quantity, curr_price, profit);

					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			private void add_to_sell_stocks(String MarketID, String StockID, Vector<String> buy_ids, Vector<Double> buy_prices, Vector<Integer> buy_num_shares, final int quantity, double curr_price, double profit) {
				StarsRUs.global_sell++;
				String sell_id = Integer.toString(StarsRUs.global_sell);
				StarsRUs.global_sell++;


				StringBuilder insert_sell_stocks = new StringBuilder("INSERT INTO Sell_Stock VALUES( '")
						.append(sell_id).append("', ").append(quantity).append(", '").append(symbol).append("', '")
						.append(MarketID).append("', '").append(StockID).append("', '").append(StarsRUs.global_date).append("', ")
						.append(buy_prices.get(0)).append(", ").append(curr_price).append(", ").append(profit).append(", ")
						.append("20)");
				//insert into sell_stocks
				DbClient.getInstance().runQuery(new UpdateQuery(insert_sell_stocks.toString()) {

					@Override
					public void onComplete(int RowsChanged) {
						// TODO Auto-generated method stub
						System.out.println("added to sell_stocks");
						StringBuilder update_stock_account = new StringBuilder("UPDATE stock_account SET StockBalance = StockBalance -").append(quantity)
								.append(" WHERE AccountID = '").append(StockID)
								.append("'");
						DbClient.getInstance().runQuery(new UpdateQuery(update_stock_account.toString()));

						int temp = quantity;
						int i=0;
						while(temp > 0){
							//update buy_stocks
							int set = 0;
							if(buy_num_shares.get(i) > temp) {
								set = buy_num_shares.get(i) - temp;
							}
							temp -= buy_num_shares.get(i);
							System.out.println(temp);
							//update statement
							StringBuilder update_buy_stocks = new StringBuilder("UPDATE Buy_Stock SET numStillOwned = ")
									.append(set).append(" WHERE BuyID = '").append(buy_ids.get(i)).append("'");
							DbClient.getInstance().runQuery(new UpdateQuery(update_buy_stocks.toString()));

							
							i++;
							
						}
					}

				});
			}

		});
	}

	static void set_stock_id(String input) {
		stock_id = input;
	}

}
