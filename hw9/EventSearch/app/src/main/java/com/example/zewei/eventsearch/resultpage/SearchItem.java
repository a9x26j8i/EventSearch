package com.example.zewei.eventsearch.resultpage;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchItem implements Serializable {
    private String venue="";
    private String name="";
    private String category="";//it's hashcode!
    private String segment="";//sports
    private String date = "";
    private String time = "";
    private String ticketUrl ="";
    private List<String> artists ;
    private String artistString =""; // lakers | boats
    private String categoryString =""; //sports | basketball
    private String genre="";//basketball //!!!
    private String price=""; //!!!
    private String ticketstatus="";//!!!
    private String seatmapUrl = "";//!!!
    private String dateTimeParsed = "";
    private String address="";
    private String city = "";
    private String phone ="";
    private String openHours="";
    private String generalRule = "";
    private String childRule = "";
    private double longitude = -118.32586552;
    private double latitude = 34.10200961;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public String getOpenHours() {
        return openHours;
    }

    public String getGeneralRule() {
        return generalRule;
    }

    public String getChildRule() {
        return childRule;
    }

    public SearchItem(JSONObject jsonObject) {
        artists = new ArrayList<>();
        initName(jsonObject);

        initCategory(jsonObject);

        initSegment(jsonObject);

        initGenre(jsonObject);

        initCategoryString();

        initPrice(jsonObject);

        initStatus(jsonObject);

        initSeatmapUrl(jsonObject);

        initVenue(jsonObject);

        initTicketUrl(jsonObject);

        initDate(jsonObject);

        initTime(jsonObject);

        initArtistsAndString(jsonObject);

        initDateTimeParsed();

        initVenueInfo(jsonObject);
    }

    private void initVenueInfo(JSONObject jsonObject) {
        try {
            JSONObject venueObj = jsonObject.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0);
            try{
                longitude = venueObj.getJSONObject("location").getDouble("longitude");
            }catch(JSONException e){
                e.printStackTrace();
            }
            try{
                latitude = venueObj.getJSONObject("location").getDouble("latitude");
            }catch(JSONException e){
                e.printStackTrace();
            }
            try{
                this.address = venueObj.getJSONObject("address").getString("line1");

            }catch(JSONException e){
                e.printStackTrace();
            }
            try{
                this.city = venueObj.getJSONObject("city").getString("name")
                            + ", "
                            + venueObj.getJSONObject("state").getString("name");
            }catch(JSONException e){
                e.printStackTrace();
            }
            try{
                this.phone = venueObj.getJSONObject("boxOfficeInfo").getString("phoneNumberDetail");
            }catch(JSONException e){
                e.printStackTrace();
            }
            try{
                this.openHours = venueObj.getJSONObject("boxOfficeInfo").getString("openHoursDetail");
            }catch(JSONException e){
                e.printStackTrace();
            }
            try{
                this.generalRule = venueObj.getJSONObject("generalInfo").getString("generalRule");
            }catch(JSONException e){
                e.printStackTrace();
            }
            try{
                this.childRule = venueObj.getJSONObject("generalInfo").getString("childRule");
            }catch(JSONException e){
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initDateTimeParsed(){
        String result = "";
        try {
            String format = "MMM dd, yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
            Date date2 = sdf.parse(date);

            SimpleDateFormat output = new SimpleDateFormat(format, Locale.ENGLISH);
            result += output.format(date2);
        } catch (ParseException e) {
            e.printStackTrace();

        }finally {
            result += " " + time;
        }

        dateTimeParsed = result;

    }

    private void initArtistsAndString(JSONObject jsonObject) {
        try {
            JSONArray array = jsonObject.getJSONObject("_embedded").getJSONArray("attractions");
            for(int i =0; i<array.length(); i++){
                artists.add(((JSONObject)array.get(i)).getString("name"));
                artistString += " | "
                        + artists.get(i);
            }
            artistString = artistString.substring(3, artistString.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void  initTime(JSONObject jsonObject) {
        try {
            time = jsonObject.getJSONObject("dates").getJSONObject("start").getString("localTime");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initDate(JSONObject jsonObject){
        try {
            date = jsonObject.getJSONObject("dates").getJSONObject("start").getString("localDate");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initTicketUrl(JSONObject jsonObject) {
        try {
            ticketUrl = jsonObject.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initVenue(JSONObject jsonObject) {
        try {
            venue = ((JSONObject)jsonObject.getJSONObject("_embedded").getJSONArray("venues").get(0)).getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private  void initSegment(JSONObject jsonObject){
        try {
            segment = ((JSONObject)jsonObject.getJSONArray("classifications").get(0)).getJSONObject("segment").getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initCategory(JSONObject jsonObject) {
        try {
            category = ((JSONObject)jsonObject.getJSONArray("classifications").get(0)).getJSONObject("segment").getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initName(JSONObject jsonObject){
        try {
            name = jsonObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initCategoryString() {
        if (!segment.equals("")){
            categoryString += segment + " | ";
        }
        if(!genre.equals("")) {
            categoryString += genre + " | ";
        }
        if(!categoryString.equals("")){
            categoryString = categoryString.substring(0, categoryString.length()-3);
        }
    }

    private void initSeatmapUrl(JSONObject jsonObject) {
        try {
            seatmapUrl = jsonObject.getJSONObject("seatmap").getString("staticUrl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initStatus(JSONObject jsonObject) {
        try {
            ticketstatus = jsonObject.getJSONObject("dates").getJSONObject("status").getString("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initGenre(JSONObject jsonObject){
        try {
            genre = ((JSONObject)jsonObject.getJSONArray("classifications").get(0)).getJSONObject("genre").getString("name");
        } catch (JSONException e) {
            genre = "";
        }
    }

    private void initPrice(JSONObject jsonObject){
        try {
            JSONObject obj = ((JSONObject)jsonObject.getJSONArray("priceRanges").get(0));
            String min = "";
            String max = "";
            if (obj.has("min")){
                min = obj.getString("min");
            }
            if(obj.has("max")){
                max = obj.getString("max");
            }
            if(min.equals("")) {
                price = "$" + max;
            }else {
                price = "$" + min + " ~ " + "$" + max;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getArtistString() {
        return artistString;
    }

    public String getVenue() {
        return venue;
    }

    public String getDateAndTime() {
        return date + " " + time;
    }

    public String getCategory() {
        return category;
    }

    public String getCategoryString(){
        return categoryString;
    }

    public String getPrice(){
        return price;
    }

    public String getStatus(){
        return ticketstatus;
    }

    public String getTicketUrl(){return ticketUrl; }

    public String getSeatmapUrl(){
        return seatmapUrl;
    }

    public String getName() {
        return name;
    }

    public String getDateTimeParsed(){
        return dateTimeParsed;
    }

    public List<String> getArtists(){
        return artists;
    }
    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if(this == obj) {
            return true;
        }
        if(obj.getClass()!=this.getClass()){
            return false;
        }

        SearchItem i = (SearchItem)obj;
        boolean result = this.venue.equals(i.getVenue())
                && this.name.equals(i.getName())
                && this.getDateAndTime().equals(i.getDateAndTime());
        return result;
    }
}
