package messageIO.messageImp;

import messages.Message;

import java.io.IOException;

public interface MessageOutput {

    public void writeMessage(Message message)
    throws IOException;

    public void close();

}
