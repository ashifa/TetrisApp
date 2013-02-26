/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

/*
?* @(#)Recoreder2.class 0.100 2011-7-27
?*
?* Copyright 2007 Smarch.
?* All rights reserved.
?*/

import javax.swing.JFrame;
import java.awt.Toolkit;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import javax.sound.sampled.AudioInputStream;
import javax.swing.JButton;
import java.io.File;
import java.awt.BorderLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;



/** 
?* @see java录音程序 
?* 
?* 
?*/
public class RecordPanel extends JPanel implements ActionListener {

    final int bufSize = 16384;
    Capture capture = new Capture();
    AudioInputStream audioInputStream;
    JButton captB;
    String fileName = "untitled";
    String errStr;
    double duration, seconds;
    File file;
//    ArrayList lines = new ArrayList();

    public RecordPanel() {
        setLayout(new BorderLayout());
        SoftBevelBorder sbb = new SoftBevelBorder(SoftBevelBorder.LOWERED);
        setBorder(new EmptyBorder(5, 5, 5, 5));
        JPanel p1 = new JPanel();
        p1.setBorder(sbb);
        p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(new EmptyBorder(10, 0, 5, 0));
        captB = addButton("Record", buttonsPanel, true);
        p1.add(buttonsPanel);
        add(p1);
    }

    public void open() {
    }

    public void close() {
        if (capture.thread != null) {
            captB.doClick(0);
        }
    }

    private JButton addButton(String name, JPanel p, boolean state) {
        JButton b = new JButton(name);
        b.addActionListener(this);
        b.setEnabled(state);
        p.add(b);
        return b;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj.equals(captB)) {
            if (captB.getText().startsWith("Record")) {
                file = null;
                capture.start();
                fileName = "untitled";
                captB.setText("Stop");
            } else {
//                lines.clear();
                capture.stop();
                captB.setText("Record");
            }
        }
    }

    public void createAudioInputStream(File file, boolean updateComponents) {
        if (file != null && file.isFile()) {
            try {
                System.out.println("run into createAudioInputStream");
                this.file = file;
                errStr = null;
                audioInputStream = AudioSystem.getAudioInputStream(file);
                fileName = file.getName();
                long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / audioInputStream.getFormat().getFrameRate());
                duration = milliseconds / 1000.0;
                if (updateComponents) {
                }
            } catch (Exception ex) {
                reportStatus(ex.toString());
            }
        } else {
            reportStatus("Audio file required.");
        }
    }

    public void saveToFile(String name, AudioFileFormat.Type fileType) {
        if (audioInputStream == null) {
            reportStatus("No loaded audio to save");
            return;
        } else if (file != null) {
            createAudioInputStream(file, false);
        }
// reset to the beginnning of the captured data 
        try {
            audioInputStream.reset();
        } catch (Exception e) {
            reportStatus("Unable to reset stream " + e);
            return;
        }
        File myfile = new File(fileName = name);
        try {
            if (AudioSystem.write(audioInputStream, fileType, myfile) == -1) {
                throw new IOException("Problems writing to file");
            }
        } catch (Exception ex) {
            reportStatus(ex.toString());
        }
    }

    private void reportStatus(String msg) {
        if ((errStr = msg) != null) {
            System.out.println(errStr);
        }
    }

    class Capture implements Runnable {

        TargetDataLine targetDataLine;
        Thread thread;

        public void start() {
            errStr = null;
            thread = new Thread(this);
            thread.setName("Capture");
            thread.start();
        }

        public void stop() {
            thread = null;
        }

        private void shutDown(String message) {
            if ((errStr = message) != null && thread != null) {                
                thread = null;
                captB.setText("Record");
                System.err.println(errStr);
            }
        }

        @Override
        public void run() {
            duration = 0;
            audioInputStream = null;
// get an AudioInputStream of the desired format for playback 
            AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
// define the required attributes for our line, 
            // and make sure a compatible line is supported.   
            // float rate = 44100f;   
            // int sampleSize = 16;   
            // String signedString = "signed";   
            // boolean bigEndian = true;   
            // int channels = 2;   
            float rate = 8000f;
            int sampleSize = 8;
            boolean bigEndian = true;
            int channels = 1;
            AudioFormat format = new AudioFormat(encoding, rate, sampleSize,
                    channels, (sampleSize / 8) * channels, rate, bigEndian);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                shutDown("Line matching " + info + " not supported.");
                return;
            }
            // get an AudioInputStream of the desired format for playback   
            try {
                targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
                targetDataLine.open(format, targetDataLine.getBufferSize());
            } catch (LineUnavailableException ex) {
                shutDown("Unable to open the line: " + ex);
                return;
            } catch (SecurityException ex) {
                shutDown(ex.toString());
                return;
            } catch (Exception ex) {
                shutDown(ex.toString());
                return;
            }
            // play back the captured audio data   
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = targetDataLine.getBufferSize() / 8;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            byte[] data = new byte[bufferLengthInBytes];
            int numBytesRead;
            targetDataLine.start();
            while (thread != null) {
                if ((numBytesRead = targetDataLine.read(data, 0, bufferLengthInBytes)) == -1) {
                    break;
                }
                out.write(data, 0, numBytesRead);
            }
            // we reached the end of the stream. stop and close the line.   
            targetDataLine.stop();
            targetDataLine.close();
            targetDataLine = null;
            // stop and close the output stream   
            try {
                out.flush();
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // load bytes into the audio input stream for playback   
            byte audioBytes[] = out.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
            audioInputStream = new AudioInputStream(bais, format,
                    audioBytes.length / frameSizeInBytes);
            long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / format.getFrameRate());
            duration = milliseconds / 1000.0;
            try {
                audioInputStream.reset();
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
            saveToFile("d:/record.wav", AudioFileFormat.Type.WAVE);
        }
    }

    public static void main(String[] args) {
        RecordPanel test = new RecordPanel();
        test.open();
        JFrame f = new JFrame("Capture");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add("Center", test);
        f.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = 500;
        int h = 340;
        f.setLocation(screenSize.width / 2 - w / 2, screenSize.height / 2 - h
                / 2);
        f.setSize(w, h);
        f.setVisible(true);
    }
}
