package controller;

import com.jfoenix.controls.JFXButton;
import controller.cell.MsgCellRenderer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.ListView;
import messages.ChatMsg;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * @ClassName ChatHistoryController
 * @Description TODO
 * @Author lzq
 * @Date 2021/2/25 20:10
 */
public class ChatHistoryController implements Initializable {
    //单例模式
    private static ChatHistoryController instance;
    public ChatHistoryController() {
        instance = this;
    }
    public static ChatHistoryController getInstance() {
        return instance;
    }

    public ListView chatlogList;
    public JFXButton closeBtn;

    private ArrayList<ChatMsg> HistoryMessageList;
    private ObservableList<ChatMsg> messageList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HistoryMessageList =new ArrayList<>();
        DragAndDrop();
        Platform.runLater(() -> {
            ArrayList<ChatMsg> listResource = ChatController.getInstance().getChatHandler().getChatMsgList();
            for (ChatMsg value : listResource) {
                HistoryMessageList.add(value);
            }
            messageList = FXCollections.observableList(HistoryMessageList);

            chatlogList.setCellFactory(new MsgCellRenderer());
            chatlogList.setItems(messageList);
        });
    }

    private double xOffset;
    private double yOffset;
    public void DragAndDrop(){
        chatlogList.setOnMousePressed(event -> {
            xOffset = ChatController.getMessageLogStage().getX() - event.getScreenX();
            yOffset = ChatController.getMessageLogStage().getY() - event.getScreenY();
            chatlogList.setCursor(Cursor.CLOSED_HAND);
        });
        chatlogList.setOnMouseDragged(event -> {
            ChatController.getMessageLogStage().setX(event.getScreenX() + xOffset);
            ChatController.getMessageLogStage().setY(event.getScreenY() + yOffset);
        });
        chatlogList.setOnMouseReleased(event -> {
            chatlogList.setCursor(Cursor.DEFAULT);
        });
    }

    public void close(ActionEvent actionEvent) {
        ChatController.getMessageLogStage().close();
    }

}
