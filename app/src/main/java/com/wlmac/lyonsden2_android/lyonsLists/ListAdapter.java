package com.wlmac.lyonsden2_android.lyonsLists;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;

import java.util.ArrayList;

/**
 * This class is a customized adapter that is used for translating basic data into the custom
 * cell view of this program.
 *
 * @author sketch204
 * @version 1, 2016/08/08
 */
public class ListAdapter extends ArrayAdapter {

    private ArrayList<String[]> content;
    private ArrayList<Button> buttons = new ArrayList<>();

    private final View.OnClickListener onClick;
    /** The layout of the cell to be used with this list. Can be either default or large. */
    private final int layout;
    /** The id of the TextView that displays the title of the cell. Changes depending on which layout is used. */
    private final int titleID;
    /** The id of the TextView that displays the subtitle of the cell. Changes depending on which layout is used. */
    private final int infoID;
    /** The id of the cell's delete button. Changes depending on which layout is used. */
    private final int deleteID;
    private final int dateID;
    private boolean hasSubtitle = true;

//    private boolean canEdit = false;
//    private boolean editing = false;

    /**
     * The default constructor of this List adapter, creates the adapter and initializes all of
     * its GUI components.
     * @param context The context that this adapter's list belongs to.
     */
    public ListAdapter (Context context, ArrayList<String[]> content, boolean isExpandable, boolean hasSubtitle) {
        super (context, -1, content);    // Super call
        this.content = content;
        this.onClick = null;
        this.hasSubtitle = hasSubtitle;

        if (isExpandable) {
            layout = R.layout.list_expandable_item;
            titleID = R.id.LSITitle;
            infoID = R.id.LSIInfo;
            dateID = R.id.LSIDate;
            deleteID = -1;
        } else {
            layout = R.layout.list_default_item;
            titleID = R.id.LIDTitleLabel;
            infoID = R.id.LIDInfoLabel;
            dateID = -1;
            deleteID = -1;
        }
    }

    /**
     * The default constructor of this List adapter, creates the adapter and initializes all of
     * its GUI components.
     * @param context The context that this adapter's list belongs to.
     * @param onClick The onItemClickListener to apply to the each row item. (To retrieve the list position of the cell, use the tag of the parent view of the button)
     */
    public ListAdapter (Context context, ArrayList<String[]> content, boolean isExpandable, boolean hasSubtitle, View.OnClickListener onClick) {
        super (context, -1, content);    // Super call
        this.content = content;
        this.onClick = onClick;
        this.hasSubtitle = hasSubtitle;
//        this.canEdit = true;

        if (isExpandable) {
            layout = R.layout.list_expandable_item;
            titleID = R.id.LSITitle;
            infoID = R.id.LSIInfo;
            dateID = R.id.LSIDate;
            deleteID = R.id.LIDDeleteButton;
        } else {
            layout = R.layout.list_default_item;
            titleID = R.id.LIDTitleLabel;
            infoID = R.id.LIDInfoLabel;
            dateID = -1;
            deleteID = R.id.LIDDeleteButton;
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Create an instance of the cell, from the XML cell layout file.
        View rowView = (convertView != null) ? convertView : ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layout, parent, false);
        // Declare instance of all required GUI components in the cell.
        ((TextView) rowView.findViewById(titleID)).setText(content.get(position)[0]);
        ((TextView) rowView.findViewById(titleID)).setTypeface(Retrieve.typeface(getContext()));
        if (hasSubtitle) {
            ((TextView) rowView.findViewById(infoID)).setText(content.get(position)[1]);
            ((TextView) rowView.findViewById(infoID)).setTypeface(Retrieve.typeface(getContext()));
        } else {
            rowView.findViewById(infoID).setVisibility(View.GONE);
        }

        if (dateID != -1) {
            ((TextView) rowView.findViewById(dateID)).setText(content.get(position)[2]);
            ((TextView) rowView.findViewById(dateID)).setTypeface(Retrieve.typeface(getContext()));
        }

        if (deleteID != -1) {
            buttons.add((Button) rowView.findViewById(deleteID));
            buttons.get(position).setOnClickListener(onClick);
            rowView.setTag(position);
        }

//        if (editing)
//            setEditing(editing);

        return rowView;
    }

    @Override
    public void notifyDataSetChanged() {
        buttons.clear();
        super.notifyDataSetChanged();
    }

//    public void setEditing(boolean editing) {
//        if (canEdit) {
//            this.editing = editing;
//            int visibility = (editing) ? View.VISIBLE : View.GONE;
//            for (Button button : buttons) {
//                button.setOnClickListener(onClick);
//                button.setVisibility(visibility);
//            }
//        }
//    }
}