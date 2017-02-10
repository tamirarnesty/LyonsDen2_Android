package com.wlmac.lyonsden2_android.resourceActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;

// TODO: FIX TIME FORMATTING

/**
 * This activity is used for viewing an announcement or an event. The data that should be passed to
 * this activity is: a title with key 'title', description with key 'info', a date&time with key 'date'
 * and a location with key 'location'. The image that would be displayed in this activity must have a
 * Drawable object assigned to the static member <i>image</i>. If image is null (default value) then
 * the components will be resized and repositioned appropriately.
 *
 * @author sketch204
 * @version 1, 2016/08/05
 */
public class InfoActivity extends AppCompatActivity {
    private String creatorName;
    private String creatorID;

    private TextView locationLabel;

    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        Intent intent = getIntent();

        String[] list = new String[4];
        if (intent.getStringExtra("tag").equals("announcement")) {
            list = intent.getStringArrayExtra("announcement");
        } else if (intent.getStringExtra("tag").equals("club")) {
            list = intent.getStringArrayExtra("club");
        }

        FirebaseDatabase.getInstance().getReference("users/students").child(list[4]).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onRetrieveName(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String location = list[3];
        creatorID = list[4];
        locationLabel = (TextView) findViewById(R.id.ISLocationLabel);
        Retrieve.isUserTeacher(this, key, new Retrieve.StatusHandler() {
            @Override
            public void handle(boolean status) {
                if (status) {
                    // An instance of the location TextView of this activity
                    locationLabel.setText(creatorName);
                }
                else {
                    if (key.equals(creatorID)) {
                        locationLabel.setText(creatorName);
                    }
                    locationLabel.setText(location);
                }
            }
        });

        // An instance of the title TextView of this activity
        TextView titleLabel = (TextView) findViewById(R.id.ISTitleLabel);
        titleLabel.setText(list[0]);

        // An instance of the description TextView of this activity
        TextView infoLabel = (TextView) findViewById(R.id.ISDescriptionLabel);
        infoLabel.setText(list[1]);

        // An instance of the date&time TextView of this activity
        TextView dateLabel = (TextView) findViewById(R.id.ISDateLabel);
        String dateTime = list[2];
        dateLabel.setText(dateTime);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (true) {
            getMenuInflater().inflate(R.menu.edit_menu, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editAction) {
            enterEditMode();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void enterEditMode() {
        Toast.makeText(this, "Some test", Toast.LENGTH_SHORT).show();
    }

    private void onRetrieveName(String creatorName) {
        this.creatorName = creatorName;
    }
}
