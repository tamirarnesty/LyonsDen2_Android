package com.wlmac.lyonsden2_android.lyonsLists;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.R;

import java.util.ArrayList;

/**
 * Created by sketch204 on 2016-12-12.
 */

public class SectionedList extends ArrayAdapter {
    private ArrayList<String[]>[] content;
    private ArrayList<String> headers;
    private ArrayList<Boolean> sectionMap;
    private Context context;
    private int sectionCounter = 0;
    private int childCounter = 0;

    public SectionedList (Context context, ArrayList<String[]>[] content, ArrayList<String> headers, ArrayList<Boolean> sectionMap) {
        super (context, -1, content);
        this.context = context;
        this.content = content;
        this.headers = headers;
        this.sectionMap = sectionMap;
        Log.d("Section List", "Instance Created!");
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("Section List", "Creating View...");
        if (position >= sectionMap.size()) {    // If out of bounds
            Log.d("Section List", "No Views to Create!");
            return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.default_list_cell, parent, false);
        }

        View rowView;

        if (sectionMap.get(position)) {
            Log.d("Section List", "View is a header!");
            rowView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_section_separator, parent, false);

            ((TextView) rowView.findViewById(R.id.LSSHeader)).setText(headers.get(sectionCounter));
            sectionCounter ++;
        } else {
            Log.d("Section List", "View is a child!");
            rowView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.default_list_cell, parent, false);

            // Declare instance of all required GUI components in the cell.
            ((TextView) rowView.findViewById(R.id.LIDTitleLabel)).setText(content[sectionCounter].get(childCounter)[0]);
            ((TextView) rowView.findViewById(R.id.LIDInfoLabel)).setText(content[sectionCounter].get(childCounter)[1]);
        }
        Log.d("Section List", "View Created!");
        return rowView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.d("Section List", "Data Set Changed! Updating views...");
    }
}
