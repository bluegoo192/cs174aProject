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
	
	private static String user;
	private static String stock_id;
	
	
	static JTextField amount;
	
	
	public static void createStocksPage() {
		user = CustomerDashboard.getUser();
		stock_id = CustomerDashboard.get_stock_account();
		
		if(stock_id.equals("")) {
			//create stock account
			//create new stock account
			int stock_id = (int)(Math.random()* 30000);
			String stock_id_string = Integer.toString(stock_id);
			BuyStocksPage.set_stock_id(stock_id_string);
			StringBuilder create_stock_account = new StringBuilder("INSERT INTO stock_account VALUES(")
					.append("'").append(stock_id_string).append("'").append(",").append("0")
					.append(",").append("'").append(user).append("'").append(")");
			DbClient.getInstance().runQuery(new UpdateQuery(create_stock_account.toString()) {
				@Override
				public void onComplete(int result) {
					System.out.println("created stock account");
				}
			});
		}
		
		
		
		frame = new JFrame("Buy/Sell Stocks");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension d = new Dimension(800, 800);
      
        frame.getContentPane().setPreferredSize(d);
        JPanel panel = new JPanel(new GridLayout(4,4,4,4));
       
		
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
		
		
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
