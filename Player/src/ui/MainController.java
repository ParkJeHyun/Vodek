package ui;

import controller.PlayController;
import controller.ScriptData;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
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
    @FXML private ImageView ivMinimize;
    @FXML private ImageView ivMaximize;
    @FXML private ImageView ivExit;
    //Center
    @FXML private BorderPane bpPlayer;
    @FXML private GridPane gpRoot;
    @FXML private MediaView mvPlay;
    //Bottom Controller
    @FXML private GridPane gpControl;
    @FXML private Slider sdVolume;
    @FXML private Label lbCurrent;
    @FXML private Label lbEnd;
    @FXML private Slider sdTime;
    @FXML private ProgressBar progressBar;
    @FXML private ImageView ivSearchTab;
    @FXML private ImageView ivList;
    @FXML private ImageView ivVolume;
    @FXML private ImageView ivPlay;
    @FXML private ImageView ivStop;
    @FXML private ImageView ivFast;
    @FXML private ImageView ivSlow;
    //Search
    @FXML private ImageView ivSearch;
    @FXML private TextField tfKeyword;
    @FXML private ListView lvResult;

    private Light.Distant enterLight;
    private Light.Distant defaultLight;

    private static MediaPlayer player;
    private Duration duration;
    private static int currentFile;
    private static MediaPlayer.Status playStatus;

    private static int listSelected;

    public static String eventFromOther;
    public static WaitEvent wating;

    private boolean isMaxmize;
    private double nowWidth;
    private double nowHeight;
    private double nowLocateX;
    private double nowLocateY;
    private double defaltStageWidth;
    private double defaltStageHeight;

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

    private ArrayList<ScriptData> searchResultSet;

    private Image imgStopOn;
    private Image imgStopOff;
    private Image imgPlayOn;
    private Image imgPlayOff;
    private Image imgFastOn;
    private Image imgFastOff;
    private Image imgSlowOn;
    private Image imgSlowOff;
    private Image imgListOn;
    private Image imgListOff;
    private Image imgPauseOn;
    private Image imgPauseOff;
    private Image imgSearchOn;
    private Image imgSearchOff;
    private Image imgSearchTabOn;
    private Image imgSearchTabOff;
    private Image imgVolumeOn;
    private Image imgVolumeOff;
    private Image imgMuteOn;
    private Image imgMuteOff;

    @FXML public void initialize(){
        imgStopOn = new Image("ui/img/stop_icon_on.png");
        imgStopOff = new Image("ui/img/stop_icon_off.png");
        imgPlayOn = new Image("ui/img/play_icon_on.png");
        imgPlayOff = new Image("ui/img/play_icon_off.png");
        imgFastOn = new Image("ui/img/fast_icon_on.png");
        imgFastOff = new Image("ui/img/fast_icon_off.png");
        imgSlowOn = new Image("ui/img/slow_icon_on.png");
        imgSlowOff = new Image("ui/img/slow_icon_off.png");
        imgListOn = new Image("ui/img/list_icon_on.png");
        imgListOff = new Image("ui/img/list_icon_off.png");
        imgPauseOn = new Image("ui/img/pause_icon_on.png");
        imgPauseOff = new Image("ui/img/pause_icon_off.png");
        imgSearchOn = new Image("ui/img/search_icon_on.png");
        imgSearchOff = new Image("ui/img/search_icon_off.png");
        imgSearchTabOn = new Image("ui/img/search_tab_on.png");
        imgSearchTabOff = new Image("ui/img/search_tab_off.png");
        imgVolumeOn = new Image("ui/img/volume_icon_on.png");
        imgVolumeOff = new Image("ui/img/volume_icon_off.png");
        imgMuteOn = new Image("ui/img/mute_icon_on.png");
        imgMuteOff = new Image("ui/img/mute_icon_off.png");

        eventFromOther = "none";
        wating = new WaitEvent();

        openFileList = new ArrayList<File>();
        listSelected = -1;
        this.voulmeBtnFlag = false;
        this.searchFlag = false;
        this.isMaxmize = false;
        this.currentFile = 0;
        this.playStatus = MediaPlayer.Status.READY;
        this.playController = PlayController.getInstance();
        this.searchResultSet = new ArrayList<ScriptData>();

        lvResult.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setResultListEvent();
            }
        });
        if(this.playListController == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/PlayList.fxml"));
                this.playListRoot = loader.load();
                this.playListController = (PlayListController) loader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        settingRootPane();
        settingIvEvnet();
    }

    public void setStage(final Stage stage){
        this.stage = stage;
        this.defaltStageWidth = stage.getWidth();
        this.defaltStageHeight = stage.getHeight();
        this.nowWidth = stage.getWidth();
        this.nowHeight = stage.getHeight();

        this.stage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                mvPlay.setFitWidth(newSceneWidth.doubleValue());
            }
        });
        this.stage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                mvPlay.setFitHeight(newSceneHeight.doubleValue() - gpControl.getHeight() - gpWindow.getHeight());
            }
        });

        gpWindow.setPrefWidth(stage.getWidth());
        rootPane.setPrefWidth(stage.getWidth());
        gpRoot.getColumnConstraints().get(0).setPrefWidth(stage.getWidth());
        gpRoot.getColumnConstraints().get(1).setMaxWidth(0.0);//Width(0.0);
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

    private void setPlayer(){
        player.setOnEndOfMedia(new PlayEnd());
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
        sdTime.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                progressBar.setProgress(newValue.doubleValue() / 100.0);
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

    private void settingIvEvnet(){
        this.enterLight = new Light.Distant();
        this.enterLight.setColor(new Color(0.2,0.7,0.9,1.0));

        this.defaultLight = new Light.Distant();
        this.defaultLight.setColor(new Color(1.0,1.0,1.0,1.0));

        this.ivExit.setEffect(new Lighting(defaultLight));
        this.ivMaximize.setEffect(new Lighting(defaultLight));
        this.ivMinimize.setEffect(new Lighting(defaultLight));

        setMinimizeEvent();
        setMaximizeEvent();
        setExitEvent();
        setSearchTabEvent();
        setListEvent();
        setVolumeEvent();
        setPlayEvent();
        setStopEvent();
        setSlowEvent();
        setFastEvent();
        setSearchEvent();
    }

    //Window ImageVIew Event Handler
    private void setExitEvent(){
        this.ivExit.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Light light = new Light.Distant();
                light.setColor(new Color(0.5, 0.0, 0.0, 1.0));
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
                if (playListStage != null && playListStage.isShowing()) {
                    playListController.closePlayList();
                }
                stage.close();
                event.consume();
            }
        });
    }

    private void setMaximizeEvent(){
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
                if (isMaxmize) {
                    //Maximize상태에서 돌아올 때
                    isMaxmize = false;
                    stage.setX(nowLocateX);
                    stage.setY(nowLocateY);
                    stage.setWidth(nowWidth);
                    stage.setHeight(nowHeight);
                    setSearchTabSize();
                    stage.setMaximized(false);
                } else {
                    //Maximize상태로 만들 때
                    isMaxmize = true;
                    nowWidth = stage.getWidth();
                    nowHeight = stage.getHeight();
                    nowLocateX = stage.getX();
                    nowLocateY = stage.getY();
                    stage.setMaximized(true);
                    setSearchTabSize();
                }
                event.consume();
            }
        });
    }

    private void setMinimizeEvent(){
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

    //Contoller ImageView Event Handler
    private void setPlayEvent(){
        this.ivPlay.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (playStatus == MediaPlayer.Status.PLAYING) {
                    //Click PauseButton
                    ivPlay.setImage(imgPauseOn);
                }
                else {
                    ivPlay.setImage(imgPlayOn);
                }
            }
        });
        this.ivPlay.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (playStatus == MediaPlayer.Status.PLAYING) {
                    //Click PauseButton
                    ivPlay.setImage(imgPauseOff);
                }
                else {
                    ivPlay.setImage(imgPlayOff);
                }
            }
        });
        this.ivPlay.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (playStatus == MediaPlayer.Status.PAUSED) {
                    //Click PauseButton
                    ivPlay.setImage(imgPauseOn);
                    player.play();
                    playStatus = MediaPlayer.Status.PLAYING;
                    return;
                } else if (playStatus == MediaPlayer.Status.PLAYING) {
                    //Click PlayButton
                    ivPlay.setImage(imgPlayOn);
                    player.pause();
                    playStatus = MediaPlayer.Status.PAUSED;
                    return;
                } else if (playStatus == MediaPlayer.Status.STOPPED || playStatus == MediaPlayer.Status.READY) {
                    if (!openFileExist()) {
                        //Open File not Exist
                        openBtnEventListener();
                    } else {
                        //Open File Exist
                        if (listSelected != -1) {
                            player = new MediaPlayer(new Media(openFileList.get(listSelected).toURI().toString()));
                            setPlayer();
                            reSizeWindow(playController.getVideowidthHeight(openFileList.get(currentFile)));
                            openTextFile();
                            player.setAutoPlay(true);
                            playStatus = MediaPlayer.Status.PLAYING;
                            ivPlay.setImage(imgPauseOn);
                            mvPlay.setMediaPlayer(player);
                        } else {
                            player = new MediaPlayer(new Media(openFileList.get(currentFile).toURI().toString()));
                            setPlayer();
                            reSizeWindow(playController.getVideowidthHeight(openFileList.get(currentFile)));
                            openTextFile();
                            player.setAutoPlay(true);
                            playStatus = MediaPlayer.Status.PLAYING;
                            ivPlay.setImage(imgPauseOn);
                            mvPlay.setMediaPlayer(player);
                        }
                    }
                }
                event.consume();
            }
        });
    }

    private void openTextFile(){
        File openFile = openFileList.get(currentFile);
        String path = openFile.getPath().replace(openFile.getName(),"");
        path += openFile.getName().split("[.]")[0] + ".txt";
        File textFile = new File(path);
        playController.openTextFile(textFile);
    }

    private void setStopEvent(){
        this.ivStop.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivStop.setImage(imgStopOn);
            }
        });
        this.ivStop.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivStop.setImage(imgStopOff);
            }
        });
        this.ivStop.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (playStatus == MediaPlayer.Status.PLAYING) {
                    playStatus = MediaPlayer.Status.STOPPED;
                    player.stop();
                    ivPlay.setImage(imgPauseOff);
                    stage.setWidth(defaltStageWidth);
                    stage.setHeight(defaltStageHeight);
                }
                event.consume();
            }
        });
    }

    private void setFastEvent(){
        this.ivFast.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivFast.setImage(imgFastOn);
            }
        });
        this.ivFast.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivFast.setImage(imgFastOff);
            }
        });
        this.ivFast.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(playStatus == MediaPlayer.Status.PLAYING){
                    double currentRate = player.getRate();
                    if(currentRate > 1.5){
                        return;
                    }
                    currentRate += 0.1d;
                    player.setRate(currentRate);
                }
                event.consume();
            }
        });
    }

    private void setSlowEvent(){
        this.ivSlow.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivSlow.setImage(imgSlowOn);
            }
        });
        this.ivSlow.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivSlow.setImage(imgSlowOff);
            }
        });
        this.ivSlow.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(playStatus == MediaPlayer.Status.PLAYING){
                    double currentRate = player.getRate();
                    if(currentRate < 0.8){
                        return;
                    }
                    currentRate -= 0.1d;
                    player.setRate(currentRate);
                }
                event.consume();
            }
        });
    }

    private void setVolumeEvent(){
        this.ivVolume.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(voulmeBtnFlag){
                    ivVolume.setImage(imgMuteOn);
                }
                else {
                    ivVolume.setImage(imgVolumeOn);
                }
            }
        });
        this.ivVolume.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(voulmeBtnFlag){
                    ivVolume.setImage(imgMuteOff);
                }
                else {
                    ivVolume.setImage(imgVolumeOff);
                }
            }
        });
        this.ivVolume.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(voulmeBtnFlag){
                    voulmeBtnFlag = false;
                    if(player!=null) {
                        player.setMute(false);
                    }
                    ivVolume.setImage(new Image("/ui/img/volume_icon_on.png"));
//            Image img = new Image(getClass().getResourceAsStream("not.png"));
//            btVolume.setGraphic(new ImageView(img));
                }
                else {
                    voulmeBtnFlag = true;
                    if(player!=null) {
                        player.setMute(true);
                    }
                    ivVolume.setFitWidth(20.0);
                    ivVolume.setFitHeight(20.0);
                    ivVolume.setImage(new Image("/ui/img/mute_icon_on.png"));
//            Image img = new Image(getClass().getResourceAsStream("not.png"));
//            btVolume.setGraphic(new ImageView(img));
                }
                event.consume();
            }
        });
    }

    private void setListEvent(){
        this.ivList.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivList.setImage(imgListOn);
            }
        });
        this.ivList.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivList.setImage(imgListOff);
            }
        });
        this.ivList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(playListStage == null){
                    playListStage = new Stage();
                    playListStage.initStyle(StageStyle.UNDECORATED);
                    playListStage.setScene(new Scene(playListRoot, 200, 400));
                    playListStage.setX(stage.getX() + stage.getWidth() + 10.0d);
                    playListStage.setY(stage.getY() + 10.0d);
                    playListController.setStage(playListStage);
                }
                if(!playListStage.isShowing()) {
                    playListController.setPlayList();
                    playListStage.setX(stage.getX() + stage.getWidth() + 10.0d);
                    playListStage.setY(stage.getY() + 10.0d);
                    playListStage.show();
                }
                else{
                    playListController.closePlayList();
                }
                event.consume();
            }
        });
    }

    private void setSearchTabEvent(){
        this.ivSearchTab.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivSearchTab.setImage(imgSearchTabOn);
            }
        });
        this.ivSearchTab.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivSearchTab.setImage(imgSearchTabOff);
            }
        });
        this.ivSearchTab.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double width = stage.getWidth();

                if(searchFlag) {
                    //search창을 제거할 때
                    stage.setWidth(width - 250.0d);
                    if(isMaxmize){
                        stage.setWidth(width);
                        nowWidth -= 250.0d;
                    }
                    searchFlag = !searchFlag;
                    setSearchTabSize();
                }
                else{
                    //search창을 만들 때
                    stage.setWidth(width + 250.0d);
                    if(isMaxmize){
                        stage.setWidth(width);
                        nowWidth += 250.0d;
                    }
                    searchFlag = !searchFlag;
                    setSearchTabSize();
                }

                event.consume();
            }
        });
    }

    //SearchTab ImageView Event Handler
    private void setSearchEvent(){
        this.ivSearch.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivSearch.setImage(imgSearchOn);
            }
        });
        this.ivSearch.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ivSearch.setImage(imgSearchOff);
            }
        });
        this.ivSearch.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (tfKeyword.getText().length() != 0) {
                    searchResultSet = playController.getSearchResult(tfKeyword.getText());
                    setResultList();
                }
                event.consume();
            }
        });
    }

    public void enterEvent(){
        if (tfKeyword.getText().length() != 0) {
            searchResultSet = playController.getSearchResult(tfKeyword.getText());
            setResultList();
        }
    }

    private void setResultList(){
        ObservableList observableList = FXCollections.observableArrayList();
        observableList.setAll(this.searchResultSet);
        lvResult.getStylesheets().add("ui/css/resultlist.css");
        lvResult.setItems(observableList);
//        lvResult.setPrefHeight(50 * searchResultSet.size());
//        lvResult.setMinHeight(50 * searchResultSet.size());
//        lvResult.setMinHeight(50 * searchResultSet.size());
        lvResult.setCellFactory(new Callback<ListView<ScriptData>, ListCell<ScriptData>>() {
            @Override
            public ListCell<ScriptData> call(ListView<ScriptData> param) {
                return (new ListCell<ScriptData>() {
                    @Override
                    public void updateItem(ScriptData item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            ResultController result = new ResultController();
                            result.setInfo(item);
                            setGraphic(result.getGridPane());
                        }
                    }
                });
            }
        });
    }

    public void setResultListEvent(){
        ScriptData click = (ScriptData)lvResult.getSelectionModel().getSelectedItem();
        if(click!=null){
            Duration jumpDuration = Duration.seconds(click.getTimeSec());
            player.seek(jumpDuration);
            updateValues();
        }
    }

    private void openBtnEventListener(){
        if(this.playStatus == MediaPlayer.Status.PLAYING){
            player.stop();
        }
        String path = System.getProperty("user.dir");
        File dir = new File(path);
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Open Video Files");
        filechooser.setInitialDirectory(dir);
        setFileChooserExtensionFilter(filechooser);
        List<File> openFile = filechooser.showOpenMultipleDialog(this.stage);

        if(openFile != null) {
            openFileList = new ArrayList<File>(openFile);
        }
        if(openFileList.size()!=0) {
            //File open success
            currentFile = 0;
            player = new MediaPlayer(new Media(openFileList.get(currentFile).toURI().toString()));
            setPlayer();
            reSizeWindow(this.playController.getVideowidthHeight(openFile.get(currentFile)));
            openTextFile();
            player.setAutoPlay(true);
            playStatus = MediaPlayer.Status.PLAYING;
            ivPlay.setImage(imgPauseOff);
            this.mvPlay.setMediaPlayer(player);
        }
    }

    private void setFileChooserExtensionFilter(FileChooser filechooser){
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Video Files","*.mp4","*.avi");
        filechooser.getExtensionFilters().add(filter);
    }

    private void reSizeWindow(String size){
        Media media = player.getMedia();
        String widthAndHeight = size;
        double width = Double.parseDouble(widthAndHeight.split("[#]")[0]);
        double height = Double.parseDouble(widthAndHeight.split("[#]")[1]);

        if(searchFlag){
            this.stage.setWidth(width + 20.0d + 250.0d);
        }
        else{
            this.stage.setWidth(width + 20.0d);
        }

        this.stage.setHeight(height + gpWindow.getHeight() + gpControl.getHeight() + 30.0d);
        setSearchTabSize();
        this.mvPlay.setFitHeight(height);
        //this.mvPlay.setFitWidth(width);
    }

    public static List<String> getPlayListName(){
        List<String> playListName = new ArrayList<String>();
        for(int i=0;i<openFileList.size();i++){
            playListName.add(openFileList.get(i).getName());
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
            for (int j =0;j<openSize;j++){
                if(openFileList.get(j).getName().equals(deleteList.get(i))){
                    openFileList.remove(j);
                    if(j==currentFile){
                        player.stop();
                        playStatus = MediaPlayer.Status.STOPPED;
                    }
                    break;
                }
            }
        }
    }

    public static void playFileInList(String selectedItem){
        int openSize = openFileList.size();
        for(int j=0;j<openSize;j++){
            if(openFileList.get(j).getName().equals(selectedItem)){
                if(playStatus == MediaPlayer.Status.PLAYING){
                    player.stop();
                }
                currentFile = j;
                wating.run();
                break;
            }
        }

    }

    public static void setListSelected(String selectedItem){
        int openSize = openFileList.size();
        for(int j=0;j<openSize;j++){
            if(openFileList.get(j).getName().equals(selectedItem)){
                listSelected = j;
                wating.run();
                break;
            }
        }
    }

    protected void updateValues() {

        if(playStatus == MediaPlayer.Status.STOPPED){
            ivPlay.setImage(imgPlayOff);
        }
        if(playStatus == MediaPlayer.Status.PLAYING){
            ivPlay.setImage(imgPauseOff);
        }
        if (lbCurrent != null && lbEnd != null && sdTime != null && sdVolume != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = player.getCurrentTime();
                    lbCurrent.setText(formatTime(currentTime, duration).split("[#]")[0]+" / ");
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

    private void setSearchTabSize(){
        rootPane.setPrefWidth(stage.getWidth());
        rootPane.setMaxWidth(stage.getWidth());

        if(searchFlag){
            mvPlay.setFitWidth(stage.getWidth() - 280.0d);
            bpPlayer.setMinWidth(stage.getWidth() - 250.0d);
            bpPlayer.setMaxWidth(stage.getWidth() - 250.0d);
            bpPlayer.setPrefWidth(stage.getWidth() - 250.0d);
            gpRoot.getColumnConstraints().get(1).setPrefWidth(250.0d);
            gpRoot.getColumnConstraints().get(1).setMaxWidth(250.0d);
        }
        else{
            mvPlay.setFitWidth(stage.getWidth() - 30.0d);
            bpPlayer.setMinWidth(stage.getWidth());
            bpPlayer.setMaxWidth(stage.getWidth());
            bpPlayer.setPrefWidth(stage.getWidth());
            gpRoot.getColumnConstraints().get(1).setPrefWidth(0.0);
            gpRoot.getColumnConstraints().get(1).setMaxWidth(0.0);
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

    public static void getOtherControllerEvent(String event){
        eventFromOther = event;
        wating.run();
    }

    private class WaitEvent implements Runnable{

        @Override
        public void run() {
            if(eventFromOther.equals("twoClick")){
                if(playStatus == MediaPlayer.Status.PLAYING){
                    player.stop();
                    ivPlay.setImage(imgPauseOn);
                    playStatus = MediaPlayer.Status.STOPPED;
                }
                player = new MediaPlayer(new Media(openFileList.get(currentFile).toURI().toString()));
                setPlayer();
                reSizeWindow(playController.getVideowidthHeight(openFileList.get(currentFile)));
                openTextFile();
                player.setAutoPlay(true);
                playStatus = MediaPlayer.Status.PLAYING;
                mvPlay.setMediaPlayer(player);
            }
            if(eventFromOther.contains("Move")){
                String moveString = eventFromOther.split("[#]")[1];
                for(int i=0;i<searchResultSet.size();i++){
                    if(moveString.equals(searchResultSet.get(i).getTimeString())){
                        Duration jumpDuration = Duration.seconds(searchResultSet.get(i).getTimeSec());
                        player.seek(jumpDuration);
                        updateValues();
                    }
                }
            }
        }
    }

    private class PlayEnd implements Runnable {
        @Override
        public void run() {
            System.out.println("FIle end!");
            currentFile += 1;
            if(currentFile == openFileList.size()){
                //마지막 파일 재생 종료
                player.stop();
                playStatus = MediaPlayer.Status.STOPPED;
            }
            else{
                player = new MediaPlayer(new Media(openFileList.get(currentFile).toURI().toString()));
                setPlayer();
                reSizeWindow(playController.getVideowidthHeight(openFileList.get(currentFile)));
                openTextFile();
                player.setAutoPlay(true);
                playStatus = MediaPlayer.Status.PLAYING;
                ivPlay.setImage(imgPauseOn);
                mvPlay.setMediaPlayer(player);
            }
        }
    }
}
