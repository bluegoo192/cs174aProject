package Database;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Arthur on 12/2/17.
 */
public class MovieApi {

    /**
     * Get a movie's id, title, rating, and production_year
     * @param name Name of the movie
     * @return Future ResultSet
     */
    public CompletableFuture<ResultSet> getMovieInfo(String name) {
        CompletableFuture<ResultSet> promise = new CompletableFuture<>();
        String query = "SELECT * FROM Movies WHERE title = " + name;
        DbClient.getInstance().runQuery(new RetrievalQuery(query) {
            @Override
            public void onComplete(ResultSet result) {
                promise.complete(result);
            }
        }.setType(1));
        return promise;
    }

    /**
     * Get a movie's id, title, rating, and production_year
     * @param id ID of the movie
     * @return Future ResultSet
     */
    public CompletableFuture<ResultSet> getMovieInfo(int id) {
        CompletableFuture<ResultSet> promise = new CompletableFuture<>();
        String query = "SELECT * FROM Movies WHERE id = " + id;
        DbClient.getInstance().runQuery(new RetrievalQuery(query) {
            @Override
            public void onComplete(ResultSet result) {
                promise.complete(result);
            }
        }.setType(1));
        return promise;
    }

    /**
     * Get titles of movies wwith a rating of 5
     * @return Future ResultSet
     */
    public CompletableFuture<ResultSet> getTopMovies() {
        CompletableFuture<ResultSet> promise = new CompletableFuture<>();
        String query = "SELECT title FROM Movies WHERE rating = 5";
        DbClient.getInstance().runQuery(new RetrievalQuery(query) {
            @Override
            public void onComplete(ResultSet result) {
                promise.complete(result);
            }
        }.setType(1));
        return promise;
    }

    /**
     * Get reviews of a given movie
     * @param movieId ID of the movie
     * @return Future ResultSet
     */
    public CompletableFuture<ResultSet> getReviews(int movieId) {
        CompletableFuture<ResultSet> promise = new CompletableFuture<>();
        String query = "SELECT * FROM Reviews WHERE movie_id = "+movieId;
        DbClient.getInstance().runQuery(new RetrievalQuery(query) {
            @Override
            public void onComplete(ResultSet result) {
                promise.complete(result);
            }
        }.setType(1));
        return promise;
    }
}
