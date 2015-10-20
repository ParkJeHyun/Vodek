package controller;


import it.sauronsoftware.jave.*;

import java.io.File;

/**
 * This Class Control VideoFile and AudioFile from ControlMain.
 * using JAVE library.
 * Created by JeHyun Park on 2015-10-06.
 */
public class ExtractAudio {
    private Encoder encoder;
    private static ExtractAudio instance = null;

    public static ExtractAudio getInstance(){
        if(instance == null){
            instance = new ExtractAudio();
        }
        return instance;
    }

    public ExtractAudio(){
        encoder = new Encoder();
    }

    public String getVideoInfo(File videoFile){
        String outString = "";
        outString += videoFile.getName() + "#";
        try {
            MultimediaInfo info = encoder.getInfo(videoFile);
            VideoInfo video = info.getVideo();
            int sec = (int)info.getDuration()/1000;
            int min = sec/60;
            int hour = min/60;
            min %= 60;
            sec %= 60;
            outString += hour+":"+min+":"+sec +"#";
        } catch (InputFormatException e) {
            e.printStackTrace();
        } catch (EncoderException e) {
            e.printStackTrace();
        }
        long size = videoFile.length();
        float mbSize = Math.round(((float) size / (1024 * 1024) * 10) / 10.0);
        outString += mbSize + "MB";
        return outString;
    }

    public String getVideowidthHeight(File videoFile){
        MultimediaInfo info;
        String widthHeight = "";
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

    public void makeAudioFile(File sourceFile,File outAudioFile){
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le");
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("wav");
        attrs.setAudioAttributes(audio);
        audio.setChannels(new Integer(1));
        audio.setSamplingRate(new Integer(16000));
        try {
            encoder.encode(sourceFile, outAudioFile, attrs);

        } catch (InputFormatException e) {
            e.printStackTrace();
        } catch (EncoderException e) {
            e.printStackTrace();
        }
    }
}
