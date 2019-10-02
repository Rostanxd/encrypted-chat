package com.cinthia.view;

import com.cinthia.model.ChatMessage;
import com.cinthia.model.ClientChat;
import com.cinthia.model.ListenServerThread;
import com.cinthia.util.RivestShamirAdleman;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private JTextField hostText;
    private JTextField portText;
    private JButton connectButton;
    public JPanel panelMain;
    public JTextField messageTxt;
    public JTextField keyTxt;
    public JButton sendButton;
    private JLabel interactingLabel;
    public JPanel panelColor;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Socket socket;

    public String server, type;
    private int port;
    public boolean connected;
    private ClientChat clientChat;
    private ListenServerThread listenServerThread;

//    public Client(String type) {
//        this.server = "127.0.0.1";
//        this.port = 1000;
//        this.type = type;
//
//        setDefaultProperties();
//
//        connectButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (connected) {
//                    disconnect();
//                } else {
//                    connect();
//                }
//            }
//        });
//        sendButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                int key;
//                try {
//                    key = Integer.parseInt(keyTxt.getText());
//                    if (key == 0) {
//                        JOptionPane.showMessageDialog(null, "Please type a key to continue!");
//                    } else {
//                        writeMsgToServer(encodingMessageRSA(key, messageTxt.getText()));
//                        keyTxt.setEditable(false);
//                        messageTxt.setEditable(false);
//                        sendButton.setEnabled(false);
//                    }
//                } catch (NumberFormatException nfe) {
//                    JOptionPane.showMessageDialog(null, "Error key!");
//                    System.out.println("NumberFormatException: " + nfe.getMessage());
//                }
//            }
//        });
//    }
//
//    public void setDefaultProperties() {
//        if (type.equals("ENC")) {
//            this.clientChat = new ClientChat(type, "Alice");
//            this.interactingLabel.setText("Here you can write a message, encrypted and send it to server \n" +
//                    "to turn a light.");
//            this.sendButton.setText("Turn on");
//        } else {
//            this.clientChat = new ClientChat(type, "Bob");
//            this.interactingLabel.setText("Here you can turn off the light sending the correct message.");
//            this.sendButton.setText("Turn off");
//        }
//
//        connected = false;
//
//        hostText.setText(this.server);
//        portText.setText(String.valueOf(this.port));
//        connectButton.setText("Connect");
//
//        hostText.setEditable(true);
//        portText.setEditable(true);
//        connectButton.setEnabled(true);
//
//        keyTxt.setEditable(false);
//        messageTxt.setEditable(false);
//        sendButton.setEnabled(false);
//    }
//
//    private void connect() {
//        connected = true;
//        connectButton.setText("Disconnect");
//        hostText.setEditable(false);
//        portText.setEditable(false);
//
//        //  Setting host and port
//        this.server = hostText.getText();
//        try {
//            this.port = Integer.parseInt(portText.getText());
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(null,
//                    "Invalid port: " + e.getMessage());
//        }
//
//        if (type.equals("ENC")) {
//            keyTxt.setEditable(true);
//            messageTxt.setEditable(true);
//            sendButton.setEnabled(true);
//        } else {
//            keyTxt.setEditable(false);
//            messageTxt.setEditable(false);
//            sendButton.setEnabled(false);
//        }
//
//        try {
//            this.socket = new Socket(this.server, this.port);
//            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
//            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            System.out.println("Connection accepted " + socket.getInetAddress()
//                    + ":" + socket.getPort());
//        } catch (IOException e) {
//            connected = false;
//            connectButton.setText("Connect");
//            hostText.setEditable(true);
//            portText.setEditable(true);
//            keyTxt.setEditable(false);
//            messageTxt.setEditable(false);
//            sendButton.setEnabled(false);
//            JOptionPane.showMessageDialog(null,
//                    "Error connecting to server: " + e.getMessage());
//
//        }
//
//        //  Creating a thread to listen server
//        listenServerThread = new ListenServerThread(this, this.socket, this.objectInputStream,
//                this.objectOutputStream);
//        listenServerThread.start();
//
//        //  Send a message to the server
//        try {
//            objectOutputStream.writeObject(this.clientChat);
//        } catch (IOException e) {
//            connected = false;
//            connectButton.setText("Connect");
//            hostText.setEditable(true);
//            portText.setEditable(true);
//            keyTxt.setEditable(false);
//            messageTxt.setEditable(false);
//            sendButton.setEnabled(false);
//            JOptionPane.showMessageDialog(null,
//                    "Exception doing login: " + e.getMessage());
//        }
//    }
//
//    public void disconnect() {
//        connected = false;
//        connectButton.setText("Connect");
//        hostText.setEditable(true);
//        portText.setEditable(true);
//        keyTxt.setEditable(false);
//        messageTxt.setEditable(false);
//
//        keyTxt.setText("");
//        messageTxt.setText("");
//
//        try {
//            objectOutputStream.writeObject(new ChatMessage(ChatMessage.LOGOUT, ""));
//        } catch (IOException e) {
//            System.out.println("Error writing to the server: username; " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        try {
//            listenServerThread.closeStreams();
//            System.out.println("Disconnection successful from the server.");
//        } catch (IOException e) {
//            connected = true;
//            connectButton.setText("Disconnect");
//            hostText.setEditable(false);
//            portText.setEditable(false);
//            keyTxt.setEditable(true);
//            messageTxt.setEditable(true);
//            System.out.println("Error disconnecting from the server: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private void writeMsgToServer(String message) {
//        try {
//            if (this.type.equals("ENC")) {
//                String encrypted = RivestShamirAdleman.encode(Integer.parseInt(this.keyTxt.getText()), message);
//                objectOutputStream.writeObject(new ChatMessage(ChatMessage.ENCRYPTED_MESSAGE, encrypted));
//            } else {
//                objectOutputStream.writeObject(new ChatMessage(ChatMessage.UNLOCK_MESSAGE, message));
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private String encodingMessageRSA(int key, String message) {
//        return message;
//    }
//
//    public void showMessage(String message) {
//        JOptionPane.showMessageDialog(null, message);
//    }
}
