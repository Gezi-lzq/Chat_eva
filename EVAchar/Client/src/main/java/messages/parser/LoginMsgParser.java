package messages.parser;

import client.Client;
import handler.ViewHandler;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import messages.Message;
import messages.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginMsgParser extends MessageParser {
    private final static LoginMsgParser parser = new LoginMsgParser();

    public static LoginMsgParser getParser() {
        return parser;
    }
    public LoginMsgParser() {
        super(MessageType.Login);
    }

    static Logger logger = LoggerFactory.getLogger(LoginMsgParser.class);

    @Override
    public void parse(Message msg) {
        //登录过程完毕，申请关闭连接
        logger.info("登录过程完毕，申请关闭连接");
        Client.getInstance().requestDisconnection(!msg.getFlag());

        Platform.runLater(()-> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("登录结果");
            if(msg.getFlag()){
                //登录成功
                //读取该消息内的载入信息
                Client.getInstance().setUser(msg.getUser());
                Client.getInstance().setFriendList(msg.getUserList());
                //启动聊天相关
                logger.info("Start Load ChatWindow....");
                if(ViewHandler.LoadChatWindow()){
                    Client.getInstance().StartChatConnect();;
                }
            }else {
                alert.setHeaderText("登录失败");
                alert.setContentText(msg.getMsg_content());
                alert.showAndWait();
            }
            logger.info("消息解析完毕.....");
        });
    }

}
