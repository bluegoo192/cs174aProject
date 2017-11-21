import java.sql.ResultSet;

/**
 * Created by Arthur on 11/21/17.
 */
public abstract class DbQuery {
    private String query;

    public DbQuery(String query) {
        this.query = query;
    }

    public abstract void onComplete(ResultSet result);
}
