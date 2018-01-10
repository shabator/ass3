package bgu.spl181.net.srv.bidi;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Movies {

    @SerializedName("movies")
    @Expose
    private List<Movie> movies = null;

    public Movies(List<Movie> movies) {
        super();
        this.movies = movies;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
