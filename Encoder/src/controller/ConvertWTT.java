package controller;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;

/**
 * Created by user on 2015-10-19.
 */
public class ConvertWTT {
    public static ConvertWTT instance = null;
    public static ConvertWTT getInstance(){

        if(instance == null){
            instance = new ConvertWTT();
        }
        return instance;
    }

    public static void waveToText(String name) throws Exception
    {
        int i=0,num=5,hour=0,min=0,sec=0;
        int frameSize=0;

        String results_w =  "";
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
        configuration.setSampleRate(16000);
        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
        for(i=0;i<num;i++){
            File file = new File(name+"/output"+i+".wav");
            recognizer.startRecognition(new FileInputStream(file));
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioInputStream.getFormat();
            frameSize = (int)format.getFrameRate();
            int time = (int)audioInputStream.getFrameLength()/frameSize;
            System.out.println(time);


            if(sec+time>=60) {
                min += (sec+time)/60;
                sec = sec+time%60;
                if(min>=60)
                {
                    hour+=min/60;
                    min%=60;
                }
            }
            else
                sec+=time;

            results_w = results_w+hour+":"+min+":"+sec+"# ";
            SpeechResult result = recognizer.getResult();
            while ((result = recognizer.getResult()) != null) {
                System.out.println(i);
                System.out.println(result.getHypothesis());
                results_w = results_w + result.getHypothesis() + " ";
            }
            recognizer.stopRecognition();
            results_w = results_w + "\n";
        }
        try
        {
            FileWriter fw = new FileWriter(name+".txt"); // 절대주소 경로 가능
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(results_w);
            bw.close();
        }
        catch (IOException e)
        {
            System.err.println(e); // 에러가 있다면 메시지 출력
            System.exit(1);
        }

        System.out.println(results_w);
    }
}
