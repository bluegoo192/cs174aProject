package Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Arthur on 11/28/17.
 */
public class UpdateQuery extends DbQuery {
    public UpdateQuery(String query) {
        super(query);
    }

    @Override
    public void onComplete(ResultSet result) {
        throw new IllegalArgumentException("How did this even happen?  ResultSet was returned from an update query??");
    }

    @Override
    public void onComplete(int numRowsAffected) {
        // optional: add logging info
    }

    @Override
    protected void executeStringQuery(Statement statement) {
        try {
            int result = statement.executeUpdate(this.getQuery());
            onComplete(result);
        } catch (Exception e) {
            this.onError(e);
        }
    }

    @Override
    protected void executePreparedStatement() {
        try {
            onComplete(getQueryStatement().executeUpdate());
        } catch (Exception e) {
            this.onError(e);
        }
    }
}
