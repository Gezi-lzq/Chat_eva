package messageIO.messageImp;

import messages.Message;

import java.io.IOException;

public interface MessageInput{

    public Message readMessage()throws IOException;;

    public void close();

}
