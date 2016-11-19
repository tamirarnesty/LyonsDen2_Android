package com.wlmac.lyonsden2_android.lyonsLists;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.google.firebase.database.FirebaseDatabase;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;
import com.wlmac.lyonsden2_android.resourceActivities.InfoActivity;

/**
 * Created by sketch204 on 2016-10-16.
 */

public class EventList extends LyonsList {
    private int curExpandedCellIndex = -1;
    private ExpandableListAdapter adapter;
    private String[][] content = new String[0][0];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("EventList", "Commencing Loading Label Cycling");
        loadingLabel.startCycling();

        Log.d("EventList", "Initializing List");
        ((ListView) findViewById(R.id.LSClubsList)).setVisibility(View.GONE);
        /* An instance of the eventList of this activity. */
        ExpandableListView eventList = (ExpandableListView) findViewById(R.id.LSEventsList);

        Log.d("Event Parser", "Initiating Parse!");
        adapter = new ExpandableListAdapter(this, content);
        eventList.setAdapter(adapter);

        Retrieve.eventData(FirebaseDatabase.getInstance().getReference("announcements"), new Retrieve.EventDataHandler() {
            @Override
            public void handle(String[][] eventData) {
                onEventsLoaded(eventData);
            }
        });
//        parseForEvents(adapter, content, FirebaseDatabase.getInstance().getReference("announcements"), loadingLabel, loadingCircle);

        eventList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                parent.collapseGroup(groupPosition);
                return true;
            }
        });

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

    private void onEventsLoaded(String[][] eventData) {
        this.content = eventData;
        adapter.updateDataSet(content);
        loadingLabel.dismiss();
        loadingCircle.setVisibility(View.GONE);
    }

    public void openAnnouncement (View view) {
        Log.d("EventList", "Opening Announcement");
        // Event keys: title, info, date, location

        Intent intent = new Intent(this, InfoActivity.class);
        intent.putExtra("title", content[curExpandedCellIndex][0]);
        intent.putExtra("info", content[curExpandedCellIndex][1]);
        intent.putExtra("date", content[curExpandedCellIndex][2]);
        intent.putExtra("location", content[curExpandedCellIndex][3]);

        InfoActivity.image = null;

        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.getSupportActionBar().setTitle(getResources().getString(R.string.LSTitleEvents));
    }
}