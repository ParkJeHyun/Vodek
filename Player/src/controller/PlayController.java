package controller;

import it.sauronsoftware.jave.*;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015-10-20.
 */
public class PlayController {
    private static PlayController instance = null;

    public static PlayController getInstance(){
        if(instance == null){
            instance = new PlayController();
        }
        return instance;
    }

    public String getVideowidthHeight(File videoFile){
        MultimediaInfo info;
        String widthHeight = "";
        Encoder encoder = new Encoder();
        try {
            info = encoder.getInfo(videoFile);
            VideoInfo video = info.getVideo();
            VideoSize size = video.getSize();
            widthHeight = widthHeight+size.getWidth()+"#"+size.getHeight();

        } catch (InputFormatException e) {
            e.printStackTrace();
        } catch (EncoderException e) {
            e.printStackTrace();
        }
        return widthHeight;
    }

}
