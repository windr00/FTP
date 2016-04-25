package com.ftpserver.agent;

import com.ftpserver.fileIO.FileIO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashMap;

public class SafePassAgent {

    private static SafePassAgent _instance;
    private HashMap<String, MODE> ipRestrctItemMap = new HashMap<>();

    private String filepath;

    public static SafePassAgent getInstance() {
        if (_instance == null) {
            _instance = new SafePassAgent();
        }
        return _instance;
    }

    public void init(String path) throws Exception {
        filepath = path;
        FileIO fileIO = FileIO.getInstance();
        String jstring = new String(fileIO.read(path));
        JSONArray jsonArray = JSONArray.fromObject(jstring);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ipRestrctItemMap.put(jsonObject.getString("ip"), MODE.valueOf(jsonObject.getString("mode")));
        }
    }

    public boolean isAllowed(String ip) throws Exception {
        ipRestrctItemMap.clear();
        init(filepath);
        if (ipRestrctItemMap.containsKey("*.*.*.*")) {
            return ipRestrctItemMap.get("*.*.*.*") == MODE.ALLOW;
        }
        if (ipRestrctItemMap.containsKey(ip)) {
            MODE mode = ipRestrctItemMap.get(ip);
            if (mode == MODE.ALLOW) {
                return true;
            }
        }
        return false;
    }


    public enum MODE {
        ALLOW,
        DISALLOW
    }
}
