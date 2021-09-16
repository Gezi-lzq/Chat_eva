package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static config.Config.chatPORT;
import static config.Config.loginPORT;

public class ClientManager {

    private ServerSocket serverSocket_login;
    private ServerSocket serverSocket_chat;
    private static ClientManager parent;

    public List<ClientHandler> clientList=new ArrayList<ClientHandler>();
    private ClientHandler client;
    private static ExecutorService newCachedThreadPool;
    static Logger logger = LoggerFactory.getLogger(ClientManager.class);

    public static ClientManager getParent() {
        return parent;
    }

    public void init(){
        parent=this;
        newCachedThreadPool = Executors.newCachedThreadPool();
        loginInit();
        ChatInit();
    }

    public void loginInit(){
        Thread server = new Thread() {
            @Override
            public void run() {
                try {
                    serverSocket_login = new ServerSocket(loginPORT);
                    logger.info("开始监听" + loginPORT + "端口，等待客户端连接...");
                } catch (IOException e) {
                    System.out.println("监听本地端口" + loginPORT + "时出错");
                }
                if(serverSocket_login!=null)
                    while (true) {
                        try {
                            logger.info("等待客户端连接...");
                            Socket socket = serverSocket_login.accept();

                            System.out.println(socket.getInetAddress().getHostAddress()+"    客户端连接loginPort...");

                            ClientHandler clientHandler=new ClientHandler(socket, parent);
                            //若clientHandler初始化正常，则加入到clientList
                            if(clientHandler.getConnected())
                                clientList.add(clientHandler);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            }
        };
        server.setName("登录端口监听线程");
        server.start();
    }

    public void ChatInit(){
        Thread chatServer = new Thread() {
            @Override
            public void run() {
                try {
                    serverSocket_chat = new ServerSocket(chatPORT);
                    logger.info("开始监听" + chatPORT + "端口，等待客户端连接...");
                } catch (IOException e) {
                    System.out.println("监听本地端口" + chatPORT + "时出错");
                }
                if(serverSocket_chat!=null)
                    while (true) {
                        try {
                            Socket socket = null;
                            logger.info("等待客户端连接...");
                            socket = serverSocket_chat.accept();
                            logger.info(socket.getInetAddress().getHostAddress()+"客户端连接...将进行身份验证查询");

                            ClientHandler ch = getClientHandle_ip(socket.getInetAddress().getHostAddress());
                            if (ch != null) {
                                ch.setConnection(socket);
                                ch.setSoTimeout();
                                logger.info(socket.getInetAddress().getHostAddress() + "客户端成功连接聊天端口");
                                ch.setConnected(true);
                            }else {
                                logger.info("未找到");
                            }

                        } catch (Exception e) {
                            logger.info("客户端连接异常");
                            e.printStackTrace();
                        }
                    }
                }
            };
        chatServer.setName("聊天端口监听线程");
        chatServer.start();
    }

    public ClientHandler getClientHandle_id(String ID){
        if(ID!=null) {
            for (int i = clientList.size() - 1; i >= 0; i--) {
                client = clientList.get(i);
                if (client.getID().equals(ID)) {
                    return client;
                }
            }
        }
        return null;
    }
    
    public ClientHandler getClientHandle_ip(String Ip){
        if(Ip!=null) {
            for (int i = clientList.size() - 1; i >= 0; i--) {
                client = clientList.get(i);
                if (client.getIp().equals(Ip)&&!client.getConnected()) {
                    return client;
                }
            }
        }
        return null;
    }

    public void removeClient(String ID){
        ClientHandler clientHandler=getClientHandle_id(ID);
        /*
        通知它的好友更新列表
        */
        clientList.remove(clientHandler);
    }

}
