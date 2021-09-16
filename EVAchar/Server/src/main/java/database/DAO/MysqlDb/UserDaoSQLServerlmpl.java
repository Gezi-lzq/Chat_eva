package database.DAO.MysqlDb;

import database.DAO.deoImp.UserDao;
import database.info.UserInfo;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoSQLServerlmpl extends BaseDao implements UserDao {

    @Override
    public void addUser(UserInfo userInfo) {
        String psql="insert into im_user(user_id,sex,nike_name,password,phone_number,email,avatar,status,sign_info)" +
                "values(?,?,?,?,?,?,?,?,?);";
        Object[] values={userInfo.getUserID(),userInfo.getSex(),userInfo.getName(),userInfo.getPassword(),userInfo.getPhone_number(),userInfo.getEmail(),userInfo.getAvatar(),userInfo.getStatus(),userInfo.getSign_info()};
        super.executeUpdate(psql,values);
    }

    @Override
    public void delUser(String userID) {
        String psql="DELETE FROM im_user WHERE user_id = ?";
        Object[] value={ userID };
        super.executeUpdate(psql,value);
    }

    @Override
    public void alterUser(UserInfo userInfo) {
         String psql="update im_user set phone_number=?,email=?,avatar=?,status=?,sign_info= ? where id= ?";
         Object[] value={userInfo.getPhone_number(),userInfo.getEmail(),userInfo.getAvatar(),userInfo.getStatus(),userInfo.getSign_info(),userInfo.getId()};
//        String psql="update im_user set status=? where id=?";
//        Object[] value={userInfo.getStatus(),userInfo.getId()};
        super.executeUpdate(psql,value);
    }

    @Override
    public UserInfo findUser_fromID(String userID) {
        UserInfo userInfo=new UserInfo();
        ResultSet resultSet = null;
        String psql="SELECT * FROM im_user WHERE user_id= ? ";
        Object[] value={ userID };
        resultSet= super.executeQuerySQL(psql,value);

/*
 使用rs.getString();前一定要加上rs.next();
 原因：ResultSet对象代表SQL语句执行的结果集，维护指向其当前数据行的光标。
 每调用一次next()方法，光标向下移动一行。
 最初它位于第一行之前!!!!，
 因此第一次调用next()应把光标置于第一行上，使它成为当前行。
 随着每次调用next()将导致光标向下移动一行。
 在ResultSe对象及其t父辈Statement对象关闭之前，光标一直保持有效。
 */
            try {
                if(resultSet.next()){
                    userInfo.setUserID(resultSet.getString("user_id"));
                    userInfo.setId(resultSet.getInt("id"));
                    userInfo.setName(resultSet.getString("nike_name"));
                    userInfo.setSex(resultSet.getInt("sex"));
                    userInfo.setPassword(resultSet.getString("password"));
                    userInfo.setPhone_number(resultSet.getString("phone_number"));
                    userInfo.setEmail(resultSet.getString("email"));
                    userInfo.setAvatar(resultSet.getString("avatar"));
                    userInfo.setStatus_int(resultSet.getInt("status"));
                    userInfo.setSign_info(resultSet.getString("sign_info"));
                }else{
                    System.out.println("未查询到"+Thread.currentThread());
                    return null;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }finally {
                try {
                    resultSet.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        return userInfo;
    }
}
