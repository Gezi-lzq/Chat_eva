package client;

import controller.ChatController;
import controller.LoginController;
import messages.Message;
import messages.MessageType;
import messages.User;
import messages.parser.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Base64Util;

import java.io.IOException;
import java.util.*;

import static config.Config.*;
import static config.Config.filePath;

public class Client {
    private static final Logger  logger = LoggerFactory.getLogger(Client.class);
    //单例
    private static final Client instance=new Client();
    private Client (){}
    public static Client getInstance(){
        return instance;
    }

    private int port;
    private ConnectManager connectManager;

    private final Object lockObject = new Object(); //锁对象，用于线程通讯，唤醒重试线程
    private final static int THREAD_SLEEP_MILLS = 30000;
    private int socketHeartIntervalTime = 30;

    private ClientSendThread clientSendThread =null;
    private ClientRecvThread clientRecvThread =null;

    private static Set<MessageParser> parsers = new HashSet<>();

    //客户端信息
    private User user;
    private HashMap<String,User> friendList;
    private ArrayList<User> userArrayList;

    static {
        parsers.add(ChatMsgParser.getParser());
        parsers.add(LoginMsgParser.getParser());
        parsers.add(RegisterMsgParser.getParser());
        parsers.add(FriendApplicationMsgParser.getParser());
        parsers.add(ListUpdateMsgParser.getParser());
        parsers.add(OnlineStatusMsgParser.getParser());
    }

    /***
     * @Title:StartLoginConnect
     * @Description:初始连接服务器
     * @param
     * @return void
     * @Author: lzq
     */
    public void StartLoginConnect(){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                port=loginPORT;
                try {
                    initConnect(port);
                } catch (IOException e) {
                    logger.info("StartLoginConnect()异常");
                }
            }
        });
        thread.setName("登录线程");
        thread.start();
    }
    /***
     * @Title:initConnect
     * @Description:启动消息发送与监听线程
     * @param port
     * @return void
     * @Author: lzq
     */
    public void initConnect(int port) throws IOException {
        try {
            logger.info("连接服务器...");
            connectManager =new ConnectManager(HOST,port);
        } catch (IOException e) {
            LoginController.getInstance().showErrorDialog("连接异常","请重新检测网络");
            e.printStackTrace();
            logger.info("出现连接异常");
            connectManager.Allclose();
            throw e;
        }
    }
