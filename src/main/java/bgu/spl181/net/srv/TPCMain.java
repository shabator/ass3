package bgu.spl181.net.srv;


import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.srv.bidi.BlockBusterProtocol;
import bgu.spl181.net.srv.bidi.Movie;
import bgu.spl181.net.srv.bidi.User;
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
        Server<String > tpcServer;
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

        BBMovies sharedData = new BBMovies(hashMovies, hashUsers);
//        BlockBusterProtocol BBprotocol = new BlockBusterProtocol(sharedData);



        BlockBusterProtocol BB = new BidiMessagingProtocol;
        Supplier<BlockBusterProtocol> s = new Supplier<BlockBusterProtocol>() {
            @Override
            public BlockBusterProtocol get() {
                return new BlockBusterProtocol(sharedData);
            }
        };


        Supplier<MessageEncoderDecoderImpl> s2 = new Supplier<MessageEncoderDecoderImpl>() {
            @Override
            public MessageEncoderDecoderImpl get() {
                return new MessageEncoderDecoderImpl();
            }
        };

        tpcServer = Server.threadPerClient(port, s, s2);




//                ()-> { return new BlockBusterProtocol(sharedData);},
//                ()-> {return new MessageEncoderDecoderImpl();}
//        );
//        tpcServer.serve();

    }

}