import java.sql.*;

/**
 * Created by Arthur on 11/12/17.
 */
public class DbCient {
    Connection conn;
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/silversteinDB";

    //  Database credentials
    static final String USER = "silverstein";
    static final String PASS = "954";
    private static DbCient ourInstance = new DbCient();

    public static DbCient getInstance() {
        return ourInstance;
    }

    private DbCient() {
    }

    public boolean connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to database");
            connection.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void test() {
        System.out.println("tes");
    }

}
