package bgu.spl181.net.srv.bidi;

import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.api.bidi.ConnectionsImpl;
import bgu.spl181.net.srv.BBSharedData;
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//import com.google.gson.stream.JsonReader;
//
//import java.io.FileNotFoundException;
//import java.io.FileReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockBusterProtocol extends USTBP {

    //    private HashMap<String, AtomicBoolean> users;
    private int connID;
    private ConnectionsImpl connections;
    private BBSharedData sharedData;
    private boolean isLogin = false;
    private User thisUser;

    public BlockBusterProtocol(BBSharedData SharedData) {
        sharedData = SharedData;
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
        if(thisUser != null) {
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
        else
        {
            ans = "registration succeeded";
            sharedData.getUsers().put(userName, new User(userName, "Normal", password, country, new ArrayList<>(), 0));
        }
        connections.send(connID, ans);

    }

    public void balanceInfo() {
        if(!thisUser.isLoggedIn()){
            String ans = "request balance failed";
            error(ans);}
        int balance = thisUser.getBalance();
        String ans = "balance " + balance;
        ack(ans);
        }

     public void balanceAdd(int amount){
         if(!thisUser.isLoggedIn()){
             String ans = "request balance failed";
             error(ans);}
        thisUser.setBalance(thisUser.getBalance() + amount);
        String ans = "balance " + thisUser.getBalance() + " added " +amount;
        ack(ans);
     }
     public void info(String movieName)
     {
         String ans = "";
         if(movieName==null) {
             for(String movie : (Set<String>)sharedData.getMovies().keySet())
             {
                 ans = ans + " " +'"' + movie + '"';
             }
             ack("info"+ans);
         }
         else
         {
            Movie movie = (Movie)sharedData.getMovies().get(movieName);
            if(movie == null) {
                error("Movie does not exists");
                return;
            }
            ack("info " + movie.getName() + " " + movie.getAvailableAmount() + " " + movie.getPrice() + " " + movie.getBannedCountries().toString()); // look if u can to string it!!!
         }
     }

    private void ack(String ans){
        connections.send(connID, "ACK " +ans);

    }

    private void error(String ans){
        connections.send(connID, "ERROR " +ans);

    }

    private void broadcast(String ans){
        Collection<User> users= sharedData.getUsers().values();
        for(User user : users)
        {
        if(user.isLoggedIn() && user.getID()!= -1)
            connections.send(user.getID(), "BROADCAST "+ans);
        }
    }

}
