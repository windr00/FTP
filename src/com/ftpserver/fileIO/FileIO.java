package com.ftpserver.fileIO;

import com.ftpserver.Config;

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

    public byte[] read(String path) throws Exception {
        File file = new File(Config.getInstance().getRoot() + path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String tmp = null;
        StringBuilder sb = new StringBuilder();

        tmp = br.readLine();
        while (tmp != null) {
            sb.append(tmp);
            tmp = br.readLine();
        }
        br.close();

        return sb.toString().getBytes();
    }

    public void write(String path, char[] content, int length) throws Exception {
        File file = new File(Config.getInstance().getRoot() + path);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        char[] c = new char[length];
        for (int i = 0; i < length; i++) {
            c[i] = content[i];
        }
        bw.write(c);
        bw.close();
    }

    public boolean isDir(String path) throws Exception {
        return new File(Config.getInstance().getRoot() + path).isDirectory();
    }

    public boolean exist(String path) {
        File file = new File(Config.getInstance().getRoot() + path);
        return file.exists();
    }

    public BufferedReader open(String path) throws Exception {
        File file = new File(Config.getInstance().getRoot() + path);
        return new BufferedReader(new FileReader(file));
    }

    public String[] lsdir(String path) throws Exception {
        return new File(Config.getInstance().getRoot() + path).list();
    }
}
