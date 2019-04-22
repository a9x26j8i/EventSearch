package com.example.zewei.eventsearch.details;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zewei.eventsearch.R;
import com.example.zewei.eventsearch.resultpage.InternalStorage;
import com.example.zewei.eventsearch.resultpage.SearchItem;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {
    LinearLayout myLayout;
    SearchItem searchItem;

    public EventFragment() {
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
//        View tab = LayoutInflater.from(this).inflate(R.layout.detail_tab_view, null);
//        ImageView tabImage = tab.findViewById(R.id.tab_icon);
//        TextView tabText = tab.findViewById(R.id.tab_label);
//        tabText.setText(fillLabels[i]);
//        tabImage.setImageResource(fillImgs[i]);
//        tab.setMinimumWidth(255);
//        tabLayout.getTabAt(i).setCustomView(tab);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myLayout = getView().findViewById(R.id.info_container);
        if(!searchItem.getArtistString().equals("")){
            addItem("Artist/Team(s)",searchItem.getArtistString());
        }
        if(!searchItem.getVenue().equals("")){
            addItem("Venue", searchItem.getVenue());
        }
        if(!searchItem.getDateAndTime().equals("")){
            addItem("Time", searchItem.getDateTimeParsed());
        }
        if(!searchItem.getCategoryString().equals("")){
            addItem("Category", searchItem.getCategoryString());
        }
        if(!searchItem.getPrice().equals("")){
            addItem("Price Range", searchItem.getPrice());
        }
        if(!searchItem.getStatus().equals("")){
            addItem("Ticket Status", searchItem.getStatus().substring(0,1).toUpperCase() + searchItem.getStatus().substring(1));
        }
        if(!searchItem.getTicketUrl().equals("")){
            addItemUrl("Buy Tick At", "Ticketmaster",searchItem.getTicketUrl());
        }
        if(!searchItem.getSeatmapUrl().equals("")){
            addItemUrl("Seat Map","View Here", searchItem.getSeatmapUrl());
        }

    }

    public void testStorage(){
        List<SearchItem> cachedEntries = null;
        try {
            cachedEntries = (List<SearchItem>) InternalStorage.readObject(getContext(), "saved_events");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (SearchItem item : cachedEntries) {
            Log.i("eventfragment", "otherplace:" + item.getName());
        }
    }

    public static EventFragment newInstance(SearchItem searchItem){
        EventFragment eventFragment = new EventFragment();
        Bundle args = new Bundle();
        args.putSerializable("data", searchItem);
        eventFragment.setArguments(args);

        return eventFragment;
    }

    private void addItem(String key, String value) {
        View infoItem = LayoutInflater.from(getContext()).inflate(R.layout.info_item_view, null);
        TextView keyView = infoItem.findViewById(R.id.text_key);
        TextView valueView = infoItem.findViewById(R.id.text_value);
        keyView.setText(key);
        valueView.setText(value);
        myLayout.addView(infoItem);
    }

    private void addItemUrl(String key, String value, String url) {
        View infoItem = LayoutInflater.from(getContext()).inflate(R.layout.info_item_view, null);
        TextView keyView = infoItem.findViewById(R.id.text_key);
        TextView valueView = infoItem.findViewById(R.id.text_value);
        keyView.setText(key);
//        valueView.setText(value);
        String html = "<a href='" + url + "'>" + value + "</a>";
        valueView.setMovementMethod(LinkMovementMethod.getInstance());
        valueView.setText(Html.fromHtml(html,0));

        myLayout.addView(infoItem);
    }
}
