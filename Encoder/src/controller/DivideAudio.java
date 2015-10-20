package controller;

import statics.staticData;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015-10-09.
 */
public class DivideAudio {
    private static DivideAudio instance = null;

    private long bufferSize;
    private static boolean flag = true;

    public static DivideAudio getInstance(){
        if(instance==null){
            instance = new DivideAudio();
        }
        return instance;
    }

    public void divideFile(File inputFile){
        File dir = makeDir(inputFile.getPath().replace(inputFile.getName(),"")+inputFile.getName().split("[.]")[0]);

        try {
            ArrayList<Integer> cutSpotSet = findCutSpot(inputFile);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputFile);
            AudioFormat format = audioInputStream.getFormat();
            int size = 0;
            long past = 0;
            long writeSize = 0;
            long nowSize = 0;
            int j = 0;
            for(int i=0;i<cutSpotSet.size();i++){
                long cutSpot = cutSpotSet.get(i)*bufferSize;
                writeSize = cutSpot - past;
                nowSize += writeSize;
                past = cutSpot;

                if(nowSize <= 5*format.getFrameRate()){
                    continue;
                }

                if(size >= inputFile.length()){
                    break;
                }

                File spilitWav = new File(dir.getPath() + "/output" + (j + "") + ".wav");
                AudioInputStream input = new AudioInputStream(audioInputStream,format,nowSize);
                AudioSystem.write(input, AudioFileFormat.Type.WAVE, spilitWav);
                size += spilitWav.length();
                j++;
                writeSize = 0;
                nowSize = 0;
            }
            System.out.println("Split Audio Complete!!!");

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File makeDir(String dirName){

        File dir = new File(dirName);

        System.out.println(dir.getPath());
        dir.mkdirs();

        return dir;
    }

    private ArrayList<Integer> findCutSpot(File inputFile){
        ArrayList<Integer> cutSpotSet = new ArrayList<Integer>();
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputFile);

            AudioFormat format = audioInputStream.getFormat();
            bufferSize = format.getFrameSize() * (long) format.getFrameRate()/10;

            byte[] buffer = new byte[(int) bufferSize];


            for (int i=0;;i++)  {
                if(audioInputStream.read(buffer) == -1){
                    break;
                }
                float level = calculateLevel(buffer, format);
                if (level < 0.05) {
                    flag = !flag;
                    if (flag) {
                        cutSpotSet.add(i);
                    }
                }
            }
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cutSpotSet;
    }

    private float calculateLevel (byte[] buffer, AudioFormat format) {
        int max = 0;
        float level;
        boolean use16Bit = (format.getSampleSizeInBits() == 16);
        boolean signed = (format.getEncoding() ==
                AudioFormat.Encoding.PCM_SIGNED);
        boolean bigEndian = (format.isBigEndian());
        if (use16Bit) {
            for (int i=0; i<buffer.length; i+=2) {
                int value = 0;
                // deal with endianness
                int hiByte = (bigEndian ? buffer[i] : buffer[i+1]);
                int loByte = (bigEndian ? buffer[i+1] : buffer [i]);
                if (signed) {
                    short shortVal = (short) hiByte;
                    shortVal = (short) ((shortVal << 8) | (byte) loByte);
                    value = shortVal;
                } else {
                    value = (hiByte << 8) | loByte;
                }
                max = Math.max(max, value);
            } // for
        } else {
            // 8 bit - no endianness issues, just sign
            for (int i=0; i<buffer.length; i++) {
                int value = 0;
                if (signed) {
                    value = buffer [i];
                } else {
                    short shortVal = 0;
                    shortVal = (short) (shortVal | buffer [i]);
                    value = shortVal;
                }
                max = Math.max (max, value);
            } // for
        } // 8 bit
        // express max as float of 0.0 to 1.0 of max value
        // of 8 or 16 bits (signed or unsigned)
        if (signed) {
            if (use16Bit) { level = (float) max / staticData.MAX_16_BITS_SIGNED; }
            else { level = (float) max / staticData.MAX_8_BITS_SIGNED; }
        } else {
            if (use16Bit) { level = (float) max / staticData.MAX_16_BITS_UNSIGNED; }
            else { level = (float) max / staticData.MAX_8_BITS_UNSIGNED; }
        }
        //System.out.println(level);
        return level;
    } // calculateLevel
}
