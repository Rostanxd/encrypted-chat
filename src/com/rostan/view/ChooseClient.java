package com.rostan.view;

import com.rostan.CallClients;
import com.rostan.CallServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChooseClient {
    private JComboBox clientCombo;
    public JPanel panelMain;
    private JButton continueButton;

    public ChooseClient() {

        continueButton.setText("Continue");
        clientCombo.addItem("Server");
        clientCombo.addItem("Encoder/Decoder");

        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("Server".equals(clientCombo.getSelectedItem())) {
                    CallServer.main(null);
                } else {
                    CallClients.main(null);
                }
            }
        });
    }
}
