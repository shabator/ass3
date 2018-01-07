package bgu.spl181.net.srv;

import bgu.spl181.net.srv.bidi.Movie;
import bgu.spl181.net.srv.bidi.User;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class BBSharedData {

    protected ConcurrentHashMap<String,Movie>  movies;
    protected ConcurrentHashMap<String,User>  users;

    public BBSharedData( ConcurrentHashMap<String,Movie> movies, ConcurrentHashMap<String,User> users){
        this.users = users;
        this.movies = movies;
    }
    public ConcurrentHashMap getUsers(){
        return users;
    }
    public ConcurrentHashMap getMovies(){
        return movies;
    }


}
