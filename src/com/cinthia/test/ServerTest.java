package com.cinthia.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket sc = null;
        DataInputStream in;
        DataOutputStream out;

        final int PUERTO = 1000;

        try {
            serverSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor iniciado");
            while (true) {
                sc = serverSocket.accept();
                System.out.println("Cliente conectado!");
                in = new DataInputStream(sc.getInputStream());
                out = new DataOutputStream(sc.getOutputStream());
                System.out.println(in.readUTF());
                out.writeUTF("Hola mundo desde el servidor");
                sc.close();
                System.out.println("Cliente desconectado!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
