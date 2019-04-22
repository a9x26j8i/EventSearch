package com.example.zewei.eventsearch;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.LoginFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.zewei.eventsearch.adapter.MyArrayAdapter;
import com.example.zewei.eventsearch.resultpage.ResultActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ch.hsr.geohash.GeoHash;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    public static final int PARSE_SUGGESTION = 0;
    private static final int PARSE_SEARCH = 1;
    private static final String TAG = "searchfrag";
    private List<String> suggestions;
    private AutoCompleteTextView input_keyword;
    private TextView input_distance;
    private MyArrayAdapter adapter;
    private Handler handler;
    private Spinner spinner_category;
    private Spinner spinner_unit;
    private RadioGroup radioGroup;
    private RadioButton radio_here;
    private RadioButton radio_other;
    private TextView input_location;
    private Button button_search;
    private Button button_clear;

    private TextView label_keyword_err;
    private TextView label_location_err;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager mLocationManager;

    double latitude;
    double longitude;


    private boolean isLocationFinish = false;

    RequestQueue mQueue;

    String keyword;
    String segmentId;
    final String[] geoPoint = {"9q5ce"};
    String distance;
    String unit;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        initFields();

        initAutocomplete();

        initSpinners();

        initButtons();

        initRadioGroup();

        initLocation();

        super.onActivityCreated(savedInstanceState);
    }

    private void initLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                SearchFragment.this.latitude = location.getLatitude();
                SearchFragment.this.longitude = location.getLongitude();
                Log.i("locloc", "onLocationChanged: "+ location.getLongitude() + location.getLatitude());

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    private void startGetFieldandGo() {
        keyword = input_keyword.getText().toString();
        segmentId = toSegmentIdQuery();
        distance = isBlank(input_distance.getText().toString()) ?
                "10" : input_distance.getText().toString();
        unit = spinner_unit.getSelectedItemPosition() == 0 ? "miles" : "km";
        //geopoint
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            geoPoint[0] = "gbsuu";
        }
        //      this.latitude = 34.0298839;
//        //      this.longitude = -118.3023437;
//        Log.i(TAG, "latitude: " + latitude);
//        Log.i(TAG, "longitude: " + longitude);
        if (radioGroup.getCheckedRadioButtonId() == R.id.radio_here) {
            geoPoint[0] = GeoHash.geoHashStringWithCharacterPrecision(this.latitude, this.longitude, 5);
            if (geoPoint[0] == "s0000") {
                geoPoint[0] = "9q5ce";
            }
            getToResultPage();
        } else {
            String targetPlace = input_location.getText().toString().trim();
            String url = getResources().getString(R.string.api) + "/customloc";
            url = Uri.parse(url).buildUpon()
                    .appendQueryParameter("there", targetPlace)
                    .build().toString();
            Log.i("geohash", "chufale:"+ url);
            JsonObjectRequest locRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            geoPoint[0] = response.getString("geohash");
                        } catch (JSONException e) {
                            Log.i("geohash", "getGeohash error");
                            e.printStackTrace();
                        }
                        Log.i("geohash", "goe geohash:" + geoPoint[0]);
                        getToResultPage();
                    }, error -> Log.i("geohash", "response error"));
            mQueue.add(locRequest);
        }

//        geoPoint[0] = GeoHash.geoHashStringWithCharacterPrecision(this.latitude, this.longitude, 5);
//        if (geoPoint[0] == "s0000") {
//            geoPoint[0] = "9q5ce";
//        }
//        getToResultPage();
    }

    private void getToResultPage() {
        Intent intent = new Intent(getActivity(), ResultActivity.class);
        String url = getSearchUrl();
        intent.putExtra("url_result", url);
        getActivity().startActivity(intent);
    }

    private String getSearchUrl() {
        Log.i("searchfrag", " " + keyword + " " + segmentId + " " + geoPoint[0] + " " + distance + " " + unit);
        String url = "http://eventsearchapi.us-west-1.elasticbeanstalk.com/api/search";
        url = Uri.parse(url).buildUpon()
                .appendQueryParameter("keyword", keyword)
                .appendQueryParameter("segmentId", segmentId)
                .appendQueryParameter("geoPoint", geoPoint[0])
                .appendQueryParameter("radius", distance)
                .appendQueryParameter("unit", unit)
                .build().toString();
        Log.i(TAG, "url:" + url);
        return url;
    }

    private String toSegmentIdQuery() {
        String selected = spinner_category.getSelectedItem().toString();
        switch (selected) {
            case "All":
                return "";
            case "Music":
                return "KZFzniwnSyZfZ7v7nJ";
            case "Sports":
                return "KZFzniwnSyZfZ7v7nE";
            case "Arts & Theatre":
                return "KZFzniwnSyZfZ7v7na";
            case "Film":
                return "KZFzniwnSyZfZ7v7nn";
            case "Miscellaneous":
                return "KZFzniwnSyZfZ7v7n1";
            default:
                return "ERROR";
        }
    }

    private String toGeoPointQuery() {
//        fusedLocationProviderClient.getLastLocation()
//                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if(location != null) {
//                            refreshLocation(location.getLatitude(), location.getLongitude());
//
//                        }
//                    }
//                });

//        result = GeoHash.geoHashStringWithCharacterPrecision(latitude[0], longitude[0], 5);
        return null;
    }

    private void refreshLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        Log.i(TAG, "latitude: " + latitude);
        Log.i(TAG, "longitude: " + longitude);
    }


    private void retrieveData(Editable s) {
        String url = makeSuggestUrl(input_keyword.getText().toString());
        jsonParse(url, PARSE_SUGGESTION);
        adapter.notifyDataSetChanged();
    }

    public void jsonParse(String url, final int parseWhat) {
//        String url = "https://api.myjson.com/bins/144dhi";
//        String url = "http://eventsearchapi.us-west-1.elasticbeanstalk.com/api/suggest?input=lakers";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            switch (parseWhat) {
                                case PARSE_SUGGESTION:
                                    //get suggestions array
                                    String[] result = parseSuggestions(response);
//                                    String[] result2 = {"aaa", "aab", "aac", "aad", "aae"};

//                                    suggestions.addAll(Arrays.asList(result));

                                    adapter.clear();
                                    adapter.addAll(result);
                                    Log.i("searchfrag", "got result:");
                                    Log.i("searchfrag", Arrays.toString(result));
//                                    Toast.makeText(getContext(), "got json", Toast.LENGTH_SHORT).show();
                                    break;
                                case PARSE_SEARCH:

                            }
