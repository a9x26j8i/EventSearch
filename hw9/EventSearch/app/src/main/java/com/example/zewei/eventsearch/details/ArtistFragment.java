package com.example.zewei.eventsearch.details;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.LoginFilter;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.zewei.eventsearch.R;
import com.example.zewei.eventsearch.resultpage.ResultActivity;
import com.example.zewei.eventsearch.resultpage.SearchItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends Fragment {

    SearchItem searchItem;
    List<String> artists;
    LinearLayout myLayout;
    LinearLayout myLayout2;
    RequestQueue mQueue;
    List<String> requestPhotoUrl;
    List<List<String>> photoUrl;
    String singername ="";
    String singerfollowers = "";
    String singerPopulatiry = "";
    String checkAtUrl = "";


    public ArtistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myLayout = getView().findViewById(R.id.artist_container);
        myLayout2 = getView().findViewById(R.id.artist_container2);
        mQueue = Volley.newRequestQueue(getContext());
        generateRequestPhotoUrl();
        Log.i("artists", artists.toString());
        photoUrl = new ArrayList<>();
        photoUrl.add(new ArrayList<String>());
        photoUrl.add(new ArrayList<String>());

        for(int i = 0; i<requestPhotoUrl.size(); i++){
            generatePhotoUrl(requestPhotoUrl.get(i), i);
        }
        if(artists.size() == 0){
            getView().findViewById(R.id.artist_no_record).setVisibility(View.VISIBLE);
        }

    }

    private void generatePhotoUrl(String url, int position){
        Log.i("photourl", position + " start:");
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        extractUrl(response, position);
                        if(isMusic()) {
                            String url = getString(R.string.api) + "/singers?singer=" + artists.get(position);
                           mQueue.add(new JsonObjectRequest(url, null, response1 -> {
                                parseSinger(response1);
                                Log.i("singer", "onResponse: " + singername);
                                generateContent(position);
                            }, error -> {
                            }));
                        }else{
                            generateContent(position);
                            Log.i("photourl", photoUrl.get(position).toString());

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("resultActivity", "response error");
                    }
                });
        mQueue.add(jsonObjectRequest);
        Log.i("photourl", "add:"+position);
    }

    private void parseSinger(JSONObject jsonObject){
        try {
            jsonObject= jsonObject.getJSONObject("body").getJSONObject("artists")
                    .getJSONArray("items").getJSONObject(0);
            singername = jsonObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            Long num = null;
            num = Long.parseLong(jsonObject.getJSONObject("followers").getString("total"));
            DecimalFormat formatter = new DecimalFormat("###,###,###,###");
            singerfollowers = formatter.format(num);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            singerPopulatiry = jsonObject.getString("popularity");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            checkAtUrl = jsonObject.getJSONObject("external_urls").getString("spotify");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void generateContent(int position){
//        addItem("Name", artists.get(position), position);

        addTitle(artists.get(position), position);

        if (isMusic()) {

            Log.i("singer", "it's music ");
            if(!singername.equals("")){
                addItem("Name", singername, position);
            }
            if(!singerfollowers.equals("")){
                addItem("Followers", singerfollowers, position);
            }
            if(!singerPopulatiry.equals("")){
                addItem("Popularity", singerPopulatiry, position);
            }
            if(!checkAtUrl.equals("")){
                addItemUrl("Check At", "Spotify", checkAtUrl, position);
//                addItem("check at", checkAtUrl,position);
            }
        }



        //add photos
        for(String url : photoUrl.get(position)){
            addPhoto(url, position);
        }
    }

    private void extractUrl(JSONObject response, int position){
        List<String> list = new ArrayList<>();
        try {
            JSONArray array = response.getJSONArray("items");
            for (int i = 0; i < array.length(); i++) {
                list.add(array.getJSONObject(i).getString("link"));
            }
            photoUrl.get(position).addAll(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void generateRequestPhotoUrl(){
        requestPhotoUrl = new ArrayList<>();
        for (int i=0; i<artists.size(); i++) {
            if(i == 2) {
                break;
            }
            String url = getResources().getString(R.string.api) + "/photos";
            url = Uri.parse(url).buildUpon()
                    .appendQueryParameter("q", artists.get(i))
                    .build().toString();
            requestPhotoUrl.add(url);
        }
//        Log.i("photourl", requestPhotoUrl.toString());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchItem = (SearchItem)getArguments().getSerializable("data");
            artists = searchItem.getArtists();
        }
    }

    public static ArtistFragment newInstance(SearchItem searchItem){
        ArtistFragment artistFragment = new ArtistFragment();
        Bundle args = new Bundle();
        args.putSerializable("data", searchItem);
        artistFragment.setArguments(args);

        return artistFragment;
    }

    private void addItem(String key, String value, int position) {
        View infoItem = LayoutInflater.from(getContext()).inflate(R.layout.info_item_view, null);

        TextView keyView = infoItem.findViewById(R.id.text_key);
        TextView valueView = infoItem.findViewById(R.id.text_value);
        keyView.setText(key);
        valueView.setText(value);
        if (position == 0){
            myLayout.addView(infoItem);
        } else{
            myLayout2.addView(infoItem);
        }

    }

    private void addTitle(String title, int position) {
        View titleTextView = LayoutInflater.from(getContext()).inflate(R.layout.title_view, null);
        ((TextView)(titleTextView.findViewById(R.id.title_view_text))).setText(title);
        if (position == 0){
            myLayout.addView(titleTextView);

        } else{
            myLayout2.addView(titleTextView);
        }
    }

    private boolean isMusic(){
        return searchItem.getCategory().equals("KZFzniwnSyZfZ7v7nJ");
    }

    private void addPhoto(String url, int position) {
        ImageView imageView = new ImageView(getContext());
        if(position == 0) {
            myLayout.addView(imageView);
        }else{
            myLayout2.addView(imageView);
        }
//        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        Picasso
                .get()
                .load(url)
                .resize(1200,0)
                .into(imageView);
//        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        imageView.setAdjustViewBounds(true);
        Log.i("imgwidth", "" + imageView.getWidth());
    }

    private void addItemUrl(String key, String value, String url, int position) {
        View infoItem = LayoutInflater.from(getContext()).inflate(R.layout.info_item_view, null);
        TextView keyView = infoItem.findViewById(R.id.text_key);
        TextView valueView = infoItem.findViewById(R.id.text_value);
        keyView.setText(key);
//        valueView.setText(value);
        String html = "<a href='" + url + "'>" + value + "</a>";
        valueView.setMovementMethod(LinkMovementMethod.getInstance());
        valueView.setText(Html.fromHtml(html,0));
        if(position == 0){
            myLayout.addView(infoItem);
        }else{
            myLayout2.addView(infoItem);
        }

    }
}
