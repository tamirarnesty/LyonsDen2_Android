package com.wlmac.lyonsden2_android;

import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

/**
 * The activity to that will display the calendar and its events.
 *
 * @author sketch204
 * @version 1, 2016/08/08
 */
public class CalendarActivity extends AppCompatActivity {
    /** An instance of the root layout of this activity. */
    private DrawerLayout rootLayout;
    /** An instance of the ListView used in this activity's navigation drawer. */
    private ListView drawerList;
    /** The drawer toggler used this activity. */
    private ActionBarDrawerToggle drawerToggle;

    // TODO: Calendar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);
        rootLayout = (DrawerLayout) findViewById(R.id.CalDLayout);
        drawerList = (ListView) findViewById(R.id.CalDList);
        drawerToggle = HomeActivity.initializeDrawerToggle(this, rootLayout);
        HomeActivity.setupDrawer(this, drawerList, rootLayout, drawerToggle);
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
