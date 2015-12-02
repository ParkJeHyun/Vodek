package controller;

/**
 * Created by Administrator on 2015-10-26.
 */
public class ScriptData {
    private int timeSec;
    private String timeString;
    private String text;

    public ScriptData(String timeString, int timeSec, String text){
        this.timeString = timeString;
        this.timeSec = timeSec;
        this.text = text;
    }

    public int getTimeSec(){
        return this.timeSec;
    }

    public String getTimeString(){
        return this.timeString;
    }

    public String getText(){
        return this.text;
    }
}
