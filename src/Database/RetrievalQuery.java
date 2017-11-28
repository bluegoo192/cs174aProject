package Database;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Arthur on 11/28/17.
 */
public abstract class RetrievalQuery extends DbQuery {

    public RetrievalQuery(String query) {
        super(query);
    }

    @Override
    public abstract void onComplete(ResultSet result);

    @Override
    public void onComplete(int numRowsAffected) {
        throw new IllegalArgumentException("How did this even happen?  int was returned from an selection query??");
    }

    @Override
    public void execute(Statement statement) {
        try {
            ResultSet result = statement.executeQuery(this.getQuery());
            this.onComplete(result);
        } catch (Exception e) {
            this.onError(e);
        }

    }
}
