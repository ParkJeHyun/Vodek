package ui;

import controller.ConvertWTT;
import controller.VideoController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015-10-17.
 */
public class MainController {
    @FXML BorderPane rootPane;
    @FXML ListView lvInputFileList;
    @FXML TextArea taFileInfo;
    @FXML TextField tfFilename;
    @FXML TextField tfFiletime;
    @FXML TextField tfFilesize;
    @FXML ImageView ivExit;
    @FXML ImageView ivMinimize;
    @FXML ImageView ivMaximize;
    @FXML ImageView ivPlay;
    @FXML ImageView ivAdd;
    @FXML ImageView ivMinus;
    @FXML TextArea taLog;

    private Stage stage;
    private double xOffset;
    private double yOffset;
    private boolean isMaxmize;
    private double nowWidth;
    private double nowHeight;
    private double nowLocateX;
    private double nowLocateY;
    private double defaltStageWidth;
    private double defaltStageHeight;

    private Light.Distant enterLight;
    private Light.Distant defaultLight;

    private ArrayList<File> fileList;

    private VideoController videoController;
    private ProgressController progressController;
    private ConvertWTT convertWTT;

    private Parent progressRoot;
    private Stage progressStage;
    private Scene progressScene;

    private static WaitEvent waiting;
    private static String progressEvent;

