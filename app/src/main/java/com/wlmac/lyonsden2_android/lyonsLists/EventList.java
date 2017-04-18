package com.wlmac.lyonsden2_android.lyonsLists;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;
import com.wlmac.lyonsden2_android.resourceActivities.InfoActivity;

import java.util.ArrayList;

/**
 * Created by sketch204 on 2016-10-16.
 */

public class EventList extends LyonsList {
    public static boolean didUpdateDataset = false;
    protected ActionSlideExpandableListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("EventList", "Commencing Loading Label Cycling");
        loadingLabel.startCycling();
        listView = (ActionSlideExpandableListView) findViewById(R.id.LSEventList);
        refreshLayout = ((SwipeRefreshLayout) findViewById(R.id.LSEventRefresh));
        (findViewById(R.id.LSClubRefresh)).setVisibility(View.GONE);
        (findViewById(R.id.LSClubRefresh)).setEnabled(false);

        adapter = new ListAdapter(this, content, true, true);

        listView.setItemActionListener(new ActionSlideExpandableListView.OnActionClickListener() {
            @Override
            public void onClick(View itemView, View clickedView, int position) {
                Log.d("EventList", "I hear a click nearby!");
                if (clickedView.getId() == R.id.LSIOpenButton) {
                    Log.d("EventList", "Opening Announcement");
                    // Event keys: title, info, date, location

                    Intent intent = new Intent(EventList.this, InfoActivity.class);
                    String[] line = new String[5];
                    line[0] = content.get(position)[0];
                    line[1] = content.get(position)[1];
                    line[2] = content.get(position)[2];
                    line[3] = content.get(position)[3];
                    line[4] = content.get(position)[4];
                    intent.putExtra("tag", "announcement");
                    intent.putExtra("initiator", "events");
                    intent.putExtra("announcement", line);

                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        }, R.id.LSIOpenButton);

        listView.setAdapter(adapter, R.id.LSICell, R.id.LSIExpandable);
        loadAnnouncements();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadAnnouncements();
            }
        });
        refreshLayout.setColorSchemeResources(R.color.navigationBar);

        Log.d("List Activity", "We have been created now!");
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.getSupportActionBar().setTitle(getResources().getString(R.string.LSTitleEvents));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (didUpdateDataset) {
            loadAnnouncements();
            didUpdateDataset = false;
        }
    }

    private void loadAnnouncements () {
        if (Retrieve.isInternetAvailable(this)) {
            Retrieve.eventData(this, FirebaseDatabase.getInstance().getReference("announcements"), content, new Retrieve.ListDataHandler() {
                @Override
                public void handle(ArrayList<String[]> listData) {
                    onEventsLoaded();
                }
            });
        } else {
            Toast.makeText(this, "No Internet Available!", Toast.LENGTH_SHORT).show();
        }
    }

    private void onEventsLoaded() {
        adapter.notifyDataSetChanged();
        loadingLabel.dismiss();
        loadingCircle.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);
    }
}
