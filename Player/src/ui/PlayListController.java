package ui;

import controller.PlayController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Administrator on 2015-10-20.
 */
public class PlayListController {
    //PlayList
    @FXML private ListView lvPlayList;
    @FXML private Button btExit;
    @FXML private BorderPane playList;

    private double xOffset;
    private double yOffset;
    private Stage stage;

    private final int ROW_HEIGHT = 15;


    @FXML public void initialize() {
        Parent root;
        settingRootPane();
        lvPlayList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() == 1){
                    if(lvPlayList.getSelectionModel().getSelectedItem() == null){
                        MainController.setListSelected("!@#$");
                    }
                    MainController.eventFromPlayList = "oneClick";
                    MainController.setListSelected((String) lvPlayList.getSelectionModel().getSelectedItem());

                }
                if(event.getClickCount() == 2){
                    MainController.eventFromPlayList = "twoClick";
                    MainController.playFileInList((String)lvPlayList.getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void exitBtnEventListener(ActionEvent event){
        Stage stage = (Stage)btExit.getScene().getWindow();
        stage.hide();
    }

    public void setPlayList(){
        this.lvPlayList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if(MainController.openFileExist()) {
            ObservableList<String> playNameList = FXCollections.observableList(MainController.getPlayListName());
            System.out.println(playNameList.size());
            this.lvPlayList.setEditable(false);
            this.lvPlayList.setItems(playNameList);
            this.lvPlayList.setPrefHeight(playNameList.size() * ROW_HEIGHT + 2);
        }

    }

    public void listAddBtnEventListener(ActionEvent event){
        List<File> addFileList;
        Node node = (Node) event.getSource();
        String path = System.getProperty("user.dir");
        File dir = new File(path);
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Open Video Files");
        filechooser.setInitialDirectory(dir);
        setFileChooserExtensionFilter(filechooser);
        addFileList = filechooser.showOpenMultipleDialog(node.getScene().getWindow());
        if(addFileList!=null) {
            //File open success
            MainController.addOpenFileList(addFileList);
            ObservableList<String> playNameList = FXCollections.observableList(MainController.getPlayListName());
            this.lvPlayList.setItems(playNameList);
        }
    }

    public void listDeleteBtnEventListener(ActionEvent event){
        List<String> selectedList = this.lvPlayList.getSelectionModel().getSelectedItems();
        MainController.deleteFIleList(selectedList);
        this.lvPlayList.setItems(FXCollections.observableList(MainController.getPlayListName()));
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

    public String getSelectedFileName(){
        return (String)lvPlayList.getSelectionModel().getSelectedItem();
    }

    public void closePlayList(){
        btExit.fire();
    }


}
