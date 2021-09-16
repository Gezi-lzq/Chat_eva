package database.DAO.deoImp;

import database.info.RelationshipInfo;

import java.util.List;

public interface RelationShipDao {
    void addRelationShip(RelationshipInfo relationshipInfo);
    List<RelationshipInfo> getUserId_listFriend(String userID);
    void delRelationShip(String id);
}
