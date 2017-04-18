package com.wlmac.lyonsden2_android.lyonsLists;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;
import com.wlmac.lyonsden2_android.resourceActivities.ClubActivity;

import java.util.ArrayList;

/**
 * Created by sketch204 on 2016-10-16.
 */

public class ClubList extends LyonsList {
    public static boolean contentChanged = false;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingLabel.startCycling();
        listView = (ListView) findViewById(R.id.LSClubList);
        refreshLayout = ((SwipeRefreshLayout) findViewById(R.id.LSClubRefresh));
        (findViewById(R.id.LSEventRefresh)).setVisibility(View.GONE);
        (findViewById(R.id.LSEventRefresh)).setEnabled(false);

        adapter = new ListAdapter(this, content, false, false);
        listView.setAdapter(adapter);
        loadClubs();

        // Set the click listener of this activity's eventList
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Clubs keys: title, info, leader, clubKey

                Intent intent = new Intent(parent.getContext(), ClubActivity.class);
                intent.putExtra("title", content.get(position)[0]);
                intent.putExtra("info", content.get(position)[1]);
                intent.putExtra("leaders", content.get(position)[2]);
                intent.putExtra("clubKey", content.get(position)[3]);

                ClubActivity.image = null;
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadClubs();
            }
        });
        refreshLayout.setColorSchemeResources(R.color.navigationBar);
    }

    private void loadClubs () {
        if (Retrieve.isInternetAvailable(this)) {
            Retrieve.clubData(this, FirebaseDatabase.getInstance().getReference("clubs"), content, new Retrieve.ListDataHandler() {
                @Override
                public void handle(ArrayList<String[]> listData) {
                    onClubsLoaded(listData);
                }
            });
        } else {
            Toast.makeText(this, "No Internet Available!", Toast.LENGTH_SHORT).show();
        }
    }

    private void onClubsLoaded (ArrayList<String[]> clubData) {
        adapter.notifyDataSetChanged();
        loadingLabel.dismiss();
        loadingCircle.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.getSupportActionBar().setTitle(getResources().getString(R.string.LSTitleClubs));
        if (contentChanged) {
            if (Retrieve.isInternetAvailable(this)) {
                Retrieve.clubData(this, FirebaseDatabase.getInstance().getReference("clubs"), content, new Retrieve.ListDataHandler() {
                    @Override
                    public void handle(ArrayList<String[]> listData) {
                        onClubsLoaded(listData);
                    }
                });
            } else {
                Toast.makeText(this, "No Internet Available!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
