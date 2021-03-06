package com.wlmac.lyonsden2_android.resourceActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.contactActivities.AnnouncementActivity;
import com.wlmac.lyonsden2_android.lyonsLists.ClubList;
import com.wlmac.lyonsden2_android.lyonsLists.ListAdapter;
import com.wlmac.lyonsden2_android.otherClasses.LyonsAlert;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClubActivity extends AppCompatActivity {
    public static Drawable image;

    private ArrayList<String[]> content = new ArrayList<>();

    private DatabaseReference clubRef;

    private ListAdapter adapter;

    private TextView titleView;
    private TextView infoView;

    private EditText titleField;
    private EditText infoField;

    private SwipeRefreshLayout refreshLayout;

    private boolean userIsLead = false;
    private boolean editing = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_activity);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        initializeComponents();

        ListView clubsEvents = (ListView) findViewById(R.id.ClubSAnnouncements);

        // The list adapter
        adapter = new ListAdapter(this, content, false, true);
        clubsEvents.setAdapter(adapter);

        clubsEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("EventList", "Opening Announcement");
                // Event keys: title, info, date, location

                Intent intent = new Intent(ClubActivity.this, InfoActivity.class);
                String[] line = new String[5];
                line[0] = content.get(position)[0];
                line[1] = content.get(position)[1];
                line[2] = content.get(position)[2];
                line[3] = content.get(position)[3];
                line[4] = content.get(position)[4];
                intent.putExtra("tag", "announcement");
                intent.putExtra("announcement", line);

                startActivity(intent);

                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        checkUserForLeadership();

        setFonts();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadAnnouncements();
            }
        });
        refreshLayout.setColorSchemeResources(R.color.navigationBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAnnouncements();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void initializeComponents () {
        titleView = (TextView) findViewById(R.id.ClubSTitleLabel);
        String clubTitle = getIntent().getStringExtra("title");
        titleView.setText(clubTitle);

        infoView = (TextView) findViewById(R.id.ClubSDescriptionLabel);
        infoView.setText(getIntent().getStringExtra("info"));

        ((TextView) findViewById(R.id.ClubSLeaderList)).setText("Club Leads: " + getIntent().getStringExtra("leaders"));

        clubRef = FirebaseDatabase.getInstance().getReference("clubs/" + getIntent().getStringExtra("clubKey"));
        Log.d("Club Reference Path", clubRef.toString());

        titleField = (EditText) findViewById(R.id.ClubSTitleField);
        infoField = (EditText) findViewById(R.id.ClubSDescriptionField);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.ClubSAnnouncementsResfresh);
    }

    private void setFonts() {
        int[] components = {R.id.ClubSTitleLabel, R.id.ClubSTitleField, R.id.ClubSDescriptionLabel, R.id.ClubSDescriptionField, R.id.ClubSLeaderList};
        for (int h = 0; h < components.length; h ++)
            ((TextView) findViewById(components[h])).setTypeface(Retrieve.typeface(this));
    }

    private void loadAnnouncements () {
        Retrieve.eventData(this, clubRef.child("announcements"), content, new Retrieve.ListDataHandler() {
            @Override
            public void handle(ArrayList<String[]> listData) {
                onEventsLoaded();
            }
        });
    }

    private void onEventsLoaded () {
        adapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

    private void checkUserForLeadership () {
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        clubRef.child("editors").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot editor: dataSnapshot.getChildren()) {
                        if ((editor.getValue(String.class)).equals(userID)) {
                            userIsLead = true;
                            invalidateOptionsMenu();
                            Log.d("Club Activity", "User is the club leader");
                            return;
                        }
                        userIsLead = false;
                        invalidateOptionsMenu();
                        Log.d("Club Activity", "User is not the club leader");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Another Error Occurred!\n Error #" + getResources().getInteger(R.integer.DatabaseOperationCancelled), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void toggleEditMode() {
        editing = !editing;

        int viewVisibility = (editing) ? View.GONE : View.VISIBLE;
        int editVisibility = (editing) ? View.VISIBLE : View.GONE;

        findViewById(R.id.ClubSViewBox).setVisibility(viewVisibility);
        findViewById(R.id.ClubSEditBox).setVisibility(editVisibility);

        if (!editing && (!titleView.getText().toString().equals(titleField.getText().toString()) || !infoView.getText().toString().equals(infoField.getText().toString()))) {
            invalidateOptionsMenu();
            promptUserForApproval(titleView.getText().toString(), infoView.getText().toString());
            titleView.setText(titleField.getText());
            infoView.setText(infoField.getText());
        } else {
            invalidateOptionsMenu();
            titleField.setText(titleView.getText());
            infoField.setText(infoView.getText());
        }

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        invalidateOptionsMenu();
    }

    private void promptUserForApproval(final String oldTitle, final String oldInfo) {
        final LyonsAlert alertDialog = new LyonsAlert();
        alertDialog.setTitle("Teacher Approval");
        alertDialog.setSubtitle("Please ask a teacher to approve your changes!");
        alertDialog.configureLeftButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleView.setText(oldTitle);
                infoView.setText(oldInfo);
                alertDialog.dismiss();
            }
        });
        alertDialog.configureRightButton("Approve", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Retrieve.isInternetAvailable(ClubActivity.this)) {
                    Retrieve.teacherApproval(ClubActivity.this, alertDialog.getInputText(), new Retrieve.StatusHandler() {
                        @Override
                        public void handle(boolean status) {
                            if (status) {
                                alertDialog.dismiss();
                                commitChanges();
                            } else {
                                titleView.setText(oldTitle);
                                infoView.setText(oldInfo);
                                Toast.makeText(getApplicationContext(), "Wrong Password!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                    titleView.setText(oldTitle);
                    infoView.setText(oldInfo);
                }
            }
        });
        alertDialog.show(getSupportFragmentManager(), "TeacherApprovalDialog");
        alertDialog.makeInputSecure();
    }

    private void commitChanges() {
        Map<String, Object> childrenToUpdate = new HashMap<>(2);
        if (!titleView.getText().toString().equals(titleField.getText().toString())) {
            titleView.setText(titleField.getText());
            childrenToUpdate.put("title", titleView.getText().toString());
        } else if (!infoView.getText().toString().equals(infoField.getText().toString())) {
            infoView.setText(infoField.getText());
            childrenToUpdate.put("description", infoView.getText().toString());
        }

        if (childrenToUpdate.size() > 0) {
            clubRef.updateChildren(childrenToUpdate, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Toast.makeText(getApplicationContext(), "Changes Applied!", Toast.LENGTH_SHORT).show();
                    ClubList.contentChanged = true;
                }
            });
        }
    }

    private void proposeAnnouncement () {
        Intent intent = new Intent(this, AnnouncementActivity.class);
        intent.putExtra("clubKey", clubRef.getKey());
        intent.putExtra("announcementId", "clubsAnnouncement");
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editAction) {
            toggleEditMode();
        } else if (item.getItemId() == R.id.addAction) {
            proposeAnnouncement();
        } else if (item.getItemId() == R.id.doneAction) {
            toggleEditMode();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (userIsLead) {
            if (editing)
                getMenuInflater().inflate(R.menu.club_menu_editing, menu);
            else
                getMenuInflater().inflate(R.menu.club_menu_default, menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }
}
