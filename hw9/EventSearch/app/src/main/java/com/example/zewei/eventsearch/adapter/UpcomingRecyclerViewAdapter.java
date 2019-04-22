package com.example.zewei.eventsearch.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zewei.eventsearch.R;
import com.example.zewei.eventsearch.details.UpcomingItem;

import java.util.List;


public class UpcomingRecyclerViewAdapter extends RecyclerView.Adapter<UpcomingRecyclerViewAdapter.UpcomingViewHolder> {

    List<UpcomingItem> mDataSet;

    public static class UpcomingViewHolder extends RecyclerView.ViewHolder{
        public TextView displayNameView;
        public TextView artistNameView;
        public TextView dateTimeView;
        public TextView eventTypeView;
        public CardView cardView;
        public UpcomingViewHolder(View v){
            super(v);
            displayNameView = v.findViewById(R.id.display_name);
            artistNameView = v.findViewById(R.id.artist_name);
            dateTimeView = v.findViewById(R.id.date_time);
            eventTypeView = v.findViewById(R.id.event_type);
            cardView = (CardView) v;
        }
    }

    public UpcomingRecyclerViewAdapter(List myDataset){
        mDataSet = myDataset;
    }

    @NonNull
    @Override
    public UpcomingRecyclerViewAdapter.UpcomingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView v = (CardView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.upcomming_item_view, viewGroup, false);
        UpcomingViewHolder upcomingViewHolder = new UpcomingViewHolder(v);
        return upcomingViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingRecyclerViewAdapter.UpcomingViewHolder viewHolder, int i) {
        viewHolder.displayNameView.setText(mDataSet.get(i).getDisplayName());
        viewHolder.artistNameView.setText(mDataSet.get(i).getArtistName());
        viewHolder.dateTimeView.setText(mDataSet.get(i).getDateTime());
        viewHolder.eventTypeView.setText(mDataSet.get(i).getType());
        //todo

        viewHolder.cardView.setOnClickListener(v1 -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDataSet.get(i).getUrl()));
            Log.i("songkickurl", i + ":" + mDataSet.get(i).getUrl());
            viewHolder.cardView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }



}
