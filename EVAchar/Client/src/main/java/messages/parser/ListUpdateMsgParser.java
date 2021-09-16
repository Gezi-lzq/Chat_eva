package messages.parser;

import client.Client;
import controller.ChatController;
import messages.Message;
import messages.MessageType;
import messages.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListUpdateMsgParser extends MessageParser {
    static Logger logger = LoggerFactory.getLogger(ListUpdateMsgParser.class);

    private final static ListUpdateMsgParser parser = new ListUpdateMsgParser();
    public ListUpdateMsgParser() {
        super(MessageType.ListUpdate);
    }
    public static ListUpdateMsgParser getParser() {
        return parser;
    }
    @Override
    public void parse(Message msg) {
        logger.info("好友上线"+msg.getUser().getName());
        User user=msg.getUser();
        String avatarPath= Client.getInstance().downloadAvater(user.getAvatar(),user.getUserID());
        user.setAvatar(avatarPath);
        ChatController.getInstance().updateList(user);
    }
}
