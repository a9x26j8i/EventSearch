package com.example.zewei.eventsearch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static  final int PARSE_SUGGESTION = 0;
    private static final int PARSE_SEARCH = 1;
    private static final int MY_LOCATION_REQUEST = 3;

    Toolbar toolbar;
    TabLayout tabLayout;
    TabItem tabSearch;
    TabItem tabFavourite;
    ViewPager viewPager;
    PageAdapter pageAdapter;
    RequestQueue mQueue;
    Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //set tool bar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name)); //获取资源R
        setSupportActionBar(toolbar);
        //set tablayout & viewpager & pageadapter
        tabLayout = findViewById(R.id.tablayout);
        tabSearch = findViewById(R.id.tabsearch);
        tabFavourite = findViewById(R.id.tabfavourite);

        viewPager = findViewById(R.id.viewPager);
        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        gson = new Gson();
        mQueue = Volley.newRequestQueue(this);
        //
        setTab();
        getPermission();//get geo-location permission

        String json = "[{\"age\":30,\"role\":\"wife\"},{\"age\":29,\"role\":\"daugher\"}]";
        Type familyType = new TypeToken<ArrayList<FamilyMember>>(){}.getType();
        ArrayList<FamilyMember> family = gson.fromJson(json, familyType);
        String url = "http://eventsearchapi.us-west-1.elasticbeanstalk.com/api/suggest?input=lakers";
//        jsonParse(url, PARSE_SUGGESTION);

    }


    public void jsonParse(String url, final int parseWhat) {

//        String url = "https://api.myjson.com/bins/144dhi";
//        String url = "http://eventsearchapi.us-west-1.elasticbeanstalk.com/api/suggest?input=lakers";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String[] suggestions = new String[1];
                            switch (parseWhat) {
                                case PARSE_SUGGESTION:
                                    //get suggestions array
                                    suggestions = parseSuggestions(response);
                                    break;
                                case PARSE_SEARCH:

                            }
                            Toast.makeText(MainActivity.this, suggestions[0], Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "JSON handle error!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error output
                Toast.makeText(MainActivity.this, "network error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    private String[] parseSuggestions(JSONObject response) throws JSONException {
        JSONObject jsonObject = response.getJSONObject("_embedded");
        JSONArray suggestionArray = jsonObject.getJSONArray("attractions");
        String[] result = new String[suggestionArray.length()];
        for (int i = 0; i < suggestionArray.length(); i++) {
            result[i] = ((JSONObject)suggestionArray.get(i)).getString("name");
        }
        return result;
    }

    private void getPermission() {
        int permissionCheckFine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckCoarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if ( permissionCheckFine != PackageManager.PERMISSION_GRANTED || permissionCheckCoarse != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_LOCATION_REQUEST);
        }
    }

    private void setTab(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
//                    Toast.makeText(MainActivity.this, "position1", Toast.LENGTH_SHORT).show();
                } else if(tab.getPosition() == 1) {
//                    Toast.makeText(MainActivity.this, "position2", Toast.LENGTH_SHORT).show();
                    FavouriteFragment favouriteFragment = (FavouriteFragment) pageAdapter.instantiateItem(viewPager, 1);
                    favouriteFragment.refresh();
                } else {
                    Toast.makeText(MainActivity.this, "position unknown", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }
}
