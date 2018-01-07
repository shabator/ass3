package bgu.spl181.net.srv;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl181.net.srv.bidi.BlockBusterProtocol;
import bgu.spl181.net.srv.bidi.Movie;
import bgu.spl181.net.srv.bidi.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;


public class TPCMain {

    public static void main(String args[]) {
        JsonReader reader = null;
        JsonReader reader2 = null;
//        TPCserver tpcServer = new TPCserver();

        try {
            reader = new JsonReader(new FileReader(args[0]));
            reader2 = new JsonReader(new FileReader(args[1]));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(reader, JsonObject.class);
        JsonObject obj2 = gson.fromJson(reader2, JsonObject.class);

        Movie[] movies = gson.fromJson(obj.get("users"), Movie[].class);
        User[] users = gson.fromJson(obj2.get("movies"), User[].class);
        ConcurrentHashMap<String, User> hashUsers = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Movie> hashMovies = new ConcurrentHashMap<>();

        for(Movie movie : movies) {
            hashMovies.put(movie.getId(),movie);
        }

        for(User user : users) {
            hashUsers.put(user.getUsername(),user);
        }

        BBSharedData sharedData = new BBSharedData(hashMovies, hashUsers);
        BlockBusterProtocol BBprotocol = new BlockBusterProtocol(sharedData);

    }

}