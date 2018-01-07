package bgu.spl181.net.srv;

import bgu.spl181.net.api.bidi.ConnectionsImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

public class TPCserver extends BaseServer {
    private ConnectionsImpl connections = new ConnectionsImpl();
    private int current=0;


    public TPCserver(int port, Supplier protocolFactory, Supplier encdecFactory) {
        super(port, protocolFactory, encdecFactory);
    }

    @Override
    protected void execute(BidiBlockingConnectionHandler handler) {
        new Thread(handler).start();
    }
    @Override
    public void serve()
    {
        try (ServerSocket serverSock = new ServerSocket(port)) {
            System.out.println("Server started");

            this.sock = serverSock; //just to be able to close

            while (!Thread.currentThread().isInterrupted()) {

                Socket clientSock = serverSock.accept();

                BidiBlockingConnectionHandler<T> handler = new BidiBlockingConnectionHandler<>(
                        clientSock,
                        encdecFactory.get(),
                        protocolFactory.get());

                execute(handler);
            connections.add(current, handler);
            current++;
            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");
    }

}
