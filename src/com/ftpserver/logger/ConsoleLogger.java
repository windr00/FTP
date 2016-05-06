package com.ftpserver.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by windr on 4/19/16.
 */
public class ConsoleLogger {

    public static void error(String message) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(df.format(new Date()));
        System.out.println("ERROR: " + message);
    }

    public static void info(String message) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(df.format(new Date()));
        System.out.println("INFO: " + message);

    }
}
