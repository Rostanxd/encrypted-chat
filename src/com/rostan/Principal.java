package com.rostan;

import com.rostan.view.ChooseClient;

import javax.swing.*;

public class Principal {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Encrypted Chat");
        frame.setContentPane(new ChooseClient().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
