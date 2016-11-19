package com.wlmac.lyonsden2_android.lyonsLists;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.LyonsAlert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// TODO: TEST FINALIZE EDITING OUT!

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
    private ArrayList<String>[] content = new ArrayList[]{new ArrayList<String>(), new ArrayList<String>()};
    private ArrayList<String>[] oldContent = new ArrayList[]{new ArrayList(), new ArrayList()};
    /** The list of this list viewer */
    private ListView list;

    public static DatabaseReference listRef;

    private ListAdapter adapter;

    private MenuItem addItem;
    private boolean editing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_viewer_activity);

        this.setTitle(getIntent().getStringExtra("title"));

        if (this.getTitle().equals("Members")) { parseForMemeber(); } else { parseForTeachers(); }

        // Initialize the list
        list = (ListView) findViewById(R.id.LVSList);
        // Create and set the adapter for this activity's list
//        if (this.getTitle().equals("Members"))
//            adapter = new ListAdapter(this, content, false, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d("ListViewer", "Deleting at index " + v.getTag());
//                    removeItemAtIndex((int) ((View) v.getParent()).getTag());
//                }
//            });
//        else
//            adapter = new ListAdapter(this, content, false);
        list.setAdapter(adapter);
        // Make this activity's list un-clickable
        list.setClickable(false);
    }

    private void enterEditMode () {
        editing = !editing;

        addItem.setVisible(editing);
        adapter.setEditing(editing);

        if (!editing) { finalizeEditing(); } else { oldContent = new ArrayList[]{new ArrayList(content[0]), new ArrayList(content[1])}; }
    }

    // TODO: TEST FINALIZE EDITING OUT!
    private void finalizeEditing () {
        listRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<DataSnapshot> snapshots = new ArrayList<DataSnapshot>((Collection) dataSnapshot.getChildren());

                    if (snapshots == null || snapshots.isEmpty()) {
                        Log.d("Finalization of Editing", "Failed to retrieve list from the web");
                        throw new RuntimeException("This is a crash!");
                    }

                    for (int h = 0; h < snapshots.size(); h ++) {
                        if (!content[0].contains(snapshots.get(h).getValue(String.class))) {
                            snapshots.get(h).getRef().removeValue();
                            oldContent[0].remove(h);
                            oldContent[1].remove(h);
                        }
                    }
                    checkForNewMembers();
                }
            }

            private void checkForNewMembers () {
                Map<String, Object> childrenToUpdate = new HashMap<String, Object>();

                if (oldContent[0].size() < content[0].size())
                    for (int h = oldContent[0].size(); h < content[0].size(); h ++)
                        childrenToUpdate.put(listRef.push().getKey(), content[0].get(h));

                listRef.updateChildren(childrenToUpdate, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        String toastMessage = "Update Success!";
                        if (databaseError != null) {
                            toastMessage = "Update Failed!";
                        }
                        Toast.makeText(ListViewerActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("WebEvents Parser", "Failed to load club list from the web", databaseError.toException());
                Toast.makeText(ListViewerActivity.this, "Update Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addMemberInitiated () {
        Log.d("ListViewer", "Initiating Adding Procedures");
        final LyonsAlert alertDialog = new LyonsAlert();
        alertDialog.setTitle("Club Code");
        alertDialog.setSubtitle("Please enter the club code");
        alertDialog.configureLeftButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ListViewer", "Adding Procedures Cancelled");
                alertDialog.dismiss();
            }
        });
        alertDialog.configureRightButton("Submit", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ListViewer", "Adding Procedures Success! Initiating Add");
                addMember(alertDialog.getInputText());
                alertDialog.dismiss();
            }
        });

        alertDialog.show(getSupportFragmentManager(), "ClubCodeDialog");
        Log.d("ListViewer", "Displaying Add Dialog");
    }

    private void addMember (String name) {
        Log.d("ListViewer", "Adding " + name);
        content[0].add(name);
        content[1].add("");
        adapter.notifyDataSetChanged();
    }

    private void removeItemAtIndex (int index) {
        content[0].remove(index);
        content[1].remove(index);
        adapter.notifyDataSetChanged();
    }

    private void parseForMemeber () {
        listRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot member : dataSnapshot.getChildren()) {
                    content[0].add(member.getValue(String.class));
                    content[0].add("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("WebEvents Parser", "Failed to load member list from the web", databaseError.toException());
                content[0].add("Web Parser Failure!");
                content[1].add("Failed to load complete member list from the web");
            }
        });
    }

    private void parseForTeachers () {
        listRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot teacher: dataSnapshot.getChildren()) {
                        content[0].add(teacher.child("name").getValue(String.class));
                        content[1].add(teacher.child("department").getValue(String.class) + " " + teacher.child("email").getValue(String.class));
                    }
                } else {
                    Log.d("Teacher List Retriever", "There has been an error!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Teacher List Retriever", "There has been an error!");
                throw databaseError.toException();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editAction)
            enterEditMode();
        else if (item.getItemId() == R.id.addAction)
            addMemberInitiated();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getTitle().equals("Members") && getIntent().getBooleanExtra("userIsLead", false)) {
            getMenuInflater().inflate(R.menu.edit_menu, menu);
            getMenuInflater().inflate(R.menu.add_menu, menu);
            addItem = menu.findItem(R.id.addAction);
            addItem.setVisible(false);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }
}