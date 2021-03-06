package bgu.spl181.net.srv;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.ConnectionsImpl;
import bgu.spl181.net.srv.bidi.BlockBusterProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

public abstract class BaseServer<T> implements Server<T> {

    protected final int port;
    protected final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    protected final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    protected ServerSocket sock;
    private ConnectionsImpl connections = new ConnectionsImpl();
    private int current=0;

    public BaseServer(
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {

        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {
			System.out.println("Server started");

            this.sock = serverSock; //just to be able to close

            while (!Thread.currentThread().isInterrupted()) {

                Socket clientSock = serverSock.accept();
                BidiMessagingProtocol<T> protocol = protocolFactory.get();

                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<>(
                        clientSock,
                        encdecFactory.get(),
                        protocol);


                current++;
                connections.add(current, handler);
                protocol.start(current,connections);
                execute(handler);
//                System.out.println(connections.get().size());

//                BidiMessagingProtocol BMP = protocolFactory.get();
//                BMP.start(current,connections);

//                BlockBusterProtocol BBP = (BlockBusterProtocol)protocolFactory.get();

            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }

    protected abstract void execute(BlockingConnectionHandler<T> handler);

}
