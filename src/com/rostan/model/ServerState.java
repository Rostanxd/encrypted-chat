package com.rostan.model;

public class ServerState {
    public static int CONNECTED = 0;
    public static int ENCODER_CONNECTED = 1;
    public static int DECODER_CONNECTED = 2;
    public static int MESSAGE_ENCODED_RECEIVED = 3;
    public static int MESSAGE_DECODED = 4;
    public static int DISCONNECTED = 5;
    public int state;

    public ServerState(int state) {
        this.state = state;
    }
}
