package com.example.zewei.eventsearch.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.List;

public class MyArrayAdapter extends ArrayAdapter<String> {
    private Filter filter = new KNoFilter();
    public List<String> items;

//    public MyArrayAdapter(Context context, int resource, List<String> objects) {
//        super(context, resource, objects);
//        items = objects;
//    }


    public MyArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
        super(context, textViewResourceId, objects);
        Log.v("Krzys", "Adapter created " + filter);
//        items = objects;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }



    private class KNoFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence arg0) {
            FilterResults result = new FilterResults();
            result.values = items;
            result.count = items.size();
            return result;
        }

        @Override
        protected void publishResults(CharSequence arg0, FilterResults arg1) {
            notifyDataSetChanged();
        }
    }
}

