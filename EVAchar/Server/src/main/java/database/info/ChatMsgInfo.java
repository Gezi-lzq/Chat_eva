package database.info;

import messages.ChatMsg;
import server.ClientManager;

public class ChatMsgInfo {
    private String relateId;
    private String fromId;
    private String toId;
    private String content;
    private int type;
    private int status;
    private String msg_time;

    public ChatMsgInfo(String relateId, String fromId, String toId,
                       String content, int type, int status, String msg_time) {
        this.relateId = relateId;
        this.fromId = fromId;
        this.toId = toId;
        this.content = content;
        this.type = type;
        this.status = status;
        this.msg_time = msg_time;
    }

    public String getMsg_time() {
        return msg_time;
    }

    public void setMsg_time(String msg_time) {
        this.msg_time = msg_time;
    }

    public String getRelateId() {
        return relateId;
    }

    public void setRelateId(String relateId) {
        this.relateId = relateId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static ChatMsgInfo setChatMsgInfoInformation(ChatMsg chatMsg){
        ChatMsgInfo chatMsgInfo= null;
        String relationId= "-1";
        try {
            relationId=ClientManager.getParent().getClientHandle_id(chatMsg.getFrom_ID()).getRelationList().get(chatMsg.getTo_ID()).getId();
            chatMsgInfo = new ChatMsgInfo(relationId,chatMsg.getFrom_ID(),chatMsg.getTo_ID(),chatMsg.getMsg_content(),chatMsg.getType(),1,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMsgInfo;
    }
    public static ChatMsg setChatMsgInformation(ChatMsgInfo chatMsgInfo){
        ChatMsg chatMsg=new ChatMsg();
        chatMsg.setFrom_ID(chatMsgInfo.getFromId());
        chatMsg.setTo_ID(chatMsgInfo.getToId());
        chatMsg.setMsg_content(chatMsgInfo.getContent());
        chatMsg.setType(chatMsgInfo.getStatus());
        return chatMsg;
    }

}
