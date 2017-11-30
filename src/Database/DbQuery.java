package Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Arthur on 11/27/17.
 */
public abstract class DbQuery {
    private String query;
    private PreparedStatement queryStatement = null;

    public DbQuery(String query) {
        this.query = query;
    }

    public DbQuery(PreparedStatement statement) {
        if (statement == null) query = ""; // robustness over correctness, at least for now
        this.queryStatement = statement;
    }

    public String getQuery() {
        return this.query;
    }

    public PreparedStatement getQueryStatement() {
        return this.queryStatement;
    }
    public abstract void onComplete(ResultSet result);

    public abstract void onComplete(int numRowsAffected);

    public final void execute(Statement statement) {
        if (queryStatement == null) {
            executeStringQuery(statement);
        } else {
            executePreparedStatement();
        }
    }

    public final void execute() {
        executePreparedStatement();
    }

    protected abstract void executeStringQuery(Statement statement);

    protected abstract void executePreparedStatement();

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