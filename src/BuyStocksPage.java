import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
					sellStock(((String) stocksComboBox.getSelectedItem()).split(" ")[0], quantity);
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
						stocksComboBox.addItem(result.getString("stock_symbol")+" ("+result.getInt("StockBalance")+")");
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
						currentBuyId = result.getInt("curr_buy_id");
						marketId = result.getString("marketID");
						currentStockId = result.getInt("curr_stock_id");
						balance = result.getDouble("balance");
						price = result.getDouble("price");
						if (balance < (price * quantity) + DbClient.getInstance().commission) {
							System.err.println("Can't afford purchase");
							return;
						}
						System.out.println("sf" + currentBuyId+"  "+marketId);
					} else {
						currentBuyId = 1;
						marketId = "";
						currentStockId = 1;
						balance = 0.0;
						price = 100.0;
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
									"INSERT INTO Buy_Stock VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
							statement.setString(1, Integer.toString(StarsRUs.global_buy+1));
							statement.setInt(2, quantity); 
							statement.setString(3, symbol);
							statement.setString(4, marketId);
							statement.setString(5, user+":"+symbol);
							statement.setDate(6, DbClient.getInstance().TODAY);
							statement.setDouble(7, DbClient.getInstance().commission);
							statement.setInt(8, quantity);
							statement.setDouble(9, price);
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
		DbQuery getStockOwnership = new RetrievalQuery("SELECT * FROM Buy_Stock B, Actor_Stock A, Market_Account M, Settings S" +
				" WHERE B.numStillOwned > 0 AND S.setting_id = 1 AND B.MarketID = M.AccountID AND M.Username = '"+user+"'" +
				" AND B.stock_symbol = '"+symbol+"' AND A.stock_symbol = '"+symbol+"'") {
			@Override
			public void onComplete(ResultSet result) {
				System.out.println(this.getQuery());
				int q = quantity; // so we can edit from within inner class
				double commission = DbClient.getInstance().commission;
				try {
					while (result.next() && q > 0) {
						int currentSellId = result.getInt("curr_sell_id");
						int numOwned = result.getInt("numStillOwned");
						double price = result.getDouble("price");
						PreparedStatement sell = DbClient.getInstance().getMainConnection().prepareStatement(
								"INSERT INTO Sell_Stock VALUE (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
						sell.setString(1, Integer.toString(currentSellId+1)); // SellID
						sell.setString(3, symbol); // stock_symbol
						sell.setString(4, result.getString("MarketID")); // MarketID
						sell.setString(5, result.getString("StockID")); // StockID
						sell.setDate(6, DbClient.getInstance().TODAY); // Date
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
	
	static void set_stock_id(String input) {
		stock_id = input;
	}
	
}
