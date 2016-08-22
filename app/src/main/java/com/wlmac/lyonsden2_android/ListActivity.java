package com.wlmac.lyonsden2_android;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wlmac.lyonsden2_android.otherClasses.ListAdapter;
import com.wlmac.lyonsden2_android.resourceActivities.ClubActivity;
import com.wlmac.lyonsden2_android.resourceActivities.InfoActivity;

import java.util.ArrayList;

/**
 * The activity that will be used to download and display the list of events or clubs.
 *
 * @author sketch204
 * @version 1, 2016/08/05
 */
public class ListActivity extends AppCompatActivity {
    public static boolean showingClubs = false;
    /** An ArrayList of ArrayLists each of which represents a data field for each of the list's cells. */
    private ArrayList<String>[] content = new ArrayList[]{new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>()};
    /** An instance of the list of this activity. */
    private ListView list;
    /** An instance of the root layout of this activity. */
    private DrawerLayout rootLayout;
    /** An instance of the ListView used in this activity's navigation drawer. */
    private ListView drawerList;
    /** The drawer toggler used this activity. */
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        rootLayout = (DrawerLayout) findViewById(R.id.LDLayout);
        drawerList = (ListView) findViewById(R.id.LDList);
        drawerToggle = HomeActivity.initializeDrawerToggle(this, rootLayout);
        HomeActivity.setupDrawer(this, drawerList, rootLayout, drawerToggle);

        // Initialize the list
        list = (ListView) findViewById(R.id.LSClubsEventsList);
        // Create and set the adapter for this activity's list
        ListAdapter adapter = new ListAdapter (this, content[0], content[1], null, true);
        list.setAdapter(adapter);

        if (showingClubs)
            parseForClubs(adapter);
        else
            parseForEvents(adapter, content, FirebaseDatabase.getInstance().getReference("events"));

        // Set the click listener of this activity's list
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Event keys: title, info, date, location
                // Clubs keys: title, info, leader, (pass link to members)

                Intent intent = new Intent(parent.getContext(), (showingClubs) ? ClubActivity.class : InfoActivity.class);
                intent.putExtra("title", content[0].get(position));
                intent.putExtra("info", content[1].get(position));
                if (showingClubs) {
                    intent.putExtra("leaders", content[2].get(position));
                    Bundle args = new Bundle();
//                    args.put

                } else {
                    intent.putExtra("date", content[2].get(position));
                    intent.putExtra("location", content[3].get(position));
                }

                if (showingClubs)
                    ClubActivity.image = null;
                else
                    InfoActivity.image = null;

                startActivity(intent);
            }
        });
    }

    // TODO: FIGURE OUT ERROR HANDLING. LIKE NO INTERNET

    public static void parseForEvents(final ListAdapter adapter, final ArrayList<String>[] content, DatabaseReference ref) {
        final String[] keys = {"title", "description", "dateTime", "location"};

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot event : dataSnapshot.getChildren()) {
                    for (int h = 0; h < keys.length; h ++) {
                        try {
                            content[h].add(event.child(keys[h]).getValue(String.class));
                        } catch (DatabaseException e) {
                            content[h].add(event.child(keys[h]).getValue(Long.class).toString());
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("WebEvents Parser", "Failed to load event list from the web", databaseError.toException());
            }
        });
    }

    private void parseForClubs(final ListAdapter adapter) {
        DatabaseReference webClubList = FirebaseDatabase.getInstance().getReference("clubs");
        final String[] keys = {"title", "description", "leads"};

        webClubList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot club : dataSnapshot.getChildren()) {
                    for (int h = 0; h < keys.length; h ++) {
                        content[h].add(club.child(keys[h]).getValue(String.class));
                    }
                }
                Log.d("Key:", dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("WebEvents Parser", "Failed to load club list from the web", databaseError.toException());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (showingClubs)
            this.setTitle("Clubs");
        else
            this.setTitle("Event");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
}
