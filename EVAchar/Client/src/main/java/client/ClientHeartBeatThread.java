package client;

import messageIO.MessageOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class ClientHeartBeatThread implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(ClientHeartBeatThread.class);

    private ConnectManager connectManager;
    private Object lockObject ; //锁对象，用于线程通讯，唤醒重试线程

    //3间隔多长时间发送一次心跳检测
    private int socketHeartIntervalTime;

    private volatile boolean isStop = false;

    public ClientHeartBeatThread(ConnectManager connectManager, int socketHeartIntervalTime, Object lockObject) {
        this.connectManager = connectManager;
        this.socketHeartIntervalTime = socketHeartIntervalTime;
        this.lockObject = lockObject;
    }

    @Override
    public void run() {
        logger.info("开启心跳包线程");
        MessageOutputStream heartBeatout=null;
        try {
            heartBeatout=connectManager.getStreamManager().getMessageOutputStream();
            while (!this.isStop && !connectManager.isClosed()) {
                heartBeatout.writeHeartBeat();

                try {
                    Thread.sleep(socketHeartIntervalTime * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            logger.error("客户端心跳消息发送异常");
            e.printStackTrace();
        }finally {
            this.isStop = true;
            logger.info("客户端旧心跳线程已摧毁");
            heartBeatout.close();

            //最后唤醒线程、重建连接
            synchronized (lockObject) {
                lockObject.notify();
            }
        }

    }
}
