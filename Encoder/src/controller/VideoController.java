package controller;

import java.io.File;

/**
 * Created by Administrator on 2015-10-17.
 */
public class VideoController {
    public static VideoController instance = null;

    private ExtractAudio extractAudio;
    private DivideAudio divideAudio;
    public static VideoController getInstance(){
        if(instance == null){
            instance = new VideoController();
        }
        return instance;
    }

    public void extractAudio(File videoFile){
        String workingDir = videoFile.getPath().replace(videoFile.getName(), "");
        extractAudio = ExtractAudio.getInstance();

        //File videoFile = new File(workingDir, "/media/Video/example.mp4");
        File audioFile = new File(workingDir, videoFile.getName().split("[.]")[0] + ".wav");

        if (!audioFile.exists()) {
            extractAudio.makeAudioFile(videoFile, audioFile);
        }
        System.out.println("Extra Audio File Complete!!!");
        divideAudio(audioFile);
    }

    public void divideAudio(File audioFile){
        divideAudio = DivideAudio.getInstance();
        divideAudio.divideFile(audioFile);
    }

    public String getVideoInfo(File videoFile){
        extractAudio = ExtractAudio.getInstance();
        return extractAudio.getVideoInfo(videoFile);
    }
}
