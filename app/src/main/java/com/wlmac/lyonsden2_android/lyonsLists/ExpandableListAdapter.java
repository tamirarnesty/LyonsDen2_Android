package com.wlmac.lyonsden2_android.lyonsLists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.R;

import java.util.ArrayList;

/**
 * Created by sketch204 on 2016-10-16.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private String[][] content;
//    private ArrayList<String> titles = new ArrayList<>();
//    private ArrayList<String> infos = new ArrayList<>();
//    private ArrayList<String> dates = new ArrayList<>();

    public ExpandableListAdapter(Context context, String [][] content) {
        this.context = context;
        this.content = content;
//        this.titles = titles;
//        this.infos = infos;
//        this.dates = dates;
    }

    @Override
    public int getGroupCount() {
        return content.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return content[groupPosition];
    }

    private ArrayList<String> constructChild (int index) {
        ArrayList<String> output = new ArrayList<>();
        output.add(content[index][0]);
        output.add(content[index][1]);
        output.add(content[index][2]);
        return output;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return constructChild(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return content[groupPosition].hashCode();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return content[groupPosition].hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_expandable_collapsed, null);
        }

        ((TextView) convertView.findViewById(R.id.LECTitle)).setText(content[groupPosition][0]);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_expandable_expanded, null);
        }

        ((TextView) convertView.findViewById(R.id.LECInfo)).setText(content[groupPosition][1]);
        ((TextView) convertView.findViewById(R.id.LECDate)).setText(content[groupPosition][2]);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void updateDataSet (String[][] newDataSet) {
        this.content = newDataSet;
        this.notifyDataSetChanged();
    }
}
