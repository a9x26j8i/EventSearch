package com.example.zewei.eventsearch;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zewei.eventsearch.adapter.FavAdapter;
import com.example.zewei.eventsearch.adapter.MyResultAdapter;
import com.example.zewei.eventsearch.resultpage.InternalStorage;
import com.example.zewei.eventsearch.resultpage.SearchItem;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment {

    private RecyclerView mRecycleView;
    private FavAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<SearchItem> items;
    private GestureDetector mDetector;
    private TextView noFavView;
    private MyGestureListener listener;

    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    public void init(){
        mRecycleView = getView().findViewById(R.id.fav_recycle_view);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        try {
            items = (List<SearchItem>) InternalStorage.readObject(getContext(), "saved_events");
        } catch (Exception e){
            items = new ArrayList<>();
        }
        noFavView = getView().findViewById(R.id.no_favourites);

        mAdapter = new FavAdapter(items,getContext());
        mRecycleView.setAdapter(mAdapter);

        listener = new MyGestureListener();
        mDetector = new GestureDetector(this.getContext(), listener);
        mRecycleView.setOnTouchListener(touchListener);
        listener.onDown(MotionEvent.obtain(0,0, MotionEvent.ACTION_DOWN, mRecycleView.getLeft()+30, mRecycleView.getTop()+30, 0));
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // pass the events to the gesture detector
            // a return value of true means the detector is handling it
            // a return value of false means the detector didn't
            // recognize the event
            return mDetector.onTouchEvent(event);

        }
    };

    public void refresh(){
        init();
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("touch",""+mAdapter.getCount());
            // don't return false here or else none of the other
            // gestures will work
            if(mAdapter.getCount() == 0){
                noFavView.setVisibility(View.VISIBLE);
            } else{
                noFavView.setVisibility(View.GONE);
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            return true;
        }
    }
}
