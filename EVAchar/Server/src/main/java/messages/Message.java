package messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable {

    private static final long serialVersionUID = 349711000229478085L;
    private MessageType type;
    private Boolean flag;
    private String count;
    private String msg_content;
    private String ip;
    private User user;
    private ArrayList<User> userList;
    private ArrayList<ChatMsg> chatMsgList;


    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(String msg_content) {
        this.msg_content = msg_content;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
    }

    public ArrayList<ChatMsg> getChatMsgList() {
        return chatMsgList;
    }

    public void setChatMsgList(ArrayList<ChatMsg> chatMsgList) {
        this.chatMsgList = chatMsgList;
    }
}
