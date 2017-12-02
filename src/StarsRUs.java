import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import Database.DbClient;
import Database.RetrievalQuery;
import Database.UpdateQuery;
import java.sql.*;


/**
 * Created by Arthur on 11/12/17.
 */
public class StarsRUs {

	public static String global_date;
	public static int global_mark;
	public static int global_stock;
	public static int global_deposit;
	public static int global_buy;
	public static int global_sell;
	public static int global_withdraw;
	public static boolean open_or_closed;
	
	public StarsRUs() {
		
	}


	public static void main(String[] args) {
		
/*
		StringBuilder foreign_key0 = new StringBuilder("SET FOREIGN_KEY_CHECKS=0;");
		DbClient.getInstance().runQuery(new UpdateQuery(foreign_key0.toString()) {
			@Override
			public void onComplete(int result) {
				System.out.println("foreign key 0");
			}
		});
		StringBuilder delete_buy = new StringBuilder("TRUNCATE TABLE Buy_Stock");
		DbClient.getInstance().runQuery(new UpdateQuery(delete_buy.toString()) {
			@Override
			public void onComplete(int result) {
				System.out.println("Buy Deleted");
			}
		});
		
		StringBuilder delete_sell = new StringBuilder("TRUNCATE TABLE Sell_Stock");
		DbClient.getInstance().runQuery(new UpdateQuery(delete_sell.toString()) {
			@Override
			public void onComplete(int result) {
				System.out.println("Sell Deleted");
			}
		});
		
		StringBuilder delete_mark = new StringBuilder("TRUNCATE TABLE Market_Account");
		DbClient.getInstance().runQuery(new UpdateQuery(delete_mark.toString()) {
			@Override
			public void onComplete(int result) {
				System.out.println("Market Accounts Deleted");
			}
		});
		
		StringBuilder delete_stock = new StringBuilder("TRUNCATE TABLE stock_account");
		DbClient.getInstance().runQuery(new UpdateQuery(delete_stock.toString()) {
			@Override
			public void onComplete(int result) {
				System.out.println("Stock Accounts Deleted");
			}
		});
		
		StringBuilder delete_cust = new StringBuilder("TRUNCATE TABLE Customers");
		DbClient.getInstance().runQuery(new UpdateQuery(delete_cust.toString()) {
			@Override
			public void onComplete(int result) {
				System.out.println("Customers Deleted");
			}
		});
		
		StringBuilder update_set = new StringBuilder("UPDATE Settings SET curr_mark_account_id = 1, curr_stock_account_id = 1, curr_deposit_id = 1");
		DbClient.getInstance().runQuery(new UpdateQuery(update_set.toString()) {
			@Override
			public void onComplete(int result) {
				System.out.println("update settings");
			}
		});
		
		StringBuilder foreign_key1 = new StringBuilder("SET FOREIGN_KEY_CHECKS=1;");
		DbClient.getInstance().runQuery(new UpdateQuery(foreign_key1.toString()) {
			@Override
			public void onComplete(int result) {
				System.out.println("foreign key 1");
			}
		});
		
		*/
		
		//StringBuilder add_stock = new StringBuilder("INSERT INTO Buy_Stock VALUES('1', )");


	
	

		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				System.out.println("In shutdown hook");
				//update settings
				StringBuilder update_settings = new StringBuilder("UPDATE Settings SET curr_mark_account_id = ")
						.append(global_mark).append(", curr_stock_account_id = ").append(global_stock).append(", curr_deposit_id = ")
						.append(global_deposit).append(", curr_buy_id = ").append(global_buy).append(", curr_sell_id = ").append(global_sell).append(" WHERE setting_id = 1");
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					Connection connection = DriverManager.getConnection(DbClient.DB_URL_ARTHUR, DbClient.USER_ARTHUR, DbClient.PASS_ARTHUR);
					Statement stat = connection.createStatement();
					int done = stat.executeUpdate(update_settings.toString());
					if(done == 1) {
						System.out.println("Updated globals");
					}
					
					if (stat != null) {
					    stat.close();
					}

					if (connection != null) {
					    connection.close();
					}
					
				} catch (ClassNotFoundException ce) {
					// TODO Auto-generated catch block
					ce.printStackTrace();
				}catch (SQLException se) {
					se.printStackTrace();
				}finally {
					
				}
				
		
			}
		});

		//initialize global variables
		StringBuilder initialize_var = new StringBuilder("SELECT S.Date, S.curr_mark_account_id, S.curr_stock_account_id, S.curr_deposit_id, ")
				.append("S.curr_withdraw_id, S.curr_buy_id, S.curr_sell_id, S.market_open FROM Settings S WHERE setting_id = 1");

		DbClient.getInstance().runQuery(new RetrievalQuery(initialize_var.toString()) {

			@Override
			public void onComplete(ResultSet result) {
				// TODO Auto-generated method stub

				try {
					result.next();
					global_date = result.getString(1);
					global_mark = result.getInt(2);
					global_stock = result.getInt(3);
					global_deposit = result.getInt(4);
					global_withdraw = result.getInt(5);
					global_buy = result.getInt(6);
					global_sell = result.getInt(7);
					open_or_closed = result.getBoolean(7);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}



				WelcomePage.createFrame();
				
				
				DbClient.getInstance();
				System.out.println(global_date);
				
				
				//5. Show it.

			}
		});
		





	}


}
