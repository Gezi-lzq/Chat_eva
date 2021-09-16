package controller.cell;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import messages.User;

import java.io.IOException;
import java.net.URL;

import static config.Config.filePath;

public class UserCellRenderer implements Callback<ListView<User>, ListCell<User>> {
    @Override
    public ListCell<User> call(ListView<User> param) {
        ListCell<User> cell = new ListCell<User>(){

            @Override
            protected void updateItem(User user, boolean bln) {
                super.updateItem(user, bln);
                setGraphic(null);
                setText(null);

                if (user != null) {
                    FXMLLoader fx = new FXMLLoader();
                    URL resource = getClass().getClassLoader().getResource("views/userCell.fxml");
                    if (resource == null)
                        throw new RuntimeException("资源文件找不到");
                    fx.setLocation(resource);
                    AnchorPane an = null;
                    try {
                        an = (AnchorPane)fx.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //头像
                    ImageView imageView = (ImageView) an.lookup("#useravater");
                    Image image = new Image("File:" + filePath + user.getUserID()+".jpg", 55, 55, false, true, true);
                    imageView.setImage(image);

                    //用户名
                    Label userNameLabel = (Label) an.lookup("#userNameLabel");
                    userNameLabel.setText(user.getName());
                    //ID
                    Label userIDLabel = (Label) an.lookup("#userIDLabel");
                    userIDLabel.setText("ID:" + user.getUserID());

                    //状态背景色
                    switch (user.getStatus()) {
                        case INVISIBLE:
                            an.setStyle("-fx-background-color: #717774; -fx-background-radius: 8;");
                            break;
                        case ONLINE:
                            an.setStyle("-fx-background-color: #bfbed3; -fx-background-radius: 8;");
                            break;
                        case BUSY:
                            an.setStyle("-fx-background-color: #ffd7d7; -fx-background-radius: 8;");
                            break;
                        case OFFLINE:
                            an.setStyle("-fx-background-color: #717774; -fx-background-radius: 8;");
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + user.getStatus());
                    }

                    setGraphic(an);

                }
            }
        };
        return cell;
    }
}
