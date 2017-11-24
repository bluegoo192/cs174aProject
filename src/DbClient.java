import java.sql.*;

import com.mysql.jdbc.CommunicationsException;
import com.mysql.*;

/**
 * Created by Arthur on 11/12/17.
 */


public class DbClient {

    // Connection status
    Connection conn;
    boolean connected = false;

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://cs174a.engr.ucsb.edu:3066/mschmitDB";

    //  Database credentials
    static final String USER = "mschmit";
    static final String PASS = "798";
    
    private static DbClient ourInstance = new DbClient();

    public static DbClient getInstance() {
        return ourInstance;
    }

    private DbClient() {
        Statement stmt = null;
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected");
            
            createTables(conn);
            

            //stmt.close();
            conn.close();
        }catch(CommunicationsException ce) {
        		System.out.println("Communications Exception");
    			ce.printStackTrace();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
    }

    public void createTables(Connection conn) {
    		String customers = "CREATE TABLE IF NOT EXISTS Customers (" + 
    				"	Username CHAR(20)," + 
    				"	State CHAR(2)," + 
    				"	Email CHAR(254) UNIQUE," + 
    				"	TaxID CHAR(9) UNIQUE," + 
    				"	Phone CHAR(15)," + 
    				"	Password CHAR(20)," + 
    				"	PRIMARY KEY (Username))";
    		
    		
    		Statement make_customers = null;
    		try {
    			make_customers = conn.createStatement();
    			make_customers.executeUpdate(customers);
    		}catch(SQLException se) {
    			System.out.println("Customers Table not created");
    		}finally {
    			try {
    				if(make_customers != null)
    					make_customers.close();
    			}catch(SQLException se2) {
    				se2.printStackTrace();
    			}
    		}
    		
    		String market_account = "CREATE TABLE IF NOT EXISTS Market_Account (" + 
    				"	AccountID CHAR(20)," + 
    				"	Balance REAL CHECK (Balance >= 0)," + 
    				"	Username CHAR(20) NOT NULL,\n" + 
    				"	FOREIGN KEY(username) REFERENCES Customers(username)" + 
    				"ON DELETE CASCADE ON UPDATE CASCADE," + 
    				"	PRIMARY KEY (AccountID) )";
    		
    		Statement make_market_account = null;
    		try {
    			make_market_account = conn.createStatement();
    			make_market_account.executeUpdate(market_account);
    		}catch(SQLException se) {
    			System.out.println("market account Table not created");
    		}finally {
    			try {
    				if(make_market_account != null)
    					make_market_account.close();
    			}catch(SQLException se2) {
    				se2.printStackTrace();
    			}
    		}
    		
    		String stock_account = "CREATE TABLE IF NOT EXISTS stock_account (" + 
    				"	AccountID CHAR(20)," + 
    				"	StockBalance REAL (StockBalance >= 0),," + 
    				"	Username CHAR(20) NOT NULL," + 
    				"	FOREIGN KEY(username) REFERENCES Customers(username)" + 
    				"		ON DELETE CASCADE ON UPDATE CASCADE," + 
    				"	PRIMARY KEY (AccountID))\n" ;
    		Statement make_stock_account = null;
    		try {
    			make_stock_account = conn.createStatement();
    			make_stock_account.executeUpdate(stock_account);
    		}catch(SQLException se) {
    			System.out.println("Stock account Table not created");
    		}finally {
    			try {
    				if(make_stock_account != null)
    					make_stock_account.close();
    			}catch(SQLException se2) {
    				se2.printStackTrace();
    			}
    		}
    		
    		String deposit = "CREATE TABLE Deposit(" + 
    				"	DepositID CHAR(20)," + 
    				"	AccountID CHAR(20) NOT NULL," + 
    				"	Username CHAR(20) NOT NULL," + 
    				"	Value REAL," + 
    				"	Date DATE" + 
    				"	FOREIGN KEY (username) REFERENCES Customers(username) ON DELETE CASCADE ON UPDATE CASCADE," + 
    				"	FOREIGN KEY (accountID) REFERENCES Market_Account(AccountID) ON DELETE CASCADE ON UPDATE CASCADE," + 
    				"	PRIMARY KEY(DepositID)" + 
    				")";
    		Statement make_deposit = null;
    		try {
    			make_deposit = conn.createStatement();
    			make_deposit.executeUpdate(deposit);
    		}catch(SQLException se) {
    			System.out.println("Deposit Table not created");
    		}finally {
    			try {
    				if(make_deposit != null)
    					make_deposit.close();
    			}catch(SQLException se2) {
    				se2.printStackTrace();
    			}
    		}
    		
    		String withdraw = "CREATE TABLE Withdraw(" + 
    				"	WithdrawID CHAR(20)," + 
    				"	AccountID CHAR(20) NOT NULL," + 
    				"	Username CHAR(20) NOT NULL," + 
    				"	Value REAL," + 
    				"	Date DATE," + 
    				"FOREIGN KEY (username) REFERENCES Customers(username) ON DELETE CASCADE ON UPDATE CASCADE," + 
    				"	FOREIGN KEY (accountID) REFERENCES Market_Account(AccountID) ON DELETE CASCADE ON UPDATE CASCADE," + 
    				"	PRIMARY KEY(WithdrawID)" + 
    				")";
    		Statement make_withdraw = null;
    		try {
    			make_withdraw = conn.createStatement();
    			make_withdraw.executeUpdate(withdraw);
    		}catch(SQLException se) {
    			System.out.println("Withdraw Table not created");
    		}finally {
    			try {
    				if(make_withdraw != null)
    					make_withdraw.close();
    			}catch(SQLException se2) {
    				se2.printStackTrace();
    			}
    		}
    		
    		String actor_stock = "CREATE TABLE IF NOT EXISTS Actor_Stock (" + 
    				"	Name CHAR(20)," + 
    				"	Birth DATE," + 
    				"	stock_symbol CHAR(3)," + 
    				"	current_stock_price REAL," + 
    				"	closing_prices_log CLOB(10M)," + 
    				"	PRIMARY KEY (StockSymbol))";
    		Statement make_actor_stock = null;
    		try {
    			make_actor_stock = conn.createStatement();
    			make_actor_stock.executeUpdate(actor_stock);
    		}catch(SQLException se) {
    			System.out.println("ActorStock Table not created");
    		}finally {
    			try {
    				if(make_actor_stock != null)
    					make_actor_stock.close();
    			}catch(SQLException se2) {
    				se2.printStackTrace();
    			}
    		}
    		
    		String manager = "CREATE TABLE IF NOT EXISTS Manager (" + 
    				"	ManagerID CHAR(20)," + 
    				"	Password CHAR(20) NOT NULL," + 
    				"	PRIMARY KEY (ManagerID))";
    		Statement make_manager = null;
    		try {
    			make_manager = conn.createStatement();
    			make_manager.executeUpdate(manager);
    		}catch(SQLException se) {
    			System.out.println("Manager Table not created");
    		}finally {
    			try {
    				if(make_manager != null)
    					make_manager.close();
    			}catch(SQLException se2) {
    				se2.printStackTrace();
    			}
    		}
    		
    		String accrue_interest = "CREATE TABLE IF NOT EXISTS Accrue_Interest(" + 
    				"	AccountID CHAR(20)," + 
    				"	Month Month(DATE)," + 
    				"	MoneyAdded REAL," + 
    				"	FOREIGN KEY (AccountID) REFERENCES Market_Account(AccountID) ON DELETE CASCADE ON UPDATE CASCADE," + 
    				"	PRIMARY KEY(AccountID, Month)" + 
    				")";
    		Statement make_accrue_interest = null;
    		try {
    			make_accrue_interest = conn.createStatement();
    			make_accrue_interest.executeUpdate(accrue_interest);
    		}catch(SQLException se) {
    			System.out.println("AccrueInterest Table not created");
    		}finally {
    			try {
    				if(make_accrue_interest != null)
    					make_accrue_interest.close();
    			}catch(SQLException se2) {
    				se2.printStackTrace();
    			}
    		}
    		
    		String dter = "CREATE TABLE IF NOT EXISTS Dter (" + 
    				"	ReportID CHAR(20)," + 
    				"	ManagerID CHAR(20)," + 
    				"	FOREIGN KEY ManagerID REFERENCES Manager ON DELETE SET NULL," + 
    				"	PRIMARY KEY (ReportID))";
    		Statement make_dter = null;
    		try {
    			make_dter = conn.createStatement();
    			make_dter.executeUpdate(dter);
    		}catch(SQLException se) {
    			System.out.println("DTER Table not created");
    		}finally {
    			try {
    				if(make_dter != null)
    					make_dter.close();
    			}catch(SQLException se2) {
    				se2.printStackTrace();
    			}
    		}
    		
    		String report = "CREATE TABLE IF NOT EXISTS Report (" + 
    				"	ReportID CHAR(20)," + 
    				"	ManagerID CHAR(20)," + 
    				"	CustomerID CHAR(20)," + 
    				"	Date DATE," + 
    				"	Text CLOB(5M)," + 
    				"	Type String," + 
    				"	FOREIGN KEY(managerID) REFERENCES Manager(ManagerID) ON DELETE SET " + 
    				"NULL," + 
    				"	PRIMARY KEY (ReportID))";
    		Statement make_report = null;
    		try {
    			make_report = conn.createStatement();
    			make_report.executeUpdate(report);
    		}catch(SQLException se) {
    			System.out.println("Report Table not created");
    		}finally {
    			try {
    				if(make_report != null)
    					make_report.close();
    			}catch(SQLException se2) {
    				se2.printStackTrace();
    			}
    		}
    		
    		String movie = "CREATE TABLE IF NOT EXISTS Movie (" + 
    				"	MovieID CHAR(20)," + 
    				"	Title CHAR(20)," + 
    				"	Year CHAR(4)," + 
    				"	PRIMARY KEY (MovieID)" + 
    				")";
    		Statement make_movie = null;
    		try {
    			make_movie = conn.createStatement();
    			make_movie.executeUpdate(movie);
    		}catch(SQLException se) {
    			System.out.println("Movie Table not created");
    		}finally {
    			try {
    				if(make_movie != null)
    					make_movie.close();
    			}catch(SQLException se2) {
    				se2.printStackTrace();
    			}
    		}
    		
    		String movie_contract = "CREATE TABLE IF NOT EXISTS MovieContract(" + 
    				"	StockSymbol CHAR(3) NOT NULL," + 
    				"	MovieID CHAR(20) NOT NULL," + 
    				"	Role STRING," + 
    				"	Total_Value REAL," + 
    				"	FOREIGN KEY(StockSymbol) REFERENCES Actor_Stock(StockSymbol) ON DELETE SET NULL ON UPDATE CASCADE," + 
    				"	FOREIGN KEY(MovieID) REFERENCES Movie(MovieID) ON DELETE CASCADE ON UPDATE CASCADE," + 
    				"	PRIMARY KEY(StockSymbol, MovieID)" + 
    				")";
    		Statement make_movie_contract = null;
    		try {
    			make_movie_contract = conn.createStatement();
    			make_movie_contract.executeUpdate(movie_contract);
    		}catch(SQLException se) {
    			System.out.println("MovieContract Table not created");
    		}finally {
    			try {
    				if(make_movie_contract != null)
    					make_movie_contract.close();
    			}catch(SQLException se2) {
    				se2.printStackTrace();
    			}
    		}
    		
    		String buy_stock = "CREATE TABLE IF NOT EXISTS Buy_Stock(" + 
    				"	BuyID CHAR(20)," + 
    				"	NumShares INT," + 
    				"	StockSymbol CHAR(3) NOT NULL," + 
    				"	MarketID CHAR(20) NOT NULL," + 
    				"	StockID CHAR(20) NOT NULL," + 
    				"	Date DATE," + 
    				"	Commission REAL," + 
    				"	FOREIGN KEY StockSymbol REFERENCES Actor_Stock(StockSymbol)  ON DELETE CASCADE ON UPDATE CASCADE," + 
    				"	FOREIGN KEY MarketID REFERENCES Market_Account(AccountID) ON DELETE SET NULL, " + 
    				"	FOREIGN KEY StockID REFERENCES Stock_Account(AccountID) ON DELETE SET NULL," + 
    				"	PRIMARY KEY(BuyID)" + 
    				")";
    		Statement make_buy_stock = null;
    		try {
    			make_buy_stock = conn.createStatement();
    			make_buy_stock.executeUpdate(buy_stock);
    		}catch(SQLException se) {
    			System.out.println("BuyStock Table not created");
    		}finally {
    			try {
    				if(make_buy_stock != null)
    					make_buy_stock.close();
    			}catch(SQLException se2) {
    				se2.printStackTrace();
    			}
    		}
    		
    		String sell_stock  = "CREATE TABLE IF NOT EXISTS Sell_Stock(" + 
    				"	SellID CHAR(20)," + 
    				"	NumShares INT," + 
    				"	StockSymbol CHAR(3) NOT NULL," + 
    				"	MarketID CHAR(20) NOT NULL," + 
    				"	StockID CHAR(20) NOT NULL," + 
    				"	Date DATE," + 
    				"	OriginalBuyingPrice REAL," + 
    				"	FOREIGN KEY StockSymbol REFERENCES Actor_Stock(StockSymbol) ON DELETE CASCADE ON UPDATE CASCADE," + 
    				"	FOREIGN KEY MarketID REFERENCES Market_Account(AccountID) ON DELETE SET NULL," + 
    				"	FOREIGN KEY StockID REFERENCES Stock_Account(AccountID) ON DELETE SET NULL," + 
    				"	PRIMARY KEY(SellID)" + 
    				")";
    		Statement make_sell_stock = null;
    		try {
    			make_sell_stock = conn.createStatement();
    			make_sell_stock.executeUpdate(sell_stock);
    		}catch(SQLException se) {
    			System.out.println("SellStock Table not created");
    		}finally {
    			try {
    				if(make_sell_stock != null)
    					make_sell_stock.close();
    			}catch(SQLException se2) {
    				se2.printStackTrace();
    			}
    		}
    		
    }
    
    public void test() {
        System.out.println("tes");
    }

}
