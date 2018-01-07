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
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockBusterProtocol extends USTBP {

    private HashMap<String, AtomicBoolean> users;
    private int connID;
    private Connections conn;
    private BBSharedData sharedData;
    private boolean isLogin=false;
    public BlockBusterProtocol(BBSharedData SharedData)
    {
        sharedData=SharedData;
    }




//    public static void main(String args[]) {
//        JsonReader reader = null;
//        JsonReader reader2 = null;
//
//        try {
//            reader = new JsonReader(new FileReader(args[0]));
//            reader2 = new JsonReader(new FileReader(args[1]));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        Gson gson = new Gson();
//        JsonObject obj = gson.fromJson(reader, JsonObject.class);
//        JsonObject obj2 = gson.fromJson(reader2, JsonObject.class);
//
//        Movie[] movies = gson.fromJson(obj, Movie[].class);
//        User[] users = gson.fromJson(obj2, User[].class);
//
//    }

    @Override
    public void start(int connectionId, Connections connections) {
        connID = connectionId;
        conn = connections;
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
        if(isLogin)
        ans = "Login failed";
    else if(!sharedData.getUsers().contains(userName))
        ans = "Login failed";

    User user = (User)sharedData.getUsers().get(userName);

    else if(user.isLoggedIn())
            ans = "Login failed";
    else if(!user.getPassword().equals(password))
            ans = "Login failed";
    else {
            user.setLoggedIn(true);
            ans = "Login succeed";
            isLogin=true;
    }

    }

    @Override
    public void signOut() {

    }


    @Override
    public void register(String userName, String password, ArrayList<String> dataBlock) {
        String ans;
        if(userName == null | password == null)
            ans = "registration failed";
        else if(sharedData.getUsers().contains(userName))    // username already registered
              ans = "registration failed";
        else if(isLogin)//already logged in
            ans = "registration failed";
        else if(dataBlock)// dataBlock is illegal
    }

    public void request() {

    }
}
