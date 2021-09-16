package messageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class StreamManager {

    private final static Logger logger = LoggerFactory.getLogger(StreamManager.class);
    private Socket socket;
    private MessageOutputStream msgOut;
    private MessageInputStream msgIn;

    public StreamManager(Socket socket) throws IOException {
        this.socket = socket;
        msgIn=new MessageInputStream(socket);
        msgOut=new MessageOutputStream(socket);
    }


    public MessageOutputStream getMessageOutputStream() {
        return msgOut;
    }
    public MessageInputStream getMessageInputStream() {
        return msgIn;
    }
    public void closeStream(){
        msgOut.close();
        msgIn.close();
    }


}
