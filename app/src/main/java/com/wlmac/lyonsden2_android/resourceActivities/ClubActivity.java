package com.wlmac.lyonsden2_android.resourceActivities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wlmac.lyonsden2_android.ListActivity;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.ListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// TODO: FIX THE MULTILINE ISSUE FOR INFOVIEW!

public class ClubActivity extends AppCompatActivity {
    public static Drawable image;
    /** An ArrayList of ArrayLists each of which represents a data field for each of the announcement list's cells. */
    private ArrayList<String>[] content = new ArrayList[]{new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>()};

    private DatabaseReference clubRef;

    private String oldTitle = "";
    private String oldInfo = "";

    private TextView titleView;
    private TextView infoView;

    private boolean userIsLead = false;
    private boolean editing = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_activity);

        checkUserForLeadership();

        Intent intent = getIntent();

        ImageView imageView = (ImageView) findViewById(R.id.ClubSImageView);
        imageView.setImageDrawable(image);
        if (imageView.getDrawable() == null) {
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageParams.weight = 0;

            imageView.setLayoutParams(imageParams);
        }

        titleView = (TextView) findViewById(R.id.ClubSTitleLabel);
        String clubTitle = intent.getStringExtra("title");
        titleView.setText(clubTitle);
        clubRef = FirebaseDatabase.getInstance().getReference("clubs").child(intent.getStringExtra("clubKey"));
        Log.d("Club Reference Path", clubRef.toString());

        infoView = (TextView) findViewById(R.id.ClubSDescriptionLabel);
        infoView.setText(intent.getStringExtra("info"));

        TextView leaderView = (TextView) findViewById(R.id.ClubSLeaderList);
        leaderView.setText(intent.getStringExtra("leaders"));

        ListView clubsEvents = (ListView) findViewById(R.id.ClubSAnnouncements);

        // The list adapter
        ListAdapter adapter = new ListAdapter(this, content[0], content[1], null, false);
        clubsEvents.setAdapter(adapter);

        ListActivity.parseForEvents(adapter, content, clubRef.child("announcements"));

        clubsEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(parent.getContext(), InfoActivity.class);
                intent.putExtra("title", content[0].get(position));
                intent.putExtra("info", content[1].get(position));
                intent.putExtra("date", content[2].get(position));
                intent.putExtra("location", content[3].get(position));
                startActivity(intent);
            }
        });
    }

    private void checkUserForLeadership () {
        // If user is lead
        if (true) {
            userIsLead = true;
        } else {
            // If check was not possible (Error or something else)
            userIsLead =false;
        }
    }

    private void enterEditMode () {
        editing = !editing;
        int backgroundResource = 0;
        if (editing) {
            backgroundResource = R.drawable.text_view_edit;
            oldTitle = titleView.getText().toString();
            oldInfo = infoView.getText().toString();
        } else {
            backgroundResource = R.drawable.text_view_default;
        }

        Log.d("ClubActivity:", ((editing) ? "Entering" : "Leaving") + " Edit Mode");
        Log.d("BackgroundResource", "" + backgroundResource);
        titleView.setBackgroundResource(backgroundResource);
        titleView.setCursorVisible(editing);
        titleView.setFocusableInTouchMode(editing);
        titleView.setFocusable(editing);
        titleView.setEnabled(editing);
        titleView.setClickable(editing);
        titleView.setInputType((editing) ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL);
        titleView.requestFocus();

        infoView.setBackgroundResource(backgroundResource);
        infoView.setCursorVisible(editing);
        infoView.setFocusableInTouchMode(editing);
        infoView.setFocusable(editing);
        infoView.setEnabled(editing);
        infoView.setClickable(editing);
        infoView.setInputType((editing) ? InputType.TYPE_TEXT_FLAG_MULTI_LINE : InputType.TYPE_NULL);
        if (!editing) finalizeEditing();
    }

    private void finalizeEditing () {
        Map<String, Object> childrenToUpdate = new HashMap<>(2);
        if (!titleView.getText().toString().equals(oldTitle))
            childrenToUpdate.put("title", titleView.getText().toString());
        else if (!infoView.getText().toString().equals(oldInfo))
            childrenToUpdate.put("description", infoView.getText().toString());

        if (childrenToUpdate.size() > 0) {
            clubRef.updateChildren(childrenToUpdate, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Toast.makeText(getApplicationContext(), "Changes Applied!", Toast.LENGTH_SHORT).show();
                    ListActivity.contentChanged = true;
                }
            });
        }
    }

    public void displayMembers (View view ) {
        ListViewerActivity.listRef = clubRef.child("members");
        Intent intent = new Intent (this, ListViewerActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editAction)
            enterEditMode();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (userIsLead) {
            getMenuInflater().inflate(R.menu.edit_menu, menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }
}
