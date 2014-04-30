/*
 * Copyright (c) 2014.
 * Travis O'Donnell
 * Frostburg State University
 * Computer Science
 */

package com.teodonnell0.multimedia.Audio;

import com.teodonnell0.multimedia.Settings;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by travis on 3/22/14.
 */
public class AudioReceiver implements Runnable{

    private SourceDataLine sourceDataLine;
    private InputStream inputStream;
    private byte[] buffer;
    private volatile boolean run;



    public AudioReceiver(InputStream is)
    {
            inputStream = is;
    }

    @Override
    public void run()
    {
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, getFormat());

        try
        {
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(getFormat());
            sourceDataLine.start();
        } catch (LineUnavailableException e)
        {
            e.printStackTrace();
        }

        run = true;
        int count;
        while(run)
        {
            buffer = new byte[Settings.BUFFER_SIZE];
            try
            {
                while (inputStream.read(buffer) != -1)
                {

                    sourceDataLine.write(buffer, 0, buffer.length);
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        sourceDataLine.drain();
        sourceDataLine.close();


    }



    private AudioFormat getFormat() {
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(Settings.SAMPLE_RATE, sampleSizeInBits, channels, signed, bigEndian);
    }
}
