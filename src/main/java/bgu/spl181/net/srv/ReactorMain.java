package bgu.spl181.net.srv;


import bgu.spl181.net.srv.bidi.BlockBusterProtocol;
import bgu.spl181.net.srv.bidi.Movie;
import bgu.spl181.net.srv.bidi.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;


public class ReactorMain {
    public static void main(String args[]) {
        int port = Integer.parseInt(args[0]);
        JsonReader reader = null;
        JsonReader reader2 = null;
        Server<String> reactor;
        try {
            reader = new JsonReader(new FileReader("DataBase/Movies.json"));
            reader2 = new JsonReader(new FileReader("DataBase/Users.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(reader, JsonObject.class);
        JsonObject obj2 = gson.fromJson(reader2, JsonObject.class);

        Movie[] movies = gson.fromJson(obj.get("movies"), Movie[].class);
        User[] users = gson.fromJson(obj2.get("users"), User[].class);
        ConcurrentHashMap<String, User> hashUsers = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Movie> hashMovies = new ConcurrentHashMap<>();


        for (int i = 0; i < movies.length; i++) {
            hashMovies.put(movies[i].getName(), movies[i]);
        }

        for (User user : users) {
            hashUsers.put(user.getUsername(), user);
        }

        BBMovies sharedData = new BBMovies(hashMovies, hashUsers);
        reactor = Server.reactor(
                Runtime.getRuntime().availableProcessors(),
                port,
                ()->{return new BlockBusterProtocol(sharedData);},
                ()->{return new MessageEncoderDecoderImpl();});
        reactor.serve();

    }
}