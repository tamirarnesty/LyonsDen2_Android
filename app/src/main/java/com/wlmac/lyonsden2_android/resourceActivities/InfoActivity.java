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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.LyonsAlert;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;

// TODO: FIX TIME FORMATTING

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
    private String key;
    private String[] item;
    private Intent intent;
    private EditText titleLabel;
    private EditText infoLabel;
    private EditText dateLabel;
    private EditText locationLabel;
    private String creatorName;
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        this.intent = getIntent();
        initialize();
        editCheck();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (editMode) {
            getMenuInflater().inflate(R.menu.add_menu, menu);
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
            if (student) {
                final LyonsAlert alert = new LyonsAlert();
                alert.setTitle("Teacher Approval");
                alert.setSubtitle("Please ask a teacher to approve your changes!");
                alert.configureLeftButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                                    editTransition();
                                } else
                                    Toast.makeText(InfoActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } else
                editTransition();
            return true;
        }
        else if (item.getItemId() == R.id.addAction) {
            editTransition();
            updateChanges();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize() {
        this.key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.titleLabel = (EditText) findViewById(R.id.ISTitleLabel);
        this.infoLabel = (EditText) findViewById(R.id.ISDescriptionLabel);
        this.dateLabel = (EditText) findViewById(R.id.ISDateLabel);
        this.locationLabel = (EditText) findViewById(R.id.ISLocationLabel);

        if (intent.getStringExtra("tag").equals("announcement"))
            this.item = intent.getStringArrayExtra("announcement");
        else if (intent.getStringExtra("tag").equals("club"))
            this.item = intent.getStringArrayExtra("club");

        FirebaseDatabase.getInstance().getReference("users/students").child(item[4]).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
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
        titleLabel.clearComposingText();
        infoLabel.clearComposingText();
        dateLabel.clearComposingText();
        locationLabel.clearComposingText();
    }

    private void editCheck() {
        Retrieve.isUserTeacher(InfoActivity.this, key, new Retrieve.StatusHandler() {
            @Override
            public void handle(boolean status) {
                if (status) {
                    isEditAvailable = true;
                    if (item[3].isEmpty())
                            locationLabel.setText(creatorName);
                } if (key.equals(item[4])) {
                    isEditAvailable = true;
                    student = true;
                }
                else
                    isEditAvailable = false;
                invalidateOptionsMenu();
            }
        });
    }

    private void editTransition() {
        editMode = !editMode;
        enterEditMode(editMode);
        invalidateOptionsMenu();
    }

    private void enterEditMode(boolean editMode) {
        titleLabel.setFocusable(editMode);
        titleLabel.setClickable(editMode);
        titleLabel.setFocusableInTouchMode(editMode);
        titleLabel.setEnabled(editMode);

        infoLabel.setFocusable(editMode);
        infoLabel.setClickable(editMode);
        infoLabel.setFocusableInTouchMode(editMode);
        infoLabel.setEnabled(editMode);

        dateLabel.setFocusable(editMode);
        dateLabel.setClickable(editMode);
        dateLabel.setFocusableInTouchMode(editMode);
        dateLabel.setEnabled(editMode);

        locationLabel.setFocusable(editMode);
        locationLabel.setClickable(editMode);
        locationLabel.setFocusableInTouchMode(editMode);
        locationLabel.setEnabled(editMode);

        if (editMode) {
            titleLabel.setBackgroundResource(R.color.segmentedButtonUnselected);
            infoLabel.setBackgroundResource(R.color.segmentedButtonUnselected);
            dateLabel.setBackgroundResource(R.color.segmentedButtonUnselected);
            locationLabel.setBackgroundResource(R.color.segmentedButtonUnselected);
        } else {
            titleLabel.setBackgroundResource(R.color.caldroid_transparent);
            infoLabel.setBackgroundResource(R.color.caldroid_transparent);
            dateLabel.setBackgroundResource(R.color.caldroid_transparent);
            locationLabel.setBackgroundResource(R.color.caldroid_transparent);
        }
    }

    private void updateChanges() {
        DatabaseReference dataBase = FirebaseDatabase.getInstance().getReference();
        dataBase.child("announcements").child(item[5]).child("title").setValue(titleLabel.getText());
        dataBase.child("announcements").child(item[5]).child("description").setValue(infoLabel.getText());
        dataBase.child("announcements").child(item[5]).child("dateTime").setValue(dateLabel.getText());
        dataBase.child("announcements").child(item[5]).child("location").setValue(locationLabel.getText());
        Toast.makeText(InfoActivity.this, "Information Updated", Toast.LENGTH_SHORT).show();
    }

    private void onRetrieveName(String creatorName) {
        this.creatorName = creatorName;
    }
}
