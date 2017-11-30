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
	public static int global_withdraw;


	public static void main(String[] args) {
		
/*
		StringBuilder foreign_key_checks0 = new StringBuilder("SET FOREIGN_KEY_CHECKS=0");
		DbClient.getInstance().runQuery(new UpdateQuery(foreign_key_checks0.toString()) {
			@Override
			public void onComplete(int result) {
				System.out.println("changed foreign key constraints");
			}
		});
		
		StringBuilder truncate_customers = new StringBuilder("TRUNCATE TABLE Customers");
		DbClient.getInstance().runQuery(new UpdateQuery(truncate_customers.toString()) {
			@Override
			public void onComplete(int result) {
				System.out.println("Customers truncated");
			}
		});
		StringBuilder truncate_market = new StringBuilder("TRUNCATE TABLE Market_Account");
		DbClient.getInstance().runQuery(new UpdateQuery(truncate_market.toString()) {
			@Override
			public void onComplete(int result) {
				System.out.println("Market truncated");
			}
		});
		StringBuilder truncate_stock = new StringBuilder("TRUNCATE TABLE stock_account");
		DbClient.getInstance().runQuery(new UpdateQuery(truncate_stock.toString()) {
			@Override
			public void onComplete(int result) {
				System.out.println("stock truncated");
			}
		});
		
		*/
	

		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				System.out.println("In shutdown hook");
				//update settings
				StringBuilder update_settings = new StringBuilder("UPDATE Settings SET curr_mark_account_id = ")
						.append(global_mark).append(", curr_stock_account_id = ").append(global_stock).append(", curr_deposit_id = ")
						.append(global_deposit).append(" WHERE setting_id = 1");
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					Connection connection = DriverManager.getConnection(DbClient.DB_URL_ARTHUR, DbClient.USER_ARTHUR, DbClient.PASS_ARTHUR);
					Statement stat = connection.createStatement();
					int done = stat.executeUpdate(update_settings.toString());
					if(done == 1) {
						System.out.println("Updated globals");
					}
					
				} catch (ClassNotFoundException ce) {
					// TODO Auto-generated catch block
					ce.printStackTrace();
				}catch (SQLException se) {
					se.printStackTrace();
				}
				
		
			}
		});

		//initialize global variables
		StringBuilder initialize_var = new StringBuilder("SELECT S.Date, S.curr_mark_account_id, S.curr_stock_account_id, S.curr_deposit_id, ")
				.append("S.curr_withdraw_id FROM Settings S WHERE setting_id = 1");

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
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				WelcomePage.createFrame();
				DbClient.getInstance();
				//5. Show it.

			}
		});
		





	}


}
