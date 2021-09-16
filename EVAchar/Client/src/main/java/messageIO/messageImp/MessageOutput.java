package messageIO.messageImp;

import messages.Message;

import java.io.IOException;
import java.io.ObjectOutput;
import java.net.Socket;

public interface MessageOutput {

    public void writeMessage(Message message)
    throws IOException;

    public void close();

}
