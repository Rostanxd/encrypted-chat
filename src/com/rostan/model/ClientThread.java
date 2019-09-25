package com.rostan.model;

import com.rostan.view.Server;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

public class ClientThread extends Thread {
    private String clientType;
    private ClientChat clientChat;
    private String dateStr;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Server server;

    public ClientThread(Socket socket, Server server) {
        this.server = server;
        this.socket = socket;
        System.out.println("Thread trying to create Object Input/Output Streams");

        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            clientChat = (ClientChat) objectInputStream.readObject();
            this.server.addToLog(this.clientChat.getTypeDescription() + " - " + clientChat.name + " just connected!");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Exception creating new Input/output Streams: " + e);
            return;
        }

        dateStr = new Date().toString();
    }

    public void run() {
        boolean keepGoing = true;
        while (keepGoing) {
            ChatMessage chatMessage;
            try {
                chatMessage = (ChatMessage) objectInputStream.readObject();
            } catch (IOException e) {
                System.out.println(this.clientChat.getTypeDescription() + " - " + clientChat.name + " Exception reading Streams: " + e);
                break;
            } catch (ClassNotFoundException e2) {
                break;
            }

            String message = chatMessage.getMessage();

            switch (chatMessage.getType()) {
                case ChatMessage.WHOISIN:
                    writeMsgToClient(this.clientChat.getTypeDescription() + " - " + clientChat.name + " since " + this.dateStr);
                    break;
                case ChatMessage.ENCRYPTED_MESSAGE:
                    this.server.addToLog(this.clientChat.getTypeDescription() + " - " + clientChat.name + " encrypted message is: " + message);
                    this.server.encryptedText.setText(message);
                    this.server.colorPanel.setBackground(Color.green);
                    break;
                case ChatMessage.LOGOUT:
                    this.server.addToLog(this.clientChat.getTypeDescription() + " - " + clientChat.name + " disconnected with a LOGOUT message.");
                    this.server.encryptedText.setText("");
                    this.server.colorPanel.setBackground(Color.gray);
                    keepGoing = false;
                    break;
                case ChatMessage.UNLOCK_MESSAGE:
                    this.server.addToLog(this.clientChat.getTypeDescription() + " - " + clientChat.name + " unlocking message with: " + message);
                    if (message.equals(server.encryptedText.getText())) {
                        this.server.addToLog(this.clientChat.getTypeDescription() + " - " + clientChat.name + " just decode the message.");
                        this.server.colorPanel.setBackground(Color.gray);
                    } else {
                        this.server.addToLog(this.clientChat.getTypeDescription() + " - " + clientChat.name + " invalid encrypted message.");
                    }
                    break;
            }
        }
        close();
    }

    //  Try to close all
    public void close() {
        try {
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //  Write a message to the client
    private void writeMsgToClient(String msg) {
        try {
            objectOutputStream.writeObject(msg);
        } catch (IOException e) {
            System.out.println("Error sending message to " + this.clientChat.name);
            System.out.println(e.toString());
        }
    }
}
