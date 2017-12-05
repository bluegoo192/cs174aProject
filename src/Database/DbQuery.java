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
    int type = 0; // 0 = main database, 1 = movie database

    public DbQuery(String query) {
        this.query = query;
    }

    public DbQuery(PreparedStatement statement) {
        if (statement == null) query = ""; // robustness over correctness, at least for now
        this.queryStatement = statement;
    }

    public String getQuery() {
        if (this.queryStatement != null) {
            return this.queryStatement.toString();
        }
        return query;
    }

    public DbQuery setType(int type) {  // returns this for convenience
        this.type = type;
        return this;
    }

    public PreparedStatement getQueryStatement() {
        return this.queryStatement;
    }
    public abstract void onComplete(ResultSet result);

    public void setQueryStatement(PreparedStatement statement) {
        queryStatement = statement;
    }

    public abstract void onComplete(int numRowsAffected);

    public final void execute(Statement[] statements) {
        if (queryStatement == null) {
            executeStringQuery(statements[type]);
        } else {
            executePreparedStatement();
        }
        try {
            if (queryStatement != null) queryStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void execute() {
        executePreparedStatement();
        try {
            if (queryStatement != null) queryStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void executeStringQuery(Statement statement);

    protected abstract void executePreparedStatement();

    /**
     *
     * @param e Error object
     * @return Whether or not to retry
     */
    public boolean onError(Exception e) {
        System.out.println("Failed to execute:  "+getQuery());
        e.printStackTrace();
        return true;
    }
}