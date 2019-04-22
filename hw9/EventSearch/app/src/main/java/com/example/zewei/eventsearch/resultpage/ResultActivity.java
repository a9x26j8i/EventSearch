package com.example.zewei.eventsearch.resultpage;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.zewei.eventsearch.R;
import com.example.zewei.eventsearch.adapter.MyResultAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    List<SearchItem> resultList;
    Toolbar toolbar;
//    ProgressBar progressBar;
    LinearLayout layoutProgressBar;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    RequestQueue mQueue;
    Gson gson;
    String url;
//    String[] myDataset = {"aaaaaaaa","bbbbbbbb","ccccccc"};
    public final String TAG = "resultActivity";
    public final String SAVED_EVENTS = "saved_events";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultList = new ArrayList<>();
        Intent intent = getIntent();
        url = intent.getStringExtra("url_result");
        gson = new Gson();
        mQueue = Volley.newRequestQueue(this);
        //set recyclerView
        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setVisibility(View.GONE);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        layoutProgressBar = findViewById(R.id.layout_progressbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar_result);
        //返回按钮
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

//        actionBar.setDisplayShowHomeEnabled(true);
        dismissAndShowItems();
    }

    public void testStorage(){
        if (resultList == null) {
            return;
        }
        //continue
        Log.i(TAG, "testStorage: " + resultList.size());
//        for (int i = 0; i < resultList.size(); i++) {
//            Log.i(TAG, "testStorage: " + resultList.get(i).getName());
//        }
        try {
            InternalStorage.writeObject(this, "saved_events", resultList);
            List<SearchItem> cachedEntries = (List<SearchItem>) InternalStorage.readObject(this, SAVED_EVENTS);
            for (SearchItem item : cachedEntries) {
                Log.i(TAG, "item:" + item.getName());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void dismissAndShowItems(){
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        layoutProgressBar.setVisibility(View.GONE);
                        loadContent(response);
                        showContent();

//                        testStorage();

                        Log.i("resultActivity", "onResponse: " + response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        layoutProgressBar.setVisibility(View.GONE);
                        Toast.makeText(ResultActivity.this, "network error", Toast.LENGTH_SHORT).show();
                        Log.i("resultActivity", "response error");
                    }
                });
        mQueue.add(jsonObjectRequest);
//        new Handler().postDelayed(new Runnable(){
//            public void run() {
//                layoutProgressBar.setVisibility(View.GONE);
//                showContent();
//            }
//        }, 1000);
    }

    private void loadContent(JSONObject response){
        try {
            response = response.getJSONObject("_embedded");
            JSONArray events = response.getJSONArray("events");
            SearchItem[] searchItems = new SearchItem[events.length()];

            for (int i=0; i < events.length(); i++) {
                searchItems[i] = new SearchItem((JSONObject)events.get(i));
                resultList.add(searchItems[i]);
            }

            Log.i(TAG, "loadContent: " + searchItems.length);
            adapter = new MyResultAdapter(searchItems, this);
            Log.i(TAG, "adapter: " + adapter.getItemCount());

            recyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showContent(){
        if (adapter == null || adapter.getItemCount() == 0) {
            findViewById(R.id.label_noresult).setVisibility(View.VISIBLE);

        }else {
            recyclerView.setVisibility(View.VISIBLE);
//            Toast.makeText(this, url, Toast.LENGTH_LONG).show();
        }
    }
}
