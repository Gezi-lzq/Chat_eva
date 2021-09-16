package controller;

import client.Client;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import messages.Message;
import messages.MessageType;
import messages.Status;
import messages.User;
import util.Base64Util;
import util.MD5Util;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {


    public TextField userName;
    public PasswordField userPassword;
    public AnchorPane Apone;
    public ImageView backimg;
    public JFXTextField email;
    public JFXTextField phoneNumber;
    public JFXButton closebtn;
    public JFXButton registebBtn;
    public BorderPane Pane;
    public JFXButton avatarbtn;
    public JFXComboBox<Label> sexField;
    public ImageView avatarimg;
    public JFXTextArea sign_infoText;

    private String name;
    private String password;
    private String useremail;
    private String number;
    private String sex;
    private String sign_info;
    private String avatar;


    public void registerButtonAction(ActionEvent actionEvent) {
//        LoginHandler.resetListenFlag(true);

        name=userName.getText();
        password=userPassword.getText();
        useremail=email.getText();
        number=phoneNumber.getText();
        sign_info=sign_infoText.getText();
        //检查信息填写程度
        if(!name.equals("") && !password.equals("") && !useremail.equals("") &&sex!=null && avatar!=null){
            Message message=new Message();
            message.setUser(new User(name,avatar, Status.ONLINE,sex,number,useremail,sign_info));
            message.setType(MessageType.Register);
            String MD5password = MD5Util.MD5(password);
            message.setMsg_content(MD5password);

            Client.getInstance().sendMessage(message);
        }else {
            Platform.runLater(()-> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning!");
                alert.setHeaderText("注册信息未填写完整");
                alert.setContentText("请确保信息正确填写完成，然后再进行注册");
                alert.showAndWait();
            });
        }

//        //几种弹窗的写法测试
////        Alert alert=new Alert(Alert.AlertType.INFORMATION);
////        alert.setContentText("注册成功");
////        alert.show();
//
////          new DialogBuilder(password1).setTitle("提示").setMessage("注册成功").setNegativeBtn("确定").create();
//
////        JFXAlert<String> alert1=new JFXAlert<>();
////        FlowPane Db =new FlowPane();
////        Button button1= new Button("Button1");
////        Db.getChildren().add(button1);
////        alert1.setContent(Db);
////        alert1.show();
//
    }

    private double xOffset;
    private double yOffset;
    public void initialize(URL location, ResourceBundle resources) {
        //拖放功能
        Pane.setOnMousePressed(event -> {
            xOffset = LoginController.getSecondaryStage().getX() - event.getScreenX();
            yOffset = LoginController.getSecondaryStage().getY() - event.getScreenY();
            Pane.setCursor(Cursor.CLOSED_HAND);
        });

        Pane.setOnMouseDragged(event -> {
            LoginController.getSecondaryStage().setX(event.getScreenX() + xOffset);
            LoginController.getSecondaryStage().setY(event.getScreenY() + yOffset);
        });
        Pane.setOnMouseReleased(event -> {
            Pane.setCursor(Cursor.DEFAULT);
        });

        initComboBox();
    }

    public void initComboBox(){
        sexField.getItems().addAll(new Label("男"),new Label("女"));
        sexField.setConverter(new StringConverter<Label>() {
            @Override
            public String toString(Label object) {
                return object==null? "" : object.getText();
            }

            @Override
            public Label fromString(String string) {
                return new Label(string);
            }
        });

        sexField.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Label>() {
            @Override
            public void changed(ObservableValue<? extends Label> observable, Label oldValue, Label newValue) {
                sex=newValue.getText();
            }
        });
    }

    public void closeAction(ActionEvent actionEvent) {
        LoginController.getSecondaryStage().close();
    }

    public void avatarChoice(ActionEvent actionEvent) {
        FileChooser fc=new FileChooser();
        fc.setTitle("设置头像图片");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("图片类型","*.png","*.jpg","*.bmp")
        );
        File file=fc.showOpenDialog(LoginController.getSecondaryStage());
        if(file!=null){
            Thread thread = new Thread() {
                @Override
                public void run() {
                    avatar = Base64Util.getImgStr(file.getAbsolutePath());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FileInputStream fis=new FileInputStream(file);
                                Image image=new Image("File:"+file.getAbsolutePath(),65,65,false,true,true);
                                avatarimg.setImage(image);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
            thread.setName("头像选择");
            thread.start();
        }

    }
}

