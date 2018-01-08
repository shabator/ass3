package bgu.spl181.net.srv.bidi;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;

public abstract class USTBP implements BidiMessagingProtocol {

    public abstract void logIn(String userName, String password);

    public abstract void signOut();

    public abstract void register(String userName, String password, String dataBlock) ;

}
