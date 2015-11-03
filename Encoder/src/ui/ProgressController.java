package ui;

import controller.VideoController;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015-10-28.
 */
public class ProgressController {
    @FXML private ImageView ivExit;
    @FXML private Label lbCurrent;
    @FXML private ProgressBar pbCurrent;
    @FXML private ProgressBar pbTotal;

    private Light.Distant defaultLight;

    private Stage stage;

    private CurrentProgress currentWorkThread;
    private ArrayList<File> inputFIleList;

    private VideoController videoController;

    private Thread progressThread;

    @FXML public void initialize(){
        setExitEvent();
        videoController = VideoController.getInstance();
        currentWorkThread = new CurrentProgress();
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void setFileList(ArrayList<File> inputFIleList){
        this.inputFIleList = inputFIleList;
    }

    public void doProgress(){
        progressThread = new Thread(this.currentWorkThread);
        progressThread.start();
    }

    public void setExitEvent(){
        this.ivExit.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Light light = new Light.Distant();
                light.setColor(new Color(0.5,0.0,0.0,1.0));
                ivExit.setEffect(new Lighting(light));
            }
        });
        this.ivExit.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Light light = new Light.Distant();
                light.setColor(new Color(1.0, 1.0, 1.0, 1.0));
                ivExit.setEffect(new Lighting(defaultLight));
            }
        });
        this.ivExit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.close();
                event.consume();
            }
        });
    }

    public void setCurrentName(final String fileName){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lbCurrent.setText(fileName);
            }
        });
    }

    public void exit(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.close();
            }
        });
    }


    public void stopEvent(ActionEvent event)
    {
        progressThread.interrupt();
    }

    private class CurrentProgress implements Runnable {
        DoubleProperty currentProperty;
        DoubleProperty totalProperty;
        boolean stop = false;
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    totalProperty = new SimpleDoubleProperty(inputFIleList.size());
                    pbTotal.progressProperty().unbind();
                    pbTotal.progressProperty().bind(totalProperty);
                    totalProperty.set(0.0);
                    for (int i = 0; i < inputFIleList.size(); i++) {
                        File currentFIle = inputFIleList.get(i);

                        setCurrentName(currentFIle.getName() + " (" + (i + 1) + "/" + inputFIleList.size() + ")");

                        int divideFileNum = videoController.extractAudio(currentFIle);

                        currentProperty = new SimpleDoubleProperty(divideFileNum);
                        MainController.eventFromProgress(currentFIle.getName() + "#start");
                        pbCurrent.progressProperty().unbind();
                        pbCurrent.progressProperty().bind(currentProperty);
                        currentProperty.set(0.0);

                        for (int k = 1; k < divideFileNum + 1; k++) {
                            Thread.sleep(50);
                            currentProperty.set((double) k / (double) divideFileNum);
                        }

                        MainController.eventFromProgress(currentFIle.getName() + "#complete");
                        totalProperty.set((double) (i + 1) / (double) inputFIleList.size());
                    }
                    Thread.sleep(100);
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                exit();
            }
        }

    }

}
