/*
 * Copyright (c) 2014.
 * Travis O'Donnell
 * Frostburg State University
 * Computer Science
 */

package com.teodonnell0.multimedia.Video;


import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.teodonnell0.multimedia.Settings;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;


/**
 * Created by travis on 3/22/14.
 */
public class Webcam implements Runnable {


    private int videoEffect;

    private IplImage frame;
    private volatile OpenCVFrameGrabber openCVFrameGrabber;

    private OutputStream outputStream;
    private InputStream inputStream;


    private volatile boolean run;


    public Webcam(Socket socket)
    {
        try
        {
            this.outputStream = socket.getOutputStream();
            this.inputStream = socket.getInputStream();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        openCVFrameGrabber = new OpenCVFrameGrabber(0);
        openCVFrameGrabber.setImageWidth(Settings.FRAME_WIDTH);
        openCVFrameGrabber.setImageHeight(Settings.FRAME_HEIGHT);
        openCVFrameGrabber.setFrameRate(20);

        videoEffect = 0;

        try
        {
            openCVFrameGrabber.start();
            frame = openCVFrameGrabber.grab();
        } catch (FrameGrabber.Exception e)
        {
            e.printStackTrace();
        }

    }

    public IplImage grabFrame()
    {
        try {
            frame = openCVFrameGrabber.grab();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
        switch(videoEffect)
        {
            case 0:
                return frame;
            case 1:
                return covertOriginalToGrayscale(frame);
            case 2:
                return rotate(frame, 90);
            case 3:
                return rotate(frame, 180);
            case 4:
                return rotate(frame, 270);
            default:
                return frame;
        }
    }


    public void setEffect(int i)
    {
        videoEffect = i;
    }


    private IplImage covertOriginalToGrayscale(IplImage img)
    {
        IplImage grayImg = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 1);
        cvCvtColor(img, grayImg, CV_BGR2GRAY);
        return grayImg;
    }


    private IplImage rotate(IplImage img, double angle)
    {
        IplImage clonedImg = cvCloneImage(img);
        IplImage rotatedImg = cvCreateImage(cvGetSize(clonedImg), clonedImg.depth(), clonedImg.nChannels());

        CvMat mapMatrix = cvCreateMat(2, 3, CV_32FC1);

        CvPoint2D32f centerPoint = new CvPoint2D32f();
        centerPoint.x(clonedImg.width() / 2);
        centerPoint.y(clonedImg.height() / 2);

        cv2DRotationMatrix(centerPoint, angle, 1.0, mapMatrix);

        cvWarpAffine(clonedImg, rotatedImg, mapMatrix, CV_INTER_CUBIC + CV_WARP_FILL_OUTLIERS, cvScalarAll(170));
        cvReleaseImage(clonedImg);
        cvReleaseMat(mapMatrix);

        return rotatedImg;
    }


    /**
     *
     */
    @Override
    public void run()
    {
        run = true;
        while(run)
        {
            try
            {
                String request = readRequest();
                if(request.equals(request))
                {
                    BufferedImage bufferedImage = grabFrame().getBufferedImage();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "JPG", byteArrayOutputStream);
                    byteArrayOutputStream.close();
                    outputStream.write((Integer.toString(byteArrayOutputStream.size()) + "\n").getBytes());
                    outputStream.write(byteArrayOutputStream.toByteArray());
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                Thread.sleep(5);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reads incoming data from InputStream
     * Data read contain data length of next incoming packet
     *
     * @return
     */
    private String readRequest()
    {
        StringBuilder stringBuilder = new StringBuilder(128);
        int in = -1;
        try
        {
            while((in = inputStream.read()) != '\n')
            {
                stringBuilder.append((char)in);
            }
        } catch(IOException e)
        {
            e.printStackTrace();
        }
        System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }


}








