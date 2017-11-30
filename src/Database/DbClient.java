package Database;

import java.sql.*;
import java.sql.Date;
import java.util.*;


//import java.sql.CommunicationsException;
//import com.mysql.*;


/**
 * Created by Arthur on 11/12/17.
 */


public class DbClient {

	// Connection status
	Connection connection;
	boolean connected = false;
	private Queue<DbQuery> queryQueue = new LinkedList<>();
	private boolean isRunning = false;

	public java.sql.Date TODAY = new Date(System.currentTimeMillis());

	// Connection setup
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	public static final String DB_URL_ARTHUR = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/silversteinDB?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	public static final String USER_ARTHUR = "silverstein";
	public static final String PASS_ARTHUR = "954";
	static final String DB_URL_MAGGIE = "jdbc:mysql://cs174a.engr.ucsb.edu:3066/mschmitDB";
	static final String USER_MAGGIE = "mschmit";
	static final String PASS_MAGGIE = "798";

	private static DbClient ourInstance = new DbClient();

	public static DbClient getInstance() {
		return ourInstance;
	}

	public Connection getConnection() {
		return connection;
	}

	public void run() {
		if (isRunning || !connected) return; // We can call run() whenever we want with minimal overhead
		isRunning = true;
		new Thread(new QueryRunner()).start(); // Run all queued queries in a background thread
	}

	private DbClient() {
		new Thread(new AutoConnector()).start(); // Connect asynchronously
	}

	// Execute pending queries in the queue one at a time
	// Slower than running multiple at once, but safer and easier to reason about
	private void runQueryQueue() {
		DbQuery currentQuery;
		Statement statement;
		try {
			statement = connection.createStatement();
		} catch (Exception e) {
			System.out.println("Failed to create Statement object.  Retry later.");
			e.printStackTrace();
			return;
		}
		while (!queryQueue.isEmpty()) {
			currentQuery = queryQueue.poll();
			currentQuery.execute(statement);
		}
	}

	private void onConnected() {
		for (String query : DATABASE_INIT_QUERIES) {
			runQuery(new UpdateQuery(query));
		}
		run();
	}

	public void runQuery(DbQuery query) {
		queryQueue.add(query);
		run();
	}

