package messages.parser;

import database.DAO.MysqlDb.MsgDaoSQLServerlmpl;
import database.DAO.deoImp.MessageDao;
import database.info.ChatMsgInfo;
import database.info.UserInfo;
import messages.ChatMsg;
import messages.Message;
import server.ClientHandler;
import server.ClientManager;

import java.util.ArrayList;
import java.util.List;

import static messages.MessageType.ChatMessage;

public class ChatMsgParser extends MessageParser {

    private MessageDao msgDaoSQLServerlmpl;
    private final static ChatMsgParser parser = new ChatMsgParser();
    private ChatMsgParser(){
        super(ChatMessage);
    }

    public static ChatMsgParser getParser() {
        return parser;
    }

    @Override
    public void parse(Message msg, UserInfo user) {
        if(msg.getFlag()){
            //申请消息记录
            String relationId=null;
            ClientHandler clientHandler=ClientManager.getParent().getClientHandle_id(msg.getCount());
            if(clientHandler!=null){
                relationId=clientHandler.getRelationList().get(msg.getMsg_content()).getId();

                msgDaoSQLServerlmpl=new MsgDaoSQLServerlmpl();

                List<ChatMsgInfo> messageHistory=msgDaoSQLServerlmpl.getMsgList(relationId);
                ArrayList<ChatMsg> messageList=new ArrayList<>();
                for(ChatMsgInfo value:messageHistory){
                    //System.out.println(value.getFromId()+" "+value.getContent());
                    messageList.add(ChatMsgInfo.setChatMsgInformation(value));
                }
                msg.setChatMsgList(messageList);
                clientHandler.sendMessage(msg);
            }
        }else {
            System.out.println("传递消息");
            //传递消息
            ClientHandler ToclientHandler=ClientManager.getParent().getClientHandle_id(msg.getMsg_content());
            if(ToclientHandler!=null)
                ToclientHandler.sendMessage(msg);
            else {
                System.out.println("聊天对象已下线");
            }
            System.out.println("存储消息");
            //存储消息
            MsgDaoSQLServerlmpl msgDaoSQLServerlmpl=new MsgDaoSQLServerlmpl();
            ChatMsgInfo chatMsgInfo=ChatMsgInfo.setChatMsgInfoInformation(msg.getChatMsgList().get(0));
            msgDaoSQLServerlmpl.addMsg(chatMsgInfo);
        }
    }
}
