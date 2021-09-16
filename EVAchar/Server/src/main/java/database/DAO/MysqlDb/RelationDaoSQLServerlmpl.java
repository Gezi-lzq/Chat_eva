package database.DAO.MysqlDb;

import database.DAO.deoImp.RelationShipDao;
import database.info.RelationshipInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RelationDaoSQLServerlmpl extends BaseDao implements RelationShipDao{
    @Override
    public void addRelationShip(RelationshipInfo relationshipInfo) {
        String psql ="insert into im_relation_ship(user_Id,friend_Id,status) values(?,?,?)";
        Object[] values={relationshipInfo.getUserId(),relationshipInfo.getFriendId(),relationshipInfo.getStatus()};
        int n=super.executeUpdate(psql,values);
    }

    @Override
    public List<RelationshipInfo> getUserId_listFriend(String user_ID) {
        String psql ="SELECT * FROM im_relation_ship WHERE user_Id=? OR friend_Id=?";
        Object[] values ={user_ID,user_ID};
        ResultSet rs;
        List<RelationshipInfo> friendList= new ArrayList<>();
        String id;
        String userId;
        String friendId;
        int status;

        rs=super.executeQuerySQL(psql,values);

        if(rs!=null){
            try {
                while (rs.next()){
                    id=rs.getString("id");
                    userId=rs.getString("user_Id");
                    friendId=rs.getString("friend_Id");
                    status=rs.getInt("status");
                    friendList.add(new RelationshipInfo(id,userId,friendId,status));
                }
                logger.info("已获得"+user_ID+"好友列表");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return friendList;
    }


    @Override
    public void delRelationShip(String id) {
        String psql="DELETE FROM im_relation_ship WHERE id = ?";
        Object[] value ={id};
        super.executeUpdate(psql,value);
    }

}
