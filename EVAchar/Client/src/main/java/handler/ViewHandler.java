package handler;

import controller.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ViewHandler {
    static Logger logger = LoggerFactory.getLogger(ViewHandler.class);


    public static Boolean LoadChatWindow(){
        FXMLLoader fmxlLoader = new FXMLLoader(ViewHandler.class.getResource("/views/ChatWindow.fxml"));
        Parent window=null;
        try {
            window = (Pane) fmxlLoader.load();
        } catch (IOException e) {
            logger.info("LoadChatWindow Exception");
            return false;
        }
        Scene scene=new Scene(window);
        LoginController.getInstance().setScene(scene);
        System.out.println("Load ChatWindow....");
        return true;
    }


}
