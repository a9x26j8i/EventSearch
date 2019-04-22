package com.example.zewei.eventsearch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.zewei.eventsearch.R;
import com.example.zewei.eventsearch.resultpage.InternalStorage;
import com.example.zewei.eventsearch.resultpage.SearchItem;

import java.util.List;

public class FavAdapter extends MyResultAdapter {


    private int count;

    public FavAdapter(List<SearchItem> dataSet, Context context) {
        super(dataSet, context);
        count = dataSet.size();
    }

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
                holder.setInvisiable();
                count -- ;
            } else {
                InternalStorage.addObject(context, searchItems[position]);
                holder.heartView.setImageDrawable(context.getDrawable(R.drawable.heart_fill_red));

            }
        });
    }

    public int getCount() {
        return count;
    }
}
