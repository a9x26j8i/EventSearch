package com.example.zewei.eventsearch.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zewei.eventsearch.R;
import com.example.zewei.eventsearch.details.DetailsActivity;
import com.example.zewei.eventsearch.resultpage.InternalStorage;
import com.example.zewei.eventsearch.resultpage.SearchItem;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.util.List;

public class MyResultAdapter extends RecyclerView.Adapter<MyResultAdapter.MyViewHolder>{
    protected final Context context;
    protected SearchItem[] searchItems;
    protected final String TAG = "MyResultAdapter";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView nameView;
        public TextView venueView;
        public TextView timeDateView;
        public ImageView categoryView ;
        public ImageView heartView;
        public LinearLayout itemParent;

        public MyViewHolder(View v) {
            super(v);
            nameView = v.findViewById(R.id.label_name);
            venueView = v.findViewById(R.id.label_venue);
            timeDateView = v.findViewById(R.id.label_date_and_time);
            categoryView = v.findViewById(R.id.image_category);
            heartView = v.findViewById(R.id.image_heart);
            itemParent = v.findViewById(R.id.item_parent);
        }

        public void setInvisiable(){
            itemParent.setVisibility(View.GONE);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemParent.getLayoutParams();
            params.height = 0;
            params.width = 0;
            itemParent.setLayoutParams(params);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyResultAdapter(SearchItem[] myDataset, Context context) {
        searchItems = myDataset;
        this.context = context;
    }

    public MyResultAdapter(List<SearchItem> dataSet, Context context){
        this.context = context;
        searchItems = new SearchItem[dataSet.size()];
        for (int i = 0; i<dataSet.size(); i++) {
            searchItems[i] = dataSet.get(i);
        }
    }

    public SearchItem[] getSearchItems() {
        return searchItems;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyResultAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    protected int getHeartView(SearchItem item){
        if (!InternalStorage.contains(context, item)) {
            return R.drawable.heart_outline_black;
        }else {
            return R.drawable.heart_fill_red;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.nameView.setText(searchItems[position].getName());
        holder.venueView.setText(searchItems[position].getVenue());
        holder.timeDateView.setText(searchItems[position].getDateAndTime());
        holder.categoryView.setImageDrawable(this.context.getDrawable(getCategoryDrawable(holder, position)));
        //
        // //CHANGE HEART IMAGE HERE
        holder.heartView.setImageDrawable(this.context.getDrawable(getHeartView(searchItems[position])));

        holder.itemView.findViewById(R.id.middle_content).setOnClickListener(view ->{
//            holder.nameView.getText().toString();
            Log.i(TAG, "item clicked" + " " + searchItems[position].getName());
            goToDetail(searchItems[position]);
        });

        holder.heartView.setOnClickListener(view -> {
            if (InternalStorage.contains(context, searchItems[position])) {
                InternalStorage.removeObject(context, searchItems[position]);
                holder.heartView.setImageDrawable(context.getDrawable(R.drawable.heart_outline_black));
            } else {
                InternalStorage.addObject(context, searchItems[position]);
                holder.heartView.setImageDrawable(context.getDrawable(R.drawable.heart_fill_red));

            }
        });
    }


    protected void goToDetail(SearchItem searchItem) {
        Intent intent = new Intent(this.context, DetailsActivity.class);
        intent.putExtra("search_item", searchItem);
        this.context.startActivity(intent);

    }

    protected int getCategoryDrawable(MyViewHolder holder, int position) {
        switch(searchItems[position].getCategory()){
            case "KZFzniwnSyZfZ7v7nJ":
                return R.drawable.music_icon;
            case "KZFzniwnSyZfZ7v7nE":
                return R.drawable.sport_icon;
            case "KZFzniwnSyZfZ7v7na":
                return R.drawable.art_icon;
            case "KZFzniwnSyZfZ7v7nn":
                return R.drawable.film_icon;
            case "KZFzniwnSyZfZ7v7n1":
                return R.drawable.miscellaneous_icon;
            default:
                return R.drawable.music_icon;
        }
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return searchItems.length;
    }



}
