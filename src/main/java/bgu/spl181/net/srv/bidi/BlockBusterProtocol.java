package bgu.spl181.net.srv.bidi;

import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.api.bidi.ConnectionsImpl;
import bgu.spl181.net.srv.BBMovies;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static sun.management.Agent.error;

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
    private ReadWriteLock movieLock = new ReentrantReadWriteLock();
    private boolean shouldTerminate = false;

    public BlockBusterProtocol(BBMovies sharedData) {
        this.sharedData = sharedData;
        thisUser = null;
    }

    @Override
    public void start(int connectionId, Connections newConnections) {
        connID = connectionId;
        connections = (ConnectionsImpl) newConnections;
        System.out.println("start finished");
    }

    public String MovieName(String[] movies) {
        String movieName = "";
        for (int i = 2; i < movies.length; i++) {
            movieName = movieName + movies[i] + " ";
        }
        return movieName;
    }

    public String CountryList(String[] countries, int index) {
        String countryName = "";
        for (int i = index; i < countries.length; i++)
            countryName = countryName + countries[i] + " ";

        return countryName;
    }

    @Override
    public void process(Object message) {
        String mes = (String) message;

        String[] msg = mes.split(" ");

        if (msg[0].equals("LOGIN"))
            logIn(msg[1], msg[2]);
        if (msg[0].equals("REGISTER")) {
//            if (msg.length  <4)
//                register(msg[1], msg[2], null);
//            else
            register(msg[1], msg[2], msg[3]);
        }
        if (msg[0].equals("SIGNOUT")) {
            signOut();
        }
        if (msg[0].equals("REQUEST")) {
            if (msg[1].equals("balance")) {
                if (msg[2].equals("info"))
                    balanceInfo();
                if (msg[2].equals("add"))
                    balanceAdd(msg[3]);
            }
            if (msg[1].equals("info")) {
                if (msg.length < 3)
                    info(null);
                else {
                    String movieName = "";
                    for (int i = 2; i < msg.length; i++) {
                        movieName = movieName + msg[i] + " ";
                    }
                    info(movieName);
                }
            }
            if (msg[1].equals("rent")) {
                String movieName = MovieName(msg);
                rentMovie(movieName);
            }
            if (msg[1].equals("return")) {
                String movieName = MovieName(msg);
                returnMovie(movieName);
            }

            if (msg[1].equals("addmovie")) {
                boolean stop = false;
                String movieName = "";
                int i = 1;
                while (!stop) {
                    i++;
                    movieName = movieName + msg[i] + " ";
                    char[] word = msg[i].toCharArray();
                    if (word[word.length - 1] == '"')
                        stop = true;
                }
                String country = CountryList(msg, i + 3);

                addMovie(movieName, msg[i + 1], msg[i + 2], country);
            }
            if (msg[1].equals("remmovie")) {
                String movieName = MovieName(msg);
                remmovie(movieName);
            }
            if (msg[1].equals("changeprice")) {
                if (msg.length == 4) {
                    changeprice(msg[2] + " ", msg[3]);
                    return;
                }
                String movieName = "";
                for (int i = 2; i < msg.length - 1; i++) {
                    movieName = movieName + msg[i] + " ";
                }
                changeprice(movieName, msg[msg.length - 1]);
            }
        }

    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    @Override
    public void logIn(String userName, String password) {
        if (isLogin) {   // client already logged in
            error("Login failed1");
            return;
        }
        if (!sharedData.getUsers().containsKey(userName)) {  // the user not exists
            error("Login failed2");
            return;
        }
//        boolean currentUser = (boolean)sharedData.getLoggedIN().get(connID);
        if (sharedData.getLoggedIN().get(connID)!=null) {
            error("Login failed3");
            return;
        }
        thisUser = (User)sharedData.getUsers().get(userName);
        if (!thisUser.getPassword().equals(password)) {
            error("Login failed4");
            return;
        }

        sharedData.addLoggedIN(connID, true);
        isLogin = true;
        ack("Login succeeded");

    }


    @Override
    public void signOut() {
        if (isLogin) {
            sharedData.removeloggedIN(connID);
            isLogin = false;
            ack("signout succeeded");
            shouldTerminate = true;
            connections.disconnect(connID);

            return;
        }
        error("signout failed");
    }


    @Override
    public void register(String userName, String password, String country) {
        if (userName == null | password == null || country == null) {
            error("registration failed");
            return;
        }
        if (sharedData.getUsers().contains(userName)) {    // username already registered
            error("registration failed2");
            return;
        }
        if (isLogin) {//already logged in
            error("registration failed3");
            return;
        }
        if (!country.substring(0, 7).equals("country")) {
            error("registration failed4");
            return;
        }

        String ActualCountry = country.substring(9, country.length() - 1);
        sharedData.getUsers().put(userName, new User(userName, "Normal", password, ActualCountry, new ArrayList<>(), "0"));
        ack("registration succeeded");
        sharedData.updateUsersToJson();
    }

    public void balanceInfo() {
        if (!isLogin) {
            String ans = "request balance failed";
            error(ans);
        }
        int balance = Integer.parseInt(thisUser.getBalance());
        String ans = "balance " + balance;
        ack(ans);
    }

    public void balanceAdd(String amount) {
        if (!isLogin) {
            String ans = "request balance failed";
            error(ans);
        }
        Integer sum = Integer.parseInt(thisUser.getBalance()) + Integer.parseInt(amount);
        thisUser.setBalance(sum.toString());
        String ans = "balance " + thisUser.getBalance() + " added " + amount;
        ack(ans);
        sharedData.updateUsersToJson();

    }

    public void info(String movieName) {
        String ans = "";
        if (movieName == null) {
            for (String movie : (Set<String>) sharedData.getMovies().keySet()) {
                ans = ans + " " + '"' + movie + '"';
            }
            ack("info" + ans);
        } else {
            movieName = movieName.substring(1, movieName.length() - 2);
            Movie movie = (Movie) sharedData.getMovies().get(movieName);

            if (movie == null) {
                error("Movie does not exists");
                return;
            }

            String[] countries = movie.getBannedCountries();
            String res = "";
            for (int i = 0; i < countries.length; i++)
                res = res + '"' + countries[i] + '"' + " ";
            ack("info " + movie.getName() + " " + movie.getAvailableAmount() + " " + movie.getPrice() + " " + res);
        }
    }

    public void rentMovie(String movieName) {
        movieName = movieName.substring(1, movieName.length() - 2);
        movieLock.readLock().lock();
        Movie movie = (Movie) sharedData.getMovies().get(movieName);
        movieLock.readLock().unlock();
        if (movie == null) {
            error("Movie does not exists");
            return;
        }
        for (MovieUser current : thisUser.getMovies()) {
            if (current.getName().equals(movieName)) {
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
        String[] Countries = movie.getBannedCountries();
        for (int i = 0; i < Countries.length; i++) {
            if (Countries[i].equals(thisUser.getCountry())) {
                error("The movie is banned in the user’s country");
                return;
            }
        }

        thisUser.getMovies().add(new MovieUser(movie.getId(), movie.getName()));

        Integer newBalance = Integer.parseInt(thisUser.getBalance()) - Integer.parseInt(movie.getPrice());
        thisUser.setBalance(newBalance.toString());

        Integer newAmount = Integer.parseInt(movie.getAvailableAmount()) - 1;

        movieLock.writeLock().lock();
        movie.setAvailableAmount(newAmount.toString());
        movieLock.writeLock().unlock();

        ack("rent " + '"' + movieName + '"' + " success");
        broadcast("movie " + '"' + movieName + '"' + " " + movie.getAvailableAmount() + " " + movie.getPrice());
        sharedData.updateUsersToJson();
        sharedData.updateMoviesToJson();

    }

    public void returnMovie(String movieName) {
        movieName = movieName.substring(1, movieName.length() - 2);
        movieLock.readLock().lock();
        Movie movie = (Movie) sharedData.getMovies().get(movieName);
        movieLock.readLock().unlock();
        if (movie == null) {
            error("Movie does not exists");
            return;
        }
        for (MovieUser current : thisUser.getMovies()) {
            if (current.getName().equals(movieName)) {
                thisUser.getMovies().remove(current);
                Integer newAmount = Integer.parseInt(movie.getAvailableAmount()) + 1;

                movieLock.writeLock().lock();
                movie.setAvailableAmount(newAmount.toString());
                movieLock.writeLock().unlock();

                ack("return " + '"' + movieName + '"' + " success");
                broadcast("movie " + '"' + movieName + '"' + " " + movie.getAvailableAmount() + " " + movie.getPrice());
                sharedData.updateUsersToJson();
                sharedData.updateMoviesToJson();
                return;
            }
        }
        error("The user is currently not renting the movie");

    }

    public void addMovie(String movieName, String amount, String price, String blockData) {
        movieName = movieName.substring(1, movieName.length() - 2);
        if (!thisUser.getType().equals("admin")) {
            error("User is not an administrator");
            return;
        }
        if (sharedData.getMovies().containsKey(movieName)) {
            error("Movie name already exists in the system");
            return;
        }
        if (Integer.parseInt(amount) <= 0 || Integer.parseInt(price) <= 0) {
            error("Price or Amount are smaller than or equal to 0");
            return;
        }
        Integer id = sharedData.getMovies().size() + 1;
        Movie movie = null;
        if(blockData.length()>1) {
            blockData = blockData.substring(0, blockData.length() - 1);
            LinkedList<String> countries = new LinkedList<>();
            char[] country = blockData.toCharArray();
            int i = 0;
            String countryName = "";
            while (i < country.length - 1) {
                while (country[i] == '"' || country[i] == ' ') {
                    if (i == country.length - 1) {
                        break;
                    } else
                        i++;
                }
                while (country[i] != '"') {
                    countryName = countryName + country[i];
                    i++;
                }
                if (countryName != "") {
                    countries.add(countryName);
                    countryName = "";
                }
            }
            String[] newCountry = new String[countries.size()];
            for (int j = 0; j < newCountry.length; j++) {
                newCountry[j] = countries.remove();
            }
            movie = new Movie(id.toString(), movieName, price, newCountry, amount, amount);
        }
        else
            movie = new Movie(id.toString(), movieName, price, new String[0], amount, amount);

        movieLock.writeLock().lock();
        sharedData.getMovies().put(movie.getName(), movie);
        movieLock.writeLock().unlock();

        ack("addmovie " + '"' + movieName + '"' + " success");
        broadcast("movie " + '"' + movieName + '"' + " " + movie.getAvailableAmount() + " " + movie.getPrice());
        sharedData.updateMoviesToJson();
    }


    public void remmovie(String movieName) {
        movieName = movieName.substring(1, movieName.length() - 2);
        if (!thisUser.getType().equals("admin")) {
            error("User is not an administrator");
            return;
        }
        movieLock.readLock().lock();

        if (!sharedData.getMovies().containsKey(movieName)) {
            error("Movie does not exist in the system");
            return;
        }
        movieLock.readLock().unlock();

        Collection<User> users = sharedData.getUsers().values();
        for (User user : users)
            for (MovieUser movie : user.getMovies())
                if (movie.getName().equals(movieName)) {
                    error("A copy of the movie is currently rented by a user");
                    return;
                }
        movieLock.writeLock().lock();
        sharedData.getMovies().remove(movieName);
        movieLock.writeLock().unlock();

        ack("remmovie " + '"' + movieName + '"' + " success");
        broadcast("movie " + '"' + movieName + '"' + " removed");
        sharedData.updateMoviesToJson();

    }

    public void changeprice(String movieName, String price) {
        movieName = movieName.substring(1, movieName.length() - 2);
        if (!thisUser.getType().equals("admin")) {
            error("User is not an administrator");
            return;
        }
        movieLock.readLock().lock();
        if (!sharedData.getMovies().containsKey(movieName)) {
            error("Movie does not exist in the system");
            return;
        }
        movieLock.readLock().unlock();

        if (Integer.parseInt(price) <= 0) {
            error("Price is smaller than or equal to 0");
            return;
        }

        movieLock.writeLock().lock();
        Movie movie = (Movie) sharedData.getMovies().get(movieName);
        movie.setPrice(price);
        movieLock.writeLock().unlock();

        ack("changeprice " + '"' + movieName + '"' + " success");
        broadcast("movie " + '"' + movieName + '"' + " " + movie.getAvailableAmount() + " " + movie.getPrice());
        sharedData.updateMoviesToJson();
    }

    private void ack(String ans) {
        connections.send(connID, "ACK " + ans);

    }

    private void error(String ans) {
        connections.send(connID, "ERROR " + ans);

    }

    private void broadcast(String ans) {
        Collection<Integer> usersLogedIn = sharedData.getLoggedIN().keySet();
        for (Integer conId : usersLogedIn) {
                connections.send(conId, "BROADCAST " + ans);
        }
    }


}
