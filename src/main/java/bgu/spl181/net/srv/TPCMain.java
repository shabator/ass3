package bgu.spl181.net.srv;


import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.srv.bidi.*;
import bgu.spl181.net.srv.bidi.Movies;
import bgu.spl181.net.srv.bidi.Users;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;


public class TPCMain {

    public static void main(String args[]) {
        int port = Integer.parseInt(args[0]);
        JsonReader reader = null;
        JsonReader reader2 = null;
        Server<String> tpcServer;
        try {
            reader = new JsonReader(new FileReader("DataBase/Movies.json"));
            reader2 = new JsonReader(new FileReader("DataBase/Users.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(reader, JsonObject.class);
        JsonObject obj2 = gson.fromJson(reader2, JsonObject.class);

        Movies movies = gson.fromJson(obj, Movies.class);
        Users users = gson.fromJson(obj2, Users.class);
//
//        Movie[] movies = gson.fromJson(obj.get("movies"), Movie[].class);
//        User[] users = gson.fromJson(obj2.get("users"), User[].class);
        ConcurrentHashMap<String, User> hashUsers = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Movie> hashMovies = new ConcurrentHashMap<>();

//
//        for (int i=0; i<movies.; i++) {
//            hashMovies.put(movies[i].getName(), movies[i]);
//        }
//

        for (User user : users.getUsers()) {
            hashUsers.put(user.getUsername(), user);
        }

        for(Movie movie : movies.getMovies())
            hashMovies.put(movie.getName(),movie);

        BBMovies sharedData = new BBMovies(hashMovies, hashUsers);

        tpcServer = Server.threadPerClient(port,
                ()->{return new BlockBusterProtocol(sharedData);},
                ()->{return new MessageEncoderDecoderImpl();});
        tpcServer.serve();


    }
}