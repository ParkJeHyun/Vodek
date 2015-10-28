package ui;

import controller.ScriptData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.IOException;


/**
 * Created by Administrator on 2015-10-26.
 */
public class ResultController {
    @FXML private TextField tfTime;
    @FXML private TextField tfText;
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
        tfTime.setText(data.getTimeString());
        tfText.setText(data.getText());
    }

    public GridPane getGridPane(){
        return rootPane;
    }

}
