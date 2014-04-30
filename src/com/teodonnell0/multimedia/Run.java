/*
 * Copyright (c) 2014.
 * Travis O'Donnell
 * Frostburg State University
 * Computer Science
 */

package com.teodonnell0.multimedia;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;

public class Run {

    protected static boolean isServer;
    protected static InetAddress ip;

    protected static final int EFFECT_NORMAL = 0;
    protected static final int EFFECT_GRAYSCALE = 1;
    protected static final int EFFECT_ROTATE90 = 2;
    protected static final int EFFECT_ROTATE180 = 3;
    protected static final int EFFECT_ROTATE270 = 4;

    private static Server serverFrame;
    private static Client clientFrame;
    public static void main(String[] args) {
        new Setup();
        JFrame frame = new JFrame();
        frame.setSize(640, 480);
        frame.setLayout(new BorderLayout());

        if(isServer)
        {
            serverFrame = new Server(ip);
            JMenuBar menuBar = menuBarSetup();
            frame.add(menuBar, BorderLayout.NORTH);
            frame.add(serverFrame, BorderLayout.SOUTH);

        }else
        {
            clientFrame = new Client(ip);
            JMenuBar menuBar = menuBarSetup();
            frame.add(menuBar, BorderLayout.NORTH);
            frame.add(clientFrame, BorderLayout.SOUTH);
        }

        frame.setTitle("Connected to: " + ip.getHostAddress());
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if(isServer)
            serverFrame.refreshGUI();
        else
            clientFrame.refreshGUI();



    }

    private static JMenuBar menuBarSetup()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu effectsMenu = new JMenu("Effects");
        JMenuItem normalItem = new JMenuItem("Normal");
        JMenuItem grayScaleItem = new JMenuItem("Gray Scale");
        JMenuItem rotate90Item = new JMenuItem("Rotate 90");
        JMenuItem rotate180Item = new JMenuItem("Rotate 180");
        JMenuItem rotate270Item = new JMenuItem("Rotate 270");

        /** Action Listener setup */
        normalItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNormalEffect();
            }
        });
        grayScaleItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onGrayscaleEffect();
            }
        });
        rotate90Item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRotateEffect(90);

            }
        });
        rotate180Item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRotateEffect(180);
            }
        });
        rotate270Item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRotateEffect(270);
            }
        });


        /** Add items onto menu **/
        effectsMenu.add(normalItem);
        effectsMenu.add(grayScaleItem);
        effectsMenu.add(rotate90Item);
        effectsMenu.add(rotate180Item);
        effectsMenu.add(rotate270Item);

        menuBar.add(effectsMenu);
        return menuBar;
    }


    private static void onNormalEffect() {
        if(isServer)
            serverFrame.changeEffect(EFFECT_NORMAL);
        else
            clientFrame.changeEffect(EFFECT_NORMAL);

    }

    private static void onGrayscaleEffect() {
        if(isServer)
            serverFrame.changeEffect(EFFECT_GRAYSCALE);
        else
            clientFrame.changeEffect(EFFECT_GRAYSCALE);
    }

    private static void onRotateEffect(int e) {
        switch (e) {
            case 90:
                if(isServer)
                    serverFrame.changeEffect(EFFECT_ROTATE90);
                else
                    clientFrame.changeEffect(EFFECT_ROTATE90);
                break;
            case 180:
                if(isServer)
                    serverFrame.changeEffect(EFFECT_ROTATE180);
                else
                    clientFrame.changeEffect(EFFECT_ROTATE180);
                break;
            case 270:
                if(isServer)
                    serverFrame.changeEffect(EFFECT_ROTATE270);
                else
                    clientFrame.changeEffect(EFFECT_ROTATE270);
                break;

        }
    }

}
