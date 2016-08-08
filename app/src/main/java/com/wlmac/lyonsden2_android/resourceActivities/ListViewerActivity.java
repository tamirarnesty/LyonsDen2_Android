package com.wlmac.lyonsden2_android.resourceActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wlmac.lyonsden2_android.R;

import java.util.ArrayList;

public class ListViewerActivity extends AppCompatActivity {
    public static ArrayList<String>[] content = new ArrayList[4];
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_viewer_activity);
        list = (ListView) findViewById(R.id.LVSList);

        ListItemView[] listItems = new ListItemView[content[0].size()];
        for (int h = 0; h < content[0].size(); h++) {
            listItems[h] = new ListItemView(this, null, R.attr.theme, content[0].get(h), content[1].get(h), null);
        }

        ArrayAdapter<ListItemView> adapter = new ArrayAdapter<ListItemView>(this, android.R.layout.list_content, listItems);

    }
}