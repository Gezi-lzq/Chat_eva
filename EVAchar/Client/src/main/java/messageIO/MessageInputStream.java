package messageIO;

import client.Client;
import com.alibaba.fastjson.JSON;
import messageIO.messageImp.MessageInput;
import messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class MessageInputStream implements MessageInput {
    private static final Logger logger = LoggerFactory.getLogger(MessageInputStream.class);
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
        byte type = dataInputStream.readByte();
        int len = dataInputStream.readInt();
        byte[] data = new byte[len];
        dataInputStream.readFully(data);
        logger.info("接受信息："+type+len);
        String str = new String(data);
        message=JSON.parseObject(str,Message.class);
        return message;

    }

    @Override
    public void close() {
        if(inputStream!=null||dataInputStream!=null){
            try {
                socket.shutdownInput();
                //在客户端或者服务端通过socket.shutdownInput()都是单向关闭的，即关闭客户端的输出流并不会关闭服务端的输出流，所以是一种单方向的关闭流；
            } catch (IOException e) {
                logger.info("dataInputStream关闭异常");
            }
        }
    }
}
