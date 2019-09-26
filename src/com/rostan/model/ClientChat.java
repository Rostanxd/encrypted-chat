package com.rostan.model;

import java.io.Serializable;

public class ClientChat implements Serializable {
    String type;
    String name;

    public ClientChat(String type, String name) {
        this.type = type;
        this.name = name;
    }

    String getTypeDescription() {
        if (this.type.equals("ENC")) {
            return "ENCODER";
        } else {
            return "DECODER";
        }
    }
}
