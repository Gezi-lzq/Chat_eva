package server.thread;

import database.info.UserInfo;
import messages.MessageType;
import server.ClientManager;
import server.ConnectManager;
import handler.MessageHandler;
import messageIO.MessageInputStream;
import messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerRecvThread implements Runnable {
    private final static Logger log = LoggerFactory.getLogger(ServerRecvThread.class);
    private ConnectManager connectManager;
    private volatile boolean isStop = false;
    private UserInfo userInfo;

    public ServerRecvThread(ConnectManager connectManager, UserInfo userInfo) {
        this.connectManager = connectManager;
        this.userInfo = userInfo;
    }

    @Override
    public void run() {
        log.info(connectManager.getIp()+"客户端 消息接收线程已启动");
        //线程终止条件： 设置标志位为 true or socket 已关闭
        MessageInputStream in=null;
        try {
            in=connectManager.getStreamManager().getMessageInputStream();
            while (!isStop && !connectManager.isClosed()) {
                log.info(connectManager.getIp()+"客户端：消息监听中....");
                Message message=in.readMessage();
                if(message!=null&&message.getType()!=null){
                    try {
                        log.info("form "+connectManager.getIp()+"  接受消息 "+message.getType()+"m,进行解析....");
                        message.setIp(connectManager.getIp());
                        MessageHandler.getMessageHandler().parsemsg(message,userInfo);
                        if(message.getType()==MessageType.Disconnect)
                            isStop=true;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("服务端接收消息出现异常 强制移除在线列表");
            //强制移除在线列表
            ClientManager.getParent().removeClient(userInfo.getUserID());
            //强制关闭所有连接
            connectManager.close();
        }finally {
            log.info("服务端消息接收线程已摧毁");
            //释放资源
            in.close();
        }
    }

    public boolean getStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }
}
