package handler;

import messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.ClientHandler;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class StreamHandler {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private OutputStream os;
    private InputStream is;
    static Logger logger = LoggerFactory.getLogger(StreamHandler.class);
    private InetAddress inetAddress;

    public StreamHandler(Socket socket) {
        this.socket = socket;

        InitStreamHandler();
    }
    private void InitStreamHandler(){
        inetAddress=socket.getInetAddress();
        try {
            is = socket.getInputStream();
            output=new ObjectOutputStream(os);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("获取输入流出现错误");
        }
        try {
            os=socket.getOutputStream();
            input = new ObjectInputStream(is);
        }catch (IOException e) {
            e.printStackTrace();
            logger.error("获取输出流出现错误");
        }
    }

    public Message accept_msg() {
        Message inputmsg = null;
        try {
            if(!socket.isClosed()||(is.read()!=-1)){
                inputmsg = (Message) input.readObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("输入流出现错误");
        } catch (ClassNotFoundException e) {
            logger.error("接受到非法信息");
        }


        return inputmsg;
    }

    public void send_msg(Message message){
        try {
            System.out.println("准备发送"+message.getType()+"消息给"+getip()+Thread.currentThread());

            output.writeObject(message);
            output.flush();
            logger.info("成功发送"+message.getType()+"消息");
        }catch (SocketException socketException) {
            logger.error("Socket Exception" );
        } catch (Exception e) {
            logger.error("Exception in run() method");
        }
    }

    public Boolean ClientIsConnected(){


        return false;
    }

    public String getip(){
        return inetAddress.getHostAddress();
    }


    //关闭连接，回收资源
    public void closeConnections() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (is != null){
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (os != null){
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (input != null){
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


