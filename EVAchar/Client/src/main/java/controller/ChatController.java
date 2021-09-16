package controller;


import client.Client;
import client.StartClient;
import com.jfoenix.controls.*;
import controller.cell.MsgCellRenderer;
import controller.cell.UserCellRenderer;
import handler.ChatHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import messages.ChatMsg;
import messages.Status;
import messages.User;
import util.Base64Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


import static config.Config.filePath;

public class ChatController implements Initializable {

    //单例模式
    private static ChatController instance;
    public JFXTextArea messageBox;

    private ChatHandler chatHandler;

    public ChatController() {
        instance = this;
    }
    public static ChatController getInstance() {
        return instance;
    }

    public JFXButton Findbtn;
    public ImageView avater;
    public JFXButton personaldata;
    public JFXButton imagBtn;
    public JFXButton openFilebtn;
    public JFXButton Sendbutton;
    public JFXButton Messagelogging;
    public Label userName;
    public ListView OnlineList;
    public ListView FriendList;
    public JFXComboBox Onlinestatus;
    public JFXTextField userFindField;
    public JFXButton closebtn;
    public JFXButton contractWindowbtn;
    public AnchorPane Pane;
    public ListView messageList;
    public SplitPane SplitPane;

    private User chatuser=Client.getInstance().getUser();//当前聊天对象(初始默认为自己)

    private double xOffset;
    private double yOffset;
    private ObservableList<User> onlineUser;
    private ObservableList<User> users;
    private ObservableList<ChatMsg> msgList;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //界面拖动
        DragAndDrop();

        messageList.setCellFactory(new MsgCellRenderer());

