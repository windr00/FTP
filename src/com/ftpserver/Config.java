package com.ftpserver;

import com.ftpserver.fileIO.FileIO;
import net.sf.json.JSONArray;
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
        String jstring = new String(fileIOInstance.read(path));
        JSONArray jsonArray = JSONArray.fromObject(jstring);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        this.cmdPort = Integer.parseInt(jsonObject.getString("cmdPort"));
        this.maxConnection = Integer.parseInt(jsonObject.getString("maxConnection"));
        this.root = jsonObject.getString("ftpRoot");
        this.lsCMD = jsonObject.getString("lsCMD");
    }

    public void saveSettings(String path) throws Exception {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        jsonObject.put("cmdPort", String.valueOf(this.cmdPort));
        jsonObject.put("maxConnection", String.valueOf(this.maxConnection));
        jsonObject.put("ftpRoot", this.root);
        jsonObject.put("lsCMD", this.lsCMD);
        jsonArray.add(0, jsonObject);
        byte[] buffer = jsonArray.toString().getBytes();
        if (!fileIOInstance.exist(path)) {
            fileIOInstance.create(path);
        }
        fileIOInstance.write(path, buffer, buffer.length);
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
