package com.wlmac.lyonsden2_android.resourceActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.wlmac.lyonsden2_android.R;

import java.util.ArrayList;

/**
 *
 *
 * @author sketch204
 * @version 1, 2016/08/08
 */
public class ListViewerActivity extends AppCompatActivity {
    /** The content of this list viewer to display. */
    public static ArrayList<String>[] content = new ArrayList[4];
    /** The list of this list viewer */
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_viewer_activity);
        list = (ListView) findViewById(R.id.LVSList);

    }
}