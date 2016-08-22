package com.wlmac.lyonsden2_android.resourceActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.ListAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_viewer_activity);

        parseIntentDate();
        // Initialize the list
        list = (ListView) findViewById(R.id.LVSList);
        // Create and set the adapter for this activity's list
        adapter = new ListAdapter(this, content[0], content[1], null, false);
        list.setAdapter(adapter);
        // Make this activity's list un-clickable
        list.setClickable(false);
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
}