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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;


public class DemoActionsPage{

	static JFrame frame;

	static JTextField stock_symbol;
	static JTextField new_price;
	static JTextField new_date;

	public static JFrame createDemoPage() {

		//initialize the global variables


		frame = new JFrame("Demo Actions Dashboard");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Dimension dim = new Dimension(800, 800);

		frame.getContentPane().setPreferredSize(dim);
		JPanel panel = new JPanel(new GridLayout(5,5,5,5));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		DemoActionsPage d = new DemoActionsPage();

		JButton openMarket = new JButton("Open Market");
		openMarket.addActionListener(d.new OpenListener());

		JButton closeMarket = new JButton("Close Market");
		closeMarket.addActionListener(d.new CloseListener());

		JButton accrueInterestForArthur = new JButton("Test accrue interest");
		accrueInterestForArthur.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					DbClient.getInstance().accrueInterest("accrue");
				} catch (SQLException se) {
					System.out.println("WTF");
				}
			}
		});

		stock_symbol = new JTextField("Type Stock Symbol you would like to update here");
		new_price = new JTextField("Type new price of stock here");

		JButton setButton1 = new JButton("Set");
		setButton1.addActionListener(d.new SetStockListener());

		new_date = new JTextField("Type new date here in YYYY-MM-DD format");

		JButton setButton2 = new JButton("Set");
		setButton2.addActionListener(d.new SetDateListener());


		panel.add(openMarket);
		panel.add(closeMarket);
		panel.add(stock_symbol);
		panel.add(accrueInterestForArthur);
		panel.add(new_price);
		panel.add(setButton1);
		panel.add(new_date);
		panel.add(setButton2);

		//4. Size the frame.
		frame.pack();
		frame.setContentPane(panel);

		frame.setVisible(true);

		return frame;
	}


	private class OpenListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub

			//change settings to 1
			StarsRUs.open_or_closed = true;
			System.out.println("MARKET IS " + StarsRUs.open_or_closed);

			StringBuilder update_settings = new StringBuilder("UPDATE Settings SET market_open = 1").append(" WHERE setting_id = 1");
			DbClient.getInstance().runQuery(new UpdateQuery(update_settings.toString()) {
				
			});

		}
	}

	private class CloseListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if(!StarsRUs.open_or_closed) {
				return;
			}
			StarsRUs.open_or_closed = false;
			

			StringBuilder update_settings = new StringBuilder("UPDATE Settings SET market_open = 0").append(" WHERE setting_id = 1");
			DbClient.getInstance().runQuery(new UpdateQuery(update_settings.toString()));
			
			
			//record all stock closing prices
			StringBuilder update_stocks = new StringBuilder("UPDATE Actor_Stock SET closing_prices_log = (closing_prices_log + current_stock_price)");
			DbClient.getInstance().runQuery(new UpdateQuery(update_stocks.toString()));
		}

	}

	private class SetStockListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub

			//get stock
			String stock = stock_symbol.getText();


			if(stock.length() != 3 || !stock.matches("[a-zA-Z]+")) {
				JOptionPane.showMessageDialog(null, "Stock Symbol must be of size 3 and can only contain letters", "Stock Error", 0);
				return;
			}
			
			String num = new_price.getText();
			System.out.println("got price");
			
			if(!num.matches("[0-9]+")) {
				JOptionPane.showMessageDialog(null, "Must be a real number", "Stock Error", 0);
				return;
			}
			int price = Integer.parseInt(num);
			
			StringBuilder update_stock= new StringBuilder("UPDATE Actor_Stock SET current_stock_price = '")
					.append(price).append("' WHERE stock_symbol = '").append(stock).append("'");
			DbClient.getInstance().runQuery(new UpdateQuery(update_stock.toString()));

		}

	}

	private class SetDateListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			SimpleDateFormat dateformat= new SimpleDateFormat("yyyy-MM-dd");
			//check date is legit
			String new_date_string = new_date.getText();
			System.out.println(new_date_string);

			try {
				dateformat.parse(new_date_string);
				//it will go to catch new_date_string is not a valid date; thus, anything after this can assume that it is a valid date

				StarsRUs.global_date = new_date_string;

				StringBuilder update_settings = new StringBuilder("UPDATE Settings SET Date = '")
						.append(StarsRUs.global_date).append("' WHERE setting_id = 1");
				DbClient.getInstance().runQuery(new UpdateQuery(update_settings.toString()));

			}catch(Exception e){
				JOptionPane.showMessageDialog(null, "INVALID DATE", "Error Message", 0);
				return;
			}




		}

	}

}