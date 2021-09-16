package messages.parser;

import client.Client;
import controller.ChatController;
import messages.Message;
import messages.MessageType;
import messages.User;

import javax.swing.event.CaretListener;

/**
 * @ClassName OnlineStatusMsgParser
 * @Description TODO
 * @Author lzq
 * @Date 2021/3/5 13:44
 */
public class OnlineStatusMsgParser extends MessageParser{
    private final static OnlineStatusMsgParser parser = new OnlineStatusMsgParser();

    public static OnlineStatusMsgParser getParser() {
        return parser;
    }
    public OnlineStatusMsgParser() {
        super(MessageType.OnlineStatus);
    }

    @Override
    public void parse(Message msg) {
        ChatController.getInstance().getChatHandler().setStranger(true);
        System.out.println("--信息："+msg.getUser().getName());
        User user=msg.getUser();

        String avatarPath=Client.getInstance().downloadAvater(user.getAvatar(),user.getUserID());
        user.setAvatar(avatarPath);

        ChatController.getInstance().showUserInformation(msg.getUser());
    }
}
