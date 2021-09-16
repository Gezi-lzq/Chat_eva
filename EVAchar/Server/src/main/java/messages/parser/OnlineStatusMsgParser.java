package messages.parser;

import database.DAO.MysqlDb.UserDaoSQLServerlmpl;
import database.info.UserInfo;
import messages.Message;
import messages.MessageType;
import messages.User;
import server.ClientHandler;
import server.ClientManager;

public class OnlineStatusMsgParser extends MessageParser{
    private final static OnlineStatusMsgParser parser = new OnlineStatusMsgParser();

    public static OnlineStatusMsgParser getParser() {
        return parser;
    }
    public OnlineStatusMsgParser() {
        super(MessageType.OnlineStatus);
    }

    @Override
    public void parse(Message msg, UserInfo user) {
        String userID=msg.getMsg_content();
        UserDaoSQLServerlmpl userDaoSQLServerlmpl=new UserDaoSQLServerlmpl();
        UserInfo userInfo=userDaoSQLServerlmpl.findUser_fromID(userID);
        User findUser=UserInfo.SetUserInformation(userInfo);
        System.out.println("查询到用户："+findUser.getUserID()+findUser.getName());
        msg.setUser(findUser);
        ClientHandler clientHandler=ClientManager.getParent().getClientHandle_id(msg.getCount());
        clientHandler.sendMessage(msg);
    }
}
