package bgu.spl181.net.srv;

import bgu.spl181.net.srv.bidi.User;
import bgu.spl181.net.srv.bidi.Users;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class BBUsers {


    protected ConcurrentHashMap<String, User> users;

    public BBUsers(ConcurrentHashMap<String, User> users) {
        this.users = users;

    }

    public ConcurrentHashMap getUsers() {
        return users;
    }




}
