package controller;

import client.Client;
import client.StartClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import messages.Message;
import messages.MessageType;
import util.MD5Util;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class LoginController implements Initializable {

    public Label AccountNumber;
    public Label password;
    public PasswordField passwordTextfield;
    public TextField accountTextfield;
    public BorderPane borderPane;
    public ImageView face;
    public Button closeButton;
    public Button registerButton;
    public Button loginButton;
    public Button MinimizedBtn;

    private static Client client;

    public static ChatController con;
    private Scene scene;

    //单例模式
    private static LoginController instance;
    public LoginController() {
        instance = this;
    }
    public static LoginController getInstance() {
        return instance;
    }

    public void InitClient(){
        //显示层——控制层——服务层
        client =Client.getInstance();
        client.StartLoginConnect();//开启一个登录连接的线程
    }


    private double xOffset;
    private double yOffset;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //启动客户端
        this.InitClient();

        //拖放功能
        borderPane.setOnMousePressed(event -> {
            xOffset = StartClient.getPrimaryStage().getX() - event.getScreenX();
            yOffset = StartClient.getPrimaryStage().getY() - event.getScreenY();
            borderPane.setCursor(Cursor.CLOSED_HAND);
        });
        borderPane.setOnMouseDragged(event -> {
            StartClient.getPrimaryStage().setX(event.getScreenX() + xOffset);
            StartClient.getPrimaryStage().setY(event.getScreenY() + yOffset);

        });
        borderPane.setOnMouseReleased(event -> {
            borderPane.setCursor(Cursor.DEFAULT);
        });

        //限制账号框只能输入数字
        accountTextfield.setTextFormatter(new TextFormatter<String>(new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change change) {
                String value = change.getText();
                if(value.matches("[0-9]*")){
                    return change;
                }
                return null;
            }
        }));
    }

    private Message Loginmessage;
    public Message getLoginmessage() {
        return Loginmessage;
    }
    public void loginButtonAction() throws IOException {

        //封装登录消息
        String account = accountTextfield.getText();
        String password = passwordTextfield.getText();
        Loginmessage=new Message();
        Loginmessage.setType(MessageType.Login);
        Loginmessage.setCount(account);
        //如果不另开线程，就会阻塞在这里
        //采用线程池，防止多次点击导致开了多个线程，占用资源
        String pw=MD5Util.MD5(password);
        Loginmessage.setMsg_content(pw);//封装登录信息，并对密码进行MD5加密
        new Thread(new Runnable() {
            @Override
            public void run() {
                Client.getInstance().checkClientConnect();
                Client.getInstance().sendMessage(Loginmessage);
            }
        }).start();
    }


    public void showErrorDialog(String HeaderText,String ContentText) {
        Platform.runLater(()-> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setHeaderText(HeaderText);
            alert.setContentText(ContentText);
            alert.showAndWait();
        });
    }


    public void closeSystem(){



    }

    protected static Stage registerStage;
    public static Stage getSecondaryStage() {
        return registerStage;
    }

    public void registerButtonAction(ActionEvent actionEvent) {
        Thread registThread = new Thread() {
            @Override
            public void run() {
                try {
                    //略微暂停一下，等按钮动态效果暂停后出现，观感更好
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(new Runnable() {
                    public void run() {
                        Parent root = null;
                        try {
                            root = FXMLLoader.load(getClass().getResource("/views/RegisterWindow.fxml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        registerStage=new Stage();
                        registerStage.setTitle("注册");
                        registerStage.initStyle(StageStyle.UNDECORATED);
                        Scene mainScene = new Scene(root);
                        registerStage.setResizable(false);
                        registerStage.setScene(mainScene);
                        registerStage.initOwner(StartClient.getPrimaryStage());
                        registerStage.initModality(Modality.WINDOW_MODAL);
                        registerStage.show();
                    }
                });
            }
        };
        registThread.start();
    }

    public void showScene() {
        Platform.runLater(() -> {
            Stage stage = (Stage)closeButton.getScene().getWindow();
            stage.hide();
            stage.setResizable(false);
            stage.setWidth(924.0);
            stage.setHeight(646);

            stage.setOnCloseRequest((WindowEvent e) -> {
                Platform.exit();
                System.exit(0);
            });

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            stage.setScene(this.scene);
            stage.centerOnScreen();
            stage.show();
        });
    }

    public void minimizeWindow(ActionEvent actionEvent) {
        StartClient.getPrimaryStage().setIconified(true);
    }

    public static void setCon(ChatController con) {
        LoginController.con = con;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}

