package com.example.zewei.eventsearch.details;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapView;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;

import com.example.zewei.eventsearch.R;
import com.example.zewei.eventsearch.resultpage.SearchItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class VenueFragment extends Fragment implements OnMapReadyCallback {

    LinearLayout myLayout;
    SearchItem searchItem;
    MapView mapView;
    GoogleMap gmap;

    public VenueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchItem = (SearchItem)getArguments().getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_venue, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myLayout = getView().findViewById(R.id.venue_container);
        if(!searchItem.getVenue().equals("")){
            addItem("Name", searchItem.getVenue());

        }
        if(!searchItem.getAddress().equals("")){
            addItem("Address", searchItem.getAddress());

        }
        if(!searchItem.getCity().equals("")){
            addItem("City", searchItem.getCity());

        }
        if(!searchItem.getPhone().equals("")){
            addItem("Phone Number", searchItem.getPhone());

        }
        if(!searchItem.getOpenHours().equals("")){
            addItem("Open Hours", searchItem.getOpenHours());

        }
        if(!searchItem.getGeneralRule().equals("")){
            addItem("General Rule", searchItem.getGeneralRule());

        }
        if(!searchItem.getChildRule().equals("")){
            addItem("Child Rule", searchItem.getChildRule());

        }


        mapView = getView().findViewById(R.id.mapView);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
        mapView.onStart();
        mapView.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(15);
        LatLng ny = new LatLng(searchItem.getLatitude(), searchItem.getLongitude());
        gmap.addMarker(new MarkerOptions().position(ny));
        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static VenueFragment newInstance(SearchItem searchItem){
        VenueFragment venueFragment= new VenueFragment();
        Bundle args = new Bundle();
        args.putSerializable("data", searchItem);
        venueFragment.setArguments(args);

        return venueFragment;
    }


    private void addItem(String key, String value) {
        View infoItem = LayoutInflater.from(getContext()).inflate(R.layout.info_item_view, null);
        TextView keyView = infoItem.findViewById(R.id.text_key);
        TextView valueView = infoItem.findViewById(R.id.text_value);
        keyView.setText(key);
        valueView.setText(value);
        myLayout.addView(infoItem);
    }

}
