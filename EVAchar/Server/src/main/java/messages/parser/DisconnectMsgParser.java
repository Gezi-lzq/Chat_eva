package messages.parser;

import database.info.UserInfo;
import messages.Message;
import messages.MessageType;
import server.ClientHandler;
import server.ClientManager;

public class DisconnectMsgParser extends MessageParser{
    private final static DisconnectMsgParser parser = new DisconnectMsgParser();
    public static DisconnectMsgParser getParser() {
        return parser;
    }

    public DisconnectMsgParser() {
        super(MessageType.Disconnect);
    }
    @Override
    public void parse(Message msg, UserInfo user) {
        if(msg.getFlag()){      //彻底断开连接
            ClientHandler clientHandler=ClientManager.getParent().getClientHandle_id(user.getUserID());

            //更改数据库在线状态 向在线列表广播
            clientHandler.closeOnlineStatus();

            //告知客户端连接即将断开(若还未登录成功，则为false 若登录成功进入聊天了，则为true)
            if(clientHandler.getHaveInit())
                clientHandler.requestDisconnection(false);
            else
                clientHandler.requestDisconnection(true);
//            //彻底关闭连接
//            clientHandler.closeClientConnection();
            //取消服务器在线列表的管理
            ClientManager.getParent().removeClient(user.getUserID());
        }else {         //暂时断开连接(仍在服务器在线列表的管理，仅仅关闭发送接收线程)
            ClientHandler clientHandler=ClientManager.getParent().getClientHandle_id(user.getUserID());
            //告知客户端连接即将断开
            clientHandler.requestDisconnection(false);
            clientHandler.setConnected(false);
        }
    }
}
