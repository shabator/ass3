package bgu.spl181.net.srv.bidi;

import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.BBSharedData;
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//import com.google.gson.stream.JsonReader;
//
//import java.io.FileNotFoundException;
//import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockBusterProtocol extends USTBP {

    //    private HashMap<String, AtomicBoolean> users;
    private int connID;
    private Connections connection;
    private BBSharedData sharedData;
    private boolean isLogin = false;
    private User thisUser;

    public BlockBusterProtocol(BBSharedData SharedData) {
        sharedData = SharedData;
        thisUser = null;
    }

    @Override
    public void start(int connectionId, Connections connections) {
        connID = connectionId;
        connection = connections;
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
            connection.send(connID, ans);
            return;

        } else if (!sharedData.getUsers().contains(userName)) {  // the user not exists
            ans = "Login failed";
            connection.send(connID, ans);
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
        }

        connection.send(connID, ans);

    }

    @Override
    public void signOut() {
        String ans;
        if(thisUser != null) {
            thisUser.setLoggedIn(false);
            isLogin = false;
            ans = "signout succeeded";
            connection.send(connID, ans);

        }
        ans = "signout failed";
        connection.send(connID, ans);

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
        connection.send(connID, ans);

    }

    public void balanceInfo() {
        int balance = thisUser.getBalance();
        String ans = "ACK balance" + balance;
        connection.send(connID, ans);

    }

    private void ack(String ans){
        connection.send(connID, "ACK " +ans);

    }

    private void error(String ans){
        connection.send(connID, "ERROR " +ans);

    }

    private void broadcast(String ans){
        connection.send(connID, "BROADCAST " +ans);

    }

}
