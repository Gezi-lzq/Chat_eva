package messageIO;

import com.alibaba.fastjson.JSON;
import messageIO.messageImp.MessageOutput;
import messages.Message;
import messages.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class MessageOutputStream implements MessageOutput {
    private static final Logger logger = LoggerFactory.getLogger(MessageOutputStream.class);
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private OutputStream outputStream;
    private static final byte messageType = 1 ;// 0 - 心跳检测； 1 内容传输
    private static final byte heartBeatType = 0 ;

    public MessageOutputStream(Socket socket) throws IOException {
        this.socket = socket;
        outputStream = socket.getOutputStream();
        dataOutputStream=new DataOutputStream(outputStream);
    }

    @Override
    public void writeMessage(Message message) throws IOException {
        byte[] data= JSON.toJSONString(message).getBytes();
        int len = data.length;
        dataOutputStream.writeByte(messageType);
        dataOutputStream.writeInt(len);
        dataOutputStream.write(data);
        dataOutputStream.flush();
    }

    public void writeHeartBeat()throws IOException{
        byte[] data ="HeartBeat".getBytes();
        int len=data.length;
        dataOutputStream.writeByte(heartBeatType);
        dataOutputStream.writeInt(len);
        dataOutputStream.write(data);
        dataOutputStream.flush();
    }
    @Override
    public void close() {
        try {
            socket.shutdownOutput();
        } catch (IOException e) {
            logger.info("socket出现异常");
                e.printStackTrace();
        }
    }
}
