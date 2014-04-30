package com.teodonnell0.multimedia;

import com.teodonnell0.multimedia.Audio.AudioReceiver;
import com.teodonnell0.multimedia.Audio.Microphone;
import com.teodonnell0.multimedia.Video.FrameReceiver;
import com.teodonnell0.multimedia.Video.Webcam;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by travis on 3/22/14.
 */
public class Server extends JPanel {
    protected static InetAddress hostIP;

    private static final int VIDEO_PORT = 48107;
    private static final int AUDIO_PORT = 38107;

    private static Socket videoSocket1, videoSocket2;
    private static Socket audioSocket;

    private static Thread webcamStreamThread;
    private static Thread frameRecvThread;
    private static Thread audioStreamThread;
    private static Thread audioRecvThread;

    private static Webcam webcamStreamer;
    private FrameReceiver frameReceiver;

    private Microphone microphone;
    private AudioReceiver audioReceiver;


    public Server(InetAddress ip)
    {
        System.out.println("Server");
        this.hostIP = ip;
        this.setPreferredSize(new Dimension(640, 480));
        this.setBackground(Color.BLACK);
        initialize();
    }


    public void initialize()
    {
        try
        {
            ServerSocket videoServerSocket1 = new ServerSocket(VIDEO_PORT);
            ServerSocket videoServerSocket2 = new ServerSocket(11111);
            ServerSocket audioServerSocket = new ServerSocket(AUDIO_PORT);
            videoSocket1= videoServerSocket1.accept();
            videoSocket2 = videoServerSocket2.accept();
            audioSocket = audioServerSocket.accept();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            webcamStreamer = new Webcam(videoSocket1);
            frameReceiver = new FrameReceiver(videoSocket2);

            microphone = new Microphone(audioSocket.getOutputStream());
            audioReceiver = new AudioReceiver(audioSocket.getInputStream());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        webcamStreamThread = new Thread(webcamStreamer);
        webcamStreamThread.start();

        frameRecvThread = new Thread(frameReceiver);
        frameRecvThread.start();

        audioStreamThread = new Thread(microphone);
        audioStreamThread.start();


        audioRecvThread = new Thread(audioReceiver);
        audioRecvThread.start();

    }


    public void paint(Graphics g)
    {
        Image receivedImage;
        Image webcamImage;
        super.updateUI();
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;
        if(frameReceiver.frameReady())
        {
            receivedImage = frameReceiver.getFrame();
            graphics2D.drawImage(receivedImage, 0, 0, null);
        }
        BufferedImage bi = webcamStreamer.grabFrame().getBufferedImage();
        webcamImage = bi.getScaledInstance(160, 120, BufferedImage.SCALE_SMOOTH);
        graphics2D.drawImage(webcamImage, 470, 350, null);

        //Add a border to preview frame
        graphics2D.setColor(Color.LIGHT_GRAY);
        graphics2D.drawRect(470, 350, 160, 120);
    }


    public void refreshGUI()
    {

        int frames = 0;
        double unprocessedSeconds = 0; // accumulates time
        long previousTime = System.nanoTime(); // start counting

        double secondsPerTick = 1 / 60.0;

        int tickCount = 0;
        boolean ticked = false;
        while(true) {
            long currentTime = System.nanoTime();
            long passedTime = currentTime - previousTime;
            previousTime = currentTime;
            unprocessedSeconds = unprocessedSeconds + (passedTime /
                    1000000000.0);

            while (unprocessedSeconds > secondsPerTick) {
                unprocessedSeconds -= secondsPerTick;
                ticked = true;
                // signal to render a frame
                tickCount++;
                if (tickCount % 60 == 0) {
                    previousTime += 1000;
                    frames = 0;
                }
            }
            if (ticked) {
                repaint(); // handles rendering things to the screen
                frames++;
                ticked = false;
            }
        }

    }

    public void changeEffect(int i)
    {
        webcamStreamer.setEffect(i);
    }
}
