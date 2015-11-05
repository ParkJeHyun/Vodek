package controller;

import it.sauronsoftware.jave.*;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015-10-20.
 */
public class PlayController {
    private static PlayController instance = null;
    private File textFile;
    private ArrayList<ScriptData> dataSet;

    public static PlayController getInstance(){
        if(instance == null){
            instance = new PlayController();
        }
        return instance;
    }
    public PlayController(){
        dataSet = new ArrayList<ScriptData>();
    }

    public void openTextFile(File openTextFile){
        textFile = openTextFile;
        dataSet.clear();
        try {
            BufferedReader in = new BufferedReader(new FileReader(textFile));
            String readString;

            while((readString = in.readLine()) != null){
                String[] readData = readString.split("[#]");
                dataSet.add(new ScriptData(readData[0],getTimeSec(readData[0]),readData[1]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ScriptData> getSearchResult(String keyWord){
        ArrayList<ScriptData> resultSet = new ArrayList<ScriptData>();

        for(int i=0;i<this.dataSet.size();i++){
            if(dataSet.get(i).getText().contains(keyWord)){
                resultSet.add(dataSet.get(i));
            }
        }

        return resultSet;
    }

    public int getTimeSec(String time){
        int timeSec = 0;
        String[] timeSet = time.split("[:]");
        timeSec += Integer.parseInt(timeSet[0]) * 3600;
        timeSec += Integer.parseInt(timeSet[1]) * 60;
        timeSec += Integer.parseInt(timeSet[2]);

        return timeSec;
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
