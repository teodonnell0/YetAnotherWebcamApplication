package com.teodonnell0.multimedia.Audio;

import com.teodonnell0.multimedia.Settings;

import javax.sound.sampled.*;import javax.sound.sampled.AudioFileFormat;import javax.sound.sampled.AudioFormat;import javax.sound.sampled.AudioSystem;import javax.sound.sampled.DataLine;import javax.sound.sampled.LineUnavailableException;import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.InterruptedException;import java.lang.Override;import java.lang.Runnable;import java.lang.Thread;import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by travis on 3/22/14.
 */
public class Microphone implements Runnable {
    private TargetDataLine targetDataLine;
    private final AudioFileFormat.Type WAVE = AudioFileFormat.Type.WAVE;
    private InputStream inputStream;
    private byte[] buffer;
    private Socket socket;
    private ServerSocket serverSocket;
    private volatile boolean run;

    private OutputStream outputStream;



    public Microphone(OutputStream os) {
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, getFormat());
        this.outputStream = os;

        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(getFormat());
            targetDataLine.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void run() {
        run = true;
        while (true) {
            int bytes_read = 0;
            int bytes_counted = 0;
            buffer = new byte[Settings.BUFFER_SIZE];
            try {
                while (run) {
                    bytes_read = targetDataLine.read(buffer, 0, Settings.BUFFER_SIZE);
                    if(bytes_read > 0)
                    {
                        outputStream.write(buffer, 0, Settings.BUFFER_SIZE);
                        bytes_counted += bytes_read;
                        Thread.sleep(20, 0);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            break;
        }

    }



    private AudioFormat getFormat() {
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(Settings.SAMPLE_RATE, sampleSizeInBits, channels, signed, bigEndian);
    }
}
