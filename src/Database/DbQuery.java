package Database;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Arthur on 11/27/17.
 */
public abstract class DbQuery {
    private String query;

    public DbQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return this.query;
    }

    public abstract void onComplete(ResultSet result);

    public abstract void onComplete(int numRowsAffected);

    public abstract void execute(Statement statement);

    /**
     *
     * @param e Error object
     * @return Whether or not to retry
     */
    public boolean onError(Exception e) {
        System.out.println("Failed to execute:  "+query);
        e.printStackTrace();
        return true;
    }
}