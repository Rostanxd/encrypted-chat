package com.cinthia.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTest {
    public static void main(String[] args) {
        final String HOST = "127.0.0.1";
        final int PUERTO = 1000;
        DataInputStream in;
        DataOutputStream out;

        try {
            Socket socket = new Socket(HOST, PUERTO);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("Hola desde el cliente");
            System.out.println(in.readUTF());
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
