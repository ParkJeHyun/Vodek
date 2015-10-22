import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.MainController;

public class PlayerMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/Player.fxml"));
        Parent root = (Parent)loader.load();
        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.setResizable(true);
        Scene scene = new Scene(root,600,400, Color.BLACK);
        MainController controller = (MainController)loader.getController();

        controller.setStage(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
