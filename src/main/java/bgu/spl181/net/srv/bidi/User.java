
package bgu.spl181.net.srv.bidi;


import java.util.List;

import bgu.spl181.net.srv.bidi.Movie;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    private int id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("movies")
    @Expose
    private List<Movie> movies = null;
    @SerializedName("balance")
    @Expose
    private int balance;
    private boolean isLoggedIn= false;

    /**
     * No args constructor for use in serialization
     * 
     */
    public User() {
    }

    /**
     * 
     * @param balance
     * @param username
     * @param movies
     * @param type
     * @param password
     * @param country
     */
    public User(String username, String type, String password, String country, List<Movie> movies, int balance) {
        super();
        this.username = username;
        this.type = type;
        this.password = password;
        this.country = country;
        this.movies = movies;
        this.balance = balance;
        id = -1;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.isLoggedIn = loggedIn;
    }

    public void setID(int ID) {
        this.id = ID;
    }

    public int getID() { return id; }


}
