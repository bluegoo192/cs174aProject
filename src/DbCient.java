import java.sql.*;

/**
 * Created by Arthur on 11/12/17.
 */
public class DbCient {
    Connection conn;
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://cs174a.engr.ucsb.edu";

    //  Database credentials
    static final String USER = "silverstein";
    static final String PASS = "954";
    private static DbCient ourInstance = new DbCient();

    public static DbCient getInstance() {
        return ourInstance;
    }

    private DbCient() {
        Statement stmt = null;
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            System.out.println("Connected");

            //stmt.close();
            conn.close();
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

    public void test() {
        System.out.println("tes");
    }

}
