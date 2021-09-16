package messages.parser;

import messages.Message;
import messages.MessageType;

public abstract class MessageParser {
    private MessageType type;

    public MessageParser(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return this.type;
    }

    public abstract void parse(Message msg);

}
