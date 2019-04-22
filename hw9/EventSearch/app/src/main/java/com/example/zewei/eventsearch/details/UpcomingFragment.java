package com.example.zewei.eventsearch.details;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.zewei.eventsearch.R;
import com.example.zewei.eventsearch.adapter.UpcomingRecyclerViewAdapter;
import com.example.zewei.eventsearch.resultpage.SearchItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingFragment extends Fragment {

    LinearLayout myLayout;
    SearchItem searchItem;
    Spinner spinnerType;
    Spinner spinnerSequence;
    RecyclerView recyclerView;
    LinearLayout loadingBar;
    TextView upcomingNorecord;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    RequestQueue mQueue;
    List<UpcomingItem> upcomingItems;
    UpcomingItemsComparator comparator;

    public UpcomingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchItem = (SearchItem)getArguments().getSerializable("data");
        }
    }

    private void initFields(){
        myLayout = getView().findViewById(R.id.container_upcoming);
        spinnerType = getView().findViewById(R.id.sort_type_spinner);
        spinnerSequence = getView().findViewById(R.id.sort_sequence_spinner);
        recyclerView = getView().findViewById(R.id.recycleView_upcoming);
        loadingBar = getView().findViewById(R.id.loading_bar);
        upcomingNorecord = getView().findViewById(R.id.upcoming_norecord);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        mQueue = Volley.newRequestQueue(getContext());
        upcomingItems = new ArrayList<>();
        comparator = new UpcomingItemsComparator();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initFields();
        initSpinners();

        String url = Uri.parse(getResources().getString(R.string.api)+"/songkick/events" ).buildUpon()
                .appendQueryParameter("venuename", searchItem.getVenue()).build().toString();

        JsonObjectRequest upcomingRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    getUpcomingItems(response);
                    mAdapter = new UpcomingRecyclerViewAdapter(upcomingItems);
                    recyclerView.setAdapter(mAdapter);
                    loadingBar.setVisibility(View.GONE);
                    if(upcomingItems.size() == 0) {
                        upcomingNorecord.setVisibility(View.VISIBLE);
                    }
                }, error -> Log.i("geohash", "response error"));
        mQueue.add(upcomingRequest);





//        addItem("field", "upcomming");
//        addItem("name",searchItem.getName());
    }

    private void getUpcomingItems(JSONObject jsonObject){
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = jsonObject.getJSONObject("resultsPage")
                        .getJSONObject("results")
                        .getJSONArray("event");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for(int i = 0; i<jsonArray.length(); i++){
            if(i == 5){
                break;
            }
            try {
                UpcomingItem item = new UpcomingItem(jsonArray.getJSONObject(i));
                upcomingItems.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static UpcomingFragment newInstance(SearchItem searchItem){
        UpcomingFragment upcomingFragment= new UpcomingFragment();
        Bundle args = new Bundle();
        args.putSerializable("data", searchItem);
        upcomingFragment.setArguments(args);

        return upcomingFragment;
    }

    private void initSpinners() {
        //spinnerType
        String[] type = getResources().getStringArray(R.array.sort_type);
        SpinnerAdapter adapterType = new ArrayAdapter<String>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, type);
        spinnerType.setAdapter(adapterType);
        //spinnerSequence
        String[] sequence = getResources().getStringArray(R.array.sort_sequence);
        SpinnerAdapter adapterSequence = new ArrayAdapter<String>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, sequence);
        spinnerSequence.setAdapter(adapterSequence);
        spinnerSequence.setEnabled(false);

        //
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(upcomingItems != null && upcomingItems.size()>0){
                    if(position == 0){
                        spinnerSequence.setSelection(0);
                        spinnerSequence.setEnabled(false);
                    }else{
                        spinnerSequence.setEnabled(true);
                    }
                    comparator.transformType(position);
                    comparator.transformSequence(spinnerSequence.getSelectedItemPosition());
                    upcomingItems.sort(comparator);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSequence.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(upcomingItems != null && upcomingItems.size()>0){
                    comparator.transformSequence(position);
                    upcomingItems.sort(comparator);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

class UpcomingItemsComparator implements Comparator<UpcomingItem> {

    int type = 0;
    int sequence = 0;

    public void transformType(int type){
        this.type = type;
    }

    public void transformSequence(int sequence){
        this.sequence = sequence;
    }

    @Override
    public int compare(UpcomingItem o1, UpcomingItem o2) {
        int result = 0;
        switch (type){
            case 0:
                result = o1.getDate().compareToIgnoreCase(o2.getDate());
                break;
            case 1:
                result = o1.getArtistName().compareToIgnoreCase(o2.getArtistName());
                break;
            case 2:
                result = o1.getDate().compareToIgnoreCase(o2.getDate());
                break;
            case 3:
                result = o1.getArtistName().compareToIgnoreCase(o2.getArtistName());
                break;
            case 4:
                result = o1.getType().compareToIgnoreCase(o2.getType());
                break;
            default:
                result = o1.getDate().compareToIgnoreCase(o2.getDate());
        }
        if(sequence == 1){
            result = -result;
        }

        return result;
    }
}