package com.example.fitnesstracker;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SearchGoogleAPI {
    private static final String API_KEY = "AIzaSyBAjZmD81Xw2DBr1qtRsA1xo3soXljy9UA";
    private static final String SEARCH_ID_cx = "013095947397730219345:aazcj4usxys";
    public static String search(String keyword, String[] params, String[] values) {
        keyword = keyword.replace(" ", "+");
        URL url = null;
        HttpURLConnection connection = null;
        String textResult = "";
        String query_parameter="";
        if (params!=null && values!=null){
            for (int i =0; i < params.length; i ++){
                query_parameter += "&";
                query_parameter += params[i];
                query_parameter += "=";
                query_parameter += values[i];
            }
        }
        try {
            url = new URL("https://www.googleapis.com/customsearch/v1?key="+
                    API_KEY+ "&cx="+ SEARCH_ID_cx + "&q="+ keyword + query_parameter);
            connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                textResult += scanner.nextLine();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            connection.disconnect();
        }
        return textResult;
    }
    public static String getSnippet(String result){
        String snippet = null;
        try{
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            if(jsonArray != null && jsonArray.length() > 0) {
                snippet =jsonArray.getJSONObject(0).getString("snippet");
                Log.i("snippet", jsonArray.getJSONObject(0).toString());
            }
        }catch (Exception e){
            e.printStackTrace();
            snippet = "NO INFO FOUND";
        }
        snippet = snippet.replaceAll("\\r\\n|\\r|\\n", " ");
        snippet = snippet.replace("...", "");
        return snippet;
    }
    public static String getImageURL(String result){
        String pageMap = null;
        String URL = null;
        try{
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            if(jsonArray != null && jsonArray.length() > 0) {
                pageMap =jsonArray.getJSONObject(0).getString("pagemap");
                JSONObject jsonObjectTwo = new JSONObject(pageMap);
                JSONArray jsonArrayTwo = jsonObjectTwo.getJSONArray("metatags");
                if(jsonArrayTwo != null && jsonArrayTwo.length() > 0){
                    URL = jsonArrayTwo.getJSONObject(0).getString("og:image");
                }

                Log.i("pageMap", pageMap);
            }
        }catch (Exception e){
            e.printStackTrace();
            URL = "NO INFO FOUND";

        }

        return URL;
    }
}



