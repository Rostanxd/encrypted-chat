package com.rostan.view;

import com.rostan.model.ChatMessage;

import javax.swing.*;
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
    private JTextField messageText;
    private JTextArea logTextArea;

    private Boolean running, keepGoing;
    private int port, uniqueId;
    private ArrayList<ClientThread> clientThreads;
    private SimpleDateFormat sdf;
    private ServerRunning serverRunning;
    private ServerSocket serverSocket;
    private Socket socket;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Server");
        frame.setContentPane(new Server().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    //  Constructor
    public Server() {
        //  Form
        this.logTextArea.setEditable(false);

        //  Initializing variables
        this.startButton.setText("Start");
        this.running = true;
        this.portText.setText("1000");
        this.sdf = new SimpleDateFormat("HH:mm:ss");

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

            // Infinite loop to wait for connections
            while (keepGoing) {
                // Format message saying we are waiting
                this.addToLog("Server waiting for clients on port: " + this.port + ".");
                socket = serverSocket.accept();

                //  Controlling the loop
                if (!keepGoing) {
                    break;
                }

                //  Making a thread of it
                ClientThread clientThread = new ClientThread(socket);
                clientThreads.add(clientThread);
                clientThread.start();
            }

            // I was asked to stop
            try {
                serverSocket.close();
                for (int i = 0; i < clientThreads.size(); ++i) {
                    ClientThread clientThread = clientThreads.get(i);
                    try {
                        clientThread.sInput.close();
                        clientThread.sOutput.close();
                        clientThread.socket.close();
                        clientThread.close();
                    } catch (IOException ioE) {
                        // not much I can do
                    }
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
        portText.setEditable(true);
        running = true;
        keepGoing = false;

        addToLog("Server destroyed and closed.");
        try {
            new Socket("localhost", this.port);
        } catch (Exception e) {
            //  Pass
        }
    }

    private void addToLog(String text) {
        String time = sdf.format(new Date()) + ": ";
        this.logTextArea.append(time + text + "\n");
    }

    class ClientThread extends Thread {
        Socket socket;
        ChatMessage cm;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username, date;

        //  Constructor
        ClientThread(Socket socket) {
            // a unique id
            id = ++uniqueId;
            this.socket = socket;
            System.out.println("Thread trying to create Object Input/Output Streams");

            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
                addToLog(username + " just connected.");
            } catch (IOException e) {
                addToLog("Exception creating new Input/output Streams: " + e);
                return;
            } catch (ClassNotFoundException e) {
                addToLog("Exception creating new Input/output Streams: " + e);
                return;
            }

            date = new Date().toString() + "\n";
        }

        public void run() {
            boolean keepGoing = true;
            while (keepGoing) {
                try {
                    cm = (ChatMessage) sInput.readObject();
                } catch (IOException e) {
                    addToLog(username + " Exception reading Streams: " + e);
                    break;
                } catch (ClassNotFoundException e2) {
                    break;
                }

                String message = cm.getMessage();

                switch (cm.getType()) {

                    case ChatMessage.MESSAGE:
                        broadcast(username + ": " + message);
                        break;
                    case ChatMessage.LOGOUT:
                        addToLog(username + " disconnected with a LOGOUT message.");
                        keepGoing = false;
                        break;
                    case ChatMessage.WHOISIN:
//                        writeMsg("List of the users connected at " + sdf.format(new Date()) + "\n");
                        // scan al the users connected
                        for (int i = 0; i < clientThreads.size(); ++i) {
                            ClientThread ct = clientThreads.get(i);
                            writeMsg((i + 1) + ") " + ct.username + " since " + ct.date);
                        }
                        break;
                }
            }
            //  Remove myself from the arrayList containing the list of the connected Clients
            remove(id);
            close();
        }

        //  Try to close everything
        private void close() {
            try {
                if (sOutput != null) {
                    sOutput.close();
                }
            } catch (Exception e) {
            }
            try {
                if (sInput != null) {
                    sInput.close();
                }
            } catch (Exception e) {
            }
            ;
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
            }
        }

        /*
         * Write a String to the Client output stream
         */
        private boolean writeMsg(String msg) {
            // if Client is still connected send the message to it
            if (!socket.isConnected()) {
                close();
                return false;
            }
            // write the message to the stream
            try {
                sOutput.writeObject(msg);
            } // if an error occurs, do not abort just inform the user
            catch (IOException e) {
//                display("Error sending message to " + username);
//                display(e.toString());
            }
            return true;
        }
    }

    synchronized void remove(int id) {
        // scan the array list until we found the Id
        for (int i = 0; i < clientThreads.size(); ++i) {
            ClientThread ct = clientThreads.get(i);
            // found it
            if (ct.id == id) {
                clientThreads.remove(i);
                return;
            }
        }
    }

    private synchronized void broadcast(String message) {
        String time = sdf.format(new Date());
        String messageLf = time + " " + message + "\n";

        for (int i = clientThreads.size(); --i >= 0;) {
            ClientThread ct = clientThreads.get(i);
            if (!ct.writeMsg(messageLf)) {
                clientThreads.remove(i);
                addToLog("Disconnected Client " + ct.username + " removed from list.");
            }
        }
    }

    class ServerRunning extends Thread {
        public void run() {
            startServer();
        }
    }
}