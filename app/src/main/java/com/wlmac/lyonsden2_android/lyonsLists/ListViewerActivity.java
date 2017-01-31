package com.wlmac.lyonsden2_android.lyonsLists;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.LoadingLabel;
import com.wlmac.lyonsden2_android.otherClasses.LyonsAlert;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;
import com.wlmac.lyonsden2_android.resourceActivities.TextViewerActivity;

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
    /** An ArrayList containing all the items in the list, broken down into section.. */
    private ArrayList<ArrayList<String[]>> content = new ArrayList<>();
    /** An ArrayList containing all the teacher departments. This is the section header source. */
    private ArrayList<String> departments = new ArrayList<>();
    /** The list of this list viewer */
    private ListView list;
    /** The adapter of this list viewer's list. */
    private SectionedListAdapter adapter;
    /** The loading label. */
    private LoadingLabel loadingLabel;
    /** The loading indicator. */
    private ProgressBar loadingCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_viewer_activity);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        list = (ListView) findViewById(R.id.LVSList);
        this.getSupportActionBar().setTitle(getIntent().getStringExtra("title"));

        if (getSupportActionBar().getTitle().toString().equals("Teachers")) {
            loadingLabel = new LoadingLabel((TextView) findViewById(R.id.LVSLoadingLabel), this);
            loadingLabel.startCycling();
            loadingCircle = (ProgressBar) findViewById(R.id.LVSLoadingWheel);

            String[] array = {"Administration",  "Arts", "Business", "Co-op and Leadership", "Computer Science and Technology", "English", "Languages", "Library", "Mathematics", "Physical Education", "Science", "Social Science", "Special Education", "Student Services", "Other"};  // Length: 15
            for (int h = 0; h < array.length; h ++) {
                departments.add(array[h]);
            }

            parseForTeachers();

            // Initialize the list
            adapter = new SectionedListAdapter(this, content, departments, new ArrayList<String[]>(), createTeacherOnClick());
            list.setAdapter(adapter);
            list.setAlpha(0);
        } else {
            findViewById(R.id.LVSLoadingWheel).setVisibility(View.GONE);
            final ArrayList<String> content = new ArrayList<>();
            final String folderName;

            if (getSupportActionBar().getTitle().toString().equals("Licences")) {
                content.add("Firebase");
                content.add("OpenSans-Light");
                folderName = "licences/";
            } else {    // if (getSupportActionBar().getTitle().toString().equals("Help"))
                content.add("Announcement Proposals");
                content.add("Club Leaders Guide");
                content.add("Home Screen Guide");
                folderName = "help_files/";
            }

            list.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, content));

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ListViewerActivity.this, TextViewerActivity.class);
                    intent.putExtra("filename", folderName + content.get(position) + ".txt");
                    startActivity(intent);
                }
            });
        }

    }

    private View.OnClickListener createTeacherOnClick () {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View teacher) {
                final LyonsAlert alertDialog = new LyonsAlert();
                alertDialog.setTitle("Contact Teacher");
                alertDialog.setSubtitle("Are you sure you want to email this teacher right now?");
                alertDialog.configureLeftButton("No", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.configureRightButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View button) {
                        alertDialog.dismiss();
                        String[] teacherInfo = adapter.getItem((Integer) teacher.getTag());
                        if (teacherInfo == null || teacherInfo.length < 2) {
                            Toast.makeText(getApplicationContext(), "Another Error Occurred!\nError #" + getResources().getInteger(R.integer.TeacherInfoRetrievalFailure), Toast.LENGTH_LONG).show();
                        }
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, teacherInfo[2]);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "A question from " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        intent.putExtra(Intent.EXTRA_TEXT, "Dear " + teacherInfo[0] + ",\n");
                        try {
                            startActivity(Intent.createChooser(intent, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialog.hideInput();
                alertDialog.show(getSupportFragmentManager(), "TeacherEmailApprovalDialog");
            }
        };
    }

    /** Called when the teacher list has been loaded. Hides loading labels and updates the list. */
    private void onTeachersLoaded () {
        Log.d("List Viewer", "Web Content Loaded!");
        adapter.notifyDataSetChanged();
        list.animate().setDuration(300).alpha(1);
        loadingLabel.dismiss();
        loadingCircle.animate().setDuration(300).alpha(0);
    }

    /**
     * Populates the content ArrayList by taking the unsorted ArrayList and breaking it into sections.
     * @param unsorted The ArrayList to be sorted into section and be inserted into content.
     */
    private void populateContent (ArrayList<String[]> unsorted) {
        content.clear();

        // Output initialization
        for (int h = 0; h < departments.size(); h ++) {
            content.add(new ArrayList<String[]>());
        }

        for (String[] teacher : unsorted) {
            content.get(departments.indexOf(teacher[1])).add(teacher);
        }

    }

    /**
     * This method downloads the teachers list from the database, sorts it into department section
     * and populates the content ArrayList using populateContent(ArrayList<String[]> unsorted) method.
     * This method checks for internet availability as well as database directory existance and will show
     * appropriate errors.
     */
    private void parseForTeachers () {
        if (Retrieve.isInternetAvailable(this)) {   // Check internet availability
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
                        populateContent(unsorted);
                        onTeachersLoaded();
                    } else {
                        Log.d("Teacher List Retriever", "The database entry does not exist!");
                        Toast.makeText(ListViewerActivity.this,
                                      "Another Error Occurred!\n Error #" + getResources().getInteger(R.integer.DatabaseDirectoryNonExistent),
                                      Toast.LENGTH_LONG).show();
                        onTeachersLoaded();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("Teacher List Retriever", "There has been an error!");
                    Toast.makeText(ListViewerActivity.this,
                                   "Another Error Occurred!\n Error #" + getResources().getInteger(R.integer.DatabaseOperationCancelled),
                                   Toast.LENGTH_LONG).show();
                    onTeachersLoaded();
                }
            });
        } else {
            Toast.makeText(this, "No Internet Available!", Toast.LENGTH_SHORT).show();
            onTeachersLoaded();
        }
    }
}