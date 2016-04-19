package com.ftpserver.fileIO;

import java.io.*;

/**
 * Created by windr on 4/19/16.
 */
public class FileIO {

    private static FileIO _instance;

    public static FileIO getInstance() {
        if (_instance == null) {
            _instance = new FileIO();
        }
        return _instance;
    }

    public char[] read(String path) throws Exception {
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String tmp = null;
        StringBuilder sb = new StringBuilder();

        tmp = br.readLine();
        while (tmp != null) {
            sb.append(tmp);
            tmp = br.readLine();
        }
        br.close();

        return sb.toString().toCharArray();
    }

    public void write(String path, char[] content) throws Exception {
        File file = new File(path);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(content);
        bw.close();
    }

    public boolean exist(String path) {
        File file = new File(path);
        return file.exists();
    }
}
