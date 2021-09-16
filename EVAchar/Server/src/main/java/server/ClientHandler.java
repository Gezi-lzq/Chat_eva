package server;

import database.DAO.MysqlDb.UserDaoSQLServerlmpl;
import database.info.RelationshipInfo;
import database.info.UserInfo;
import messages.MessageType;
import messages.User;
import server.thread.ServerRecvThread;
import server.thread.ServerSendThread;
import messages.Message;
import messages.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ChatService;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientHandler {

    private Boolean connected=false;
    private Boolean haveInit=false;
    private ConnectManager connectManager;
    private String ip;
    private ClientManager parent;

    private UserInfo user;

    private ServerSendThread serverSendThread;
    private ServerRecvThread serverRecvThread;
    private Thread SendThread;
    private Thread RecvThread;

    private ChatService chatService;

    private int socketReadTimeout=60;

    private ArrayList<User> friendList;
    private HashMap<String,RelationshipInfo> relationList =new HashMap<>();

    static Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    public ClientHandler(Socket socket, ClientManager parent) {
        this.parent = parent;
        user=new UserInfo();

        setConnection(socket);
    }

    /***
     * @Title:setConnection
     * @Description:对该socket进行一下初始化操作
     *        在ClientHandler对象内初始化一个connectManager对象
     *              启动serverSendThread启动
     *              启动serverRecvThread线程
     *        如果初始化时异常，则令connected为false
     *
     * @param socket
     * @return void
     * @Author: lzq
     */
    public void setConnection(Socket socket){
        try {
            connectManager=new ConnectManager(socket);
            ip=connectManager.getIp();
            logger.info("一客户端"+ip+"成功连接");

            serverSendThread=new ServerSendThread(connectManager);
            serverRecvThread=new ServerRecvThread(connectManager,user);

            SendThread=new Thread(serverSendThread);
            RecvThread=new Thread(serverRecvThread);

            SendThread.setName(ip+"ServerSendThread");
            RecvThread.setName(ip+"serverRecvThread");

            SendThread.start();
            RecvThread.start();

            connected=true;
        } catch (IOException e) {
            logger.info("出现连接异常");
            connectManager.close();
            e.printStackTrace();
        }
    }


    public void closeOnlineStatus(){
        if(haveInit) {
            //数据库更改在线状态
            UserDaoSQLServerlmpl userDaoSQLServerlmpl = new UserDaoSQLServerlmpl();
            user.setStatus(Status.OFFLINE);
            userDaoSQLServerlmpl.alterUser(user);

//        给在线列表发送下线通知
            Message message = new Message();
            message.setType(MessageType.ListUpdate);
            message.setUser(UserInfo.SetUserInformation(user));
            this.ListBroadcast(message);
        }
    }


    public void closeClientConnection(){
        connectManager.close();
    }



    /***
     * @Title:requestDisconnection
     * @Description:初步关闭连接 通过SendThread发送给客户端消息断开申请
     *                         逐步关闭服务器发送线程，客户端接收线程
     * @param flag   通知服务器是否是彻底断开连接
     * @return void
     * @Author: lzq
     */
    public void requestDisconnection(Boolean flag){
        logger.info(" 准备关闭 发送线程");
        serverSendThread.setStop(true);

        Message message=new Message();
        message.setType(MessageType.Disconnect);
        message.setFlag(flag);
        //发送消息，唤醒线程（可能处于阻塞），以便结束旧的发送线程
        //同时通知服务器关闭接收线程
        sendMessage(message);
    }

    public void closeRecvThread(){
        logger.info(ip+" 关闭 接收线程");
        serverRecvThread.setStop(true);
        RecvThread.interrupt();
    }

    public Boolean getConnected() {
        return connected;
    }
    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    public void ListBroadcast(Message message){
        for(User user:friendList){
            ClientHandler clientHandler=ClientManager.getParent().getClientHandle_id(user.getUserID());
            if(clientHandler!=null ? clientHandler.getConnected():false){
                logger.info("发送至"+user.getName()+"---由"+message.getUser().getName());
                clientHandler.sendMessage(message);
            }
        }
    }

    public void setRelationList(List<RelationshipInfo> relationList){
        for(RelationshipInfo value : relationList){
            if(value.getUserId().equals(user.getUserID())){
                this.relationList.put(value.getFriendId(), value);
            }else {
                this.relationList.put(value.getUserId(), value);
            }
        }
    }

    public HashMap<String, RelationshipInfo> getRelationList() {
        return relationList;
    }

    public String getID() {
        return user.getUserID();
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public String getIp() {
        return ip;
    }

    public ArrayList<User> getFriendList() {
        return friendList;
    }

    public void setFriendList(ArrayList<User> friendList) {
        this.friendList = friendList;
    }


    public void setSoTimeout() throws SocketException {
        connectManager.getClientSocket().setSoTimeout(socketReadTimeout*1000);
    }

    public void sendMessage(Message message){
        if(message!=null){
            message.setIp(ip);
            serverSendThread.addMsgToQueue(message);
        }
    }

    public Boolean getHaveInit() {
        return haveInit;
    }

    public void setHaveInit(Boolean haveInit) {
        this.haveInit = haveInit;
    }
}
