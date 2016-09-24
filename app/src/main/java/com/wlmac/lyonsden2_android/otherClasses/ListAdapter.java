package com.wlmac.lyonsden2_android.otherClasses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;
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
    /**
     * An ArrayList of titles to be displayed in the list.
     * Must be indexed appropriately with first item of the list going top to bottom, being at index 0. */
    private final ArrayList<String> titles;
    /**
     * An ArrayList of subtitles to be displayed in the list.
     * Must be indexed appropriately with first item of the list going top to bottom, being at index 0. */
    private final ArrayList<String> infos;
    /**
     * An ArrayList of Drawables to be inserted into the list.
     * Must be indexed appropriately with first item of the list going top to bottom, being at index 0. */
    private final ArrayList<Drawable> images;

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

    // TODO: FIGURE OUT WHY CANT CALL CONSTRUCTOR FROM ANOTHER CONSTRUCTOR

    /**
     * The default constructor of this List adapter, creates the adapter and initializes all of
     * its GUI components.
     * @param context The context that this adapter's list belongs to.
     * @param titles The ArrayList which will contain the title for each cell.
     * @param infos The ArrayList which will contain the subtitle for each cell.
     * @param images The ArrayList which will contain the image for each cell.
     * @param isLarge If true, then the height for each cell will be @dimen/largeCellSize, otherwise it will be @dimen/defaultCellSize.
     */
    public ListAdapter (Context context, ArrayList<String> titles, ArrayList<String> infos, @Nullable ArrayList<Drawable> images, boolean isLarge) {
        super (context, -1, titles);    // Super call
        this.context = context;
        this.titles = titles;
        this.infos = infos;
        this.images = images;
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
    public ListAdapter (Context context, ArrayList<String> titles, ArrayList<String> infos, @Nullable ArrayList<Drawable> images, boolean isLarge, View.OnClickListener onClick) {
        super (context, -1, titles);    // Super call
        this.context = context;
        this.titles = titles;
        this.infos = infos;
        this.images = images;
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
        ((TextView) rowView.findViewById(titleID)).setText(titles.get(position));
        ((TextView) rowView.findViewById(infoID)).setText(infos.get(position));

        if (images != null && images.get(position) != null) {   // If there is an image, then
            ((ImageView) rowView.findViewById(imageID)).setImageDrawable(images.get(position));     // Set the image
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
}