/***
 * @Title:StartChatConnect
 * @Description:打开聊天窗口并再次连接服务器
 * @param
 * @return void
 * @Author: lzq
 */
    public void StartChatConnect(){
        Thread startChatConnect = new Thread(() -> {
            //打开聊天窗口
            LoginController.getInstance().showScene();
            //再次连接服务器
            logger.info("再次连接服务器....");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //连接服务器
            port=chatPORT;
            while (true) {
                try {
                    this.initConnect(port);
                    this.startSendThread();
                    this.startRecvThread();
                    ClientHeartBeatThread heartBeatThread = new ClientHeartBeatThread(connectManager, socketHeartIntervalTime, lockObject);
                    Thread thread=new Thread(heartBeatThread);
                    thread.setName("heartBeatThread");
                    thread.start();

                    //1、连接成功后阻塞，由心跳检测异常唤醒
                    //方式1
                    synchronized (lockObject) {
                        try {
                    //  调用该方法的线程进入WAITING状态，只有等待另外线程的通知或被中断才会返回，需要注意，
                    //  调用wait()方法后，会释放对象的锁。
                            lockObject.wait();
                        } catch (InterruptedException e) {
                            logger.error("socket客户端进行连接发生异常");
                            e.printStackTrace();
                            try {
                                Thread.sleep(THREAD_SLEEP_MILLS);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    //当心跳包发送失败后，程序强制结束（后期考虑连接失败后自动重连）
                    LoginController.getInstance().showErrorDialog("连接异常","与服务器连接断开，该软件将自动关闭");
                    Thread.sleep(1000);
                    ChatController.getInstance().closeClient();

                } catch (IOException e) {
                    logger.info(" StartChatConnect 异常");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
        startChatConnect.setName("StartChatConnect");
        startChatConnect.start();
    }

    public void sendMessage(Message message){

        this.startRecvThread();
        this.startSendThread();

        if(message!=null){
            message.setIp(connectManager.getIp());
            clientSendThread.addMsgToQueue(message);
        }
    }

    public void startRecvThread(){
        logger.info("即将进行聊天消息监听...");
        if(clientRecvThread==null ? true : clientRecvThread.getStop()){
            logger.info("准备重新开启RecvThread，将检测连接情况");
            if(connectManager.checkConnection()) {
                clientRecvThread = new ClientRecvThread(connectManager, user);
                Thread RecvThread = new Thread(clientRecvThread);
                RecvThread.setName("serverRecvThread");
                RecvThread.start();
            }
        }
    }

    public void startSendThread(){
        logger.info("即将进行聊天消息发送...");
        if(clientSendThread==null ? true : clientSendThread.isStop()){
            logger.info("准备重新开启SendThread，将检测连接情况");
            if(connectManager.checkConnection()) {
                clientSendThread = new ClientSendThread(connectManager);
                Thread SendThread = new Thread(clientSendThread);
                SendThread.setName("ServerSendThread");
                SendThread.start();
            }
        }
    }

    /***
     * @Title:requestDisconnection
     * @Description:初步关闭连接 通过SendThread发送给服务器消息断开申请
     *                         逐步关闭客户端发送线程，服务器接收线程，服务发送线程与
     * @param flag   通知服务器是否是彻底断开连接
     * @return void
     * @Author: lzq
     */
    public void requestDisconnection(Boolean flag){
        logger.info(" 关闭 发送线程");
        Message message=new Message();
        message.setType(MessageType.Disconnect);
        message.setFlag(flag);
        //发送消息，唤醒线程（可能处于阻塞），以便结束旧的发送线程
        //同时通知服务器关闭接收线程
        sendMessage(message);
        clientSendThread.setStop(true);
    }

    
    /***
     * @Title:checkClientConnect
     * @Description:检测客户端的连接 如果连接异常，则重新申请连接
     * @param 
     * @return void
     * @Author: lzq 
     */
    public void checkClientConnect(){
        logger.info("checkClientConnect");

        if(connectManager!=null?!connectManager.checkConnection():true){
            try {
                logger.info("重新连接服务器");
                connectManager=new ConnectManager(HOST,port);
            } catch (IOException e) {
                LoginController.getInstance().showErrorDialog("连接异常","请重新检测网络");
                logger.info("连接出现异常");
                e.printStackTrace();
            }
        }else {
            logger.info("服务器连接正常");
        }
    }


    /***
     * @Title:parsemsg
     * @Description:消息解析，根据消息类型进行不同操作
     *                  开启一个线程来处理消息的解析
     * @param message 要解析的信息
     * @return void
     * @Author: lzq
     */
    public static void parsemsg(Message message){
        logger.info("解析该消息.....");
        Thread parsemsgThread=new Thread(new Runnable() {
            @Override
            public void run() {
                if(message.getType()!=null)
                    for (MessageParser parser : parsers) {
                        if(parser.getType()==message.getType()){
                            parser.parse(message);
                        }
                    }
            }
        });
        parsemsgThread.setName("parseMessage");
        parsemsgThread.start();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        String avatarPath=downloadAvater(user.getAvatar(),user.getUserID());
//        user.setAvatar(avatarPath);
        this.user = user;
    }


    public void setFriendList(ArrayList<User> userList) {
        friendList= new HashMap<>();
        userArrayList=new ArrayList<>();
        for (User value : userList) {

            String avatarPath=downloadAvater(value.getAvatar(),value.getUserID());
            value.setAvatar(avatarPath);

            friendList.put(value.getUserID(), value);
            userArrayList.add(value);
        }
    }

    public void addFriend(User user) {
        friendList.put(user.getUserID(), user);
        userArrayList.add(user);
        ChatController.getInstance().getChatHandler().addChatUser(user);
    }

    public ArrayList<User> getUserArrayList() {
        return userArrayList;
    }

    public String downloadAvater(String img, String imgname){
        String imgpath=filePath+imgname+".jpg";
        Base64Util.base64ChangeImage(img,imgpath);
        return imgpath;
    }

    public User getFriendUser(String ID){
        User user=null;
        user=friendList.get(ID);
        return user;
    }

}