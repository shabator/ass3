package bgu.spl181.net.api.bidi;

import bgu.spl181.net.srv.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T>{

    private ConcurrentHashMap<Integer,ConnectionHandler> connections = new ConcurrentHashMap();

    public boolean send(int connectionId, T msg){
        if(!connections.contains(connectionId))
            return false;
        connections.get(connectionId).send(msg);
            return true;
    }

    public void broadcast(T msg){
        for( int i : connections.keySet())
            connections.get(i).send(msg);
    }

    public void disconnect(int connectionId){
        connections.remove(connectionId);
    }
    public void add(Integer ID, ConnectionHandler handler)
    {
        connections.put(ID, handler);
    }
    public ConcurrentHashMap<Integer, ConnectionHandler> get(){
        return connections;
    }
}
