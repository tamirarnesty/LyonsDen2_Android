package com.wlmac.lyonsden2_android;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wlmac.lyonsden2_android.contactActivities.AnnouncementActivity;
import com.wlmac.lyonsden2_android.contactActivities.MusicActivity;
import com.wlmac.lyonsden2_android.lyonsLists.ListViewerActivity;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;
import com.wlmac.lyonsden2_android.otherClasses.ToastView;

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
    private RelativeLayout extraButtonsContainer;
    private Button extraButtonsToggle;
    private boolean isShowingExtraButtons = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_activity);
        extraButtonsContainer = (RelativeLayout) findViewById(R.id.CSButtonContainer);
        extraButtonsToggle = (Button) findViewById(R.id.CSToggleButton);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        if (getIntent().getBooleanExtra("afterProposal", false)) {
            Toast.makeText(this, "Submitted!", Toast.LENGTH_SHORT).show();
        }

        rootLayout = (DrawerLayout) findViewById(R.id.ConDLayout);
        drawerList = (ListView) findViewById(R.id.ConDList);
        drawerToggle = HomeActivity.initializeDrawerToggle(this, rootLayout);
        HomeActivity.setupDrawer(this, drawerList, rootLayout, drawerToggle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int textHeight = Retrieve.heightForText("Report a Bug", this, 12) + 16; // Accounts for padding
        extraButtonsContainer.animate().translationYBy(textHeight).setDuration(0).start();
        extraButtonsToggle.animate().translationYBy(textHeight).setDuration(0).start();
        extraButtonsToggle.setBackgroundResource(R.drawable.arrow_up_48dp);
        isShowingExtraButtons = true;
    }

    /** Called when the Propose Announcement button is pressed. */
    public void proposeAnnouncement (View view) {
        // Segue into Announcement Proposal Activity
        Intent intent = new Intent (this, AnnouncementActivity.class);
        intent.putExtra("clubKey", "no-key");
        startActivity(intent);
    }

    /** Called when the Propose sont for Radio button is pressed. */
    public void proposeRadio (View view) {
        Intent intent = new Intent(this, MusicActivity.class);
        startActivity(intent);
    }

    /** Called when Contact a Teacher button is pressed. */
    public void requestTeacherList (View view) {
        // Segue into a teacher list.
        Intent intent = new Intent(this, ListViewerActivity.class);
        intent.putExtra("title", "Teachers");
        startActivity(intent);
    }

    /** Called when Emergency Hotline button is pressed. */
    public void emergency (View view) {
        // TODO: Implement emergency hotline
    }

    public void toggleButtons (View view) {
        int textHeight = Retrieve.heightForText("Report a Bug", this, 12) + 16; // Accounts for padding
        textHeight = (isShowingExtraButtons) ? textHeight * -1 : textHeight;
        extraButtonsContainer.animate().translationYBy(textHeight).setDuration(300).start();
        extraButtonsToggle.animate().translationYBy(textHeight).setDuration(300).start();
            extraButtonsToggle.setBackgroundResource((isShowingExtraButtons) ? R.drawable.arrow_down_48dp : R.drawable.arrow_up_48dp);
        isShowingExtraButtons = !isShowingExtraButtons;
    }

    public void reportBug (View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, "TheLyonsKeeper@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Hey Keeper, I found a bug!");
        intent.putExtra(Intent.EXTRA_TEXT, "Before the bug occurred I did this:");
        try {
            startActivity(Intent.createChooser(intent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void displayList (View view) {
        if ((view.getTag()).equals("1")) {   // If Licences
            Log.d("Contact", "Lets pretend I displayed the Licences");
            Intent intent = new Intent(this, ListViewerActivity.class);
            intent.putExtra("title", "Licences");
            startActivity(intent);
        } else {        // If Help
            Log.d("Contact", "Lets pretend I displayed the Help Files");
            Intent intent = new Intent(this, ListViewerActivity.class);
            intent.putExtra("title", "Help");
            startActivity(intent);
        }
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
