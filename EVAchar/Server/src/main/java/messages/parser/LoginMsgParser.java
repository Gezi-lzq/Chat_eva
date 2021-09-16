package messages.parser;

import database.DAO.MysqlDb.UserDaoSQLServerlmpl;
import database.info.UserInfo;
import messages.Message;
import messages.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.ClientManager;
import service.LoginService;


public class LoginMsgParser extends MessageParser {
    private final static Logger log = LoggerFactory.getLogger(LoginMsgParser.class);
    private final static LoginMsgParser parser = new LoginMsgParser();

    public static LoginMsgParser getParser() {
        return parser;
    }
    public LoginMsgParser() {
        super(MessageType.Login);
    }

    @Override
    public void parse(Message msg, UserInfo user){
        log.info(msg.getIp()+"进入登录消息解析"+msg.getCount()+" "+msg.getMsg_content());
        user.setUserID(msg.getCount());//解析出账号给ClientHandler，作为key

        UserInfo userInfo;
        Object object= LoginService.CheckAccountAvailability(msg.getCount(),msg.getMsg_content());
        if(object.getClass().equals(UserInfo.class)) {
            userInfo=(UserInfo) object;
            UserDaoSQLServerlmpl userSQL = new UserDaoSQLServerlmpl();
            userInfo.setStatus_int(1);//更改在线状态
            userSQL.alterUser(userInfo);

            //设置客户端托管者所引用的user对象的对应的信息
            UserInfo.resetUserInfo(user,userInfo);

        }if(object.getClass().equals(Message.class)){
            Message message=(Message)object;
            ClientManager.getParent().getClientHandle_id(user.getUserID()).sendMessage(message);
        }
        new LoginService(user).startLoginService();
    }


}
