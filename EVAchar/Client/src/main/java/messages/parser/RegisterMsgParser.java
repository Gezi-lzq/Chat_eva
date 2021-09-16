package messages.parser;


import client.Client;
import controller.LoginController;
import handler.ViewHandler;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import messages.Message;
import messages.MessageType;

public class RegisterMsgParser extends MessageParser{
    private final static RegisterMsgParser parser = new RegisterMsgParser();

    public static RegisterMsgParser getParser() {
        return parser;
    }
    public RegisterMsgParser() {
        super(MessageType.Register);
    }

    @Override
    public void parse(Message msg) {
        Platform.runLater(()-> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("注册结果");
            if(msg.getFlag()){
                alert.setHeaderText("注册成功");
                alert.setContentText("用户"+msg.getUser().getName()+"的ID为："+msg.getUser().getUserID());

                //读取该消息内的载入信息
                Client.getInstance().setUser(msg.getUser());
                Client.getInstance().setFriendList(msg.getUserList());

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //启动聊天相关界面与连接
                LoginController.getSecondaryStage().close();
                if(ViewHandler.LoadChatWindow()){
                    Client.getInstance().StartChatConnect();;
                }
            }else {
                alert.setHeaderText("注册失败");
            }
            alert.showAndWait();
        });

    }



}
