package com.wlmac.lyonsden2_android.lyonsLists;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.LoadingLabel;

import java.util.ArrayList;

/**
 * This activity is used for displaying a list of items. The rows of this list are not clickable, therefore
 * this list should only be used for <b>displaying</b> information. To use this activity you must first pass two
 * ArrayList objects of type String, both being equal in length, into the Intent. One of the ArrayList
 * objects should contain the titles of each row and have the key 'titles' assigned to it. The other
 * ArrayList object should contain the subtitles of each row and have the key 'subtitles' assigned to it.
 * If either the titles of subtitles are not required then and ArrayList containing empty strings should
 * be passed, otherwise this activity will automatically display an error if either of the ArrayList objects
 * are null.
 *
 * @author sketch204
 * @version 1, 2016/08/08
 */
public class ListViewerActivity extends AppCompatActivity {
    /** The content of this list viewer to display. */
    private ArrayList<String[]>[] content = new ArrayList[15];
    private ArrayList<String> departments = new ArrayList<>();
    private ArrayList<Boolean> sectionMap = new ArrayList<>();
    /** The list of this list viewer */
    private ListView list;
    private SectionedList adapter;
    private LoadingLabel loadingLabel;
    private ProgressBar loadingCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_viewer_activity);

        loadingLabel = new LoadingLabel((TextView) findViewById(R.id.LVSLoadingLabel), this);
        loadingLabel.startCycling();
        loadingCircle = (ProgressBar) findViewById(R.id.LVSLoadingWheel);

        this.setTitle(getIntent().getStringExtra("title"));

        parseForTeachers();

        // Initialize the list
        list = (ListView) findViewById(R.id.LVSList);
//        adapter = new ListAdapter(this, content, false);
        adapter = new SectionedList(this, content, departments, sectionMap);

        list.setAdapter(adapter);
        // Make this activity's list un-clickable
        list.setClickable(false);

        String[] array = {"Administration", "Arts", "Business", "Co-op and Leadership", "Computer Science and Technology", "English", "Languages", "Library", "Mathematics", "Physical Education", "Science", "Social Science", "Special Education", "Student Services", "Other"};
        for (int h = 0; h < array.length; h ++) {
            departments.add(array[h]);
        }

    }

    private void onTeachersLoaded () {
        Log.d("List Viewer", "Web Content Loaded!");
        adapter.notifyDataSetChanged();
        loadingLabel.dismiss();
        loadingCircle.setVisibility(View.GONE);
    }

    private void createSections (ArrayList<String[]> unsorted) {
        // Output initialization
        for (int h = 0; h < content.length; h ++) {
            content[h] = new ArrayList<>();
        }

        for (String[] teacher : unsorted) {
            content[departments.indexOf(teacher[1])].add(teacher);
        }
    }

    private void createMap () {
        int childCounter = 0;

        for (int h = 0; h < content.length; h ++) {
            if (childCounter == content[h].size()) {
                childCounter = 0;
                sectionMap.add(true);
            } else {
                childCounter ++;
                sectionMap.add(false);
            }
        }
    }

    private void parseForTeachers () {
        Log.d("Teacher List Retriever", "Initiating Download...");
        FirebaseDatabase.getInstance().getReference("users").child("teachers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String[]> unsorted = new ArrayList<String[]>();
                    for (DataSnapshot teacher : dataSnapshot.getChildren()) {
                        String[] teacherInfo = {teacher.child("name").getValue(String.class),
                                                teacher.child("department").getValue(String.class), teacher.child("email").getValue(String.class)};
                        unsorted.add(teacherInfo);
                    }
                    createSections(unsorted);
                    createMap();
                    onTeachersLoaded();
                } else {
                    Log.d("Teacher List Retriever", "The database entry does not exist!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Teacher List Retriever", "There has been an error!");
                throw databaseError.toException();
            }
        });
    }
}