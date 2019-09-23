package com.rostan.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

public class ClientThread extends Thread {
    public int id;
    private String username, dateStr;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public ClientThread(Socket socket, int unique) {
        id = ++unique;
        this.socket = socket;
        System.out.println("Thread trying to create Object Input/Output Streams");

        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            username = (String) objectInputStream.readObject();
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
                System.out.println(username + " Exception reading Streams: " + e);
                break;
            } catch (ClassNotFoundException e2) {
                break;
            }

            String message = chatMessage.getMessage();

            switch (chatMessage.getType()) {
                case ChatMessage.MESSAGE:
                    System.out.println(username + ": " + message);
                    break;
                case ChatMessage.LOGOUT:
                    System.out.println(this.username + " disconnected with a LOGOUT message.");
                    keepGoing = false;
                    break;
                case ChatMessage.WHOISIN:
                    writeMsgToClient(this.username + " since " + this.dateStr);
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
            System.out.println("Error sending message to " + username);
            System.out.println(e.toString());
        }
    }
}
