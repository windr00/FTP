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
    private String root = "/Users/windr/Desktop/FTP/";
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




    public int getCmdPort() {
        return cmdPort;
    }


    public int getMaxConnection() {
        return maxConnection;
    }

    public String getLsCMD() {
        return lsCMD;
    }

    public String getRoot() {
        return root;
    }

}
