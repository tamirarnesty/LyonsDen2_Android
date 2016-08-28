package com.wlmac.lyonsden2_android;

import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.wlmac.lyonsden2_android.otherClasses.LyonsAlert;

// TODO: IMPLEMENT CLUB CODE CHECKER
// TODO: MAKE A LOADING INDICATOR FOR WHEN CHECKING THE CLUB CODE

public class UserActivity extends AppCompatActivity {
    /** An instance of the root layout of this activity. */
    private DrawerLayout rootLayout;
    /** An instance of the ListView used in this activity's navigation drawer. */
    private ListView drawerList;
    /** The drawer toggler used this activity. */
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        // Drawer setup
        rootLayout = (DrawerLayout) findViewById(R.id.LDLayout);
        drawerList = (ListView) findViewById(R.id.LDList);
        drawerToggle = HomeActivity.initializeDrawerToggle(this, rootLayout);
        HomeActivity.setupDrawer(this, drawerList, rootLayout, drawerToggle);


    }

    private void checkClubCode (String code) {

    }

    public void clubLeadershipApplication (View view) {
        final LyonsAlert alertDialog = new LyonsAlert();
        alertDialog.setTitle("Club Code");
        alertDialog.setSubtitle("Please enter the club code");
        alertDialog.configureLeftButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.configureRightButton("Submit", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkClubCode(alertDialog.getInputText());
                alertDialog.dismiss();
            }
        });

        alertDialog.show(getSupportFragmentManager(), "ClubCodeDialog");
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


