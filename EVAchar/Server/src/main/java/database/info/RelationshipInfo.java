package database.info;

public class RelationshipInfo {
    private String id;
    private String userId;
    private String friendId;
    private Integer status;

    public RelationshipInfo(String id,String userId, String friendId, Integer status) {
        this.id=id;
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RelationshipInfo() {
    }

    public RelationshipInfo(String userId, String friendId, Integer status) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
