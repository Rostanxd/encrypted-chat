package com.rostan.view;

import com.rostan.model.ChatMessage;
import com.rostan.model.ClientThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Server {
    private JPanel panelMain;
    private JTextField portText;
    private JButton startButton;
    private JTextArea logTextArea;
    private JScrollPane logScrollPane;
    public JTextField encryptedText;
    public JPanel colorPanel;

    private Boolean running, keepGoing;
    private int port;
    private ArrayList<ClientThread> clientThreads;
    private SimpleDateFormat sdf;
    private ServerRunning serverRunning;
    private ServerSocket serverSocket;
    private Socket socket;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Server");
        frame.setContentPane(new Server().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }

    //  Constructor
    public Server() {
        //  Form
        this.logTextArea.setEditable(false);
        this.encryptedText.setEditable(false);
        this.colorPanel.setBackground(Color.gray);

        //  Initializing variables
        this.startButton.setText("Start");
        this.running = true;
        this.portText.setText("1000");
        this.sdf = new SimpleDateFormat("HH:mm:ss");
        this.encryptedText.setText("");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (running) {
                    try {
                        port = Integer.parseInt(portText.getText().trim());
                        portText.setEditable(false);
                        serverRunning = new ServerRunning();
                        serverRunning.start();

                    } catch (NumberFormatException numberEx) {
                        startButton.setText("Start");
                        portText.setEditable(true);
                        running = true;
                        JOptionPane.showMessageDialog(null, "Error: Invalid port number.");

                    } catch (Exception ex) {
                        startButton.setText("Start");
                        portText.setEditable(true);
                        running = true;
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage() + ".");
                        ex.printStackTrace();
                    }
                } else {
                    stopServer();
                }
            }
        });
    }

    private void startServer() {
        startButton.setText("Stop");
        running = false;

        this.clientThreads = new ArrayList<ClientThread>();
        this.keepGoing = true;

        //  Create socket server and wait for connection requests
        try {
            //  The socket server and wait for connection requests
            serverSocket = new ServerSocket(this.port);

            //  Controlled loop to wait for connections
            while (keepGoing) {
                // Format message saying we are waiting
                this.addToLog("Server waiting for clients on port: " + this.port + ".");
                this.colorPanel.setBackground(Color.yellow);
                socket = serverSocket.accept();

                //  Controlling the loop
                if (!keepGoing) {
                    break;
                }

                //  Making a thread of it
                ClientThread clientThread = new ClientThread(socket, this);
                clientThreads.add(clientThread);
                clientThread.start();
            }

            //  Closing the server socket, and closing all the client sockets
            try {
                serverSocket.close();
                for (ClientThread clientThread : clientThreads) {
                    clientThread.close();
//                    remove(clientThread.id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            e.printStackTrace();
        }
    }

    private void stopServer() {
        startButton.setText("Start");
        encryptedText.setText("");
        colorPanel.setBackground(Color.gray);
        portText.setEditable(true);
        running = true;
        keepGoing = false;

        addToLog("Server destroyed and closed.");
        this.colorPanel.setBackground(Color.gray);
        try {
            new Socket("localhost", this.port);
        } catch (Exception e) {
            //  Pass
        }
    }

    //  Function to print the log in the screen
    public void addToLog(String text) {
        String time = sdf.format(new Date()) + ": ";
        this.logTextArea.append(time + text + "\n");
    }

    //  Function to remove the client from the list.
//    private synchronized void remove(int id) {
//        // scan the array list until we found the Id

//        for (int i = 0; i < clientThreads.size(); ++i) {
//            ClientThread clientThread = clientThreads.get(i);
//            // found it
//            if (clientThread.id == id) {
//                clientThreads.remove(i);
//                return;
//            }
//        }
//    }

    class ServerRunning extends Thread {
        public void run() {
            startServer();
        }
    }
}