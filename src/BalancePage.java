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


public class BalancePage {

	static JFrame frame;

	private static String user;
	private static String account;



	public static void createBalancePage() {
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
					String balance_info = "";
					if(!result.next()) {
						balance_info = "YOU HAVE NO MARKET ACCOUNTS";
						return;
					}else {
						balance_info = result.getString(1);
					}
					findStockAccounts(balance_info);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});



	}

	private static void findStockAccounts(String mark_balance) {


		/**
		 * 
			"CREATE TABLE IF NOT EXISTS stock_account (" +
					"	AccountID CHAR(20)," +
					"	StockBalance REAL CHECK (StockBalance >= 0), " +
					"	stock_symbol CHAR(3) NOT NULL, " +
					"	Username CHAR(20) NOT NULL," +
					"	FOREIGN KEY(Username) REFERENCES Customers(Username)" +
					"		ON DELETE CASCADE ON UPDATE CASCADE," +
					"	FOREIGN KEY (stock_symbol) REFERENCES Actor_Stock(stock_symbol)," +
					"	PRIMARY KEY (AccountID))" ,
		 * 
		 */
		StringBuilder findStocks = new StringBuilder("SELECT S.stock_symbol, S.StockBalance ")
				.append("FROM stock_account S ").append("WHERE ")
				.append("S.Username = ").append("'").append(user)
				.append("'");

		DbClient.getInstance().runQuery(new RetrievalQuery(findStocks.toString()) {
			@Override
			public void onComplete(ResultSet result) {
				Vector<String> stock_info = new Vector<String>();
				try {
					if(!result.next()) {
						String stocks = "YOU HAVE NO STOCK ACCOUNTS";
						stock_info.add(stocks);

						return;
					}else {
						do {
							String curr_result;
							curr_result = result.getString(1);
							curr_result += ", ";
							curr_result += result.getString(2);
							stock_info.add(curr_result);
						}while(result.next());
						create_frame(mark_balance, stock_info);
						return;
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
	}

	private static void create_frame(String mark_info, Vector<String> stock_info) {

		frame = new JFrame("Balance Info");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Dimension d = new Dimension(800, 800);

		frame.getContentPane().setPreferredSize(d);
		JPanel panel = new JPanel(new GridLayout(4,4,4,4));
		JLabel label1 = new JLabel("Market Account Balance: $" + mark_info);
		
		JLabel stock_label = new JLabel("Stock Account Balances: ");
        stock_label.setVerticalAlignment(JLabel.CENTER);
        
        JList stock_list = new JList(stock_info);
        
        stock_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        stock_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        stock_list.setVisibleRowCount(-1);

        JScrollPane stockScroller = new JScrollPane(stock_list);
        stockScroller.setPreferredSize(new Dimension(250, 80));




		panel.add(label1);
		panel.add(stock_label);
		panel.add(stockScroller);
		
		JButton backButton = new JButton("Back to Dash");
		backButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//CustomerDashboard.createDashboard(user);
				
				frame.setVisible(false);
				frame.dispose();

			}


		});
		
		panel.add(backButton);


		frame.setContentPane(panel);
		frame.pack();



		frame.setVisible(true);
	}


}
