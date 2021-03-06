package com.ftpserver.agent;

import com.ftpserver.fileIO.FileIO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Micro on 2016/4/19.
 */
public class UserLoginAgent {

    private static UserLoginAgent _instance;

    private HashMap<String, String> userNamePathList;

    private String filepath;

    private FileIO fileIO;

    private UserLoginAgent() {
        userNamePathList = new HashMap<>();
        fileIO = FileIO.getInstance();
    }

    public static UserLoginAgent getInstance() {
        if (_instance == null) {
            _instance = new UserLoginAgent();
        }
        return _instance;
    }

    public void init(String path) throws Exception {
        filepath = path;
        String jstring = new String(fileIO.read(path));
        JSONArray jsonArray = JSONArray.fromObject(jstring);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = jsonObject.getString("name");
            String pass = jsonObject.getString("passwd");
            userNamePathList.put(name, pass);
        }
    }

    public boolean userAuthenticate(String username, String userpass) throws Exception {
        userNamePathList.clear();
        init(filepath);

        if (username.toLowerCase().equals("anonymous") && userNamePathList.containsKey("anonymous")) {
            return true;
        } else if (userNamePathList.containsKey(username) && userNamePathList.get(username).equals(userpass)) {
            return true;
        }
        return false;
    }
}
