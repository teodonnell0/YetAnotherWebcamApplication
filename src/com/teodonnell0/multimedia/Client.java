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
import java.net.Socket;

/**
 * Created by travis on 3/22/14.
 */
public class Client extends JPanel {
    protected static InetAddress hostIP;
    protected static boolean isServer;

    private static Socket videoSocket1, videoSocket2;
    private static Socket audioSocket;

    private static Thread webcamStreamThread;
    private static Thread frameRecvThread;
    private static Thread audioStreamThread;

    private Webcam webcamStreamer;
    private FrameReceiver frameReceiver;

    private static Thread audioRecvThread;
    private Microphone microphone;
    private AudioReceiver audioReceiver;


    public Client(InetAddress ip)
    {
        System.out.println("Client");
        this.hostIP = ip;
        this.setPreferredSize(new Dimension(640, 480));
        this.setBackground(Color.BLACK);
        initialize();
    }


    public void initialize()
    {
        try
        {
            videoSocket1 = new Socket(hostIP, Settings.VIDEO_PORT);
            videoSocket2 = new Socket(hostIP, 11111);
            audioSocket = new Socket(hostIP, Settings.AUDIO_PORT);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            webcamStreamer = new Webcam(videoSocket2);
            frameReceiver = new FrameReceiver(videoSocket1);

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
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000D / 60D;

        int ticks = 0;
        int frames = 0;

        long lastTimer = System.currentTimeMillis();
        double delta = 0;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;
            boolean shouldRender = false;

            while (delta >= 1) {
                ticks++;
                delta -= 1;
                shouldRender = true;
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (shouldRender) {
                frames++;
                repaint();
            }

            if (System.currentTimeMillis() - lastTimer >= 1000) {
                lastTimer += 1000;
                frames = 0;
                ticks = 0;
            }

        }
    }

    protected void changeEffect(int i)
    {
        webcamStreamer.setEffect(i);
    }

}
