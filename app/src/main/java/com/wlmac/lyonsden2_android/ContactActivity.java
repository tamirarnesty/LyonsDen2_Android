package com.wlmac.lyonsden2_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.wlmac.lyonsden2_android.contactActivities.AnnouncementActivity;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_activity);
    }

    // Called when the Propose Announcement button is pressed
    public void proposeAnnouncement (View view) {
        // Segue into Announcement Proposal Activity
        Intent intent = new Intent (this, AnnouncementActivity.class);
        startActivity(intent);
    }

    // Called when the Propose sont for Radio button is pressed
    public void proposeRadio (View view) {
        // TODO: Create a song proposal activity
    }

    // Called when Contact a Teacher button is pressed
    public void requestTeacherList (View view) {
        // TODO: Implement a teacher list

        for (int j = 0; j < 50; j ++) {
            ListActivity.content[0].add("I'm a title");
            ListActivity.content[1].add("I'm a description");
            ListActivity.content[2].add("I'm a time");
            ListActivity.content[3].add("I'm a locaiton");
        }
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    // Called when Emergency Hotline button is pressed
    public void emergency (View view) {
        // TODO: Implement the emergency hotline
    }
}
