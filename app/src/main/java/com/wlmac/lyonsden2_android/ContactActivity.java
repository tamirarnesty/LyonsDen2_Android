package com.wlmac.lyonsden2_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wlmac.lyonsden2_android.contactActivities.AnnouncementActivity;
import com.wlmac.lyonsden2_android.lyonsLists.ListViewerActivity;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;

/**
 * The activity used to display the methods for contacting the school.
 *
 * @author sketch204
 * @version 1, 2016/08/06
 */
public class ContactActivity extends AppCompatActivity {
    private ActionBarDrawerToggle drawerToggle;
    private RelativeLayout extraButtonsContainer;
    private Button extraButtonsToggle;
    private boolean isShowingExtraButtons = false;
    private boolean postFirstLaunch = false;

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

        /* An instance of the root layout of this activity. */
        DrawerLayout rootLayout = (DrawerLayout) findViewById(R.id.NDLayout);
        /* An instance of the ListView used in this activity's navigation drawer. */
        ListView drawerList = (ListView) findViewById(R.id.NDList);
        drawerToggle = Retrieve.drawerToggle(this, rootLayout);
        Retrieve.drawerSetup(this, drawerList, rootLayout, drawerToggle);

        setFonts();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("ContactActivity", "OnResume is Called!!!");

        float transitionHeight = Retrieve.heightForText("Sign out", this, 12) + (Retrieve.dpFromInt(16, getResources())); // Accounts for padding
        if (!postFirstLaunch) {// || isShowingExtraButtons) {
            postFirstLaunch = true;
            extraButtonsContainer.animate().translationYBy(transitionHeight).setDuration(0).start();
            extraButtonsToggle.animate().translationYBy(transitionHeight).setDuration(0).start();
        }

        extraButtonsToggle.setBackgroundResource(R.drawable.ic_expand_dark_accent_24dp);
        isShowingExtraButtons = false;

        // Programatically Close the Drawer, for when you segue into this view by pressing back
        ((DrawerLayout) findViewById(R.id.NDLayout)).closeDrawer(Gravity.LEFT);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("ContactActivity", "OnPause is Called!!!");

        if (isShowingExtraButtons) {
            toggleButtons(extraButtonsToggle);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void setFonts() {
        int[] components = {R.id.CSTextView, R.id.CSAnnouncementButton, R.id.CSTeacherButton, R.id.CSRadioButton, R.id.CSEmergencyButton, R.id.CSReportButton, R.id.CSHelpButton, R.id.CSLicencesButton};
        for (int h = 0; h < components.length; h++)
            ((TextView) findViewById(components[h])).setTypeface(Retrieve.typeface(this));
    }

    /** Called when the Propose Announcement button is pressed. */
    public void proposeAnnouncement(View view) {
        // Segue into Announcement Proposal Activity
        Intent intent = new Intent(this, AnnouncementActivity.class);
        intent.putExtra("clubKey", "no-key");
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    /** Called when the Propose sont for Radio button is pressed. */
    public void proposeRadio(View view) {
        Toast.makeText(getApplicationContext(), "You're a nosy one aren't you!", Toast.LENGTH_SHORT).show();
    }

    /** Called when Contact a Teacher button is pressed. */
    public void requestTeacherList(View view) {
        // Segue into a teacher list.
        Intent intent = new Intent(this, ListViewerActivity.class);
        intent.putExtra("title", "Teachers");
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, 1);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void dialPhone (String choice) {
        if (choice.equals("WLMCI")) {
            Log.d("ContactActivity", "WLMCI Pressed");
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:416-395-3330" ));
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                checkPermissions();
                return;
            } else {
                startActivity(intent);
            }
        } else if (choice.equals("Kids Help Phone")) {
            Log.d("ContactActivity", "Kids Help Phone Pressed");
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:1-800-668-6868"));
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                checkPermissions();
                return;
            } else {
                startActivity(intent);
            }
        }
    }
    /** Called when Emergency Hotline button is pressed. */
    public void emergency(View view) {
        if (checkPermissions()) {
            final CharSequence [] callOptions = new CharSequence[]{"WLMCI", "Kids Help Phone"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Who would you like to contact?");
            builder.setItems(callOptions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialPhone((String) callOptions[which]);
                }
            });
            builder.show();
        } else {
            Log.d("Contact Activity", "Something went wrong");
        }
    }

    public void toggleButtons (View view) {
        float transitionHeight = Retrieve.heightForText("Sign out", this, 12) + (Retrieve.dpFromInt(16, getResources())); // Accounts for padding
        transitionHeight = (!isShowingExtraButtons) ? transitionHeight * -1 : transitionHeight;
        extraButtonsContainer.animate().translationYBy(transitionHeight).setDuration(300).start();
        extraButtonsToggle.animate().translationYBy(transitionHeight).setDuration(300).start();
        extraButtonsToggle.setBackgroundResource((!isShowingExtraButtons) ? R.drawable.ic_collapse_dark_accent_24dp : R.drawable.ic_expand_dark_accent_24dp);
        isShowingExtraButtons = !isShowingExtraButtons;
    }

    public void reportBug (View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"TheLyonsKeeper@gmail.com"});
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
