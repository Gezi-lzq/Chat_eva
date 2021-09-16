package database.DAO.MysqlDb;

import database.DAO.deoImp.MessageDao;
import database.info.ChatMsgInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MsgDaoSQLServerlmpl extends BaseDao implements MessageDao {
    @Override
    public void addMsg(ChatMsgInfo msgInfo) {
        String psql="INSERT INTO im_message (relateId,fromId,toId,content,type,status) VALUES (?,?,?,?,?,?)";
        Object[] values={msgInfo.getRelateId(),msgInfo.getFromId(), msgInfo.getToId(),msgInfo.getContent(),msgInfo.getType(),msgInfo.getStatus()};
        super.executeUpdate(psql,values);
    }

    @Override
    public void delMsg(String id) {
        String psql="DELETE FROM im_message WHERE id = ?";
        Object[] value={id};
        super.executeUpdate(psql,value);
    }

    @Override
    public List<ChatMsgInfo> getMsgList(String related) {
        if(related==null)
            return null;
        String psql="SELECT * FROM im_message WHERE relateId=?";
        ResultSet rs=null;
        Object[] value={related};
        List<ChatMsgInfo> msgInfoList =new ArrayList<>();
        rs=super.executeQuerySQL(psql,value);
        String relateId;
        String fromId;
        String toId;
        String content;
        int type;
        int status;
        String time;
        if (rs!=null){
            try {
                while (rs.next()){
                    relateId=rs.getString("relateId");
                    fromId=rs.getString("fromId");
                    toId=rs.getString("toId");
                    content=rs.getString("content");
                    type=rs.getInt("relateId");
                    status=rs.getInt("type");
                    time=rs.getString("message_time");
                    msgInfoList.add(new ChatMsgInfo(relateId,fromId,toId,content,type,status,time));
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return msgInfoList;
    }


}
