package com.example.zewei.eventsearch.details;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zewei.eventsearch.MainActivity;
import com.example.zewei.eventsearch.R;
import com.example.zewei.eventsearch.resultpage.InternalStorage;
import com.example.zewei.eventsearch.resultpage.SearchItem;

import org.w3c.dom.Text;

public class DetailsActivity extends AppCompatActivity {

    private String[] fillLabels = {
            "EVENT",
            "ARTIST(S)",
            "VENUE",
            "UPCOMING"
    };

    DetailsPageAdapter pageAdapter;
    TabLayout tabLayout;
    ViewPager viewPager;
    SearchItem searchitem;
    Toolbar toolbar;

    private int[] fillImgs = {
            R.drawable.info_outline,
            R.drawable.artist,
            R.drawable.venue,
            R.drawable.upcoming
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        searchitem = (SearchItem)intent.getSerializableExtra("search_item");
//        Toast.makeText(this, searchitem.getName(),Toast.LENGTH_SHORT).show();
        tabLayout = findViewById(R.id.details_tablayout);
        viewPager = findViewById(R.id.details_viewpager);
        pageAdapter = new DetailsPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        pageAdapter.getDada(searchitem);
        toolbar = findViewById(R.id.details_toolbar);
        viewPager.setAdapter(pageAdapter);

        initUpper();
        setToolbar();

        setTab();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        MenuItem item = menu.findItem(R.id.ic_fav);

        //init ic_fav
        if(InternalStorage.contains(this, searchitem)) {
            item.setIcon(R.drawable.heart_fill_red);
        } else {
            item.setIcon(R.drawable.heart_outline_white);
        }

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (InternalStorage.contains(DetailsActivity.this, DetailsActivity.this.searchitem)) {
                    InternalStorage.removeObject(DetailsActivity.this, DetailsActivity.this.searchitem);
                    item.setIcon(R.drawable.heart_outline_white);

                } else {
                    InternalStorage.addObject(DetailsActivity.this, DetailsActivity.this.searchitem);
                    item.setIcon(R.drawable.heart_fill_red);
                }
                return false;//?
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void setToolbar(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(searchitem.getName());


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.ic_fav){
            togglefav();
            return true;
        } else if (item.getItemId() == R.id.ic_twitter){
            Intent intent = new Intent(Intent.ACTION_VIEW, getTwitterUri());
            startActivity(intent);
            return true;
        }
        return false;
    }

    private void togglefav(){
        if (InternalStorage.contains(this, searchitem)) {

        } else {

        }
    }

//    https://twitter.com/intent/tweet?text=Check%20out%20Los%20Angeles%20Lakers%20vs.%20Utah%20Jazz%20located%20at%20STAPLES%20Center.%20Website%3A%20https%3A%2F%2Fwww.ticketmaster.com%2Flos-angeles-lakers-vs-utah-jazz-los-angeles-california-11-23-2018%2Fevent%2F2C005508EECC0B21%20%23CSCI571EventSearch
    private Uri getTwitterUri(){
        String url = "https://twitter.com/intent/tweet";
        String content = "Check out "
                + searchitem.getName()
                + " located at "
                + searchitem.getVenue()
                + " Website: "
                + searchitem.getTicketUrl();
        Uri uri = Uri.parse(url);
        return uri.buildUpon()
                .appendQueryParameter("text",content)
                .build();
    }

    //GONE
    private void initUpper(){
        for (int i = 0; i<fillImgs.length;i++) {
            View tab = LayoutInflater.from(this).inflate(R.layout.detail_tab_view, null);
            ImageView tabImage = tab.findViewById(R.id.tab_icon);
            TextView tabText = tab.findViewById(R.id.tab_label);
            tabText.setText(fillLabels[i]);
            tabImage.setImageResource(fillImgs[i]);
            tab.setMinimumWidth(255);
            tabLayout.getTabAt(i).setCustomView(tab);
        }
    }

    private void setTab(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                setColor(tab);

                if (tab.getPosition() == 0) {
//                    setColor(tab);
//                    Toast.makeText(DetailsActivity.this, "position1", Toast.LENGTH_SHORT).show();
                } else if(tab.getPosition() == 1) {
//                    setColor(tab);
//                    Toast.makeText(DetailsActivity.this, "position2", Toast.LENGTH_SHORT).show();
                } else if(tab.getPosition()==2) {
//                    setColor(tab);
//                    Toast.makeText(DetailsActivity.this, "position3", Toast.LENGTH_SHORT).show();
                } else if (tab.getPosition() == 3) {
//                    setColor(tab);
//                    Toast.makeText(DetailsActivity.this, "position4", Toast.LENGTH_SHORT).show();
                }else {
//                    Toast.makeText(DetailsActivity.this, "position unknown", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        setColor(tabLayout.getTabAt(0));
    }

    private void setColor(TabLayout.Tab tab) {
//        TextView tabTextView = ((TextView)tab.getCustomView().findViewById(R.id.tab_label));
        for (int i =0;i<4;i++) {
            if (tab.getPosition() == i) {
                ((TextView)tabLayout.getTabAt(i).getCustomView().findViewById(R.id.tab_label)).setTextColor(getResources().getColor(R.color.textWhite, null));
            }else {
                ((TextView)tabLayout.getTabAt(i).getCustomView().findViewById(R.id.tab_label)).setTextColor(getResources().getColor(R.color.textDim, null));
            }

        }
    }


}
