package messages.parser;

import controller.ChatController;
import messages.ChatMsg;
import messages.Message;


import java.util.ArrayList;

import static messages.MessageType.ChatMessage;

public class ChatMsgParser extends MessageParser {

    private final static ChatMsgParser parser = new ChatMsgParser();
    private ChatMsgParser(){
        super(ChatMessage);
    }

    public static ChatMsgParser getParser() {
        return parser;
    }

    @Override
    public void parse(Message msg) {
        if(!msg.getFlag())
            ChatController.getInstance().getChatHandler().ClientAddUserMsg(msg.getChatMsgList().get(0));
        else {
            ArrayList<ChatMsg> history= msg.getChatMsgList();

            ChatController.getInstance().getChatHandler().setChatMsgList(history);
            ChatController.getInstance().OpenMessageLogPane();

        }
    }
}
