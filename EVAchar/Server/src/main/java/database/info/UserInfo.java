package database.info;

import messages.Status;
import messages.User;
import util.Base64Util;

import static messages.Status.*;

public class UserInfo {
    private Integer id ;
    private String name;
    private String userID;
    private String password;
    private String avatar;
    private Status status;
    private String sex;
    private String phone_number;
    private String email;
    private String sign_info;

    public UserInfo(User user) {
        this.name = user.getName();
        this.userID = user.getUserID();
        this.status = user.getStatus();
        this.sex = user.getSex();
        this.phone_number = user.getPhone_number();
        this.email = user.getEmail();
        this.sign_info = user.getSign_info();
        this.avatar=user.getAvatar();
    }

    public static User SetUserInformation(UserInfo userInfo){
        User user=new User(userInfo.getName(),userInfo.getUserID(),userInfo.getAvatar(),userInfo.getUserstatus(),userInfo.getUserSex(),userInfo.getPhone_number(),userInfo.getEmail(),userInfo.getSign_info());
        String imagPath=user.getAvatar();
        String imagData=Base64Util.getImgStr(imagPath);
        user.setAvatar(imagData);
        return user;
    }

    public UserInfo(String name, String userID, String password, Status status,
                    String sex, String phone_number, String email, String sign_info,String avatar) {
        this.name = name;
        this.userID = userID;
        this.password = password;
        this.status = status;
        this.sex = sex;
        this.phone_number = phone_number;
        this.email = email;
        this.sign_info = sign_info;
        this.avatar=avatar;
    }

    public UserInfo() {
    }


    public static void resetUserInfo(UserInfo user, UserInfo userInfo){
        user.setStatus_int(userInfo.getStatus());
        user.setPhone_number(userInfo.getPhone_number());
        user.setAvatar(userInfo.getAvatar());
        user.setSign_info(userInfo.getSign_info());
        user.setEmail(userInfo.getEmail());
        user.setPassword(userInfo.getPassword());
        user.setName(userInfo.getName());
        user.setUserID(userInfo.getUserID());
        user.setId(userInfo.getId());
        user.setSex(userInfo.getSex());
    }

    public String getSign_info() {
        return sign_info;
    }

    public void setSign_info(String sign_info) {
        this.sign_info = sign_info;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getStatus() {
        int n=0;
        switch (status){
            case ONLINE:n=1;
            break;
            case BUSY:n=2;
            break;
            case OFFLINE:n= 3;
            break;
            case INVISIBLE:n= 4;
            break;
        }
        return n;
    }
    public Status getUserstatus(){
        return status;
    }

    public void setStatus_int(int status) {
        switch (status){
            case 1: this.status=ONLINE;
                break;
            case 2:this.status=BUSY;
                break;
            case 3:this.status=OFFLINE;
                break;
            case 4:this.status=INVISIBLE;
                break;
        }
    }

    public void setStatus(Status status){
        this.status=status;
    }

    public int getSex() {
        int n;
        switch (sex){
            case "男":n=1;break;
            case "女":n=0;break;
            default:n=3;
        }
        return n;
    }
    public String getUserSex() {

        return sex;
    }

    public void setSex(int sex) {
        switch (sex){
            case 0:this.sex="女";break;
            case 1:this.sex="男";break;
            case 3:this.sex="未知";break;
        }
    }
    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
