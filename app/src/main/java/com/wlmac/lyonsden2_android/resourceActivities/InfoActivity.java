package com.wlmac.lyonsden2_android.resourceActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wlmac.lyonsden2_android.HomeActivity;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.lyonsLists.EventList;
import com.wlmac.lyonsden2_android.otherClasses.LyonsAlert;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;

/**
 * This activity is used for viewing an announcement or an event. The data that should be passed to
 * this activity is: a title with key 'title', description with key 'info', a date&time with key 'date'
 * and a location with key 'location'. The image that would be displayed in this activity must have a
 * Drawable object assigned to the static member <i>image</i>. If image is null (default value) then
 * the components will be resized and repositioned appropriately.
 *
 * @author Ademir Gotov
 * @version 1, 2016/08/05
 */
public class InfoActivity extends AppCompatActivity {
    private boolean isEditAvailable;
    private boolean student;
    private String[] item;
    private Intent intent;
    private TextView titleLabel;
    private TextView infoLabel;
    private TextView dateLabel;
    private TextView locationLabel;
    private boolean editing;

    private String[] tempData = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        this.intent = getIntent();
        editCheck();
        initialize();
        setFonts();
    }

    private void initialize() {
        this.titleLabel = (TextView) findViewById(R.id.ISTitleLabel);
        this.infoLabel = (TextView) findViewById(R.id.ISDescriptionLabel);
        this.dateLabel = (TextView) findViewById(R.id.ISDateLabel);
        this.locationLabel = (TextView) findViewById(R.id.ISLocationLabel);

        if (intent.getStringExtra("tag").equals("announcement"))
            this.item = intent.getStringArrayExtra("announcement");
        else if (intent.getStringExtra("tag").equals("club"))
            this.item = intent.getStringArrayExtra("club");

        FirebaseDatabase.getInstance().getReference("users/students/" + item[4] + "/name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onRetrieveName(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(InfoActivity.this, "Another Error Occured!\n" + getResources().getInteger(R.integer.DatabaseOperationCancelled), Toast.LENGTH_LONG).show();
                Log.d("Event Retriever", "Request Cancelled!");
            }
        });

        titleLabel.setText(item[0]);
        infoLabel.setText(item[1]);
        dateLabel.setText(item[2]);
        locationLabel.setText(item[3]);
    }

    private void editCheck() {
        final String key = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Retrieve.isUserTeacher(InfoActivity.this, key, new Retrieve.StatusHandler() {
            @Override
            public void handle(boolean status) {
                isEditAvailable = status;
                student = isEditAvailable = key.equals(item[4]);
                invalidateOptionsMenu();
            }
        });
    }

    private void setFonts() {
        titleLabel.setTypeface(Retrieve.typeface(this));
        infoLabel.setTypeface(Retrieve.typeface(this));
        dateLabel.setTypeface(Retrieve.typeface(this));
        locationLabel.setTypeface(Retrieve.typeface(this));
    }

    private void toggleEditMode() {
        editing = !editing;

        int bgRes = (editing) ? R.drawable.text_view_edit : R.drawable.text_view_default;
        int color = (editing) ? R.color.whiteText : R.color.accent;

        titleLabel.setBackgroundResource(bgRes);
        titleLabel.setTextColor(getResources().getColor(color));
        titleLabel.setFocusable(editing);
        titleLabel.setClickable(editing);
        titleLabel.setFocusableInTouchMode(editing);
        titleLabel.setEnabled(editing);

        infoLabel.setBackgroundResource(bgRes);
        infoLabel.setTextColor(getResources().getColor(color));
        infoLabel.setFocusable(editing);
        infoLabel.setClickable(editing);
        infoLabel.setFocusableInTouchMode(editing);
        infoLabel.setEnabled(editing);

        if (editing) {
            tempData[0] = item[0];
            tempData[1] = item[1];
        }

        invalidateOptionsMenu();
    }

    private void updateChanges() {
        if (Retrieve.isInternetAvailable(this)) {
            DatabaseReference dataBase = FirebaseDatabase.getInstance().getReference("announcements/" + item[5]);
            dataBase.child("title").setValue(titleLabel.getText().toString());
            dataBase.child("description").setValue(infoLabel.getText().toString());

            item[0] = titleLabel.getText().toString();
            item[1] = infoLabel.getText().toString();

            notifyInitiator();

            Toast.makeText(InfoActivity.this, "Information Updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(InfoActivity.this, "No Internet Available!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This will notify the Initiating Activity about the changes that have been made and will cause it to
     * update its data.
     */
    private void notifyInitiator () {
        String initiator = getIntent().getStringExtra("initiator");
        if (initiator.equals("home")) {
            HomeActivity.didUpdateDataset = true;
        } else if (initiator.equals("events")) {
            EventList.didUpdateDataset = true;
        }
    }

    private void onRetrieveName(String creatorName) {
        if (item[3].isEmpty() && !student && isEditAvailable)
            locationLabel.setText(creatorName);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (editing) {
            getMenuInflater().inflate(R.menu.done_menu, menu);
        } else {
            if (isEditAvailable) {
                getMenuInflater().inflate(R.menu.edit_menu, menu);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editAction) {
            toggleEditMode();
            return true;
        } else if (item.getItemId() == R.id.doneAction) {
            // If nothing was changed
            if (titleLabel.getText().toString().equals(tempData[0]) && infoLabel.getText().toString().equals(tempData[1])) {
                toggleEditMode();
            } else if (student) {   // If cur user is student
                final LyonsAlert alert = new LyonsAlert();
                alert.setTitle("Teacher Approval");
                alert.setSubtitle("Please ask a teacher to approve your changes!");
                alert.makeInputSecure();
                alert.configureLeftButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        titleLabel.setText(tempData[0]);
                        infoLabel.setText(tempData[1]);
                        toggleEditMode();
                        alert.dismiss();
                    }
                });
                alert.configureRightButton("Approve", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String input = alert.getInputText();
                        Retrieve.teacherApproval(InfoActivity.this, input, new Retrieve.StatusHandler() {
                            @Override
                            public void handle(boolean status) {
                                if (status) {
                                    alert.dismiss();
                                    toggleEditMode();
                                    updateChanges();
                                } else
                                    Toast.makeText(InfoActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                alert.show(getSupportFragmentManager(), "TeacherApprovalDialog");
            } else {    // Otherwise he must be a teacher
                toggleEditMode();
                updateChanges();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
