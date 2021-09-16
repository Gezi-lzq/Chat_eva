package database.testdemo;

import database.DAO.MysqlDb.UserDaoSQLServerlmpl;
import database.info.UserInfo;

public class Test {
    public static void main(String[] args) {

//        UserInfo userInfo=new UserInfo("格子格子","0000001","123456", Status.ONLINE,"男","12345678987","lzqtxwd@gamil.com","hello world");
//        userInfo.setAvatar("000101001001");
        UserDaoSQLServerlmpl userDaoSQLServerlmpl = new UserDaoSQLServerlmpl();
//        userDaoSQLServerlmpl.addUser(userInfo);
        UserInfo finduser = userDaoSQLServerlmpl.findUser_fromID("0000001");
        System.out.println("0000001的用户名："+finduser.getName()+"："+finduser.getSign_info());

    }
}