        Platform.runLater(() -> {
            //载入用户头像
            Image image=new Image("File:"+filePath+ Client.getInstance().getUser().getUserID()+".jpg",55,55,false,true,true);
            avater.setImage(image);
            //载入好友列表
            users = FXCollections.observableList(Client.getInstance().getUserArrayList());
            FriendList.setItems(users);
            FriendList.setCellFactory(new UserCellRenderer());
            //载入在线好友列表
            ArrayList<User> UserList=Client.getInstance().getUserArrayList();
            chatHandler=new ChatHandler(UserList);
            ArrayList<User> onlineUsers=new ArrayList<>();
            for(int i=0;i<UserList.size();i++){
                System.out.println(UserList.get(i).getName()+UserList.get(i).getStatus());
                if( UserList.get(i).getStatus()!=Status.OFFLINE){
                    onlineUsers.add(UserList.get(i));
                }
            }
            onlineUser=FXCollections.observableList(onlineUsers);
            OnlineList.setItems(onlineUser);
            OnlineList.setCellFactory(new UserCellRenderer());
        });
        SelectionChatUser();

//        //设置聊天框监听 键盘ENTER触发 消息发送按钮
//        messageBox.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
//            if (ke.getCode().equals(KeyCode.ENTER)) {
//                SentMsg();
//                ke.consume();
//            }
//        });
    }
    /***
     * @Title:DragAndDrop
     * @Description:实现窗口拖拽
     * @Author: lzq on 2021/2/24 16:18
     */
    public void DragAndDrop(){
        SplitPane.setOnMousePressed(event -> {
            xOffset = StartClient.getPrimaryStage().getX() - event.getScreenX();
            yOffset = StartClient.getPrimaryStage().getY() - event.getScreenY();
            SplitPane.setCursor(Cursor.CLOSED_HAND);
        });
        SplitPane.setOnMouseDragged(event -> {
            StartClient.getPrimaryStage().setX(event.getScreenX() + xOffset);
            StartClient.getPrimaryStage().setY(event.getScreenY() + yOffset);
        });
        SplitPane.setOnMouseReleased(event -> {
            SplitPane.setCursor(Cursor.DEFAULT);
        });
    }



    /***
     * @Title:updateList
     * @Description:更新窗口的用户列表
     * @param user  在线状态改变的用户
     * @return void
     * @Author: lzq on 2021/2/24 16:21
     */
    public void updateList(User user){
        Platform.runLater(() -> {
            if(user.getStatus()!=Status.OFFLINE)
                OnlineList.getItems().add(user);
            else {
                ObservableList<User> list=OnlineList.getItems();
                for(int i=0;i<list.size();i++){
                    if(list.get(i).getUserID().equals(user.getUserID())){
                        list.remove(i);
                    }
                }
                if(chatuser.getUserID()==user.getUserID())
                    chatuser=Client.getInstance().getUser();
            }
            ObservableList<User> list=FriendList.getItems();
            int i=0;
            for(i=0;i<list.size();i++){
                if(list.get(i).getUserID().equals(user.getUserID())){
                    list.set(i,user);
                }
            }
//            if(i>=list.size()){
//                list.add(user);
//            }
        });
    }


    /***
     * @Title:SelectionChatUser
     * @Description: 监听列表上用户所选择的聊天对象
     * @Author: lzq
     */
    public void SelectionChatUser(){
        OnlineList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                Platform.runLater(() -> {
                    chatuser=(User)newValue;
                    userName.setText(chatuser.getName());
                    loadUserChatMessage();
                });
            }
        });
    }



    /***
     * @Title:loadUserChatMessage
     * @Description:载入该聊天对象在本地具有的聊天记录
     * @Author: lzq
     */
    public void loadUserChatMessage(){
        chatHandler.loadChatPaneMsg(chatuser.getUserID());
    }

    /***
     * @Title:updateChatWindow
     * @Description:更新窗口的聊天列表
     * @param List
     * @return void
     * @Author: lzq
     */
    public void updateChatWindow(ObservableList<ChatMsg> List){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                messageList.setItems(List);
            }
        });
    }

    /***
     * @Title:SentMsg
     * @Description:发送按钮_发送聊天消息
     * @Author: lzq
     */
    public void SentMsg() {
        String msg = messageBox.getText();
        if (!messageBox.getText().isEmpty()&&chatuser!=null) {
            System.out.println("发送消息");
            //封装聊天消息
            ChatMsg chatMsg=new ChatMsg();
            chatMsg.setType(0);
            chatMsg.setFrom_ID(Client.getInstance().getUser().getUserID());
            chatMsg.setTo_ID(chatuser.getUserID());
            chatMsg.setMsg_content(messageBox.getText());

            System.out.println("执行ClientAddUserMsg");
            chatHandler.ClientAddUserMsg(chatMsg);

            chatHandler.SendUserMsg(chatMsg);

            messageBox.clear();
        }
    }



    public void openFileLocation(ActionEvent actionEvent) {

    }

    /***
     * @Title:Findusers
     * @Description:申请获得某用户信息（若为本用户好友，则不向服务器申请）
     * @param actionEvent
     * @return void
     * @Author: lzq
     */
    public void Findusers(ActionEvent actionEvent) {
        String userId=userFindField.getText();
        if(userId.equals(Client.getInstance().getUser().getUserID())){
            showUserInformation(Client.getInstance().getUser());
            chatHandler.setStranger(false);
        }else {
            User user=null;
            user=Client.getInstance().getFriendUser(userId);
            if(user!=null){
                chatHandler.setStranger(false);
                showUserInformation(user);
            }else{
                chatHandler.sendUserInfoApplication(userId);
            }
        }
    }

    protected static Stage UserInformationStage;
    public static Stage getUserInformationStage() {
        return UserInformationStage;
    }
    public void showUserInformation(User user){
        Thread UserInformationThread = new Thread() {
            @Override
            public void run() {
                if(user!=null){
                    chatHandler.setFindUserInformation(user);
                    try {
                        //略微暂停一下，等按钮动态效果暂停后出现，观感更好
                        Thread.sleep(700);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(new Runnable() {
                        public void run() {
                            Parent root = null;
                            try {
                                root=FXMLLoader.load(getClass().getResource("/views/UserInformation.fxml"));
                                UserInformationStage=new Stage();
                                UserInformationStage.initStyle(StageStyle.TRANSPARENT);
                                Scene mainScene = new Scene(root,543,372);
                                mainScene.setFill(null);
                                UserInformationStage.setResizable(false);
                                UserInformationStage.setScene(mainScene);
                                UserInformationStage.initOwner(StartClient.getPrimaryStage());
                                UserInformationStage.initModality(Modality.WINDOW_MODAL);
                                UserInformationStage.setOpacity(0.92);
                                UserInformationStage.show();
                                System.out.println("启动信息面板");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            LoginController.getInstance().showErrorDialog("未知","未查询到该账号信息");
                        }
                    });
                }
            }
        };
        UserInformationThread.start();
    }

    public void OpenPersonaldata(ActionEvent actionEvent) {
        showUserInformation(Client.getInstance().getUser());
        chatHandler.setStranger(false);
    }


    public void sendImag(ActionEvent actionEvent) {
        ImageView img=new ImageView();
        FileChooser fc=new FileChooser();
        fc.setTitle("选择图片");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("图片类型","*.png","*.jpg","*.bmp")
        );
        File file=fc.showOpenDialog(LoginController.getSecondaryStage());
        if(file!=null){
            Thread thread = new Thread() {
                @Override
                public void run() {
                    String imgcontent = Base64Util.getImgStr(file.getAbsolutePath());
                    System.out.println("发送图片消息");
                    //封装聊天消息
                    ChatMsg chatMsg=new ChatMsg();
                    chatMsg.setType(1);
                    chatMsg.setFrom_ID(Client.getInstance().getUser().getUserID());
                    chatMsg.setTo_ID(chatuser.getUserID());
                    chatMsg.setMsg_content(imgcontent);

                    System.out.println("执行ClientAddUserMsg");
                    chatHandler.ClientAddUserMsg(chatMsg);

                    chatHandler.SendUserMsg(chatMsg);

                }
            };
            thread.setName("头像选择");
            thread.start();
        }

    }




    public ChatHandler getChatHandler() {
        return chatHandler;
    }
    /***
     * @Title:MessageloggingAction
     * @Description:准备查看消息记录面板
     * @param actionEvent
     * @return void
     * @Author: lzq
     */
    public void MessageloggingAction(ActionEvent actionEvent) {
        Task<Void> task=new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                chatHandler.sendChatHistoryApplication(chatuser.getUserID());
                return null;
            }
        };
        new Thread(task).start();
    }

    protected static Stage MessageLogStage;
    public static Stage getMessageLogStage() {
        return MessageLogStage;
    }

    public void OpenMessageLogPane(){
        Thread MessageLogThread = new Thread() {
            @Override
            public void run() {
                try {
                    //略微暂停一下，等按钮动态效果暂停后出现，观感更好
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(new Runnable() {
                    public void run() {
                        Parent root = null;
                        try {
                            root=FXMLLoader.load(getClass().getResource("/views/ChatHistoryWindow.fxml"));

                            MessageLogStage=new Stage();
                            MessageLogStage.initStyle(StageStyle.TRANSPARENT);
                            Scene mainScene = new Scene(root);
                            mainScene.setFill(null);
                            MessageLogStage.setResizable(false);
                            MessageLogStage.setScene(mainScene);
                            MessageLogStage.initOwner(StartClient.getPrimaryStage());
                            MessageLogStage.initModality(Modality.WINDOW_MODAL);
                            MessageLogStage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        MessageLogThread.start();
    }

    public void OnlinestatusAct(ActionEvent actionEvent) {
    }


    public void closeWindow(ActionEvent actionEvent) {
        try {
            Client.getInstance().requestDisconnection(true);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        closeClient();
    }
    public void closeClient(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Platform.exit();
                System.exit(0);
            }
        });
    }
    public void contractWindow(ActionEvent actionEvent) {
        StartClient.getPrimaryStage().setIconified(true);
    }
}

