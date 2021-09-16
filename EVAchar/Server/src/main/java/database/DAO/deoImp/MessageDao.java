package database.DAO.deoImp;

import database.info.ChatMsgInfo;

import java.util.List;

public interface MessageDao {
    abstract void addMsg(ChatMsgInfo msgInfo);
    void delMsg(String id);
    List<ChatMsgInfo> getMsgList(String related);
}
