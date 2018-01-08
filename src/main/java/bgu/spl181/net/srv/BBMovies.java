package bgu.spl181.net.srv;

import bgu.spl181.net.srv.bidi.Movie;
import bgu.spl181.net.srv.bidi.User;

import java.util.concurrent.ConcurrentHashMap;

public class BBMovies extends BBUsers{


    protected ConcurrentHashMap<String, Movie> movies;
    private int currentMovieId = 0;

    public BBMovies(ConcurrentHashMap<String, Movie> movies , ConcurrentHashMap<String, User> users) {
        super(users);
        this.movies = movies;

    }

    public ConcurrentHashMap getMovies() {
        return movies;
    }
}
