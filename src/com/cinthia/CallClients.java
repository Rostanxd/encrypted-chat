package com.cinthia;

import com.cinthia.view.Client;

import javax.swing.*;

public class CallClients {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Client Encoder");
        frame.setContentPane(new Client("ENC").panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        JFrame frame2 = new JFrame("Client Decoder");
        frame2.setContentPane(new Client("DEC").panelMain);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.pack();
        frame2.setVisible(true);
    }
}