//                            Toast.makeText(MainActivity.this, suggestions[0], Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
//                            Toast.makeText(MainActivity.this, "JSON handle error!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error output
//                Toast.makeText(MainActivity.this, "network error", Toast.LENGTH_SHORT).show();
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
            result[i] = ((JSONObject) suggestionArray.get(i)).getString("name");
        }
        return result;
    }

    private String makeSuggestUrl(String query) {
        String url = "http://eventsearchapi.us-west-1.elasticbeanstalk.com/api/suggest";
        url = Uri.parse(url).buildUpon()
                .appendQueryParameter("input", query)
                .build().toString();
        return url;
    }

    private void initAutocomplete() {
        adapter = new MyArrayAdapter(getContext(), android.R.layout.simple_list_item_1, suggestions);
        input_keyword.setAdapter(adapter);
        input_keyword.addTextChangedListener(new TextWatcher() {
            long lastPress = 0l;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (System.currentTimeMillis() - lastPress > 600) {
                    lastPress = System.currentTimeMillis();
                    retrieveData(s);
                }

            }
        });
    }

    private void initFields() {
        input_keyword = getActivity().findViewById(R.id.input_keyword);
        input_distance = getView().findViewById(R.id.input_distance);
        input_location = getView().findViewById(R.id.input_location);
        suggestions = new LinkedList<>();
        mQueue = Volley.newRequestQueue(getContext());
        spinner_category = getView().findViewById(R.id.spinner_category);
        spinner_unit = getView().findViewById(R.id.spinner_unit);
        button_search = getView().findViewById(R.id.button_search);
        button_clear = getView().findViewById(R.id.button_clear);
        radioGroup = getView().findViewById(R.id.radiogroup);
        radio_here = getView().findViewById(R.id.radio_here);
        radio_other = getView().findViewById(R.id.radio_other);
        label_keyword_err = getView().findViewById(R.id.label_keyword_err);
        label_location_err = getView().findViewById(R.id.label_location_err);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    private void initSpinners() {
        //spinner_category
        String[] categories = getResources().getStringArray(R.array.resource_category);
        SpinnerAdapter adapter_category = new ArrayAdapter<String>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, categories);
        spinner_category.setAdapter(adapter_category);
        //spinner_unit
        String[] units = getResources().getStringArray(R.array.resource_miles);
        SpinnerAdapter adapter_unit = new ArrayAdapter<String>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, units);
        spinner_unit.setAdapter(adapter_unit);
    }

    private boolean isBlank(String str) {
        return str.trim().equals("");
    }

    private void initButtons() {
        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
//                Log.i(TAG, "clicked");
            }
        });

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validity = true;
                boolean validity_keyword = !isBlank(input_keyword.getText().toString());
                boolean validity_location = checkLocationValidity();
                boolean validity_internet = checkInternet();
                if (!validity_keyword) {
                    label_keyword_err.setVisibility(View.VISIBLE);
                    validity = false;
                } else {
                    label_keyword_err.setVisibility(View.GONE);
                }
                if (!validity_location) {
                    label_location_err.setVisibility(View.VISIBLE);
                    validity = false;
                } else {
                    label_location_err.setVisibility(View.GONE);
                }
                if (!validity) {
                    Toast.makeText(getContext(), "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
                }
                if (!validity_internet) {
                    Toast.makeText(getContext(), "no internet connection", Toast.LENGTH_SHORT).show();
                }

                if (!validity || !validity_internet) {
                    return;
                } else {
                    startGetFieldandGo();
                }
            }
        });
    }

    private boolean checkInternet() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
//            Toast.makeText(getContext(), "no internet connection", Toast.LENGTH_SHORT).show();
            return false;
        } else {
//            Toast.makeText(getContext(), "yes internet", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private boolean checkLocationValidity() {
        if (radioGroup.getCheckedRadioButtonId() == R.id.radio_here) {
            return true;
        } else {
            return !input_location.getText().toString().trim().equals("");
        }
    }

    private void initRadioGroup() {
        input_location.setEnabled(false);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_here:
                        input_location.setText("");
                        input_location.setEnabled(false);
                        label_location_err.setVisibility(View.GONE);
                        break;
                    case R.id.radio_other:
                        input_location.setEnabled(true);
                        break;
                }
            }
        });
    }

    private void reset() {
        input_keyword.setText("");
        input_distance.setText("");
        spinner_category.setSelection(0);
        spinner_unit.setSelection(0);
        //reset radiogroup
        radioGroup.check(R.id.radio_here);
        input_location.setText("");
        input_location.setEnabled(false);
        label_keyword_err.setVisibility(View.GONE);
        label_location_err.setVisibility(View.GONE);
    }
}
