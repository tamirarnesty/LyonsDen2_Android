package com.wlmac.lyonsden2_android.lyonsLists;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.R;

import java.util.ArrayList;

/**
 * This class is a customized adapter that is used for translating basic data into the custom
 * cell view of this program.
 *
 * @author sketch204
 * @version 1, 2016/08/08
 */
public class ListAdapter extends ArrayAdapter {
    /** The context (usually activity) that this adapter's list will belong to. */
    private final Context context;
    private ArrayList<String[]> content;
    private Drawable[] images;
    private ArrayList<Button> buttons = new ArrayList<>();

    private final View.OnClickListener onClick;
    /** The layout of the cell to be used with this list. Can be either default or large. */
    private final int layout;
    /** The id of the TextView that displays the title of the cell. Changes depending on which layout is used. */
    private final int titleID;
    /** The id of the TextView that displays the subtitle of the cell. Changes depending on which layout is used. */
    private final int infoID;
    /** The id of the ImageView that displays the image of the cell. Changes depending on which layout is used. */
    private final int imageID;
    /** The id of the cell's delete button. Changes depending on which layout is used. */
    private final int deleteID;

    private boolean canEdit = false;
    private boolean editing = false;

    /**
     * The default constructor of this List adapter, creates the adapter and initializes all of
     * its GUI components.
     * @param context The context that this adapter's list belongs to.
     * @param titles The ArrayList which will contain the title for each cell.
     * @param infos The ArrayList which will contain the subtitle for each cell.
     * @param images The ArrayList which will contain the image for each cell.
     * @param isLarge If true, then the height for each cell will be @dimen/largeCellSize, otherwise it will be @dimen/defaultCellSize.
     */
    public ListAdapter (Context context, ArrayList<String[]> content, boolean isLarge) {
        super (context, -1, content);    // Super call
        this.context = context;
        this.content = content;
        this.images = null;
        this.onClick = null;

        if (isLarge) {
            layout = R.layout.large_list_cell;
            titleID = R.id.LIDTitleLabel;
            infoID = R.id.LIDInfoLabel;
            imageID = R.id.LIDImageView;
            deleteID = -1;
        } else {
            layout = R.layout.default_list_cell;
            titleID = R.id.LIDTitleLabel;
            infoID = R.id.LIDInfoLabel;
            imageID = R.id.LIDImageView;
            deleteID = -1;
        }
    }

    /**
     * The default constructor of this List adapter, creates the adapter and initializes all of
     * its GUI components.
     * @param context The context that this adapter's list belongs to.
     * @param titles The ArrayList which will contain the title for each cell.
     * @param infos The ArrayList which will contain the subtitle for each cell.
     * @param images The ArrayList which will contain the image for each cell.
     * @param isLarge If true, then the height for each cell will be @dimen/largeCellSize, otherwise it will be @dimen/defaultCellSize.
     * @param onClick The onItemClickListener to apply to the each row item. (To retrieve the list position of the cell, use the tag of the parent view of the button)
     */
    public ListAdapter (Context context, ArrayList<String[]> content, boolean isLarge, View.OnClickListener onClick) {
        super (context, -1, content);    // Super call
        this.context = context;
        this.content = content;
        this.images = null;
        this.onClick = onClick;
        this.canEdit = true;

        if (isLarge) {
            layout = R.layout.large_list_cell;
            titleID = R.id.LIDTitleLabel;
            infoID = R.id.LIDInfoLabel;
            imageID = R.id.LIDImageView;
            deleteID = R.id.LIDDeleteButton;
        } else {
            layout = R.layout.default_list_cell;
            titleID = R.id.LIDTitleLabel;
            infoID = R.id.LIDInfoLabel;
            imageID = R.id.LIDImageView;
            deleteID = R.id.LIDDeleteButton;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Create an instance of the cell, from the XML cell layout file.
        View rowView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layout, parent, false);
        // Declare instance of all required GUI components in the cell.
        ((TextView) rowView.findViewById(titleID)).setText(content.get(position)[0]);
        ((TextView) rowView.findViewById(infoID)).setText(content.get(position)[1]);

        if (images != null && images[position] != null) {   // If there is an image, then
            ((ImageView) rowView.findViewById(imageID)).setImageDrawable(images[position]);     // Set the image
        } else {                                                // Otherwise
            rowView.findViewById(imageID).setVisibility(View.GONE);                                 // Hide the ImageView.
        }

        if (deleteID != -1) {
            buttons.add((Button) rowView.findViewById(deleteID));
            buttons.get(position).setOnClickListener(onClick);
            rowView.setTag(position);
        }

        if (editing)
            setEditing(editing);

        return rowView;
    }

    @Override
    public void notifyDataSetChanged() {
        buttons.clear();
        super.notifyDataSetChanged();
    }

    public boolean isEditing () {
        return editing;
    }

    public void setEditing(boolean editing) {
        if (canEdit) {
            this.editing = editing;
            int visibility = (editing) ? View.VISIBLE : View.GONE;
            for (Button button : buttons) {
                button.setOnClickListener(onClick);
                button.setVisibility(visibility);
            }
        }
    }

    public void updateDataSet (String[][] newDataSet) {
//        this.content = newDataSet;
        this.notifyDataSetChanged();
    }
}