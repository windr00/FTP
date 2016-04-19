package com.ftpserver;

/**
 * Created by windr on 4/19/16.
 */
public class Config {


    private static Config _instance;
    private String root = "";

    public static Config getInstance() {
        if (_instance == null) {
            _instance = new Config();
        }
        return _instance;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }
}
