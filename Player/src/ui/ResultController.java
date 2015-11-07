package ui;

import controller.ScriptData;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import sun.applet.Main;

import java.io.IOException;


/**
 * Created by Administrator on 2015-10-26.
 */
public class ResultController {
    @FXML private Label lbTime;
    @FXML private Label lbText;
    @FXML private ImageView ivCapture;
    @FXML private GridPane rootPane;

    public ResultController()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/ResultList.fxml"));
        fxmlLoader.setController(this);
        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setInfo(ScriptData data)
    {
        lbTime.setText(data.getTimeString());
        lbTime.setUnderline(false);
        lbText.setText(data.getText());
        lbText.setUnderline(false);
        setEventText();
    }

    public void setEventText(){
        lbTime.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MainController.getOtherControllerEvent("Move#"+lbTime.getText());
            }
        });
        lbText.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MainController.getOtherControllerEvent("Move#"+lbTime.getText());
            }
        });
    }

    public GridPane getGridPane(){
        return rootPane;
    }

}
