package com.wlmac.lyonsden2_android.lyonsLists;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.LoadingLabel;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;

import java.util.ArrayList;

/**
 * Created by sketch204 on 2016-10-16.
 */

public class LyonsList extends AppCompatActivity {
    /** An instance of the root layout of this activity. */
    protected DrawerLayout rootLayout;
    /** The drawer toggler used this activity. */
    protected ActionBarDrawerToggle drawerToggle;
    protected ArrayList<String[]> content = new ArrayList<>();
    protected LoadingLabel loadingLabel;
    protected ProgressBar loadingCircle;

    protected ActionSlideExpandableListView listView;
    protected ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        Log.d("LyonsList", "Initializing Drawer");
        rootLayout = (DrawerLayout) findViewById(R.id.NDLayout);
        drawerToggle = Retrieve.drawerToggle(this, rootLayout);
        Retrieve.drawerSetup(this, (ListView) findViewById(R.id.NDList), rootLayout, drawerToggle);

        listView = (ActionSlideExpandableListView) findViewById(R.id.LSList);

        Log.d("LyonsList", "Initializing Loading Components");
        loadingLabel = new LoadingLabel((TextView) findViewById(R.id.LSLoadingLabel), this);
        loadingCircle = (ProgressBar) findViewById(R.id.LSLoadingWheel);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
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
