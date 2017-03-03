package com.wlmac.lyonsden2_android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
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
    private boolean isShowingExtraButtons = true;
    private LinearLayout actionSheet;
    int actionSheetHeight;
    private RelativeLayout contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_activity);
        extraButtonsContainer = (RelativeLayout) findViewById(R.id.CSButtonContainer);
        extraButtonsToggle = (Button) findViewById(R.id.CSToggleButton);
        actionSheet = (LinearLayout) findViewById(R.id.CSActionSheet);
        contentView = (RelativeLayout) findViewById(R.id.CSContentView);
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
        int textHeight = Retrieve.heightForText("Report a Bug", this, 12) + 16; // Accounts for padding
        int transitionHeight = Retrieve.dpFromInt(Retrieve.heightForText("Report a Bug", this, 12) + 16, getResources());
        actionSheetHeight = (int) getResources().getDimension(R.dimen.CSActionSheet) + 60;
        Log.d("ContactActivity", "" + textHeight);
        Log.d("ContactActivity", "" + transitionHeight);
        extraButtonsContainer.animate().translationYBy(textHeight).setDuration(0).start();
        extraButtonsToggle.animate().translationYBy(textHeight).setDuration(0).start();
        actionSheet.animate().translationYBy(actionSheetHeight).setDuration(0).start();

        extraButtonsToggle.setBackgroundResource(R.drawable.arrow_up_48dp);
        isShowingExtraButtons = true;

//        ((DrawerLayout) findViewById(R.id.ConDLayout)).closeDrawer((ListView) findViewById(R.id.ConDList), false);
        ((DrawerLayout) findViewById(R.id.NDLayout)).closeDrawer(Gravity.LEFT);
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
//        Intent intent = new Intent(this, MusicActivity.class);
//        startActivity(intent);
    }

    /** Called when Contact a Teacher button is pressed. */
    public void requestTeacherList(View view) {
        // Segue into a teacher list.
        Intent intent = new Intent(this, ListViewerActivity.class);
        intent.putExtra("title", "Teachers");
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, 1);
            return;
        }
    }

    public void actionSheet(View view) {
        if (view.getTag().toString().equals("3")) { // cancel
            Log.d("ContactActivity", "Cancel Pressed");

        } else if (view.getTag().toString().equals("1")) { // help phone
            Log.d("ContactActivity", "Kids Help Phone Pressed");
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:1-800-668-6868"));
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                checkPermissions();
                return;
            } else {
                startActivity(intent);
            }
        } else {
            if (view.getTag().toString().equals("2")) { // wlmci
                Log.d("ContactActivity", "WLMCI Pressed");
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:416-395-3330" ));
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                    return;
                } else {
                    startActivity(intent);
                }
            }
        }
        actionSheet.animate().translationYBy(actionSheetHeight).setDuration(300).start();
        contentView.animate().alpha(1).setDuration(300).start();
        contentView.setEnabled(true);
        contentView.setClickable(true);
        findViewById(R.id.CSAnnouncementButton).setClickable(true);
        findViewById(R.id.CSTeacherButton).setClickable(true);
        findViewById(R.id.CSRadioButton).setClickable(true);
        findViewById(R.id.CSEmergencyButton).setClickable(true);
    }


    /** Called when Emergency Hotline button is pressed. */
    public void emergency(View view) {
        Toast.makeText(getApplicationContext(), "Calling...", Toast.LENGTH_SHORT).show();
        actionSheet.animate().translationYBy(-actionSheetHeight).setDuration(300).start();
        contentView.animate().alpha(0.5f).setDuration(300).start();
        contentView.setEnabled(false);
        contentView.setClickable(false);
        checkPermissions();
        findViewById(R.id.CSAnnouncementButton).setClickable(false);
        findViewById(R.id.CSTeacherButton).setClickable(false);
        findViewById(R.id.CSRadioButton).setClickable(false);
        findViewById(R.id.CSEmergencyButton).setClickable(false);
    }

    public void toggleButtons (View view) {
//        int transitionHeight = Retrieve.dpFromInt(Retrieve.heightForText("Report a Bug", this, 12) + 16, getResources());
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
