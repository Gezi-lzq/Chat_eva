package messages.parser;

import database.DAO.MysqlDb.BaseDao;
import database.DAO.MysqlDb.UserDaoSQLServerlmpl;
import database.info.UserInfo;
import messages.Message;
import messages.MessageType;
import messages.User;
import service.LoginService;
import util.Base64Util;

import static config.Config.filePath;

public class RegisterMsgParser extends MessageParser{
    private final static RegisterMsgParser parser = new RegisterMsgParser();

    public static RegisterMsgParser getParser() {
        return parser;
    }
    public RegisterMsgParser() {
        super(MessageType.Register);
    }
    @Override
    public void parse(Message msg, UserInfo user) {
        User adduser=msg.getUser();
        String password=msg.getMsg_content();

        UserDaoSQLServerlmpl userSQL=new UserDaoSQLServerlmpl();
        int n= BaseDao.getRow("im_user");
        String ID=String.valueOf(n+1346000);//生成ID序号

        //把头像图片存入服务器，信息改为图片地址
        String avatarPath=downloadAvater(adduser.getAvatar(),ID);
        adduser.setAvatar(avatarPath);

        adduser.setUserID(ID);
        UserInfo userInfo=new UserInfo(adduser);
        userInfo.setPassword(password);

        userSQL.addUser(userInfo);

        //设置客户端托管者所引用的user对象的对应的信息
        UserInfo.resetUserInfo(user,userInfo);

        new LoginService(user).startLoginService();
    }

    public String downloadAvater(String img,String imgname){
        String imgpath=filePath+imgname+".jpg";
        Base64Util.base64ChangeImage(img,imgpath);
        return imgpath;
    }
}
