package service;

import database.DAO.MysqlDb.RelationDaoSQLServerlmpl;
import database.DAO.MysqlDb.UserDaoSQLServerlmpl;
import database.info.RelationshipInfo;
import database.info.UserInfo;
import messages.Message;
import messages.MessageType;
import messages.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.ClientHandler;
import server.ClientManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class LoginService{

    private Message Initmessage;
    private UserInfo user;
    static Logger logger = LoggerFactory.getLogger(LoginService.class);

    //避免使用Executors 创建线程池，主要是避免使用其中的默认实现，那么我们
    //可以自己直接调用ThreadPoolExecutor 的构造函数来自己创建线程池。在创建的
    //同时，给BlockQueue 指定容量就可以了。
    private static ExecutorService newCachedThreadPool = new ThreadPoolExecutor(10, 10,
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue(10));

    //使用Executors 创建线程池，如果不设置LinkedBlockingQueue 的
    //容量的话，其默认容量将会是Integer.MAX_VALUE。
    //而newFixedThreadPool 中创建LinkedBlockingQueue 时，并未指定容
    //量。此时，LinkedBlockingQueue 就是一个无边界队列，对于一个无边界队列
    //来说，是可以不断的向队列中加入任务的，这种情况下就有可能因为任务过多而导
    //致内存溢出问题。博客 --”多线程的使用，避免ExecutorService 的创建处理“

    public LoginService(UserInfo user) {
        this.user = user;
    }

    public void startLoginService(){
        Thread loginService=new Thread(() -> {
            ClientHandler clientHandler=ClientManager.getParent().getClientHandle_id(user.getUserID());
            String ip=clientHandler.getIp();
            if (user.getName() != null) {//说明登录成功
                //准备加载信息
                logger.info(user.getName() + "登录成功");
                User client = UserInfo.SetUserInformation(user);
                Initmessage =new Message();
                Initmessage.setUser(client);
                Initmessage.setFlag(true);
                Initmessage.setMsg_content("登录成功");
                Initmessage.setType(MessageType.Login);

                clientHandler.setHaveInit(true);

                logger.info("查询"+user.getName()+"好友");
                RelationDaoSQLServerlmpl relatSQL = new RelationDaoSQLServerlmpl();
                UserDaoSQLServerlmpl userSQL = new UserDaoSQLServerlmpl();

                List<RelationshipInfo> friendList = relatSQL.getUserId_listFriend(user.getUserID());

                ClientManager.getParent().getClientHandle_id(user.getUserID()).setRelationList(friendList);

                ArrayList<User> userList = new ArrayList<>();
                for (RelationshipInfo rs : friendList) {
                    UserInfo userInfo=null;
                    if(!rs.getFriendId().equals(user.getUserID())) {
                        userInfo = userSQL.findUser_fromID(rs.getFriendId());
                    }else {
                        userInfo = userSQL.findUser_fromID(rs.getUserId());
                    }
                    userList.add(UserInfo.SetUserInformation(userInfo));
                }

                ClientManager.getParent().getClientHandle_id(user.getUserID()).setFriendList(userList);

                Initmessage.setUserList(userList);
                logger.info("已经封装完成"+user.getUserID()+"载入消息");

                //发送客户端的载入信息（本人用户（User信息包括头像信息）+好友用户列表）
                ClientManager.getParent().getClientHandle_id(user.getUserID()).sendMessage(Initmessage);
                logger.info("已发送客户端的载入信息"+user.getName());

//                    更新该用户好友的在线列表
                Message listMessage=new Message();
                listMessage.setType(MessageType.ListUpdate);
                listMessage.setUser(client);
                ClientManager.getParent().getClientHandle_id(user.getUserID()).ListBroadcast(listMessage);

                } else {
                    logger.info(ip + "登录失败");
//                    ClientManager.getParent().removeClient(user.getUserID());
                }
            logger.info("准备关闭与该客户端的连接......");

//            clientHandler.setConnected(false);
        });
        loginService.setName("loginService");
        newCachedThreadPool.execute(loginService);
    }

    public static Object CheckAccountAvailability(String ID,String password){
        UserDaoSQLServerlmpl userSQL =new UserDaoSQLServerlmpl();
        UserInfo userInfo = userSQL.findUser_fromID(ID);
        System.out.println("CheckAccountAvailability");
        if(userInfo==null) {
            System.out.println("该连接 账号不存在");
            Message message=new Message();
            message.setType(MessageType.Login);
            message.setFlag(false);
            message.setMsg_content("账号不存在");
            return message;
        } else if(userInfo.getPassword().equals(password)){
            int status = userInfo.getStatus();
            if (status == 3)//若为离线状态
                return userInfo;
            else {
                Message message = new Message();
                message.setType(MessageType.Login);
                message.setFlag(false);
                message.setMsg_content("该账号已登录(或者为上次异常退出), 若非本人,则请再次登录");
                userInfo.setStatus_int(3);
                //将此账号再次改为离线状态
                userSQL.alterUser(userInfo);
                System.out.println("该账号已登录(或者为上次异常退出)");
                return message;
            }
        }else {
            System.out.println("该连接 账号密码错误");
            Message message=new Message();
            message.setType(MessageType.Login);
            message.setFlag(false);
            message.setMsg_content("账号或密码错误，请认真核对");
            return message;
        }
    }

}
