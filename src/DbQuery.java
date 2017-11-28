import java.sql.ResultSet;

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

    /**
     *
     * @param e Error object
     * @return Whether or not to retry
     */
    public boolean onError(Exception e) {
        e.printStackTrace();
        return true;
    }
}