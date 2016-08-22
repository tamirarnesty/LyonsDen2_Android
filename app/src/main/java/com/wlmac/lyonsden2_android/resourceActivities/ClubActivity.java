package com.wlmac.lyonsden2_android.resourceActivities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wlmac.lyonsden2_android.ListActivity;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.ListAdapter;

import java.util.ArrayList;

public class ClubActivity extends AppCompatActivity {
    public static Drawable image;
    /** An ArrayList of ArrayLists each of which represents a data field for each of the announcement list's cells. */
    private ArrayList<String>[] content = new ArrayList[]{new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>()};

    private DatabaseReference clubRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_activity);

        Intent intent = getIntent();

        ImageView imageView = (ImageView) findViewById(R.id.ClubSImageView);
        imageView.setImageDrawable(image);
        if (imageView.getDrawable() == null) {
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageParams.weight = 0;

            imageView.setLayoutParams(imageParams);
        }

        TextView titleView = (TextView) findViewById(R.id.ClubSTitleLabel);
        String clubTitle = intent.getStringExtra("title");
        titleView.setText(clubTitle);
        clubRef = FirebaseDatabase.getInstance().getReference("clubs").child(clubTitle);  //.getDatabase().getReference(clubTitle).getDatabase();

        TextView infoView = (TextView) findViewById(R.id.ClubSDescriptionLabel);
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

    public void displayMembers (View view ) {
        clubRef.child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> titles = new ArrayList<>();
                ArrayList<String> subTitles = new ArrayList<>();

                for (DataSnapshot member : dataSnapshot.getChildren()) {
                    titles.add(member.getValue(String.class));
                    subTitles.add("");
                }
                segueIntoMemeberList(titles, subTitles);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("WebEvents Parser", "Failed to load member list from the web", databaseError.toException());
            }
        });
    }

    private void segueIntoMemeberList (ArrayList<String> titles, ArrayList<String> subTitles) {
        // Create intent
        Intent intent = new Intent(this, ListViewerActivity.class);
        // Insert data into intent
        intent.putStringArrayListExtra("titles", titles);
        intent.putStringArrayListExtra("subtitles", subTitles);
        // Display list
        startActivity(intent);
    }
}
