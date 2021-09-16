package client;

import controller.LoginController;
import messageIO.StreamManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectManager {
    private final static Logger logger = LoggerFactory.getLogger(ConnectManager.class);

    private Socket socket;

    private String HOST;
    private int PORT;

    private StreamManager streamManager;
    private InetAddress inetAddress;


    public ConnectManager(String host, int PORT) throws IOException {
        this.HOST = host;
        this.PORT = PORT;
        this.connectServer();
        logger.info("已正常连接服务器");
        streamManager=new StreamManager(socket);
    }

    public void connectServer() throws IOException {
        socket = new Socket(HOST,PORT);
        inetAddress=socket.getInetAddress();
    }

    public String getIp(){
        return inetAddress.getHostAddress();
    }

    public StreamManager getStreamManager(){
        return streamManager;
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public void Allclose() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Boolean checkConnection (){
        if(socket==null){
            logger.info("连接异常，将重新连接服务器....");
            return false;
        }else {
            try {
                socket.sendUrgentData(0xFF);
                logger.info("检测完毕，心跳包发送正常，正常连接服务器");
                return true;
            } catch (IOException e) {
                logger.info("心跳包发送失败");
                return false;
            }
        }
    }

}
