package com.wlmac.lyonsden2_android;

import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.wlmac.lyonsden2_android.otherClasses.ListAdapter;

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
    public static ArrayList<String>[] content = new ArrayList[]{new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>()};
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
        // Set the click listener of this activity's list
        this.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: Do something here, (onItemClickListener)
                // TODO: TEMPORARY OnItemClickListener
                Toast.makeText(ListActivity.this, "BAH BAH", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (showingClubs)
        this.setTitle("Clubs");
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
