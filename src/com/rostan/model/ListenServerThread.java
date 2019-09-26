package com.rostan.model;

import com.rostan.view.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ListenServerThread extends Thread {
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Client client;

    public ListenServerThread(ObjectInputStream objectInputStream,
                              ObjectOutputStream objectOutputStream) {
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
    }

    public void run() {
        while (true) {
            try {
                String msg = (String) objectInputStream.readObject();
            } catch (IOException e) {
                System.out.println("Server has close the connection: " + e);
                break;
            } // can't happen with a String object but need the catch anyhow
            catch (ClassNotFoundException ignored) {

            }
        }
    }
}
