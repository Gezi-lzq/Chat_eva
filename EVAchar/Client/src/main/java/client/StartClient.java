package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

public class StartClient extends Application {
    private static Stage primaryStageObj;
    public static Stage getPrimaryStage() {
        return primaryStageObj;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //JavaFX应用程序的主类需要继承自application.Application类。
        // start()方法是所有JavaFX应用程序的入口。
        primaryStageObj = primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("CHAT");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("/views/resources/EVAchat.jpg"));
        //使用FXMLLoader加载FXML文件（加载FXML源文件并返回其代表的图形界面元素）
        Parent root = FXMLLoader.load(getClass().getResource("/views/LoginWindow.fxml"));
        Scene mainScene = new Scene(root, 350, 420);
        mainScene.setRoot(root);
        primaryStage.setScene(mainScene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> Platform.exit());
    }
}
