/*
 * Copyright (c) 2014.
 * Travis O'Donnell
 * Frostburg State University
 * Computer Science
 */

package com.teodonnell0.multimedia.Video;

import com.teodonnell0.multimedia.Settings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

/**
 * Created by travis on 3/22/14.
 */
public class FrameReceiver implements Runnable{

    private InputStream inputStream;
    private OutputStream outputStream;
    private volatile BufferedImage frame;

    private boolean frameAvailable;
    private volatile boolean run;
    private ByteArrayOutputStream byteArrayOutputStream;
    private ByteArrayInputStream byteArrayInputStream;
    public FrameReceiver(Socket socket)
    {
        try
        {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        frameAvailable = false;
    }

    @Override
    public void run()
    {


        run = true;
        while(run)
        {
            try
            {
                requestFrame();
                int expectedBytes = Integer.parseInt(readResponse());
                byteArrayOutputStream = new ByteArrayOutputStream(expectedBytes);
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                int bytesIn = 0;

                while(bytesRead < expectedBytes)
                {
                    bytesIn = inputStream.read(buffer);
                    bytesRead += bytesIn;
                    byteArrayOutputStream.write(buffer, 0, bytesIn);
                }

                byteArrayOutputStream.close();
                byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                BufferedImage incomingFrame = ImageIO.read(byteArrayInputStream);
                frame = incomingFrame;
                frameAvailable = true;
                byteArrayInputStream.close();
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }catch (IOException e)
            {
                e.printStackTrace();
                System.exit(0);
            }
        }

    }

    private void requestFrame()
    {
        try
        {
            outputStream.write(("next" + "\n").getBytes());
            outputStream.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private String readResponse()
    {
        StringBuilder stringBuilder = new StringBuilder(128);
        try
        {
            int in = -1;
            while((in = inputStream.read()) != '\n')
            {
                stringBuilder.append((char)in);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        return stringBuilder.toString();
    }

    public boolean frameReady()
    {
        return frameAvailable;
    }


    public Image getFrame()
    {
        if(frameAvailable)
        {
            return frame;
        }
        else
            return new BufferedImage(Settings.FRAME_WIDTH, Settings.FRAME_HEIGHT, BufferedImage.TYPE_INT_RGB);
    }
}
