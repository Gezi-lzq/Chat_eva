package messages.parser;

import database.DAO.MysqlDb.RelationDaoSQLServerlmpl;
import database.DAO.MysqlDb.UserDaoSQLServerlmpl;
import database.info.RelationshipInfo;
import database.info.UserInfo;
import messages.Message;
import messages.MessageType;
import messages.User;
import server.ClientHandler;
import server.ClientManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName FriendApplicationMsgParser
 * @Description TODO
 * @Author lzq
 * @Date 2021/3/5 14:55
 */
public class FriendApplicationMsgParser extends MessageParser {
    private final static FriendApplicationMsgParser parser = new FriendApplicationMsgParser();
    public static FriendApplicationMsgParser getParser() {
        return parser;
    }

    public FriendApplicationMsgParser() {
        super(MessageType.FriendApplication);
    }

    @Override
    public void parse(Message msg, UserInfo userInfo) {
        if(msg.getFlag()==false&&msg.getUser()!=null) {
            //为好友申请信息
            String ID = msg.getMsg_content();
            ClientHandler clientHandler = ClientManager.getParent().getClientHandle_id(ID);
            if (clientHandler != null) {
                clientHandler.sendMessage(msg);
            }
        }else if(msg.getFlag()==false&&msg.getUser()==null){
            //为好友申请结果信息，且结果为不同意

        }else if(msg.getFlag()==true&&msg.getUser()!=null){
            //为好友申请结果信息，且结果为同意
            //好友关系存储数据库
            System.out.println("好友关系存储数据库");
            RelationDaoSQLServerlmpl relationDaoSQLServerlmpl=new RelationDaoSQLServerlmpl();
            relationDaoSQLServerlmpl.addRelationShip(new RelationshipInfo(msg.getCount(),msg.getMsg_content(),0));


            //此处存在大块代码复制，设计不当，暂时写这里
            //更新ClientHandle内的好友列表
            UserDaoSQLServerlmpl userSQL = new UserDaoSQLServerlmpl();
            User user=msg.getUser();
            List<RelationshipInfo> friendList = relationDaoSQLServerlmpl.getUserId_listFriend(user.getUserID());
            ClientManager.getParent().getClientHandle_id(user.getUserID()).setRelationList(friendList);
            ArrayList<User> userList = new ArrayList<>();
            for (RelationshipInfo rs : friendList) {
                UserInfo Info=null;
                if(!rs.getFriendId().equals(user.getUserID())) {
                    Info = userSQL.findUser_fromID(rs.getFriendId());
                }else {
                    Info = userSQL.findUser_fromID(rs.getUserId());
                }
                userList.add(UserInfo.SetUserInformation(Info));
            }
            ClientManager.getParent().getClientHandle_id(user.getUserID()).setFriendList(userList);


            //将申请结果发送给对方
            ClientManager.getParent().getClientHandle_id(msg.getCount()).sendMessage(msg);
        }

    }
}
