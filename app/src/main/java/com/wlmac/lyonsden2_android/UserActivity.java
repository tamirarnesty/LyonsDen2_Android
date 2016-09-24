package com.wlmac.lyonsden2_android;

import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.wlmac.lyonsden2_android.otherClasses.LyonsAlert;

// TODO: IMPLEMENT CLUB CODE CHECKER
// TODO: MAKE A LOADING INDICATOR FOR WHEN CHECKING THE CLUB CODE
// TODO: MAKE KEYBOARD GO AWAY WHEN !editing

public class UserActivity extends AppCompatActivity {
    /** An instance of the root layout of this activity. */
    private DrawerLayout rootLayout;
    /** An instance of the ListView used in this activity's navigation drawer. */
    private ListView drawerList;
    /** The drawer toggler used this activity. */
    private ActionBarDrawerToggle drawerToggle;
    private EditText displayName;
    private EditText email;
    private EditText accessLevel;

    private boolean editing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
        instantiateComponents();

        // Drawer setup
        rootLayout = (DrawerLayout) findViewById(R.id.LDLayout);
        drawerList = (ListView) findViewById(R.id.LDList);
        drawerToggle = HomeActivity.initializeDrawerToggle(this, rootLayout);
        HomeActivity.setupDrawer(this, drawerList, rootLayout, drawerToggle);
    }

    private void instantiateComponents () {
        displayName = (EditText) findViewById(R.id.USNameField);
        email = (EditText) findViewById(R.id.USEmailField);
        accessLevel = (EditText) findViewById(R.id.USAccessField);
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

    public void editAccount (View view) {
        if (view.getId() == R.id.USUpdate) {
            Log.d("User Activity", "Let's pretend that i'm actually updating your info");
        } else if (view.getId() == R.id.USDelete) {
            Log.d("User Activity", "Let's pretend that i'm actually deleting your account");
        } else if (view.getId() == R.id.USSignOut) {
            Log.d("User Activity", "Let's pretend that i'm actually signing you out");
        } else {
            Log.d("User Activity", "Let's pretend that i'm actually resetting your password");
        }
    }

    private void enterEditMode () {
        editing = !editing;

        displayName.setCursorVisible(editing);
        displayName.setFocusableInTouchMode(editing);
        displayName.setFocusable(editing);
        displayName.setEnabled(editing);
        displayName.setClickable(editing);
        displayName.setInputType((editing) ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL);
        displayName.requestFocus();

        email.setCursorVisible(editing);
        email.setFocusableInTouchMode(editing);
        email.setFocusable(editing);
        email.setEnabled(editing);
        email.setClickable(editing);
        email.setInputType((editing) ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL);

        // TODO: MAKE KEYBOARD GO AWAY WHEN !editing
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editAction)
            enterEditMode();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }
}


