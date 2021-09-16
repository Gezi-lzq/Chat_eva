package controller;

import client.Client;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import messages.Message;
import messages.MessageType;
import messages.Status;
import messages.User;

import java.net.URL;
import java.util.ResourceBundle;

import static config.Config.filePath;

/**
 * @ClassName UserInformationPane
 * @Description TODO
 * @Author lzq
 * @Date 2021/3/4 18:15
 */
public class UserInformationPane implements Initializable {


    public JFXButton closebtn;
    public AnchorPane pane;
    public Label onstatus;
    public JFXButton YesBtn;
    public JFXButton NoBtn;
    private User user=null;
    public Label sign_info;
    public ImageView avater;
    public Label number;
    public Label userName;
    public Label userID;
    public Label sex;
    public Label Emali;
    public JFXButton addFriendBtn;
    //单例模式
    private static UserInformationPane instance;
    public UserInformationPane() {
        instance = this;
    }
    public static UserInformationPane getInstance() {
        return instance;
    }

    public Boolean isStranger =true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DragAndDrop();
        user=ChatController.getInstance().getChatHandler().getFindUserInformation();
        isStranger=ChatController.getInstance().getChatHandler().getStranger();

        YesBtn.setVisible(false);
        YesBtn.setDisable(true);
        NoBtn.setVisible(false);
        NoBtn.setDisable(true);

        userName.setText(user.getName());
        userID.setText(user.getUserID());
        sex.setText(user.getSex());
        Emali.setText(user.getEmail());
        number.setText(user.getPhone_number());
        onstatus.setText(user.getStatus().toString());
        sign_info.setText("    "+user.getSign_info());
        Image image=new Image("File:" +filePath+user.getUserID()+".jpg",140,140,false,true,true);
        avater.setImage(image);
        if(user.getStatus()== Status.OFFLINE|| !isStranger){
            addFriendBtn.setVisible(false);
            addFriendBtn.setDisable(true);
        }

    }

    private double xOffset;
    private double yOffset;
    public void DragAndDrop(){
        pane.setOnMousePressed(event -> {
            xOffset = ChatController.getUserInformationStage().getX() - event.getScreenX();
            yOffset = ChatController.getUserInformationStage().getY() - event.getScreenY();
            pane.setCursor(Cursor.CLOSED_HAND);
        });
        pane.setOnMouseDragged(event -> {
            ChatController.getUserInformationStage().setX(event.getScreenX() + xOffset);
            ChatController.getUserInformationStage().setY(event.getScreenY() + yOffset);
        });
        pane.setOnMouseReleased(event -> {
            pane.setCursor(Cursor.DEFAULT);
        });
    }

    /***
     * @Title:addFriendAct
     * @Description:直接封装消息发送给服务器，申请添加好友
     * @param actionEvent
     * @return void
     * @Author: lzq
     */
    public void addFriendAct(ActionEvent actionEvent) {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                Message message=new Message();
                message.setFlag(false);
                message.setType(MessageType.FriendApplication);
                message.setCount(Client.getInstance().getUser().getUserID());
                message.setMsg_content(user.getUserID());
                message.setUser(Client.getInstance().getUser());
                //加入服务器发送队列
                System.out.println("发送消息");
                Client.getInstance().sendMessage(message);
                LoginController.getInstance().showErrorDialog("发送完毕","已向对方发送好友申请");
            }
        });
        thread.setName("好友添加申请");
        thread.start();
    }
    public void OpenFriendApplicationMode(){

        addFriendBtn.setVisible(false);
        addFriendBtn.setDisable(true);
        System.out.println("OpenFriendApplicationMode");
        YesBtn.setVisible(true);
        YesBtn.setDisable(false);
        NoBtn.setVisible(true);
        NoBtn.setDisable(false);
    }

    public void close(ActionEvent actionEvent) {
        ChatController.getUserInformationStage().close();
    }

    public void agreeAddFriend(ActionEvent actionEvent) {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                Message message=new Message();
                message.setFlag(true);
                message.setType(MessageType.FriendApplication);
                message.setCount(user.getUserID());
                message.setMsg_content(Client.getInstance().getUser().getUserID());
                message.setUser(Client.getInstance().getUser());
                Client.getInstance().sendMessage(message);

                Client.getInstance().addFriend(user);
                ChatController.getInstance().updateList(user);
            }
        });
        thread.setName("好友添加申请");
        thread.start();
    }

    public void refuseAddFriend(ActionEvent actionEvent) {
    }
}
