import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

import Database.DbClient;
import Database.RetrievalQuery;
import Database.UpdateQuery;

public class BuyStocksPage {

	static JFrame frame;
	
	static String user;
	private static String stock_id;
	
	
	static JTextField amount;
	
	
	public static void createStocksPage(String username) {
		user = username;
		
		frame = new JFrame("Buy/Sell Stocks");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension d = new Dimension(800, 800);
      
        frame.getContentPane().setPreferredSize(d);
        JPanel panel = new JPanel(new GridLayout(4,4,4,4));
       
		
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
		
		//create stock account if not exists
		//check if exists
		StringBuilder find_stock_account = new StringBuilder("SELECT S.AccountID ")
				.append("FROM stock_account S ").append("WHERE S.Username = ")
				.append("'").append(user).append("'");
		DbClient.getInstance().runQuery(new RetrievalQuery(find_stock_account.toString()) {
			@Override
			public void onComplete(ResultSet result) {
				try {
					if(!result.next()) {
						//no stock account
						//create new stock account
						int stock_id = (int)(Math.random()* 30000);
						String stock_id_string = Integer.toString(stock_id);
						BuyStocksPage.set_stock_id(stock_id_string);
						StringBuilder create_stock_account = new StringBuilder("INSERT INTO stock_account VALUES(")
								.append("'").append(stock_id_string).append("'").append(",").append("100")
								.append(",").append("'").append(user).append("'").append(")");
						DbClient.getInstance().runQuery(new UpdateQuery(create_stock_account.toString()) {
							@Override
							public void onComplete(int result) {
								System.out.println("created stock account");
							}
						});

						
						return;
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				try {
					String stock_id = result.getString(1);
					BuyStocksPage.set_stock_id(stock_id);
					System.out.println("found stock account");
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
		
		//now that stock_id has the stock id, do something
	}
	
	static void set_stock_id(String input) {
		stock_id = input;
	}
	
}
