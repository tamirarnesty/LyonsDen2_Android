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
    private ArrayList<String[]> content;

    public ExpandableListAdapter(Context context, ArrayList<String[]> content) {
        this.context = context;
        this.content = content;
    }

    @Override
    public int getGroupCount() {
        return content.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return content.get(groupPosition);
    }

    private ArrayList<String> constructChild (int index) {
        ArrayList<String> output = new ArrayList<>();
        output.add(content.get(index)[0]);
        output.add(content.get(index)[1]);
        output.add(content.get(index)[2]);
        return output;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return constructChild(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return content.get(groupPosition).hashCode();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return content.get(groupPosition).hashCode();
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

        ((TextView) convertView.findViewById(R.id.LECTitle)).setText(content.get(groupPosition)[0]);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_expandable_expanded, null);
        }

        ((TextView) convertView.findViewById(R.id.LECInfo)).setText(content.get(groupPosition)[1]);
        ((TextView) convertView.findViewById(R.id.LECDate)).setText(content.get(groupPosition)[2]);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
