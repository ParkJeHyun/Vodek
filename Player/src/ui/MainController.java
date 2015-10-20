package ui;

import controller.PlayController;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.*;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    //Main
    @FXML private BorderPane rootPane;
    //Window part
    @FXML private GridPane gpWindow;
    @FXML private Button btExit;
    @FXML private Button btMaximize;
    @FXML private Button btMinimize;
    //Center
    @FXML private GridPane gpRoot;
    @FXML private MediaView mvPlay;
    //Bottom Controller
    @FXML private GridPane gpControl;
    @FXML private Slider sdVolume;
    @FXML private Button btVolume;
    @FXML private Button btOpen;
    @FXML private Label lbCurrent;
    @FXML private Label lbEnd;
    @FXML private Slider sdTime;
    @FXML private Button btPlay;

    private static MediaPlayer player;
    private Duration duration;
    private static int currentFile;
    private MediaPlayer.Status playStatus;

    private boolean isMaxmize;
    private double nowWidth;
    private double nowHeight;
    private double nowLocateX;
    private double nowLocateY;

    private double pastVolume;
    private boolean voulmeBtnFlag;
    private static ArrayList<File> openFileList;
    private boolean searchFlag;
    private PlayController playController;
    private PlayListController playListController;
    private Parent playListRoot;

    private double xOffset;
    private double yOffset;

    private Stage stage;
    private Stage playListStage;

    @FXML public void initialize(){
        openFileList = new ArrayList<File>();
        this.voulmeBtnFlag = false;
        this.searchFlag = false;
        this.isMaxmize = false;
        this.currentFile = 0;
        this.playStatus = MediaPlayer.Status.READY;
        this.playController = PlayController.getInstance();
        settingRootPane();

        if(this.playListController == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/PlayList.fxml"));
                this.playListRoot = loader.load();
                this.playListController = (PlayListController) loader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void exitBtnEventListener(ActionEvent event){
        Stage stage = (Stage)btExit.getScene().getWindow();
        if(this.playListStage!=null && this.playListStage.isShowing()) {
            this.playListController.closePlayList();
        }
        stage.close();
    }

    public void maximizeBtnEventListener(ActionEvent event){
        Stage stage = (Stage)btMaximize.getScene().getWindow();
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
    }

    public void minimizeBtnEventListener(ActionEvent event){
        Stage stage = (Stage)btMinimize.getScene().getWindow();
        stage.setIconified(true);
    }

    public void playBtnEventListener(ActionEvent event){
        if(playStatus == MediaPlayer.Status.PAUSED ){
            //Click PauseButton
            player.play();
            this.playStatus = MediaPlayer.Status.PLAYING;
            return;
        }
        else if(playStatus == MediaPlayer.Status.PLAYING){
            //Click PlayButton
            player.pause();
            this.playStatus = MediaPlayer.Status.PAUSED;
            return;
        }
        else {
            if (!openFileExist()) {
                //Open File not Exist
                btOpen.fire();
            } else {
                //Open File Exist
                Stage stage = (Stage)btPlay.getScene().getWindow();
                player = new MediaPlayer(new Media(openFileList.get(currentFile).toURI().toString()));
                setPlayer();
                reSizeWindow(openFileList.get(currentFile), stage);

                player.setAutoPlay(true);
                this.playStatus = player.getStatus();
                this.mvPlay.setMediaPlayer(player);
            }
        }
    }

    public void stopBtnEventListener(ActionEvent event){
        this.playStatus = MediaPlayer.Status.STOPPED;
        player.stop();
    }

    public void fastBtnEventListener(ActionEvent event){

    }

    public void slowBtnEventListener(ActionEvent event){

    }

    public void listBtnEventListener(ActionEvent event){
        if(this.playListStage == null){
            this.playListStage = new Stage();
            this.playListStage.initStyle(StageStyle.UNDECORATED);
            this.playListStage.setScene(new Scene(this.playListRoot, 200, 400));
            this.playListStage.setX(stage.getX() + stage.getWidth() + 10.0d);
            this.playListStage.setY(stage.getY() + 10.0d);
            this.playListController.setStage(playListStage);
        }
        if(!this.playListStage.isShowing()) {
            this.playListController.setPlayList();
            this.playListStage.setX(stage.getX() + stage.getWidth() + 10.0d);
            this.playListStage.setY(stage.getY() + 10.0d);
            this.playListStage.show();
        }
        else{
            this.playListController.closePlayList();
        }
    }

    public void volumeBtnEventListener(ActionEvent event){
        if(this.voulmeBtnFlag){
            this.voulmeBtnFlag = false;
            player.setMute(false);
//            Image img = new Image(getClass().getResourceAsStream("not.png"));
//            btVolume.setGraphic(new ImageView(img));
        }
        else {
            this.voulmeBtnFlag = true;
            this.pastVolume = this.sdVolume.getValue();
            player.setMute(true);
//            Image img = new Image(getClass().getResourceAsStream("not.png"));
//            btVolume.setGraphic(new ImageView(img));
        }
    }

    public void searchBtnEventListener(ActionEvent event){
        this.searchFlag = !this.searchFlag;
        double width = this.stage.getWidth();

        if(this.searchFlag) {
            this.stage.setWidth(width * 10 / 7 + 1.0d);
            this.rootPane.setPrefWidth(this.stage.getWidth());
            this.gpRoot.getColumnConstraints().get(0).setPercentWidth(70.0);
            this.gpRoot.getColumnConstraints().get(1).setPercentWidth(30.0);
        }
        else{
            this.stage.setWidth((width - 1.0d) * 7 / 10);
            this.rootPane.setPrefWidth(this.stage.getWidth());
            this.gpRoot.getColumnConstraints().get(0).setPercentWidth(100.0);
            this.gpRoot.getColumnConstraints().get(1).setPercentWidth(0.0);
        }
    }

    public void openBtnEventListener(ActionEvent event){
        if(this.playStatus == MediaPlayer.Status.PLAYING){
            player.stop();
        }
        Node node = (Node) event.getSource();
        String path = System.getProperty("user.dir");
        File dir = new File(path);
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Open Video Files");
        filechooser.setInitialDirectory(dir);
        setFileChooserExtensionFilter(filechooser);
        List<File> openFile = filechooser.showOpenMultipleDialog(node.getScene().getWindow());

        if(openFile != null) {
            this.openFileList = new ArrayList<File>(openFile);
        }
        System.out.println(openFileList.size());
        if(this.openFileList.size()!=0) {
            //File open success
            Stage stage = (Stage)btOpen.getScene().getWindow();
            this.currentFile = 0;
            player = new MediaPlayer(new Media(openFileList.get(currentFile).toURI().toString()));
            setPlayer();
            reSizeWindow(openFile.get(currentFile), stage);
            player.setAutoPlay(true);
            this.playStatus = MediaPlayer.Status.PLAYING;
            this.mvPlay.setMediaPlayer(player);
        }

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

    private void setFileChooserExtensionFilter(FileChooser filechooser){
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Video Files","*.mp4","*.avi");
        filechooser.getExtensionFilters().add(filter);
    }

    private void setPlayer(){

        player.currentTimeProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });

        player.setOnReady(new Runnable() {
            @Override
            public void run() {
                Duration currentTime = player.getCurrentTime();
                duration = player.getMedia().getDuration();

                updateValues();
            }
        });

        sdTime.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (sdTime.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    player.seek(duration.multiply(sdTime.getValue() / 100.0));
                }
            }
        });

        sdVolume.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (sdVolume.isValueChanging()) {
                    player.setVolume(sdVolume.getValue() / 100.0);
                }
            }
        });
    }

    private void reSizeWindow(File currentFile, Stage stage){
        Media media = player.getMedia();
        String widthAndHeight = this.playController.getVideowidthHeight(currentFile);
        double width = Double.parseDouble(widthAndHeight.split("[#]")[0]);
        double height = Double.parseDouble(widthAndHeight.split("[#]")[1]);
        stage.setWidth(width+10.0d);
        stage.setHeight(height + gpWindow.getHeight() + gpControl.getHeight() + 20.0d);
        this.rootPane.setPrefWidth(stage.getWidth());
        this.rootPane.setPrefHeight(stage.getHeight());
        this.mvPlay.setFitHeight(height);
        this.mvPlay.setFitWidth(width);
    }

    public static List<String> getPlayListName(){
        List<String> playListName = new ArrayList<String>();
        System.out.println(openFileList.size());
        for(int i=0;i<openFileList.size();i++){
            playListName.add(openFileList.get(i).getName().split("[.]")[0]);
        }

        return playListName;
    }

    public static boolean openFileExist(){
        if(openFileList.size() == 0){
            return false;
        }
        else{
            return true;
        }
    }

    public static void addOpenFileList(List<File> addFileList){
        ArrayList<File> addFiles = new ArrayList<File>(addFileList);

        for(int i=0;i<addFiles.size();i++){
            boolean flag = false;
            for(int j=0;j<openFileList.size();j++){
                if(addFileList.get(i).getPath().equals(openFileList.get(j).getPath())){
                    flag = true;
                }
            }
            if(flag){
                continue;
            }
            openFileList.add(addFiles.get(i));
        }
    }

    public static void deleteFIleList(List<String> deleteList){
        for(int i=0;i<deleteList.size();i++){
            int openSize = openFileList.size();
            for(int j=0;j<openSize;j++){
                if(openFileList.get(j).getName().split("[.]")[0].equals(deleteList.get(i))){
                    openFileList.remove(j);
                    if(j==currentFile){
                        player.stop();
                    }
                    break;
                }
            }
        }
    }

    protected void updateValues() {
        if (lbCurrent != null && lbEnd != null && sdTime != null && sdVolume != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = player.getCurrentTime();
                    lbCurrent.setText(formatTime(currentTime, duration).split("[#]")[0]);
                    lbEnd.setText(formatTime(currentTime, duration).split("[#]")[1]);
                    sdTime.setDisable(duration.isUnknown());
                    if (!sdTime.isDisabled() && duration.greaterThan(Duration.ZERO) && !sdTime.isValueChanging()) {
                        sdTime.setValue(currentTime.divide(duration).toMillis() * 100.0);
                    }
                    if (!sdVolume.isValueChanging()) {
                        sdVolume.setValue((int) Math.round(player.getVolume() * 100));
                    }
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d#%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d#%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }
}
