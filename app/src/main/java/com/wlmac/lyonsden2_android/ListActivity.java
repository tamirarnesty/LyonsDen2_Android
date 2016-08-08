package com.wlmac.lyonsden2_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.wlmac.lyonsden2_android.resourceActivities.ListItemView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    public static ArrayList<String>[] content = new ArrayList[]{new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>()};
    private ListItemView[] listItems;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        listItems = new ListItemView[content[0].size()];
        for (int h = 0; h < content[0].size(); h ++) {
            listItems[h] = new ListItemView(this, null, R.attr.theme, content[0].get(h), content[1].get(h), null);
        }

        list = (ListView) findViewById(R.id.LSClubsEventsList);

        ArrayAdapter<ListItemView> adapter = new ArrayAdapter<ListItemView>(this, android.R.layout.simple_selectable_list_item, listItems);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: Do something here, (onItemClickListener)
                Toast.makeText(ListActivity.this, "BAH BAH", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
