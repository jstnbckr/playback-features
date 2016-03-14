package com.netflix.playback.features.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Util {
    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static void printWithTime(String message) {
        System.out.println(dateFormat.format(System.currentTimeMillis()) + " : " + message);
    }

    public static void sleep(long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
