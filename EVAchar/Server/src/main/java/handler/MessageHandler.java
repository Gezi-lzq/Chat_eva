package handler;

import database.info.UserInfo;
import messages.parser.*;
import messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class MessageHandler {
    private final static Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    private final static MessageHandler msghandler = new MessageHandler();
    private static Set<MessageParser> parsers = new HashSet<>();

    public static MessageHandler getMessageHandler() {
        return msghandler;
    }

    static {
        parsers.add(ChatMsgParser.getParser());
        parsers.add(LoginMsgParser.getParser());
        parsers.add(RegisterMsgParser.getParser());
        parsers.add(DisconnectMsgParser.getParser());
        parsers.add(OnlineStatusMsgParser.getParser());
        parsers.add(FriendApplicationMsgParser.getParser());
    }

    public void parsemsg(Message message, UserInfo user){
        logger.info("消息："+message.getType());
        Thread parsemsgThread=new Thread(new Runnable() {
            @Override
            public void run() {
                for (MessageParser parser : parsers) {
                    if(parser.getType()==message.getType()){
                        parser.parse(message,user);
                    }
                }
            }
        });
        parsemsgThread.setName("parseMessage");
        parsemsgThread.start();
    }


}
