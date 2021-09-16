package client;

import controller.ChatController;
import messageIO.messageImp.MessageInput;
import messages.Message;
import messages.MessageType;
import messages.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ClientRecvThread implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(ClientRecvThread.class);
    //当此变量不加volatile时，线程将无法停止，由于线程一直在自己的工作栈中取数据，而主线程修改的数据保存在
    //内存模型的主内存中，则线程无法停止。若加上volatile后则保证此参数的线程可见性，则可以顺利停止线程---博客 多线程的锁与线程通信
    private volatile boolean isStop = false;

    private ConnectManager connectManager;
    private User user;

    public ClientRecvThread(ConnectManager connectManager , User user) {
        this.connectManager = connectManager;
        this.user=user;
    }

    @Override
    public void run() {
        logger.info("开启消息接收线程");
        //线程终止条件： 设置标志位为 true or socket 已关闭
        MessageInput input=null;
        try {
            input=connectManager.getStreamManager().getMessageInputStream();
            while (!isStop && !connectManager.isClosed()) {
                Message message= null;
                logger.info("服务器消息监听中......");
                message = input.readMessage();
                logger.info("收到消息，开始检测..");
                if(message!=null&&message.getType()!=null){
                    logger.info("客户端收到消息:"+message.getType());
                    Client.parsemsg(message);
                    if(message.getType()== MessageType.Disconnect)
                        isStop=true;
                }else {
                    logger.info("消息有误，继续监听...");
                    if(message!=null){
                        logger.info(message.getType()+"  "+message.getMsg_content());
                    }
                }
            }
        } catch (IOException e) {
            logger.error("客户端接收消息线程 暂停");
//            e.printStackTrace();
        } finally {
            isStop=true;
            if(input!=null)
                input.close();
        }

    }
    public boolean getStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }


}
