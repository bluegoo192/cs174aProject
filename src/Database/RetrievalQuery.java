package Database;

import java.sql.*;

/**
 * Created by Arthur on 11/28/17.
 */
public abstract class RetrievalQuery extends DbQuery {

	protected String display_string;
	
    public RetrievalQuery(String query) {
        super(query);
    }

    public RetrievalQuery(PreparedStatement statement) {
        super(statement);
    }

    @Override
    public abstract void onComplete(ResultSet result);

    @Override
    public void onComplete(int numRowsAffected) {
        throw new IllegalArgumentException("How did this even happen?  int was returned from an selection query??");
    }

    @Override
    protected void executeStringQuery(Statement statement) {
        try {
            ResultSet result = statement.executeQuery(this.getQuery());
            this.onComplete(result);
        } catch (Exception e) {
            this.onError(e);
        }
    }

    @Override
    protected void executePreparedStatement() {
        try {
            onComplete(getQueryStatement().executeQuery());
        } catch (Exception e) {
            this.onError(e);
        }
    }
}
