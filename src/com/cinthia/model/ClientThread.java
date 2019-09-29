package com.cinthia.model;

import com.cinthia.view.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class ClientThread extends Thread {
    private ClientChat clientChat;
    private String dateStr;
    public Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Server server;
    private ArrayList<ClientThread> clientThreads;

    public ClientThread(Socket socket, Server server, ArrayList<ClientThread> clientThreads) {
        this.server = server;
        this.socket = socket;
        this.clientThreads = clientThreads;
        System.out.println("Thread trying to create Object Input/Output Streams");

        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            clientChat = (ClientChat) objectInputStream.readObject();
            this.server.addToLog(this.clientChat.getTypeDescription()
                    + " - " + clientChat.name + " just connected!");
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
                    this.server.setTurnOnLight(true);
                    sendStateToClient(new ServerState(ServerState.MESSAGE_ENCODED_RECEIVED));
                    break;
                case ChatMessage.LOGOUT:
                    this.server.addToLog(this.clientChat.getTypeDescription() + " - " + clientChat.name + " disconnected with a LOGOUT message.");
                    this.server.encryptedText.setText("");
                    this.removeClient();
                    keepGoing = false;
                    break;
                case ChatMessage.UNLOCK_MESSAGE:
                    this.server.addToLog(this.clientChat.getTypeDescription() + " - " + clientChat.name + " unlocking message with: " + message);
                    if (message.equals(server.encryptedText.getText())) {
                        this.server.addToLog(this.clientChat.getTypeDescription() + " - " + clientChat.name + " decoded the message!.");
                        sendStateToClient(new ServerState(ServerState.MESSAGE_DECODED));
                        this.server.setTurnOnLight(false);
                    } else {
                        this.server.addToLog(this.clientChat.getTypeDescription() + " - " + clientChat.name + " invalid encrypted message.");
                        sendStateToClient(new ServerState(ServerState.MESSAGE_NO_DECODED));
                    }
                    break;
            }
        }
        close();
    }

    //  Try to close all
    public void close() {
        sendStateToClient(new ServerState(ServerState.DISCONNECTED));
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

    private void sendStateToClient(ServerState serverState) {
        try {
            for (ClientThread clientThread : clientThreads) {
                clientThread.objectOutputStream.writeObject(serverState);
            }
        } catch (IOException e) {
            System.out.println("Error sending state to " + this.clientChat.name);
            System.out.println(e.toString());
        }
    }

    //  Function to remove the client from the list.
    private synchronized void removeClient () {
        for (int i = 0; i < clientThreads.size(); ++i) {
            ClientThread clientThread = clientThreads.get(i);
            if (clientThread.clientChat.type.equals(clientChat.type)) {
                clientThreads.remove(i);
                return;
            }
        }
    }
}
