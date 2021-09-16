package messages;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String userID;
    private String avatar;
    private Status status;
    private String sex;
    private String phone_number;
    private String email;
    private String sign_info;

    public User(String name, String avatar, Status status, String sex, String phone_number, String email, String sign_info) {
        this.name=name;
        this.avatar = avatar;
        this.status = status;
        this.sex = sex;
        this.phone_number = phone_number;
        this.email = email;
        this.sign_info = sign_info;
    }
    public User(String name,String userID, String avatar, Status status, String sex, String phone_number, String email, String sign_info) {
        this.name=name;
        this.userID = userID;
        this.avatar = avatar;
        this.status = status;
        this.sex = sex;
        this.phone_number = phone_number;
        this.email = email;
        this.sign_info = sign_info;
    }

    public User() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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

    public String getSign_info() {
        return sign_info;
    }

    public void setSign_info(String sign_info) {
        this.sign_info = sign_info;
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof User) {
            User anotherUser = (User)anObject;
            return anotherUser.getUserID().equals(this.getUserID());
        }
        return false;
    }
}
