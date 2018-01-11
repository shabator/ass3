
package bgu.spl181.net.srv.bidi;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {


    private String username;

    private String type;

    private String password;

    private String country;

    private List<MovieUser> movies = null;

    private String balance;


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
    public User(String username, String type, String password, String country, List<MovieUser> movies, String balance) {
        super();
        this.username = username;
        this.type = type;
        this.password = password;
        this.country = country;
        this.movies = movies;
        this.balance = balance;

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

    public List<MovieUser> getMovies() { return movies; }

    public void setMovies(List<MovieUser> movies) {
        this.movies = movies;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }


}
