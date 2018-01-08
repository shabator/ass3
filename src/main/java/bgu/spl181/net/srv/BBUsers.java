package bgu.spl181.net.srv;

import bgu.spl181.net.srv.bidi.User;

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
