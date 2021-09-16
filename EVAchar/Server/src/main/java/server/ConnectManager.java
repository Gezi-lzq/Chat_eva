package server;

import messageIO.StreamManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectManager {
    private final static Logger logger = LoggerFactory.getLogger(ConnectManager.class);

    private Socket clientSocket;

    private StreamManager streamManager;
    private InetAddress inetAddress;

    private int socketReadTimeout;

    public ConnectManager(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        inetAddress=clientSocket.getInetAddress();
        //初始化一个StreamManager对象
        streamManager=new StreamManager(clientSocket);
    }

    public String getIp(){
        return inetAddress.getHostAddress();
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public StreamManager getStreamManager(){
        return streamManager;
    }

    public boolean isClosed() {
        return clientSocket.isClosed();
    }

    public void close() {
        if (clientSocket != null ) {
            try {
                clientSocket.close();
                logger.info("关闭socket连接");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
