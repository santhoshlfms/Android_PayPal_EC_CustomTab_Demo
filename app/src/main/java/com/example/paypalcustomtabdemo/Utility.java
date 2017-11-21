package com.example.paypalcustomtabdemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by sannelson on 11/17/2017.
 */

public class Utility {

    public static String getUrlFromJSONArray(JSONObject responseFromPayPalServer) throws JSONException {
        JSONArray links = responseFromPayPalServer.getJSONArray("links");
        String _link = null;
        for (int i = 0; i < links.length(); i++) {
            JSONObject elements = links.getJSONObject(i);
            Iterator<?> keys = elements.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                if(elements.get(key).toString().equals("REDIRECT")) {
                    _link =  elements.get("href").toString();
                }
            }

        }
        return _link;
    }
}
