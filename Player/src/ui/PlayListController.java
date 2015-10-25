package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2015-10-20.
 */
public class PlayListController {
    //PlayList
    @FXML private ListView lvPlayList;
    @FXML private Button btExit;
    @FXML private BorderPane playList;
    @FXML private ImageView ivAdd;
    @FXML private ImageView ivExit;
    @FXML private ImageView ivMinus;

    private Light.Distant enterLight;
    private Light.Distant defaultLight;

    private double xOffset;
    private double yOffset;
    private Stage stage;

    private final int ROW_HEIGHT = 21;


    @FXML public void initialize() {
        Parent root;
        settingRootPane();
        settingIvEvnet();
        lvPlayList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    if (lvPlayList.getSelectionModel().getSelectedItem() == null) {
                        MainController.setListSelected("!@#$");
                    }
                    MainController.eventFromPlayList = "oneClick";
                    MainController.setListSelected((String) lvPlayList.getSelectionModel().getSelectedItem());

                }
                if (event.getClickCount() == 2) {
                    MainController.eventFromPlayList = "twoClick";
                    MainController.playFileInList((String) lvPlayList.getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    public void settingIvEvnet(){
        this.enterLight = new Light.Distant();
        this.enterLight.setColor(new Color(1.0,1.0,0.0,1.0));

        this.defaultLight = new Light.Distant();
        this.defaultLight.setColor(new Color(1.0, 1.0, 1.0, 1.0));

        this.ivAdd.setEffect(new Lighting(defaultLight));
        this.ivExit.setEffect(new Lighting(defaultLight));
        this.ivMinus.setEffect(new Lighting(defaultLight));

        setAddEvent();
        setExitEvent();
        setMinusEvent();
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void setExitEvent(){
        this.ivExit.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Light light = new Light.Distant();
                light.setColor(new Color(1.0,1.0,1.0,0.4));
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

    public void setAddEvent(){
        this.ivAdd.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivAdd.setEffect(new Lighting(enterLight));
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
                List<File> addFileList;
                String path = System.getProperty("user.dir");
                File dir = new File(path);
                FileChooser filechooser = new FileChooser();
                filechooser.setTitle("Open Video Files");
                filechooser.setInitialDirectory(dir);
                setFileChooserExtensionFilter(filechooser);
                addFileList = filechooser.showOpenMultipleDialog(stage.getScene().getWindow());
                if (addFileList != null) {
                    //File open success
                    MainController.addOpenFileList(addFileList);
                    ObservableList<String> playNameList = FXCollections.observableList(MainController.getPlayListName());
                    lvPlayList.setPrefHeight(playNameList.size() * ROW_HEIGHT + 2);
                    lvPlayList.setMinHeight(playNameList.size() * ROW_HEIGHT + 2);
                    lvPlayList.setMaxHeight(playNameList.size() * ROW_HEIGHT + 2);
                    lvPlayList.setEditable(false);
                    lvPlayList.setItems(playNameList);
                }
                event.consume();
            }
        });
    }

    public void setMinusEvent(){
        this.ivMinus.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivMinus.setEffect(new Lighting(enterLight));
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
                List<String> selectedList = lvPlayList.getSelectionModel().getSelectedItems();
                MainController.deleteFIleList(selectedList);
                lvPlayList.setItems(FXCollections.observableList(MainController.getPlayListName()));
                event.consume();
            }
        });
    }

    public void setPlayList(){
        this.lvPlayList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if(MainController.openFileExist()) {
            ObservableList<String> playNameList = FXCollections.observableList(MainController.getPlayListName());
            System.out.println(playNameList.size());
            this.lvPlayList.setPrefHeight(playNameList.size() * ROW_HEIGHT + 2);
            this.lvPlayList.setMinHeight(playNameList.size() * ROW_HEIGHT + 2);
            this.lvPlayList.setMaxHeight(playNameList.size() * ROW_HEIGHT + 2);
            this.lvPlayList.setEditable(false);
            this.lvPlayList.setItems(playNameList);

        }
    }

    private void settingRootPane(){
        playList.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });
        playList.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }
        });
    }

    private void setFileChooserExtensionFilter(FileChooser filechooser){
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Video Files","*.mp4","*.avi");
        filechooser.getExtensionFilters().add(filter);
    }

    public void closePlayList(){
        stage.close();
    }


}
