package com.ftpserver;

import com.ftpserver.fileIO.FileIO;
import net.sf.json.JSONObject;

/**
 * Created by windr on 4/19/16.
 */
public class Config {

    private static Config _instance;
    private FileIO fileIOInstance;
    private String root = "/";
    private String lsCMD = "/bin/ls -l ";
    private int cmdPort = 1025;
    private int maxConnection = 10;

    private Config() {
        fileIOInstance = FileIO.getInstance();
    }

    public static Config getInstance() {
        if (_instance == null) {
            _instance = new Config();
        }
        return _instance;
    }

    public void init(String path) throws Exception {
        byte[] jstring = fileIOInstance.read(path);
        JSONObject jsonObject = new JSONObject();
        JSONObject.fromObject(jstring);
        this.cmdPort = jsonObject.getInt("cmdPort");
        this.maxConnection = jsonObject.getInt("maxConnection");
        this.root = jsonObject.getString("ftpRoot");
        this.lsCMD = jsonObject.getString("lsCMD");

    }

    public void saveSettings() throws Exception {
        JSONObject jsonObject = new JSONObject();

    }

    public int getCmdPort() {
        return cmdPort;
    }

    public void setCmdPort(int cmdPort) {
        this.cmdPort = cmdPort;
    }

    public int getMaxConnection() {
        return maxConnection;
    }

    public void setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
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
