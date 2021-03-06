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
	Connection mainConnection;
	Connection moviesConnection;
	boolean connected = false;
	private Queue<DbQuery> queryQueue = new LinkedList<>();
	private boolean isRunning = false;
	public final int commission = 20;

	public java.sql.Date TODAY = new Date(System.currentTimeMillis());

	private static MovieApi movieApi;

	// Connection setup
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	public static final String MOVIESDB_URL = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/moviesDB?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	public static final String DB_URL_ARTHUR = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/silversteinDB?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	public static final String USER_ARTHUR = "silverstein";
	public static final String PASS_ARTHUR = "954";
	static final String DB_URL_MAGGIE = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/mschmitDB";
	static final String USER_MAGGIE = "mschmit";
	static final String PASS_MAGGIE = "798";

	private static DbClient ourInstance = new DbClient();

	public static DbClient getInstance() {
		return ourInstance;
	}

	public Connection getMainConnection() {
		return mainConnection;
	}

	public Connection getMoviesConnection() {
		return moviesConnection;
	}

	public void run() {
		if (isRunning || !connected) return; // We can call run() whenever we want with minimal overhead
		isRunning = true;
		new Thread(new QueryRunner()).start(); // Run all queued queries in a background thread
	}

	private DbClient() {
		movieApi = new MovieApi();
		new Thread(new AutoConnector()).start(); // Connect asynchronously
	}

	// Execute pending queries in the queue one at a time
	// Slower than running multiple at once, but safer and easier to reason about
	private void runQueryQueue() {
		DbQuery currentQuery;
		Statement[] statements = new Statement[2];
		try {
			statements[0] = mainConnection.createStatement();
			statements[1] = moviesConnection.createStatement();
		} catch (Exception e) {
			System.out.println("Failed to create Statement object.  Retry later.");
			e.printStackTrace();
			return;
		}
		while (!queryQueue.isEmpty()) {
			currentQuery = queryQueue.poll();
			currentQuery.execute(statements);
		}
	}

	private void onConnected() {
		for (String query : DATABASE_INIT_QUERIES) {
			runQuery(new UpdateQuery(query));
		}
		runQuery(new RetrievalQuery("SELECT Date FROM Settings WHERE setting_id = 1") {
			@Override
			public void onComplete(ResultSet result) {
				try {
					result.next();
					TODAY = result.getDate("Date");
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});
		run();
	}

	public void runQuery(DbQuery query) {
		queryQueue.add(query);
		run();
	}

	public static MovieApi getMovieApi() {
		return movieApi;
	}

	/**
	 * Connect to the database.
	 * @return Whether or not the mainConnection was successful
	 */
	private boolean connect() {
		int failures = 0;
		if (mainConnection == null) {
			try {
				System.out.println("Trying to connect");
				Class.forName(JDBC_DRIVER);
				mainConnection = DriverManager.getConnection(DB_URL_ARTHUR, USER_ARTHUR, PASS_ARTHUR);
				System.out.println("Connected to main database");
			} catch (Exception e) {
				e.printStackTrace();
				failures++;
			}
		}
		if (moviesConnection == null) {
			try {
				System.out.println("Trying to connect");
				Class.forName(JDBC_DRIVER);
				moviesConnection = DriverManager.getConnection(MOVIESDB_URL, USER_ARTHUR, PASS_ARTHUR);
				System.out.println("Connected to movies database");
			} catch (Exception e) {
				e.printStackTrace();
				failures++;
			}
		}
		return (failures == 0);
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
	public void adjustMarketAccountBalance(String accountID, long change) throws SQLException {
		PreparedStatement statement = getAdjustBalanceStatement(accountID, change);
		UpdateQuery adjustQuery = new UpdateQuery(statement);
		System.out.println(statement.toString());
		this.runQuery(adjustQuery);
	}

	private PreparedStatement getAdjustBalanceStatement(String accountID, long change) throws SQLException {
		// new avg_daily_balance =
		// (   (old_avg_daily_balance * <num days it was an average over>)
		//   + (old_balance * <days balance was at old_balance>)  )
		//  / (total days)
		System.out.println(TODAY.toString());
		
		PreparedStatement statement = mainConnection.prepareStatement(
				"UPDATE Market_Account " +
						"SET" +
						" old_ADB = ( (old_ADB * DATEDIFF(last_changed, last_interest_accrual)) + (Balance * DATEDIFF(?, last_changed)) )" +
							"/ DATEDIFF(?, last_interest_accrual)," +
						" Balance = Balance + ?," +
						" last_changed = ? " +
						" WHERE AccountID = ? ");
		statement.setString(1, TODAY.toString());
		statement.setString(2, TODAY.toString());
		statement.setLong(3, change);
		statement.setString(4, TODAY.toString());
		statement.setString(5, accountID);
		return statement;
	}

	private PreparedStatement getAdjustBalanceStatementForUsername(String username, long change) throws SQLException {
		// new avg_daily_balance =
		// (   (old_avg_daily_balance * <num days it was an average over>)
		//   + (old_balance * <days balance was at old_balance>)  )
		//  / (total days)
		System.out.println(TODAY.toString());

		PreparedStatement statement = mainConnection.prepareStatement(
				"UPDATE Market_Account " +
						"SET" +
						" old_ADB = ( (old_ADB * DATEDIFF(last_changed, last_interest_accrual)) + (Balance * DATEDIFF(?, last_changed)) )" +
						"/ DATEDIFF(?, last_interest_accrual)," +
						" Balance = Balance + ?," +
						" last_changed = ? " +
						" WHERE Username = ? ");
		statement.setString(1, TODAY.toString());
		statement.setString(2, TODAY.toString());
		statement.setLong(3, change);
		statement.setString(4, TODAY.toString());
		statement.setString(5, username);
		return statement;
	}

	public void accrueInterest(String username) throws SQLException {

		/* Step 1: Update the average daily balance */
		runQuery(new UpdateQuery(getAdjustBalanceStatementForUsername(username, 0)) {
			@Override
			public void onComplete(int numRowsAffected) {

				/* Step 2: Get the updated average daily balance and interest rate */
				String getDataQuery = "Select A.old_ADB AS old_ADB, A.AccountID AS AccountID, S.interest_rate AS interest_rate " + // old_ADB is actually current right now
						"FROM Market_Account A, Settings S WHERE A.Username = '" + username + "' AND " +
						"S.setting_id = 1";
				runQuery(new RetrievalQuery(getDataQuery) {
					@Override
					public void onComplete(ResultSet result) {
						try {
							result.next();
							String accountId = result.getString("AccountID");
							double interest = result.getLong("old_ADB") * result.getDouble("interest_rate");
							System.out.println("old ADB = "+result.getLong("old_ADB"));

							/* Step 3: update market account and record transaction in Accrue_Interest */
							PreparedStatement marketAccountStatement = mainConnection.prepareStatement("UPDATE Market_Account " +
									"SET Balance = Balance + ?," +
									" last_interest_accrual = ?," +
									" old_ADB = Balance," +
									" last_changed = ?" +
									" WHERE AccountID = ?");
							marketAccountStatement.setDouble(1, interest);
							marketAccountStatement.setDate(2, TODAY);
							marketAccountStatement.setDate(3, TODAY);
							marketAccountStatement.setString(4, accountId);
							PreparedStatement accrueInterestStatement = mainConnection.prepareStatement("INSERT INTO " +
									"Accrue_Interest (AccountID, MONTH, MoneyAdded)" +
									" VALUES (?, ?, ?)");
							accrueInterestStatement.setString(1, accountId);
							accrueInterestStatement.setDate(2, TODAY);
							accrueInterestStatement.setDouble(3, interest);
							runQuery(new UpdateQuery(marketAccountStatement));
							runQuery(new UpdateQuery(accrueInterestStatement));
						} catch (SQLException e) {
							System.out.println("FAILED TO ACCRUE INTEREST FOR "+username);
							e.printStackTrace();
						}
					}
				});
			}
		});
	}

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
					"	Name CHAR(30),"+
					"	PRIMARY KEY (Username))",
			"CREATE TABLE IF NOT EXISTS Market_Account (" +
					"	AccountID CHAR(20)," +
					"	Balance REAL CHECK (Balance >= 0)," +
					"	Username CHAR(20) NOT NULL,\n" +
					"	old_ADB REAL," +  // old average daily balance (until the most recent balance change)
					"	last_changed DATE," +
					"	last_interest_accrual DATE," +
					"	Original_Monthly_Balance REAL,"+
					"	FOREIGN KEY(username) REFERENCES Customers(username)" +
					"ON DELETE CASCADE ON UPDATE CASCADE," +
					"	PRIMARY KEY (AccountID) )",
			"CREATE TABLE IF NOT EXISTS stock_account (" +
					"	AccountID CHAR(20)," +
					"	StockBalance REAL CHECK (StockBalance >= 0), " +
					"	stock_symbol CHAR(3) NOT NULL, " +
					"	Username CHAR(20) NOT NULL," +
					"	FOREIGN KEY(Username) REFERENCES Customers(Username)" +
					"		ON DELETE CASCADE ON UPDATE CASCADE," +
					"	FOREIGN KEY (stock_symbol) REFERENCES Actor_Stock(stock_symbol)," +
					"	PRIMARY KEY (AccountID))" ,
			"CREATE TABLE IF NOT EXISTS Deposit(" +
					"	DepositID CHAR(20)," +
					"	AccountID CHAR(20) NOT NULL," +
					"	Username CHAR(20) NOT NULL," +
					"	Value REAL," +
					"	Date DATE," +
					"	OriginalBalance REAL,"+
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
					"	OriginalBalance REAL,"+
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
		
			"CREATE TABLE IF NOT EXISTS MovieContract(" +
					"	stock_symbol CHAR(3) NOT NULL," +
					"	MovieID INTEGER NOT NULL," +
					"	Role CHAR(20)," +
					"	Total_Value REAL," +
					"	FOREIGN KEY(stock_symbol) REFERENCES Actor_Stock(stock_symbol) ON UPDATE CASCADE," +
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
					"	numStillOwned INT," +
					"	price REAL," +
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
					"	Selling_Price REAL,"+
					"	Profit REAL,"+
					"	Commission REAL,"+
					"	FOREIGN KEY (stock_symbol) REFERENCES Actor_Stock(stock_symbol) ON DELETE CASCADE ON UPDATE CASCADE," +
					"	FOREIGN KEY (MarketID) REFERENCES Market_Account(AccountID)," +
					"	FOREIGN KEY (StockID) REFERENCES stock_account(AccountID)," +
					"	PRIMARY KEY(SellID)" +
					")",
			"CREATE TABLE IF NOT EXISTS Settings("+
					"	setting_id INTEGER," +
					"	Date DATE," +
					"	interest_rate REAL," +
					"	curr_mark_account_id INTEGER," +
					"	curr_stock_account_id INTEGER,"+
					"	curr_deposit_id INTEGER,"+
					"	curr_withdraw_id INTEGER, "+
					"	curr_buy_id INTEGER,"+
					"	curr_sell_id INTEGER,"+
					"	market_open BIT," +
					"	PRIMARY KEY(setting_id)"+
					")",
			"UPDATE Market_Account SET old_ADB = Balance WHERE old_ADB IS NULL"
	};
	
	//there is only one row in the settings table and it is identified by setting_id = 1
	//market open is 1 if open and 0 if closed
}
