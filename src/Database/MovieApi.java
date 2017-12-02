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

//    public CompletableFuture<ResultSet> getTopMovies() {
//        CompletableFuture<ResultSet> promise = new CompletableFuture<>();
//        String query = "SELECT * FROM Movies WHERE id = " + id;
//        DbClient.getInstance().runQuery(new RetrievalQuery(query) {
//            @Override
//            public void onComplete(ResultSet result) {
//                promise.complete(result);
//            }
//        }.setType(1));
//        return promise;
//    }
}
