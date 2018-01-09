package bgu.spl181.net.srv;


import bgu.spl181.net.srv.bidi.Movie;
import bgu.spl181.net.srv.bidi.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;


public class ReactorMain {
    public static void main(String args[]) {
        JsonReader reader = null;
        JsonReader reader2 = null;
//        BBUsers sharedData = new BBUsers();

        try {
            reader = new JsonReader(new FileReader(args[0]));
            reader2 = new JsonReader(new FileReader(args[1]));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(reader, JsonObject.class);
        JsonObject obj2 = gson.fromJson(reader2, JsonObject.class);

        Movie[] movies = gson.fromJson(obj, Movie[].class);
        User[] users = gson.fromJson(obj2, User[].class);

    }

}
