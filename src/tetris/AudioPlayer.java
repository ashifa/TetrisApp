/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author 305020571
 */
public enum AudioPlayer implements Runnable{

    ROTATE(new File("rotate.wav")),
    MOVE(new File("move.wav")),
    FALLDOWN(new File("falldown.wav")),
    CLEAR(new File("clear.wav")),
    DOUBLEKILL(new File("doublekill.caf")),
    TRIPLEKILL(new File("triplekill.caf")),
    GODLIKE(new File("godLike.caf"));
    
    private AudioInputStream audioInputStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceDataLine;
    private DataLine.Info dLi;
    private int bufferSize = 1024 * 1024;
    private byte[] buffer = new byte[bufferSize];
    private int lengthOfReadByte;
    private static ExecutorService es = Executors.newFixedThreadPool(8);

private    AudioPlayer(File file) {
        try {
            audioInputStream = AudioSystem.getAudioInputStream(file);
            audioFormat = audioInputStream.getFormat();

            dLi = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dLi);
            lengthOfReadByte = this.audioInputStream.read(buffer);
            this.audioInputStream.close();
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(AudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(AudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void play() {
        es.submit(this);
    }

    @Override
    public  void run() {

        try {
            this.sourceDataLine.open(audioFormat, bufferSize);
            this.sourceDataLine.start();
            this.sourceDataLine.write(buffer, 0, lengthOfReadByte);
        } catch (Exception e) {
            System.exit(0);
        } finally {
            this.sourceDataLine.drain();
            this.sourceDataLine.close();
        }
    }

    public static void main(String[] args) throws Exception {
        AudioPlayer.GODLIKE.play();
 
        
    }

 
}
