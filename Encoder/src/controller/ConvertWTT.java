package controller;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import ui.MainController;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;

/**
 * Created by user on 2015-10-19.
 */
public class ConvertWTT {
    public static ConvertWTT instance = null;
    private Configuration configuration;
    private StreamSpeechRecognizer recognizer;
    private int hour=0,min=0,sec=0,num=300;
    private String resultsTxt =  "";
    private File file;

    public static ConvertWTT getInstance(){
        if(instance == null){
            instance = new ConvertWTT();

        }
        return instance;
    }

    public ConvertWTT(){
        configuration = new Configuration();
        configuration.setAcousticModelPath("file:model_parameters/AcousicModeling.cd_cont_200");
        configuration.setDictionaryPath("AcousticModeling.dic");
        configuration.setLanguageModelPath("AcousticModeling.lm.dmp");
        configuration.setSampleRate(16000);
        try {
            recognizer = new StreamSpeechRecognizer(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void waveToText(String name,int i) throws Exception
    {
        int frameSize=0;

        if(i==0) {
            resultsTxt = "";
            hour=0;
            sec=0;
            min=0;
            num=10;
        }

        try {
            recognizer = new StreamSpeechRecognizer(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }

        file = new File(name+"/output"+i+".wav");

        recognizer.startRecognition(new FileInputStream(file));
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioInputStream.getFormat();

        frameSize = (int)format.getFrameRate();
        int time = (int)audioInputStream.getFrameLength()/frameSize;
        if(num!=i) {
            resultsTxt = resultsTxt +"\n"+ hour + ":" + min + ":" + sec + "# ";
            if (sec + time >= 60) {
                min += (sec + time) / 60;
                sec = time % 60;
                if (min >= 60) {
                    hour += min / 60;
                    min %= 60;
                }
            } else {
                sec += time;
            }
            num=i;
        }
        SpeechResult result;

        while ((result = recognizer.getResult()) != null) {
            //System.out.println(result.getHypothesis());
            resultsTxt = resultsTxt + result.getHypothesis() + " ";
        }

        recognizer.stopRecognition();
//        if(!(resultsTxt.split("[#]")[1].equals("  "))){
//            resultsTxt = resultsTxt + "\n";
//        }
//        resultsTxt = resultsTxt + "\n";
//        System.out.println(resultsTxt);
    }
    public void writeTxt(String name)
    {
        try
        {
            FileWriter fw = new FileWriter(name+".txt");
            BufferedWriter bw = new BufferedWriter(fw);
            MainController.eventFromProgress(name+".txt#output");
            bw.write(resultsTxt);
            bw.close();
        }
        catch (IOException e)
        {
            System.err.println(e);
            System.exit(1);
        }
    }
}
