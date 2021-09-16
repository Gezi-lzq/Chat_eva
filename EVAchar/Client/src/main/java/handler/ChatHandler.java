package handler;

import client.Client;
import controller.ChatController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import messages.ChatMsg;
import messages.Message;
import messages.MessageType;
import messages.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatHandler {
    private static final Logger logger = LoggerFactory.getLogger(ChatHandler.class);
    private HashMap<String, ArrayList<ChatMsg>> messageList;
    private ArrayList<ChatMsg> ChatMsgList;

    private User FindUserInformation;
    private Boolean isStranger=false;

    public ChatHandler(ArrayList<User> userArrayList) {
        messageList= new HashMap<String, ArrayList<ChatMsg>>();
        for(User user:userArrayList){
            messageList.put(user.getUserID(),new ArrayList<ChatMsg>());
        }
        logger.info("消息列表初始化完成");
    }

    public void addChatUser(User user){
        messageList.put(user.getUserID(),new ArrayList<ChatMsg>());
    }

    /***
     * @Title:ClientAddUserMsg
     * @Description:将消息存储到messageList内
     * @param chatMsg 所需存储的聊天消息
     * @Author: lzq
     */
    public void ClientAddUserMsg(ChatMsg chatMsg){
        if(chatMsg.getFrom_ID().equals(Client.getInstance().getUser().getUserID())){
            messageList.get(chatMsg.getTo_ID()).add(chatMsg);
        }else {
            messageList.get(chatMsg.getFrom_ID()).add(chatMsg);
        }
        ChatController.getInstance().loadUserChatMessage();
    }

    /***
     * @Title:SendUserMsg
     * @Description:封装聊天消息给客户端，发送给服务器
     * @param chatMsg
     * @return void
     * @Author: lzq
     */
    public void SendUserMsg(ChatMsg chatMsg){
        System.out.println("执行SendUserMsg");
        Message message=new Message();
        message.setType(MessageType.ChatMessage);
        message.setFlag(false);//申请获得聊天记录为true，客户端之间聊天用false
        message.setMsg_content(chatMsg.getTo_ID());
        message.setCount(chatMsg.getFrom_ID());
        ArrayList<ChatMsg> msgs=new ArrayList<ChatMsg>();
        msgs.add(chatMsg);
        message.setChatMsgList(msgs);

        Client.getInstance().sendMessage(message);
    }

    /***
     * @Title:sentChatHistoryApplication
     * @Description:向服务器发出申请获得聊天信息
     * @param chatUserID
     * @return void
     * @Author: lzq
     */
    public void sendChatHistoryApplication(String chatUserID){
        Message message=new Message();
        message.setType(MessageType.ChatMessage);
        message.setFlag(true);//申请获得聊天记录为true，客户端之间聊天用false
        message.setCount(Client.getInstance().getUser().getUserID());
        message.setMsg_content(chatUserID);
        //加入服务器发送队列
        System.out.println("发送消息");
        Client.getInstance().sendMessage(message);
    }

    /***
     * @Title:sendUserInfoApplication
     * @Description:向服务器发送申请获得用户信息
     * @param userID
     * @return void
     * @Author: lzq
     */
    public void sendUserInfoApplication(String userID){
        Message message=new Message();
        message.setType(MessageType.OnlineStatus);
        message.setCount(Client.getInstance().getUser().getUserID());
        message.setMsg_content(userID);
        //加入服务器发送队列
        System.out.println("发送消息");
        Client.getInstance().sendMessage(message);
    }


    public void loadChatPaneMsg(String ID){
        ArrayList<ChatMsg> chatMsglist =messageList.get(ID);

        ObservableList<ChatMsg> msgList= FXCollections.observableList(chatMsglist);
        ChatController.getInstance().updateChatWindow(msgList);
    }

    public ArrayList<ChatMsg> getChatMsgList() {
        return ChatMsgList;
    }

    public void setChatMsgList(ArrayList<ChatMsg> chatMsgList) {
        this.ChatMsgList = chatMsgList;
    }


    public void setFindUserInformation(User findUserInformation) {
        logger.info("用户："+findUserInformation.getUserID());
        FindUserInformation = findUserInformation;
    }

    public User getFindUserInformation() {
        return FindUserInformation;
    }

    public Boolean getStranger() {
        return isStranger;
    }
    public void setStranger(Boolean stranger) {
        isStranger = stranger;
    }
}