	/**
	 * Connect to the database.
	 * @return Whether or not the connection was successful
	 */
	private boolean connect() {
		try {
			System.out.println("Trying to connect");
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection(DB_URL_ARTHUR, USER_ARTHUR, PASS_ARTHUR);
			System.out.println("Connected to database");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// Try to connect over and over until we are successful
	private class AutoConnector implements Runnable {
		@Override
		public void run() {
			while (!connected) {
				connected = connect();
			}
			onConnected();
		}
	}

	private class QueryRunner implements Runnable {
		@Override
		public void run() {
			isRunning = true;
			try {
				runQueryQueue();
			} catch (Exception e) {
				System.out.println("Error occurred, retrying...");
			}
			isRunning = false;
		}
	}

	/**
	 * Directly adjust a market_account's balance.  Use for deposits and withdrawals
	 //* @param accountID: id of the market account
	 //* @param change: quantity to adjust by.  Negative for withdrawal, positive for deposit
	 */
//	public void adjustBalance(String accountID, long change) {
//		StringBuilder adjustQuery = new StringBuilder("UPDATE Market_Account SET ")
//				.append("Balance = Balance + ").append(change).append(", ")
//				.append("last_changed = ").append()
//				.append("WHERE AccountID = ").append(accountID).append(" ");
//		UpdateQuery adjust = new UpdateQuery(adjustQuery.toString());
//		try {
//			Connection connection = DbClient.getInstance().getConnection();
//
//			PreparedStatement statement = connection.prepareStatement(
//					"UPDATE Market_Account " +
//							"SET Balance = Balance + ?, " +
//							"last_changed = ?, " +
//							"avg_daily_balance = avg_daily_balance + (Balance *)? " +
//							"WHERE AccountID = ?");
//
//			statement.setLong(1, change);
//			statement.setDate(2, TODAY);
//			//statement.setLong(3, );
//			//pstmt.executeUpdate();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public void createEntryCustomers(String username, String password, String taxId, String state, String phone, String email) {
		StringBuilder addEntry = new StringBuilder("INSERT INTO CUSTOMERS VALUES (")
				.append(username).append(",")
				.append(state).append(",")
				.append(email).append(",")
				.append(taxId).append(",")
				.append(phone).append(",");
		runQuery(new UpdateQuery(addEntry.toString()));
	}

	// Keep this section at the end of the file so this class is easier to read
	private final String[] DATABASE_INIT_QUERIES = {
			"CREATE TABLE IF NOT EXISTS Customers (" +
					"	Username CHAR(20)," +
					"	State CHAR(2)," +
					"	Email CHAR(254) UNIQUE," +
					"	TaxID CHAR(9) UNIQUE," +
					"	Phone CHAR(10)," +
					"	Password CHAR(20)," +
					"	PRIMARY KEY (Username))",
			"CREATE TABLE IF NOT EXISTS Market_Account (" +
					"	AccountID CHAR(20)," +
					"	Balance REAL CHECK (Balance >= 0)," +
					"	Username CHAR(20) NOT NULL,\n" +
					"	avg_daily_balance REAL," +
					"	last_changed DATE," +
					"	FOREIGN KEY(username) REFERENCES Customers(username)" +
					"ON DELETE CASCADE ON UPDATE CASCADE," +
					"	PRIMARY KEY (AccountID) )",
			"CREATE TABLE IF NOT EXISTS stock_account (" +
					"	AccountID CHAR(20)," +
					"	StockBalance REAL CHECK (StockBalance >= 0)," +
					"	Username CHAR(20) NOT NULL," +
					"	FOREIGN KEY(username) REFERENCES Customers(username)" +
					"		ON DELETE CASCADE ON UPDATE CASCADE," +
					"	PRIMARY KEY (AccountID))" ,
			"CREATE TABLE IF NOT EXISTS Deposit(" +
					"	DepositID CHAR(20)," +
					"	AccountID CHAR(20) NOT NULL," +
					"	Username CHAR(20) NOT NULL," +
					"	Value REAL," +
					"	Date DATE," +
					"	FOREIGN KEY (username) REFERENCES Customers(username) ON DELETE CASCADE ON UPDATE CASCADE," +
					"	FOREIGN KEY (accountID) REFERENCES Market_Account(AccountID) ON DELETE CASCADE ON UPDATE CASCADE," +
					"	PRIMARY KEY(DepositID)" +
					")",
			"CREATE TABLE IF NOT EXISTS Withdraw(" +
					"	WithdrawID CHAR(20)," +
					"	AccountID CHAR(20) NOT NULL," +
					"	Username CHAR(20) NOT NULL," +
					"	Value REAL," +
					"	Date DATE," +
					"FOREIGN KEY (username) REFERENCES Customers(username) ON DELETE CASCADE ON UPDATE CASCADE," +
					"	FOREIGN KEY (accountID) REFERENCES Market_Account(AccountID) ON DELETE CASCADE ON UPDATE CASCADE," +
					"	PRIMARY KEY(WithdrawID)" +
					")",
			"CREATE TABLE IF NOT EXISTS Actor_Stock (" +
					"	Name CHAR(20)," +
					"	Birth DATE," +
					"	stock_symbol CHAR(3)," +
					"	current_stock_price REAL," +
					"	closing_prices_log MEDIUMTEXT," +
					"	PRIMARY KEY (stock_symbol))",
			"CREATE TABLE IF NOT EXISTS Manager (" +
					"	ManagerID CHAR(20)," +
					"	Password CHAR(20) NOT NULL," +
					"	PRIMARY KEY (ManagerID))",
			"CREATE TABLE IF NOT EXISTS Accrue_Interest(" +
					"	AccountID CHAR(20)," +
					"	MONTH DATE," +
					"	MoneyAdded REAL," +
					"	FOREIGN KEY (AccountID) REFERENCES Market_Account(AccountID) ON DELETE CASCADE ON UPDATE CASCADE," +
					"	PRIMARY KEY(AccountID, Month)" +
					")",
			"CREATE TABLE IF NOT EXISTS Dter (" +
					"	ReportID CHAR(20)," +
					"	ManagerID CHAR(20)," +
					"	FOREIGN KEY (ManagerID) REFERENCES Manager(ManagerID) ON DELETE SET NULL," +
					"	PRIMARY KEY (ReportID))",
			"CREATE TABLE IF NOT EXISTS Report (" +
					"	ReportID CHAR(20)," +
					"	ManagerID CHAR(20)," +
					"	CustomerID CHAR(20)," +
					"	Date DATE," +
					"	Text MEDIUMTEXT," +
					"	Type CHAR(20)," +
					"	FOREIGN KEY(managerID) REFERENCES Manager(ManagerID) ON DELETE SET NULL," +
					"	PRIMARY KEY (ReportID))",
			"CREATE TABLE IF NOT EXISTS Movie (" +
					"	MovieID CHAR(20)," +
					"	Title CHAR(20)," +
					"	Year CHAR(4)," +
					"	PRIMARY KEY (MovieID)" +
					")",
			"CREATE TABLE IF NOT EXISTS MovieContract(" +
					"	stock_symbol CHAR(3) NOT NULL," +
					"	MovieID CHAR(20) NOT NULL," +
					"	Role CHAR(20)," +
					"	Total_Value REAL," +
					"	FOREIGN KEY(stock_symbol) REFERENCES Actor_Stock(stock_symbol) ON UPDATE CASCADE," +
					"	FOREIGN KEY(MovieID) REFERENCES Movie(MovieID) ON DELETE CASCADE ON UPDATE CASCADE," +
					"	PRIMARY KEY(stock_symbol, MovieID)" +
					")",
			"CREATE TABLE IF NOT EXISTS Buy_Stock(" +
					"	BuyID CHAR(20)," +
					"	NumShares INT," +
					"	stock_symbol CHAR(3) NOT NULL," +
					"	MarketID CHAR(20) NOT NULL," +
					"	StockID CHAR(20) NOT NULL," +
					"	Date DATE," +
					"	Commission REAL," +
					"	FOREIGN KEY (stock_symbol) REFERENCES Actor_Stock(stock_symbol) ON UPDATE CASCADE," +
					"	FOREIGN KEY (MarketID) REFERENCES Market_Account(AccountID), " +
					"	FOREIGN KEY (StockID) REFERENCES stock_account(AccountID)," +
					"	PRIMARY KEY(BuyID)" +
					")",
			"CREATE TABLE IF NOT EXISTS Sell_Stock(" +
					"	SellID CHAR(20)," +
					"	NumShares INT," +
					"	stock_symbol CHAR(3) NOT NULL," +
					"	MarketID CHAR(20) NOT NULL," +
					"	StockID CHAR(20) NOT NULL," +
					"	Date DATE," +
					"	OriginalBuyingPrice REAL," +
					"	FOREIGN KEY (stock_symbol) REFERENCES Actor_Stock(stock_symbol) ON DELETE CASCADE ON UPDATE CASCADE," +
					"	FOREIGN KEY (MarketID) REFERENCES Market_Account(AccountID)," +
					"	FOREIGN KEY (StockID) REFERENCES stock_account(AccountID)," +
					"	PRIMARY KEY(SellID)" +
					")",
			"CREATE TABLE IF NOT EXISTS Settings("+
					"	setting_id INTEGER," +
					"	Date DATE," +
					"	market_open BIT," +
					"	curr_mark_account_id INTEGER," +
					"	curr_stock_account_id INTEGER,"+
					"	curr_deposit_id INTEGER,"+
					"	curr_withdraw_id INTEGER, "+
					"	PRIMARY KEY(setting_id)"+
					")"
	};
	
	//there is only one row in the settings table and it is identified by setting_id = 1
	//market open is 1 if open and 0 if closed
}
