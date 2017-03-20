package com.wlmac.lyonsden2_android.lyonsLists;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.wlmac.lyonsden2_android.HomeActivity;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;
import com.wlmac.lyonsden2_android.resourceActivities.InfoActivity;

import java.util.ArrayList;

/**
 * Created by sketch204 on 2016-10-16.
 */

public class EventList extends LyonsList {
    private int curExpandedCellIndex = -1;
    private ExpandableListAdapter adapter;
    public static boolean didUpdateDataset = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("EventList", "Commencing Loading Label Cycling");
        loadingLabel.startCycling();

        Log.d("EventList", "Initializing List");
        findViewById(R.id.LSClubsList).setVisibility(View.GONE);
        /* An instance of the eventList of this activity. */
        ExpandableListView eventList = (ExpandableListView) findViewById(R.id.LSEventsList);

        Log.d("Event Parser", "Initiating Parse!");
        adapter = new ExpandableListAdapter(this, content);
        eventList.setAdapter(adapter);

        loadAnnouncements();

        eventList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                parent.collapseGroup(curExpandedCellIndex);

                if (curExpandedCellIndex != groupPosition) {
                    parent.expandGroup(groupPosition, true);
                    curExpandedCellIndex = groupPosition;
                } else
                    curExpandedCellIndex = -1;
                return true;
            }
        });

        Log.d("List Activity", "We have been created now!");
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
    }

    public void openAnnouncement (View view) {
        Log.d("EventList", "Opening Announcement");
        // Event keys: title, info, date, location

        Intent intent = new Intent(this, InfoActivity.class);
        String[] line = new String[5];
        line[0] = content.get(curExpandedCellIndex)[0];
        line[1] = content.get(curExpandedCellIndex)[1];
        line[2] = content.get(curExpandedCellIndex)[2];
        line[3] = content.get(curExpandedCellIndex)[3];
        line[4] = content.get(curExpandedCellIndex)[4];
        intent.putExtra("tag", "announcement");
        intent.putExtra("initiator", "events");
        intent.putExtra("announcement", line);

        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.getSupportActionBar().setTitle(getResources().getString(R.string.LSTitleEvents));
    }
}
