/**
 * Created by Administrator on 2015-10-17.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.MainController;

import java.io.IOException;

public class EncoderMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/EncoderXml.fxml"));
            Parent root = (Parent) loader.load();
            primaryStage.initStyle(StageStyle.UNDECORATED);

            primaryStage.setResizable(true);
            Scene scene = new Scene(root, 1024, 600, Color.BLACK);
            MainController controller = (MainController) loader.getController();

            controller.setStage(primaryStage);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
