package ui;

import controller.ConvertWTT;
import controller.VideoController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015-10-17.
 */
public class Controller {
    @FXML ListView lvInputFileList;
    @FXML TextArea taFileInfo;
    @FXML TextField tfFilename;
    @FXML TextField tfFiletime;
    @FXML TextField tfFilesize;


    List<File> fileList;
    List<InputFile> inputFileList;

    VideoController videoController;
    ConvertWTT convertWTT;
    public void addBtnEventListener(ActionEvent event){
        Node node = (Node) event.getSource();
        String path = System.getProperty("user.dir");
        File dir = new File(path);
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Open Video Files");
        filechooser.setInitialDirectory(dir);
        setFileChooserExtensionFilter(filechooser);
        this.fileList = filechooser.showOpenMultipleDialog(node.getScene().getWindow());
        if(this.fileList!=null) {
            setInputFileListView();
        }
    }

    private void setFileChooserExtensionFilter(FileChooser filechooser){
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Video Files","*.mp4","*.avi");
        filechooser.getExtensionFilters().add(filter);
    }

    public void setInputFileListView(){
        List<String> fileNameList = new ArrayList<String>();
        this.inputFileList = new ArrayList<InputFile>();

        for(int i=0;i<fileList.size();i++){
            this.inputFileList.add(new InputFile(fileList.get(i)));
            fileNameList.add(fileList.get(i).getName());
        }
        ObservableList<String> list = FXCollections.observableList(fileNameList);

        this.lvInputFileList.setEditable(false);
        this.lvInputFileList.setItems(list);
        this.lvInputFileList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectFileName = (String)lvInputFileList.getSelectionModel().getSelectedItem();
                boolean find = false;
                videoController = VideoController.getInstance();
                for (int j = 0; j < inputFileList.size(); j++) {
                    File listFile = inputFileList.get(j).getFile();
                    if (selectFileName.equals(listFile.getName())) {
                        String fileInfo = videoController.getVideoInfo(inputFileList.get(j).getFile());
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
        });
    }

    public void playBtnEventListener(ActionEvent event) throws Exception {
        videoController = VideoController.getInstance();
        convertWTT = ConvertWTT.getInstance();
        for(int i=0;i<inputFileList.size();i++){
            if(inputFileList.get(i).getFlag()) {
                videoController.extractAudio(inputFileList.get(i).getFile());
                convertWTT.waveToText(inputFileList.get(i).getFile().getPath().replace(inputFileList.get(i).getFile().getName(),"")+inputFileList.get(i).getFile().getName().split("[.]")[0]);
            }
        }
    }

    public void inputFileListVIewSelectEvent(ActionEvent e){
        List<String> selectFileNameList = lvInputFileList.getSelectionModel().getSelectedItems();
        boolean find = false;
        videoController = VideoController.getInstance();
        for(int i=0;i<selectFileNameList.size() || !find;i++){
            for(int j=0;j<inputFileList.size();i++){
                File listFile = inputFileList.get(j).getFile();
                if(selectFileNameList.get(i).equals(listFile.getName())){
                    taFileInfo.setEditable(false);
                    taFileInfo.setText(videoController.getVideoInfo(inputFileList.get(j).getFile()));
                    return;
//                    find = true;
//                    break;
                }
            }
        }
    }

    private class InputFile{
        boolean selectFlag;
        File file;

        public InputFile(File file){
            selectFlag = true;
            this.file = file;
        }

        public void isSelected(){
            selectFlag = true;
        }

        public void notSelected(){
            selectFlag = false;
        }

        public boolean getFlag(){
            return this.selectFlag;
        }

        public File getFile(){
            return this.file;
        }
    }
}
