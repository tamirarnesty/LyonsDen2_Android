package com.wlmac.lyonsden2_android.lyonsLists;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wlmac.lyonsden2_android.R;

import java.util.ArrayList;

/**
 * Created by sketch204 on 2016-12-12.
 */

public class SectionedListAdapter extends ArrayAdapter {
    private String sectionKey = "/`";
    private ArrayList<ArrayList<String[]>> content;
    private ArrayList<String[]> sections;
    private ArrayList<String[]> listContent;

    public SectionedListAdapter(Context context, ArrayList<ArrayList<String[]>> content, ArrayList<String> sections, ArrayList<String[]> list) {
        super (context, -1, list);
        this.content = content;
        this.sections = toSectionsType(sections);
        this.listContent = list;
        createListMap();
        Log.d("Section List", "Instance Created!");
    }

    // Transforms from ArrayList<String> to ArrayList<String[]>, so that sections and content are of the same type, and can easily be compared
    // A section's second item will always equal to sectionKey
    private ArrayList<String[]> toSectionsType (ArrayList<String> input) {
        ArrayList<String[]> output = new ArrayList<>(input.size());     // Declare output
        for (int h = 0; h < input.size(); h ++) {
            String[] item = {input.get(h), sectionKey};     // Create the new item, first index is section, second is section key
            output.add(item);
        }
        return output;
    }

    public void createListMap () {
        Log.d("SectionedList", "Attempting to create List Map...");
        if (content.isEmpty() || sections.isEmpty()) {
            Log.d("SectionedList", "createListMap: One of the contents is empty!");
            return;
        }

        listContent.clear();
        for (int sectionCounter = 0; sectionCounter < sections.size(); sectionCounter ++) {    // For each section
            if (!content.get(sectionCounter).isEmpty()) {    // If the current section is not empty
                listContent.add(sections.get(sectionCounter));
                for (int childCounter = 0; childCounter < content.get(sectionCounter).size(); childCounter ++) { // For each item in the current section
                    listContent.add(content.get(sectionCounter).get(childCounter));
                }
            }
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("Section List", "Creating View...");
        // THE FOLLOWING SETUP WILL ONLY WORK FOR A TEACHER LIST!!!

        View rowView;

        if (listContent.get(position).length > 1 && listContent.get(position)[1].equals(sectionKey)) {  // If the current item is a section
            Log.d("Section List", "View is a header!");
            rowView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_section_separator, parent, false);

            ((TextView) rowView.findViewById(R.id.LSSHeader)).setText(listContent.get(position)[0]);
            rowView.setEnabled(false);
            rowView.setOnClickListener(null);
        } else {    // If the current item is a child
            Log.d("Section List", "View is a child!");
            rowView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.default_list_cell, parent, false);

            // Declare instance of all required GUI components in the cell.
            ((TextView) rowView.findViewById(R.id.LIDTitleLabel)).setText(listContent.get(position)[0]);
            ((TextView) rowView.findViewById(R.id.LIDInfoLabel)).setText(listContent.get(position)[2]);
            rowView.setEnabled(true);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Text", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return rowView;
    }

    @Override
    public void notifyDataSetChanged() {
        Log.d("SectionedList", "Notified of Data Set Changes");
        createListMap();
        super.notifyDataSetChanged();
    }
}
