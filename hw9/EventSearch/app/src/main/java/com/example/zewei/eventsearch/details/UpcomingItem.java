package com.example.zewei.eventsearch.details;

import com.example.zewei.eventsearch.adapter.UpcomingRecyclerViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UpcomingItem{
    String displayName = "";
    String artistName = "";
    String date = "";
    String time = "";
    String type = "";
    String url = "";

    public UpcomingItem(JSONObject jsonObject){
        setDisplayName(jsonObject);
        setArtistName(jsonObject);
        setDate(jsonObject);
        setTime(jsonObject);
        setEventType(jsonObject);
        setUrl(jsonObject);
    }


    private void setUrl(JSONObject jsonObject){
        try {
            url = jsonObject.getString("uri");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setEventType(JSONObject jsonObject){
        try {
            type = jsonObject.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setTime(JSONObject jsonObject){
        try {
            time = jsonObject.getJSONObject("start").getString("time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDate(JSONObject jsonObject){
        try {
            date = jsonObject.getJSONObject("start").getString("date");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setArtistName(JSONObject jsonObject){
        try {
            artistName = jsonObject.getJSONArray("performance")
                    .getJSONObject(0).getString("displayName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDisplayName(JSONObject jsonObject){
        try {
            displayName = jsonObject.getString("displayName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getDateTime(){
        String format = "MMM dd, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String result = "";
        try {
            Date date = sdf.parse(getDate());
            SimpleDateFormat output = new SimpleDateFormat(format, Locale.ENGLISH);
            result += output.format(date);
        } catch (Exception e) {
            result += getDate();
        }
        result += " " + getTime();

        return result;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public UpcomingItem(String displayName){
        this.displayName = displayName;
    }
}
