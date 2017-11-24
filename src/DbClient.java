import java.sql.*;

/**
 * Created by Arthur on 11/12/17.
 */
public class DbClient {

    // Connection status
    Connection connection;
    boolean connected = false;

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/silversteinDB?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    //  Database credentials
    static final String USER = "silverstein";
    static final String PASS = "954";
    private static DbCient ourInstance = new DbCient();

    public static DbCient getInstance() {
        return ourInstance;
    }

    private DbCient() {
        // Connect asynchronously
        new Thread(new AutoConnector()).start();
    }

    /**
     * Connect to the database.
     * @return Whether or not the connection was successful
     */
    private boolean connect() {
        try {
            System.out.println("Trying to connect");
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to database");
            connection.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Try to connect over and over until we are successful
    class AutoConnector implements Runnable {
        @Override
        public void run() {
            while (!connected) {
                connected = connect();
            }
        }
    }
}
