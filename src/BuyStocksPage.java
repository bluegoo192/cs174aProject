import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.*;

import Database.DbClient;
import Database.RetrievalQuery;
import Database.UpdateQuery;

public class BuyStocksPage {

	static JFrame frame;
	
	private static String user;
	private static String stock_id;
	private String[] stocks = {"Loading stocks..."};
	private HashMap<String, Double> stockQuantityMap = new HashMap<>();

	Button buyButton;
	Button sellButton;
	Button backButton;
	JComboBox<String> stocksComboBox;
	
	
	static JTextField amount;

	public BuyStocksPage() {
		buyButton = new Button();
		sellButton = new Button();
		backButton = new Button();
		stocksComboBox = new JComboBox<>(stocks);
		RetrievalQuery getStocks = new RetrievalQuery("SELECT stock_symbol, StockBalance FROM stock_account WHERE AccountID = "+user+" ") {
			@Override
			public void onComplete(ResultSet result) {
				System.out.println(result.toString());
			}
		};
		DbClient.getInstance().runQuery(getStocks);
	}
	
	
	public void createStocksPage() {
		user = CustomerDashboard.getUser();
		stock_id = CustomerDashboard.get_stock_account();
		
		if(stock_id.equals("")) {
			//create stock account
			//create new stock account
			String stock_id_string = Integer.toString(StarsRUs.global_stock);
			StarsRUs.global_stock += 1;
			BuyStocksPage.set_stock_id(stock_id_string);
//			try {
//				PreparedStatement statement = DbClient.getInstance().getConnection().prepareStatement("" +
//						"INSERT INTO stock_account VALUES (?, ?, ?, ?)");
//				statement.setString(1, stock_id_string);
//				statement.setDouble(2, 3);
//				statement.setString(3, user);
//				statement.setString(4, )
//			} catch (SQLException e) {
//				System.out.println("Failed to create statement");
//				e.printStackTrace();
//			}

			StringBuilder create_stock_account = new StringBuilder("INSERT INTO stock_account VALUES(")
					.append("'").append(stock_id_string).append("'").append(",").append("0")
					.append(",").append("'").append(user).append("'").append(")");
			build_frame();
			DbClient.getInstance().runQuery(new UpdateQuery(create_stock_account.toString()) {
				@Override
				public void onComplete(int result) {
					System.out.println("created stock account");
				}
			});
		}else {
			build_frame();
		}

		//now that stock_id has the stock id, do something
	}
	
	private void build_frame() {
		BuyStocksPage.frame = new JFrame("Buy/Sell Stocks");
		BuyStocksPage.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension d = new Dimension(800, 800);
      
        BuyStocksPage.frame.getContentPane().setPreferredSize(d);
        JPanel panel = new JPanel(new GridLayout(4,4,4,4));
		panel.add(new TextField("Enter a stock symbol"));
		panel.add(buyButton);
		panel.add(stocksComboBox);
		panel.add(sellButton);
		panel.add(backButton);


		BuyStocksPage.frame.setContentPane(panel);
        BuyStocksPage.frame.pack();
        BuyStocksPage.frame.setVisible(true);
	}
	
	static void set_stock_id(String input) {
		stock_id = input;
	}
	
}
