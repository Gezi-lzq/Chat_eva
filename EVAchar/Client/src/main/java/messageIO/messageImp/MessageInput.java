package messageIO.messageImp;

import messages.Message;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;

public interface MessageInput{

    public Message readMessage()throws IOException;;

    public void close();

}
