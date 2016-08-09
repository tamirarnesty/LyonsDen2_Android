package com.wlmac.lyonsden2_android;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.wlmac.lyonsden2_android.contactActivities.AnnouncementActivity;

/**
 * The activity used to display the methods for contacting the school.
 *
 * @author sketch204
 * @version 1, 2016/08/06
 */
public class ContactActivity extends AppCompatActivity {
    /** An instance of the root layout of this activity. */
    private DrawerLayout rootLayout;
    /** An instance of the ListView used in this activity's navigation drawer. */
    private ListView drawerList;
    /** The drawer toggler used this activity. */
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_activity);
        rootLayout = (DrawerLayout) findViewById(R.id.ConDLayout);
        drawerList = (ListView) findViewById(R.id.ConDList);
        drawerToggle = HomeActivity.initializeDrawerToggle(this, rootLayout);
        HomeActivity.setupDrawer(this, drawerList, rootLayout, drawerToggle);
    }

    /** Called when the Propose Announcement button is pressed. */
    public void proposeAnnouncement (View view) {
        // Segue into Announcement Proposal Activity
        Intent intent = new Intent (this, AnnouncementActivity.class);
        startActivity(intent);
    }

    /** Called when the Propose sont for Radio button is pressed. */
    public void proposeRadio (View view) {
        // TODO: Create a song proposal activity
    }

    /** Called when Contact a Teacher button is pressed. */
    public void requestTeacherList (View view) {
        // TODO: Implement a teacher list

        // TODO: TEMPORARY TEACHER LIST FILL
        for (int j = 0; j < 50; j ++) {
            ListActivity.content[0].add("I'm a title");
            ListActivity.content[1].add("I'm a description");
            ListActivity.content[2].add("I'm a time");
            ListActivity.content[3].add("I'm a locaiton");
        }
        // Segue into a teacher list.
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    /** Called when Emergency Hotline button is pressed. */
    public void emergency (View view) {
        // TODO: Implement the emergency hotline
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
