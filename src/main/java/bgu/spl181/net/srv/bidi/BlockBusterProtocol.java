package bgu.spl181.net.srv.bidi;

import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.api.bidi.ConnectionsImpl;
import bgu.spl181.net.srv.BBMovies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//import com.google.gson.stream.JsonReader;
//
//import java.io.FileNotFoundException;
//import java.io.FileReader;

public class BlockBusterProtocol extends USTBP {

    //    private HashMap<String, AtomicBoolean> users;
    private int connID;
    private ConnectionsImpl connections;
    private BBMovies sharedData;
    private boolean isLogin = false;
    private User thisUser;

    public BlockBusterProtocol(BBMovies sharedData) {
        this.sharedData = sharedData;
        thisUser = null;
    }

    @Override
    public void start(int connectionId, Connections newConnections) {
        connID = connectionId;
        connections = (ConnectionsImpl) newConnections;
    }

    @Override
    public void process(Object message) {

    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }

    @Override
    public void logIn(String userName, String password) {
        String ans;
        if (isLogin) {   // client already logged in
            ans = "Login failed";
            connections.send(connID, ans);
            return;

        } else if (!sharedData.getUsers().contains(userName)) {  // the user not exists
            ans = "Login failed";
            connections.send(connID, ans);
            return;
        }

        thisUser = (User) sharedData.getUsers().get(userName);

        if (thisUser.isLoggedIn())
            ans = "Login failed";
        else if (!thisUser.getPassword().equals(password))
            ans = "Login failed";
        else {
            thisUser.setLoggedIn(true);
            ans = "Login succeeded";
            isLogin = true;
            thisUser.setID(connID);
        }

        connections.send(connID, ans);

    }

    @Override
    public void signOut() {
        String ans;
        if (thisUser != null) {
            thisUser.setLoggedIn(false);
            isLogin = false;
            ans = "signout succeeded";
            thisUser.setID(-1);
            connections.send(connID, ans);
        }
        ans = "signout failed";
        connections.send(connID, ans);

    }


    @Override
    public void register(String userName, String password, String country) {
        String ans;
        if (userName == null | password == null || country == null)
            ans = "registration failed";
        else if (sharedData.getUsers().contains(userName))    // username already registered
            ans = "registration failed";
        else if (isLogin)//already logged in
            ans = "registration failed";
        else if(!country.substring(0,6).equals("country"))
            ans = "registration failed";
        else {
            ans = "registration succeeded";
            String ActualCountry = country.substring(8);
            sharedData.getUsers().put(userName, new User(userName, "Normal", password, ActualCountry, new ArrayList<>(), 0));
        }
        connections.send(connID, ans);

    }

    public void balanceInfo() {
        if (!thisUser.isLoggedIn()) {
            String ans = "request balance failed";
            error(ans);
        }
        int balance = Integer.parseInt(thisUser.getBalance());
        String ans = "balance " + balance;
        ack(ans);
    }

    public void balanceAdd(int amount) {
        if (!thisUser.isLoggedIn()) {
            String ans = "request balance failed";
            error(ans);
        }
        thisUser.setBalance(thisUser.getBalance() + amount);
        String ans = "balance " + thisUser.getBalance() + " added " + amount;
        ack(ans);
    }

    public void info(String movieName) {
        String ans = "";
        if (movieName == null) {
            for (String movie : (Set<String>) sharedData.getMovies().keySet()) {
                ans = ans + " " + '"' + movie + '"';
            }
            ack("info" + ans);
        } else {
            Movie movie = (Movie) sharedData.getMovies().get(movieName);
            if (movie == null) {
                error("Movie does not exists");
                return;
            }
            ack("info " + movie.getName() + " " + movie.getAvailableAmount() + " " + movie.getPrice() + " " + movie.getBannedCountries().toString()); // look if u can to string it!!!
        }
    }

    public void rentMovie(String movieName) {
        String ans = "";
        Movie movie = (Movie) sharedData.getMovies().get(movieName);
        if (movie == null) {
            error("Movie does not exists");
            return;
        }
        for (MovieUser current : thisUser.getMovies()) {
            if (current.getName() == movieName) {
                error("The user is already renting the movie");
                return;
            }
        }

        if (Integer.parseInt(movie.getAvailableAmount()) < 1) {
            error("There are no more copies of the movie that are available for rental");
            return;
        }

        if (Integer.parseInt(thisUser.getBalance()) < Integer.parseInt(movie.getPrice())) {
            error("The user does not have enough money in their balance");
            return;
        }

        if (movie.getBannedCountries().contains(thisUser.getCountry())) {
            error("The movie is banned in the user’s country");
            return;
        }

        thisUser.getMovies().add(new MovieUser(movie.getId(), movie.getName()));

        Integer newBalance = Integer.parseInt(thisUser.getBalance()) - Integer.parseInt(movie.getPrice());
        thisUser.setBalance(newBalance.toString());

        Integer newAmount= Integer.parseInt(movie.getAvailableAmount()) -1 ;
        movie.setAvailableAmount(newAmount.toString());

        ack("rent " +  '"' + movieName + '"' + " success" );
        broadcast("movie " +  '"' + movieName + '"' + " " + movie.getAvailableAmount() + " " + movie.getPrice());
    }

    public void returnMovie(String movieName) {
        String ans = "";
        Movie movie = (Movie) sharedData.getMovies().get(movieName);
        if (movie == null) {
            error("Movie does not exists");
            return;
        }
        for (MovieUser current : thisUser.getMovies()) {
            if (current.getName() == movieName) {
                thisUser.getMovies().remove(current);
                Integer newAmount= Integer.parseInt(movie.getAvailableAmount()) + 1 ;
                movie.setAvailableAmount(newAmount.toString());
                ack("return " +  '"' + movieName + '"' + " success" );
                broadcast("movie " +  '"' + movieName + '"' + " " + movie.getAvailableAmount() + " " + movie.getPrice());
                return;
            }
        }
        error("The user is currently not renting the movie");

    }

    public void addMovie(String movieName, String amount, String price, String blockData)
    {
        if(!thisUser.getType().equals("admin"))
        {
            error("User is not an administrator");
            return;
        }
        if(sharedData.getMovies().contains(movieName))
        {
            error("Movie name already exists in the system" );
            return;
        }
        if(Integer.parseInt(amount)<=0 || Integer.parseInt(price)<=0)
        {
            error("Price or Amount are smaller than or equal to 0");
            return;
        }
        Integer id = sharedData.getMovies().size()+1;
        String [] Countries= blockData.split(" ") ;
        ArrayList<String> bannedCountries = new ArrayList<String>();
//        for()
//
//
//        Movie movie = new Movie(id.toString(), movieName, price, bannedCountries, amount, amount);

//        sharedData.getMovies()
    }

        private void ack(String ans) {
        connections.send(connID, "ACK " + ans);

    }

    private void error(String ans) {
        connections.send(connID, "ERROR " + ans);

    }

    private void broadcast(String ans) {
        Collection<User> users = sharedData.getUsers().values();
        for (User user : users) {
            if (user.isLoggedIn() && user.getID() != -1)
                connections.send(user.getID(), "BROADCAST " + ans);
        }
    }


    }
