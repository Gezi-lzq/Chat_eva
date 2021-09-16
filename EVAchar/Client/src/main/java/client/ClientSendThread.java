package client;

import messageIO.MessageOutputStream;
import messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ClientSendThread implements Runnable {
    private final static Logger log = LoggerFactory.getLogger(ClientSendThread.class);
    //阻塞安全队列，设置队列容量，否则为无限大
    private final BlockingQueue<Message> msgQueue = new LinkedBlockingQueue<>(600);
    //等待放进队列秒数
    private final int WAIT_PUT_QUEUE_SECONDS = 10;

    private ConnectManager connectManager;

    //volatile修饰的变量对所有线程的有可见性
    private volatile boolean isStop = false;

    public ClientSendThread(ConnectManager connectManager) {
        this.connectManager = connectManager;
    }
    public boolean addMsgToQueue(Message msgData) {
        try {
            //队列已满，阻塞直到未满放进元素, 废弃不要
            //msgQueue.put(msgDataVo);
            //队列已满，阻塞等待直到未满放进元素，超过10秒算了，返回false
            return msgQueue.offer(msgData, WAIT_PUT_QUEUE_SECONDS, TimeUnit.SECONDS);
            //offer(E e, long timeout, TimeUnit unit)
            //将指定的元素插入到此队列中，等待指定的等待时间（如有必要）才能使空间变得可用。
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void run() {
        log.info("开启消息发送线程");
        MessageOutputStream output=null;
        try {
            output=connectManager.getStreamManager().getMessageOutputStream();
            while (!this.isStop && !connectManager.isClosed()) {
                //队列为空阻塞，直到队列不为空，再取出
                Message message=null;
                try {
                    log.info("等待 消息发送队列...");
                    message = msgQueue.take();
                    //检索并删除此队列的头，如有必要，等待元素可用。
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (message != null) {
                    output.writeMessage(message);
                    log.info("客户端消息已发送！");
                }
            }
        } catch (Exception e) {
            log.error("客户端消息发送异常");
//            e.printStackTrace();
        } finally {
            log.info("客户端消息发送线程已摧毁");
            isStop=true;
            //释放资源
            msgQueue.clear();
            if(output!=null)
                output.close();
        }
    }
    public boolean isStop() {
        return isStop;
    }
    public void setStop(boolean stop) {
        isStop = stop;
    }
}
