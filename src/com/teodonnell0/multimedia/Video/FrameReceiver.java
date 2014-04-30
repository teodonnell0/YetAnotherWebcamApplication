package com.teodonnell0.multimedia.Video;

import com.googlecode.javacv.cpp.opencv_core;
import com.teodonnell0.multimedia.Settings;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;
import java.net.*;
import java.util.Iterator;

/**
 * Created by travis on 3/22/14.
 */
public class FrameReceiver implements Runnable{

    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedImage incomingFrame;
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
                }
            }catch (IOException e)
            {
                e.printStackTrace();
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
        }
    }

    private String readResponse()
    {
        StringBuilder stringBuilder = new StringBuilder(128);
        int in = -1;
        try
        {
            while((in = inputStream.read()) != '\n')
            {
                stringBuilder.append((char)in);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public boolean frameReady()
    {
        return frameAvailable;
    }

    public void stop()
    {
        run = false;
    }

    public Image getFrame()
    {
        if(frameAvailable)
        {
           // frameAvailable = false;
            return frame;
        }
        else
            return new BufferedImage(Settings.FRAME_WIDTH, Settings.FRAME_HEIGHT, BufferedImage.TYPE_INT_RGB);
    }
}
