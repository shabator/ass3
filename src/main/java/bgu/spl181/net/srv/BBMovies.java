package bgu.spl181.net.srv;

import bgu.spl181.net.srv.bidi.Movie;
import bgu.spl181.net.srv.bidi.Movies;
import bgu.spl181.net.srv.bidi.User;
import bgu.spl181.net.srv.bidi.Users;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BBMovies extends BBUsers {


    protected ConcurrentHashMap<String, Movie> movies;
    private int currentMovieId = 0;
    private ReadWriteLock userLock = new ReentrantReadWriteLock();


    public BBMovies(ConcurrentHashMap<String, Movie> movies, ConcurrentHashMap<String, User> users) {
        super(users);
        this.movies = movies;
    }


    public ConcurrentHashMap getMovies() {
        return movies;
    }


    public void updateMoviesToJson() {
        Gson gson = new Gson();
        FileWriter fileWriter = null;
        JsonWriter jsonWriter = null;
        userLock.readLock().lock();
        try {
            fileWriter = new FileWriter("DataBase/Movies.json");
            jsonWriter = new JsonWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Movie> moviesList = new LinkedList<Movie>();
        for (Movie movie : movies.values())
            moviesList.add(movie);

        Movies newMovies = new Movies(moviesList);
        gson.toJson(newMovies, Movies.class, jsonWriter);
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            userLock.readLock().unlock();
        }
    }

    public void updateUsersToJson() {
        Gson gson = new Gson();
        FileWriter fileWriter = null;
        JsonWriter jsonWriter = null;
        userLock.readLock().lock();
        try {
            fileWriter = new FileWriter("DataBase/Users.json");
            jsonWriter = new JsonWriter(fileWriter);
        }
            catch (IOException e) {
                e.printStackTrace();
            }

        List<User> usersList = new LinkedList<User>();
        for (User user : users.values())
            usersList.add(user);

        Users newUsers = new Users(usersList);

        gson.toJson(newUsers, Users.class, jsonWriter);
        try {
            fileWriter.close();
        }
            catch (IOException e) {
                e.printStackTrace();
        }
            finally {
                userLock.readLock().unlock();
        }
    }
}
