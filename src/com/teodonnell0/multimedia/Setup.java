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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Setup extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField txt_ip;
    private JLabel localIP;
    private JLabel publicIP;
    private JCheckBox checkBox;
    private JLabel lbl_publicIP;
    private JLabel lbl_localIP;
    private JLabel lbl_ipToConnectTo;

    public Setup() {
        getMyLocalIP();
        getMyPublicIP();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txt_ip.getText().isEmpty()) {
                    txt_ip.setBackground(Color.RED);
                } else {
                    String ip = txt_ip.getText();
                    try {
                        Run.ip = InetAddress.getByName(ip);
                    } catch (UnknownHostException ex) {
                        ex.printStackTrace();
                    }
                    Run.isServer = checkBox.isSelected();
                    dispose();
                }
            }
        });

        pack();
        setVisible(true);
    }


    private void getMyLocalIP() {
        String command = null;

        /*
        Since ifconfig is depreciated nowadays, we try to find the command 'ip' first, if not we try for ifconfig.
        ifconfig is now depreciated, but still usable if absolutely needed
         */
        if (System.getProperty("os.name").equals("Linux") || System.getProperty("os.name").equals("unix")) {
            if (new File("/bin/ip").exists())
                command = "ip -f inet addr";
            else if (new File("/bin/ifconfig").exists())
                command = "ifconfig";
        } else if (System.getProperty("os.name").equals("Mac OS X"))
            command = "ifconfig";
        else    //Must be running windows
            command = "ipconfig";

        // Attempt to run command
        try {
            Process process = Runtime.getRuntime().exec(command);
            Scanner scanner = new Scanner(process.getInputStream());
            StringBuilder stringBuilder = new StringBuilder("");
            while (scanner.hasNext())
                stringBuilder.append(scanner.next());
            String bufferedText = stringBuilder.toString();
            Pattern pattern = Pattern.compile("192\\.168\\.[0-9]{1,3}\\.[0-9]{1,3}");   //local IP will always be found before broadcast IP
            Matcher matcher = pattern.matcher(bufferedText);
            matcher.find();
            localIP.setText(new String(matcher.group()));
        } catch (IOException e) {
            if (System.getProperty("os.name").equals("Linux") || System.getProperty("os.name").equals("unix"))
                JOptionPane.showMessageDialog(this, "Could not run command '" + command + "'.");
            else
                JOptionPane.showMessageDialog(this, "Could not run command '" + command + "'.");
        }
    }

    private void getMyPublicIP() {
        try {
            URL ipFetcher = new URL("http://ipaddr.es/automate.php");
            BufferedReader in = new BufferedReader(new InputStreamReader(ipFetcher.openStream()));
            publicIP.setText(in.readLine());
        } catch (MalformedURLException e) {
            JOptionPane.showMessageDialog(this, "Could not retrieve text from URL http://ipaddr.es/automate.php\nServer down?");
            e.printStackTrace();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "This error should never occur as long as we got a hold of URL");
            e.printStackTrace();
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
