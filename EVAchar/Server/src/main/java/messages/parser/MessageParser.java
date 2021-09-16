package messages.parser;

import database.info.UserInfo;
import handler.StreamHandler;
import messages.Message;
import messages.MessageType;
import messages.User;
import server.ClientHandler;

public abstract class MessageParser {
    private MessageType type;

    public MessageParser(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return this.type;
    }

    public abstract void parse(Message msg, UserInfo user);

}
