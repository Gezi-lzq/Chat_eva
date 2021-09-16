package controller.cell;

import client.Client;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;
import messages.ChatMsg;
import util.Base64Util;

import java.util.Date;
import java.util.Random;

import static config.Config.filePath;

/**
 * @ClassName MsgCellRenderer
 * @Description 聊天消息体
 * @Author lzq
 * @Date 2021/2/24 17:03
 */
public class MsgCellRenderer implements Callback<ListView<ChatMsg>, ListCell<ChatMsg>> {
    private int ChatMsgType_Text=0;
    private int ChatMsgType_Image=1;

    @Override
    public ListCell<ChatMsg> call(ListView<ChatMsg> param) {
        ListCell<ChatMsg> cell = new ListCell<ChatMsg>(){
            @Override
            protected void updateItem(ChatMsg message, boolean bln) {
                super.updateItem(message, bln);
                setGraphic(null);
                setText(null);
                if(message!=null){
                    HBox hBox = new HBox();

                    Image image=new Image("File:" +filePath+message.getFrom_ID()+".jpg",29, 29, false, false, true);
                    ImageView headImage=new ImageView(image);
                    hBox.setSpacing(14);
                    if(message.getType()==ChatMsgType_Text){
                        //用于撑大气泡框的标签
                        Label space1=new Label("");
                        space1.setFont(new Font(24));
                        Label space2=new Label("");
                        space2.setFont(new Font(24));
                        //消息体
                        HBox messageBody=new HBox();
                        Label label=new Label();
                        label.setText(message.getMsg_content());
                        label.setFont(new Font(19));
                        label.autosize();
                        if(message.getFrom_ID()== Client.getInstance().getUser().getUserID()){
                            //如果是本人发送的消息，蓝色白字右对齐
                            label.setTextFill(Color.web("#f1f2f6"));
                            messageBody.getChildren().addAll(space1,label,space2);
                            messageBody.setAlignment(Pos.BASELINE_RIGHT);
                            messageBody.setSpacing(14);
                            messageBody.setStyle("-fx-background-color: #40a9ff; -fx-background-radius: 8;");
                            messageBody.setOpacity(0.85);
                            hBox.getChildren().addAll(messageBody,headImage);
                            hBox.setAlignment(Pos.BASELINE_RIGHT);
                        }else {
                            //如果是对方发送的消息，白底黑左对齐
                            label.setTextFill(Color.web("#131100"));
                            messageBody.getChildren().addAll(space1,label,space2);
                            messageBody.setAlignment(Pos.BASELINE_RIGHT);
                            messageBody.setSpacing(14);
                            messageBody.setStyle("-fx-background-color: #fef8de; -fx-background-radius: 8;");
                            messageBody.setOpacity(0.85);
                            hBox.getChildren().addAll(headImage,messageBody);
                            hBox.setAlignment(Pos.BASELINE_LEFT);
                        }
                    }else if(message.getType()==ChatMsgType_Image){
                        //用于撑大气泡框的标签
                        Label space1=new Label("");
                        space1.setFont(new Font(24));
                        Label space2=new Label("");
                        space2.setFont(new Font(24));
                        //消息体
                        HBox messageBody=new HBox();
                        ImageView imageView=new ImageView();
                        Random random=new Random();
                        String imgpath=filePath+message.getFrom_ID()+random.nextLong()+".jpg";
                        Base64Util.base64ChangeImage(message.getMsg_content(),imgpath);
                        Image image1=new Image("File:"+imgpath,300, 300, true, true, true);
                        imageView.setImage(image1);

                        if(message.getFrom_ID()== Client.getInstance().getUser().getUserID()){
                            //如果是本人发送的消息，蓝色白字右对齐
                            messageBody.getChildren().addAll(space1,imageView,space2);
                            messageBody.setAlignment(Pos.BASELINE_RIGHT);
                            messageBody.setSpacing(14);
                            messageBody.setStyle("-fx-background-color: #40a9ff; -fx-background-radius: 8;");
                            messageBody.setOpacity(0.85);
                            hBox.getChildren().addAll(messageBody,headImage);
                            hBox.setAlignment(Pos.BASELINE_RIGHT);
                        }else {
                            //如果是对方发送的消息，白底黑左对齐
                            messageBody.getChildren().addAll(space1,imageView,space2);
                            messageBody.setAlignment(Pos.BASELINE_RIGHT);
                            messageBody.setSpacing(14);
                            messageBody.setStyle("-fx-background-color: #fef8de; -fx-background-radius: 8;");
                            messageBody.setOpacity(0.85);
                            hBox.getChildren().addAll(headImage,messageBody);
                            hBox.setAlignment(Pos.BASELINE_LEFT);
                        }
                    }
                    hBox.autosize();

                    setGraphic(hBox);
                }
            }
        };
        return cell;
    }
}
