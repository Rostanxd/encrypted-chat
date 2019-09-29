package com.cinthia.util;

public class RivestShamirAdleman {

    public static String encode(int key, String text) {
        char array[] = text.toCharArray();
        for (int i = 0; i < array.length; i++) {
            array[i] = (char) (array[i] + key);
        }
        return String.valueOf(array);
    }

    public static String decode(int key, String text) {
        char array[] = text.toCharArray();
        for (int i = 0; i < array.length; i++) {
            array[i] = (char) (array[i] - key);
        }
        return String.valueOf(array);
    }
}
