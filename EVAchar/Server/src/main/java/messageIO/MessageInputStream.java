package messageIO;

import com.alibaba.fastjson.JSON;
import messageIO.messageImp.MessageInput;
import messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.thread.ServerRecvThread;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class MessageInputStream implements MessageInput {
    private final static Logger log = LoggerFactory.getLogger(MessageInputStream.class);
    private Socket socket;
    private DataInputStream dataInputStream;
    private InputStream inputStream;
    private static final byte messageType = 1 ;// 0 - 心跳检测； 1 内容传输
    private static final byte heartBeatType = 0 ;


    public MessageInputStream(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = socket.getInputStream();
        dataInputStream = new DataInputStream(inputStream);
    }

    @Override
    public Message readMessage() throws IOException {
        Message message=null;
        byte[] data ;
        byte type = dataInputStream.readByte();
        int len = dataInputStream.readInt();
        data = new byte[len];
        dataInputStream.readFully(data);
        String str = new String(data);
        if(type==messageType) {
            message = JSON.parseObject(str, Message.class);
        }
        return message;
    }

    @Override
    public void close() {
//        if(inputStream!=null||dataInputStream!=null){
//            try {
//                socket.shutdownInput();
//            } catch (IOException e) {
//                log.info("消息接收 被强制关闭");
////                e.printStackTrace();
//            }
//        }
    }
}
