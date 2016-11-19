package com.wlmac.lyonsden2_android.lyonsLists;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.google.firebase.database.FirebaseDatabase;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;
import com.wlmac.lyonsden2_android.resourceActivities.ClubActivity;

import java.util.ArrayList;

/**
 * Created by sketch204 on 2016-10-16.
 */

public class ClubList extends LyonsList {
//    public static boolean contentChanged = false;
    ListView clubList;
    ListAdapter adapter;
    private ArrayList<String[]> content = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingLabel.startCycling();

        ((ExpandableListView) findViewById(R.id.LSEventsList)).setVisibility(View.GONE);
        clubList = (ListView) findViewById(R.id.LSClubsList);

        clubList = (ListView) findViewById(R.id.LSClubsList);
        adapter = new ListAdapter(this, content, true);
        clubList.setAdapter(adapter);

        Retrieve.clubData(FirebaseDatabase.getInstance().getReference("clubs"), content, new Retrieve.ClubDataHandler() {
            @Override
            public void handle(ArrayList<String[]> clubData) {
                onClubsLoaded(clubData);
            }
        });

        // Set the click listener of this activity's eventList
        clubList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            }
        });
        Log.d("List Activity", "We have been created now!");
    }

    private void onClubsLoaded (ArrayList<String[]> clubData) {
//        adapter.updateDataSet(content);
        adapter.notifyDataSetChanged();
        loadingLabel.dismiss();
        loadingCircle.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.getSupportActionBar().setTitle(getResources().getString(R.string.LSTitleClubs));
    }
}