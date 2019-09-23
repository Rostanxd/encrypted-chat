package com.rostan.view;

import com.rostan.model.ListenServerThread;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private JTextField hostText;
    private JTextField portText;
    private JButton connectButton;
    private JPanel panelMain;
    private JTextField messageTxt;
    private JTextField keyTxt;
    private JButton sendButton;
    private JTextField encryptedText;

    private ListenServerThread listenServerThread;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Socket socket;

    private String server, username;
    private int port;
    private boolean connected;
    InetAddress ipAddr;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Server");
        frame.setContentPane(new Client().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public Client() {
        this.server = "127.0.0.1";
        this.port = 1000;

        hostText.setText(this.server);
        portText.setText(String.valueOf(this.port));
        keyTxt.setEditable(false);
        messageTxt.setEditable(false);
        encryptedText.setEditable(false);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connected) {
                    disconnect();
                } else {
                    connect();
                }
            }
        });
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void connect() {
        connected = true;
        connectButton.setText("Disconnect");
        hostText.setEditable(false);
        portText.setEditable(false);
        keyTxt.setEditable(true);
        messageTxt.setEditable(true);

        try {
            this.socket = new Socket(this.server, this.port);
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Connection accepted " + socket.getInetAddress()
                    + ": " + socket.getPort());
        } catch (IOException e) {
            connected = false;
            connectButton.setText("Connect");
            hostText.setEditable(true);
            portText.setEditable(true);
            keyTxt.setEditable(false);
            messageTxt.setEditable(false);
            System.out.println("Error connecting to server: " + e.getMessage());
        }

        //  Creating a thread to listen server
        this.listenServerThread = new ListenServerThread(this.objectInputStream,
                this.objectOutputStream);
        this.listenServerThread.start();

        //  Send a message to the server
        try {
            objectOutputStream.writeObject(username);
        } catch (IOException e) {
            connected = false;
            connectButton.setText("Connect");
            hostText.setEditable(true);
            portText.setEditable(true);
            keyTxt.setEditable(false);
            messageTxt.setEditable(false);
            System.out.println("Exception doing login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void disconnect() {
        connected = false;
        connectButton.setText("Connect");
        hostText.setEditable(true);
        portText.setEditable(true);
        keyTxt.setEditable(false);
        messageTxt.setEditable(false);

        keyTxt.setText("");
        messageTxt.setText("");

        try {
            objectInputStream.close();
            objectOutputStream.close();
            socket.close();
            System.out.println("Disconnection successful from the server.");
        } catch (IOException e) {
            connected = true;
            connectButton.setText("Disconnect");
            hostText.setEditable(false);
            portText.setEditable(false);
            keyTxt.setEditable(true);
            messageTxt.setEditable(true);
            System.out.println("Error disconnecting from the server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
