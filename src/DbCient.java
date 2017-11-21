import java.sql.*;

/**
 * Created by Arthur on 11/12/17.
 */
public class DbCient {
    Connection conn;
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
    }

    /**
     * Connect to the database.
     * @return Whether or not the connection was successful
     */
    public boolean connect() {
        try {
            System.out.println("Trying to connect");
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to database");
            connection.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void test() {
        System.out.println("tes");
    }

}