    @FXML public void initialize(){
        this.isMaxmize = false;
        waiting = new WaitEvent();
        settingRootPane();
        settingIvEvnet();

        if(this.progressController == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/ProgressBar.fxml"));
                this.progressRoot = loader.load();
                this.progressController = (ProgressController) loader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setProgressStage(){
        progressStage = new Stage();
        progressStage.initStyle(StageStyle.UNDECORATED);
        progressStage.setScene(new Scene(progressRoot));
        progressStage.setX(stage.getX() + stage.getWidth() / 2 - 200);
        progressStage.setY(stage.getY() + stage.getHeight() / 2 - 100);
        progressController.setStage(progressStage);
    }

    public void setStage(final Stage stage){
        this.stage = stage;
        this.defaltStageWidth = stage.getWidth();
        this.defaltStageHeight = stage.getHeight();
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

    public void setMaximizeEvent(){
        this.ivMaximize.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Light light = new Light.Distant();
                light.setColor(new Color(1.0, 1.0, 1.0, 0.6));
                ivMaximize.setEffect(new Lighting(light));
            }
        });
        this.ivMaximize.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivMaximize.setEffect(new Lighting(defaultLight));
            }
        });
        this.ivMaximize.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(isMaxmize){
                    isMaxmize = false;
                    stage.setX(nowLocateX);
                    stage.setY(nowLocateY);
                    stage.setWidth(nowWidth);
                    stage.setHeight(nowHeight);
                    stage.setMaximized(false);
                }
                else {
                    isMaxmize = true;
                    nowWidth = stage.getWidth();
                    nowHeight = stage.getHeight();
                    nowLocateX = stage.getX();
                    nowLocateY = stage.getY();

                    stage.setMaximized(true);
                }
                event.consume();
            }
        });
    }

    public void setMinimizeEvent(){
        this.ivMinimize.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Light light = new Light.Distant();
                light.setColor(new Color(1.0,1.0,1.0,0.6));
                ivMinimize.setEffect(new Lighting(light));
            }
        });
        this.ivMinimize.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivMinimize.setEffect(new Lighting(defaultLight));
            }
        });
        this.ivMinimize.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setIconified(true);
                event.consume();
            }
        });
    }

    public void settingIvEvnet() {
        this.enterLight = new Light.Distant();
        this.enterLight.setColor(new Color(0.0, 1.0, 0.0, 1.0));

        this.defaultLight = new Light.Distant();
        this.defaultLight.setColor(new Color(1.0, 1.0, 1.0, 1.0));

        this.ivPlay.setEffect(new Lighting(defaultLight));
        this.ivExit.setEffect(new Lighting(defaultLight));
        this.ivMaximize.setEffect(new Lighting(defaultLight));
        this.ivMinimize.setEffect(new Lighting(defaultLight));
        this.ivAdd.setEffect(new Lighting(defaultLight));
        this.ivMinus.setEffect(new Lighting(defaultLight));

        setAddEvent();
        setMinusEvent();
        setPlayEvent();
        setMinimizeEvent();
        setMaximizeEvent();
        setExitEvent();
    }

    private void settingRootPane(){
        rootPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });
        rootPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }
        });
    }

    public void setAddEvent(){
        this.ivAdd.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Light light = new Light.Distant();
                light.setColor(new Color(1.0,1.0,0.0,1.0));
                ivAdd.setEffect(new Lighting(light));
            }
        });
        this.ivAdd.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivAdd.setEffect(new Lighting(defaultLight));
            }
        });
        this.ivAdd.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ArrayList<File> addArrayList;
                List<File> addList;
                String path = System.getProperty("user.dir");
                File dir = new File(path);
                FileChooser filechooser = new FileChooser();
                filechooser.setTitle("Open Video Files");
                filechooser.setInitialDirectory(dir);
                setFileChooserExtensionFilter(filechooser);
                addList = filechooser.showOpenMultipleDialog(stage.getScene().getWindow());
                if(addList != null) {
                    addArrayList = new ArrayList<File>(addList);
                    if (fileList == null) {
                        fileList = addArrayList;
                    } else {
                        for (int i = 0; i < addArrayList.size(); i++) {
                            fileList.add(addArrayList.get(i));
                        }
                    }
                    setInputFileListView();
                }
                event.consume();
            }
        });
    }

    public void setMinusEvent(){
        this.ivMinus.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Light light = new Light.Distant();
                light.setColor(new Color(1.0,1.0,0.0,1.0));
                ivMinus.setEffect(new Lighting(light));
            }
        });
        this.ivMinus.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivMinus.setEffect(new Lighting(defaultLight));
            }
        });
        this.ivMinus.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                List<String> selectedList = lvInputFileList.getSelectionModel().getSelectedItems();
                tfFilename.setText("");
                tfFilesize.setText("");
                tfFiletime.setText("");
                for(int i=0;i<selectedList.size();i++){
                    int size = fileList.size();
                    for (int j =0;j<size;j++){
                        if(fileList.get(j).getName().equals(selectedList.get(i))){
                            fileList.remove(j);
                            break;
                        }
                    }
                }
                setInputFileListView();
                event.consume();
            }
        });
    }

    public void setPlayEvent(){
        this.ivPlay.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivPlay.setEffect(new Lighting(enterLight));
            }
        });
        this.ivPlay.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivPlay.setEffect(new Lighting(defaultLight));
            }
        });
        this.ivPlay.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                taLog.setText("");
                videoController = VideoController.getInstance();
                convertWTT = ConvertWTT.getInstance();
                if (fileList.size() != 0) {
                    callProgressbar();
                    progressController.setFileList(fileList);
                    progressController.doProgress();
                }
                event.consume();
            }
        });
    }

    public void callProgressbar(){
        if(progressStage==null) {
            setProgressStage();
        }

        progressStage.show();
    }

    private void setFileChooserExtensionFilter(FileChooser filechooser){
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Video Files","*.mp4","*.avi");
        filechooser.getExtensionFilters().add(filter);
    }

    public void setInputFileListView(){
        List<String> fileNameList = new ArrayList<String>();

        for(int i=0;i<fileList.size();i++){
//            this.inputFileList.add(new InputFile(fileList.get(i)));
            fileNameList.add(fileList.get(i).getName());
        }
        ObservableList<String> list = FXCollections.observableList(fileNameList);

        this.lvInputFileList.setEditable(false);
        this.lvInputFileList.setItems(list);
        this.lvInputFileList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectFileName = (String) lvInputFileList.getSelectionModel().getSelectedItem();
                boolean find = false;
                videoController = VideoController.getInstance();
                if (selectFileName != null) {
                    for (int j = 0; j < fileList.size(); j++) {
                        File listFile = fileList.get(j);
                        if (selectFileName.equals(listFile.getName())) {
                            String fileInfo = videoController.getVideoInfo(fileList.get(j));
                            String[] fileInfos = fileInfo.split("[#]");
                            tfFilename.setText(fileInfos[0]);
                            tfFilename.setAlignment(Pos.CENTER);
                            tfFilesize.setText(fileInfos[2]);
                            tfFilesize.setAlignment(Pos.CENTER);
                            tfFiletime.setText(fileInfos[1]);
                            tfFiletime.setAlignment(Pos.CENTER);
                            //taFileInfo.setText(videoController.getVideoInfo(inputFileList.get(j).getFile()));
                            find = true;
                            break;
                        }
                    }
                }
            }
        });
    }

    public void inputFileListVIewSelectEvent(ActionEvent e){
        List<String> selectFileNameList = lvInputFileList.getSelectionModel().getSelectedItems();
        boolean find = false;
        videoController = VideoController.getInstance();
        for(int i=0;i<selectFileNameList.size() || !find;i++){
            for(int j=0;j<fileList.size();i++){
                File listFile = fileList.get(j);
                if(selectFileNameList.get(i).equals(listFile.getName())){
                    taFileInfo.setEditable(false);
                    taFileInfo.setText(videoController.getVideoInfo(listFile));
                    return;
//                    find = true;
//                    break;
                }
            }
        }
    }

    public static void eventFromProgress(String event){
        progressEvent = event;
        waiting.run();
    }

    private class WaitEvent implements Runnable{

        @Override
        public void run() {
            if(progressEvent.contains("start")){
                taLog.appendText(progressEvent.split("[#]")[0] + " Start\n");
            }
            else if(progressEvent.contains("complete")){
                taLog.appendText(progressEvent.split("[#]")[0] + " Complete!\n");
            }
            else if(progressEvent.contains("stop")){
                taLog.appendText("STOP\n");
            }
        }
    }

}
