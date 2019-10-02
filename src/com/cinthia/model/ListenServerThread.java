package com.cinthia.model;

import com.cinthia.view.Client;
import com.cinthia.view.ClientFrame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ListenServerThread extends Thread {
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private ClientFrame client;
    private ServerState serverState;

    public ListenServerThread(ClientFrame client, Socket socket, ObjectInputStream objectInputStream,
                              ObjectOutputStream objectOutputStream) {
        this.client = client;
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
    }

    public void run() {
        boolean keepGoing = true;
        while (keepGoing) {
            //  Check if the socket is still opened
            if (!socket.isClosed()) {
                try {
                    this.serverState = (ServerState) objectInputStream.readObject();
                    if (this.client.type.equals("ENC") && serverState != null && serverState.state == 1) {
                        System.out.println("Server say: Encoder just connected");
                    }
                    if (this.client.type.equals("DEC") && serverState != null && serverState.state == 2) {
                        System.out.println("Server say: Decoder just connected");
                    }
                    if (this.client.type.equals("ENC") && serverState != null && serverState.state == 3) {
                        System.out.println("Server say: Your encode message received!");
                        this.client.keyTxt.setEditable(false);
                        this.client.messageTxt.setEditable(false);
                        this.client.sendButton.setEnabled(false);
                    }
                    if (this.client.type.equals("DEC") && serverState != null && serverState.state == 3) {
                        System.out.println("Server say: Decode it!");
                        this.client.keyTxt.setEditable(true);
                        this.client.messageTxt.setEditable(true);
                        this.client.sendButton.setEnabled(true);
                    }
                    if (this.client.type.equals("ENC") && serverState != null && serverState.state == 4) {
                        System.out.println("Server say: Message decoded!");
                        this.client.keyTxt.setEditable(true);
                        this.client.messageTxt.setEditable(true);
                        this.client.sendButton.setEnabled(true);
                        this.client.keyTxt.setText("");
                        this.client.messageTxt.setText("");
                    }
                    if (this.client.type.equals("DEC") && serverState != null && serverState.state == 4) {
                        System.out.println("Server say: Message decoded!");
                        this.client.keyTxt.setEditable(false);
                        this.client.messageTxt.setEditable(false);
                        this.client.sendButton.setEnabled(false);
                        this.client.keyTxt.setText("");
                        this.client.messageTxt.setText("");
                    }
//                    if (this.client.type.equals("ENC") && serverState != null && serverState.state == 5) {
//                        System.out.println("Server say: Invalid message decoder!");
//                        this.client.keyTxt.setEditable(false);
//                        this.client.messageTxt.setEditable(false);
//                        this.client.sendButton.setEnabled(false);
//                        this.client.showMessage("Error: Decoder client, has entered a invalid message!");
//                    }
                    if (this.client.type.equals("DEC") && serverState != null && serverState.state == 5) {
                        System.out.println("Server say: Invalid message decoder!");
                        this.client.keyTxt.setEditable(true);
                        this.client.messageTxt.setEditable(true);
                        this.client.sendButton.setEnabled(true);
                        this.client.showMessage("Error: Invalid decoder message!");
                    }
                    if (this.serverState != null && this.serverState.state == 6) {
                        System.out.println("Server say: service stopped!");
                        client.setDefaultProperties();
                        keepGoing = false;
                        closeStreams();
                    }
                } catch (IOException e) {
                    System.out.println("Server has close the connection: " + e);
                    break;
                }
                catch (ClassNotFoundException ignored) {

                }
            } else {
                keepGoing = false;
            }
        }
    }

    public void closeStreams() throws IOException {
        this.socket.close();
        this.objectInputStream.close();
        this.objectOutputStream.close();
    }
}
