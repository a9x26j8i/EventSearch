package com.example.zewei.eventsearch.details;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.EventLog;
import android.util.Log;
import android.widget.Toast;

import com.example.zewei.eventsearch.FavouriteFragment;
import com.example.zewei.eventsearch.SearchFragment;
import com.example.zewei.eventsearch.resultpage.SearchItem;

public class DetailsPageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;
    private SearchItem searchItem;

    DetailsPageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
//                Log.i("detailadapter", "new!!! ");
                return EventFragment.newInstance(searchItem);
//                return new EventFragment();
            case 1:
//                Log.i("detailadapter", "new!!! ");
                return ArtistFragment.newInstance(searchItem);

            case 2:
//                Log.i("detailadapter", "new!!! ");
                return VenueFragment.newInstance(searchItem);
            case 3:
//                Log.i("detailadapter", "new!!! ");
                return UpcomingFragment.newInstance(searchItem);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {

        return numOfTabs;
    }

    public void getDada(SearchItem searchItem){
        this.searchItem = searchItem;
    }
}
