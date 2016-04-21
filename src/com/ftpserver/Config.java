package com.ftpserver;

/**
 * Created by windr on 4/19/16.
 */
public class Config {

    private static Config _instance;
    private String root = "";

    private String lsCMD = "C:\\Program Files\\Git\\usr\\bin\\ls.exe -l ";

    public static Config getInstance() {
        if (_instance == null) {
            _instance = new Config();
        }
        return _instance;
    }

    public String getLsCMD() {
        return lsCMD;
    }

    public void setLsCMD(String lsCMD) {
        this.lsCMD = lsCMD;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }
}
