package database.DAO.deoImp;

import database.info.UserInfo;

public interface UserDao {
    void addUser(UserInfo userInfo);
    void delUser(String userID);
    void alterUser(UserInfo userInfo);
    UserInfo findUser_fromID(String userID);
}
