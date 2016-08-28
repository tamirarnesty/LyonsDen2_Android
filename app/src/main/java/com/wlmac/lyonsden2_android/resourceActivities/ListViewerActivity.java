package com.wlmac.lyonsden2_android.resourceActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.ListAdapter;
import com.wlmac.lyonsden2_android.otherClasses.LyonsAlert;

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
    private ArrayList<String>[] content = new ArrayList[]{new ArrayList<String>(), new ArrayList<String>()};
    /** The list of this list viewer */
    private ListView list;

    public static ListAdapter adapter;

    private MenuItem addItem;
    private boolean editing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_viewer_activity);

        this.setTitle(getIntent().getStringExtra("title"));

        parseIntentDate();
        // Initialize the list
        list = (ListView) findViewById(R.id.LVSList);
        // Create and set the adapter for this activity's list
        if (this.getTitle().equals("Members"))
            adapter = new ListAdapter(this, content[0], content[1], null, false, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ListViewer", "Deleting at index " + v.getTag());
                    removeItemAtIndex((int) v.getTag());
                }
            });
        else
            adapter = new ListAdapter(this, content[0], content[1], null, false);
        list.setAdapter(adapter);
        // Make this activity's list un-clickable
        list.setClickable(false);
    }

    private void enterEditMode () {
        editing = !editing;

        addItem.setVisible(editing);
        adapter.setEditing(editing);
    }

    private void finalizeEditing () {

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
//        adapter.setEditing(editing);
    }

    private void addMember (String name) {
        Log.d("ListViewer", "Adding " + name);
        content[0].add(name);
        content[1].add("");
        adapter.notifyDataSetChanged();
//        adapter.setEditing(editing);
    }

    private void removeItemAtIndex (int index) {
        content[0].remove(index);
        content[1].remove(index);
        adapter.notifyDataSetChanged();
    }

    private void parseIntentDate () {
        Intent intent = getIntent();
        content[0] = intent.getStringArrayListExtra("titles");
        content[1] = intent.getStringArrayListExtra("subtitles");

        if (content[0] == null || content[1] == null) {
            content[0] = new ArrayList<>();
            content[0].add("There has been an error while retrieving the data,");
            content[1] = new ArrayList<>();
            content[1].add ("Please report this bug.");
        }
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