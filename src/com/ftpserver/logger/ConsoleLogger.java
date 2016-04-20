package com.ftpserver.logger;

/**
 * Created by windr on 4/19/16.
 */
public class ConsoleLogger {

    public static void error(String message) {
        System.out.println("ERROR: " + message);
    }

    public static void info(String message) {
        System.out.println("INFO: " + message);

    }
}
