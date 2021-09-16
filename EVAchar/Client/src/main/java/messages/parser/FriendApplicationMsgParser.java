package messages.parser;

import client.Client;
import controller.ChatController;
import controller.UserInformationPane;
import messages.Message;
import messages.MessageType;
import messages.User;

public class FriendApplicationMsgParser extends MessageParser {
    private final static FriendApplicationMsgParser parser = new FriendApplicationMsgParser();
    public static FriendApplicationMsgParser getParser() {
        return parser;
    }

    public FriendApplicationMsgParser() {
        super(MessageType.FriendApplication);
    }

    @Override
    public void parse(Message msg) {
        if(msg.getCount().equals(Client.getInstance().getUser().getUserID())){
            //解析申请结果信息
            User user=msg.getUser();
            String avatarPath=Client.getInstance().downloadAvater(user.getAvatar(),user.getUserID());
            user.setAvatar(avatarPath);

            Client.getInstance().addFriend(user);
            ChatController.getInstance().updateList(user);
        }else{
            //解析由他人发来的好友申请信息
            ChatController.getInstance().showUserInformation(msg.getUser());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            UserInformationPane.getInstance().OpenFriendApplicationMode();
        }
    }
}
