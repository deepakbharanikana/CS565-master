package com.sharetask.webservice;

import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Utility {
	
	public static String constructJSON(String tag, boolean status) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("tag", tag);
            obj.put("status", new Boolean(status));
        } catch (JSONException e) {
        }
        return obj.toString();
    }

	public static String constructJSON(String tag, boolean status,String err_msg) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("tag", tag);
            obj.put("status", new Boolean(status));
            obj.put("error_msg", err_msg);
        } catch (JSONException e) {
        }
        return obj.toString();
    }

	public static String constructJSON(String tag, boolean status, ArrayList<String> arr) {
		JSONObject obj = new JSONObject();
		
        try {
            obj.put("tag", tag);
            obj.put("list", new JSONArray(arr));
            obj.put("status", new Boolean(status));
        } catch (JSONException e) {
        }
        return obj.toString();
	}
	
	public static String constructJSON(String tag, boolean status, ArrayList<HashMap<String, String>> arr, String fake) {
		JSONObject obj = new JSONObject();
		
        try {
            obj.put("tag", tag);
            obj.put("list", new JSONArray(arr));
            obj.put("status", new Boolean(status));
        } catch (JSONException e) {
        }
        return obj.toString();
	}
	
}
