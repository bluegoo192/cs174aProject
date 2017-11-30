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
import java.util.Vector;

import javax.swing.*;


public class TransactionHistoryPage {

	static JFrame frame;
	
	static String user;
	private static String stock_account;
	private static String market_account;
	
	private static JList deposit_list;
	private static JList withdraw_list;
	private static JList buy_list;
	private static JList sell_list;
	
	//setter functions
	public static void set_stock_account(String input) {
		stock_account = input;
	}
	public static void set_market_account(String input) {
		market_account = input;
	}
	
	public static void set_deposits(Vector<String> input) {
		deposit_list = new JList(input);
	}
	public static void set_withdraws(Vector<String> input) {
		withdraw_list = new JList(input);
	}
	public static void set_buy(Vector<String> input) {
		buy_list = new JList(input);
	}
	public static void set_sell(Vector<String> input) {
		sell_list = new JList(input);
	}
	
	public static void createHistoryPage() {
		user = CustomerDashboard.getUser();
		stock_account = CustomerDashboard.get_stock_account();
		market_account = CustomerDashboard.get_market_account();
		
		
		initialize_lists();
		
		frame = new JFrame("Transaction History");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension d = new Dimension(800, 800);
        
        //create deposit list and label
        JLabel deposit_label = new JLabel("Deposits:");
        deposit_label.setVerticalAlignment(JLabel.CENTER);
        
        deposit_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        deposit_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        deposit_list.setVisibleRowCount(-1);

        JScrollPane depositScroller = new JScrollPane(deposit_list);
        depositScroller.setPreferredSize(new Dimension(250, 80));
        
        //create withdraw list and label
        JLabel withdraw_label = new JLabel("Withdraws:");
        withdraw_label.setVerticalAlignment(JLabel.CENTER);
        
        withdraw_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        withdraw_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        withdraw_list.setVisibleRowCount(-1);

        JScrollPane withdrawScroller = new JScrollPane(withdraw_list);
        withdrawScroller.setPreferredSize(new Dimension(250, 80));
        
        //create buy list and label
        JLabel buy_label = new JLabel("Buy:");
        buy_label.setVerticalAlignment(JLabel.CENTER);
        
        buy_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        buy_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        buy_list.setVisibleRowCount(-1);

        JScrollPane  buyScroller = new JScrollPane( buy_list);
        buyScroller.setPreferredSize(new Dimension(250, 80));
        
        //create sell list and label
        JLabel sell_label = new JLabel("Sell:");
        sell_label.setVerticalAlignment(JLabel.CENTER);
        
        sell_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        sell_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        sell_list.setVisibleRowCount(-1);

        JScrollPane sellScroller = new JScrollPane(sell_list);
        sellScroller.setPreferredSize(new Dimension(250, 80));
      
        frame.getContentPane().setPreferredSize(d);
        JPanel panel = new JPanel(new GridLayout(4,4,4,4));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        TransactionHistoryPage t = new TransactionHistoryPage();
        JButton backButton = new JButton("Back to Dash");
        backButton.addActionListener(t.new BackListener());

        panel.add(deposit_label);
		panel.add(depositScroller);
		panel.add(withdraw_label);
		panel.add(withdrawScroller);
		panel.add(buy_label);
		panel.add(buyScroller);
		panel.add(sell_label);
		panel.add(sellScroller);
		panel.add(backButton);
        
		frame.setContentPane(panel);
		frame.pack();
		
		
		
		frame.setVisible(true);
	}
	
	
	private static void initialize_lists() {
		//get deposit list
		StringBuilder get_deposit_list = new StringBuilder("SELECT D.Value, D.Date")
				.append(" FROM Deposit D ").append("WHERE D.AccountID = '")
				.append(market_account).append("'").append(" AND D.Username = '" )
				.append(user).append("'");
		DbClient.getInstance().runQuery(new RetrievalQuery(get_deposit_list.toString()) {
			@Override
			public void onComplete(ResultSet result) {
				Vector<String> deposit_list = new Vector<String>();
				try {
					if(!result.next()) {
						deposit_list.add("NO DEPOSITS");
						TransactionHistoryPage.set_deposits(deposit_list);
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
						deposit_list.add(curr_result);
					}while(result.next()) ;
					TransactionHistoryPage.set_deposits(deposit_list);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		//get withdraw list
		StringBuilder get_withdraw_list = new StringBuilder("SELECT W.Value, W.Date")
				.append(" FROM Withdraw W ").append("WHERE W.AccountID = '")
				.append(market_account).append("'").append(" AND W.Username = '" )
				.append(user).append("'");
		DbClient.getInstance().runQuery(new RetrievalQuery(get_withdraw_list.toString()) {
			@Override
			public void onComplete(ResultSet result) {
				Vector<String> withdraw_list = new Vector<String>();
				try {
					if(!result.next()) {
						withdraw_list.add("NO WITHDRAWS");
						TransactionHistoryPage.set_withdraws(withdraw_list);
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
						withdraw_list.add(curr_result);
					}while(result.next());
					TransactionHistoryPage.set_withdraws(withdraw_list);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		//get buy list
		StringBuilder get_buy_list = new StringBuilder("SELECT  B.stock_symbol, B.NumShares, B.Date")
				.append(" FROM Buy_Stock B ").append("WHERE B.MarketID = '")
				.append(market_account).append("'").append(" AND B.StockID = '").append(stock_account).append("'");
		DbClient.getInstance().runQuery(new RetrievalQuery(get_buy_list.toString()) {
			@Override
			public void onComplete(ResultSet result) {
				Vector<String> buy_list = new Vector<String>();
				try {
					if(!result.next()) {
						buy_list.add("YOU HAVE NOT BOUGHT ANY STOCKS THIS MONTH");
						TransactionHistoryPage.set_buy(buy_list);
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
						buy_list.add(curr_result);
					}while(result.next()) ;
					TransactionHistoryPage.set_buy(buy_list);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		//get sell list
		StringBuilder get_sell_list = new StringBuilder("SELECT  S.stock_symbol, S.NumShares, S.Date")
				.append(" FROM Sell_Stock S ").append("WHERE S.MarketID = '")
				.append(market_account).append("'").append(" AND S.StockID = '").append(stock_account).append("'");
		DbClient.getInstance().runQuery(new RetrievalQuery(get_buy_list.toString()) {
			@Override
			public void onComplete(ResultSet result) {
				Vector<String> sell_list = new Vector<String>();
				try {
					if(!result.next()) {
						sell_list.add("YOU HAVE NOT SOLD ANY STOCKS THIS MONTH");
						TransactionHistoryPage.set_sell(sell_list);
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
						sell_list.add(curr_result);
					}while(result.next());
					TransactionHistoryPage.set_sell(sell_list);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private class BackListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			frame.setVisible(false);
			frame.dispose();
		}
		
		
	}
}
