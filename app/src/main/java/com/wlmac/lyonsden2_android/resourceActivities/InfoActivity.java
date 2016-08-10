package com.wlmac.lyonsden2_android.resourceActivities;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.R;

import java.util.Arrays;

/** This activity is used for viewing an announcement or an event.
 * The data that should be passed to this activity is: a title with key 'title',
 * description with key 'info', a date&time with key 'date' and a locaiton with
 * key 'location'. The*/
public class InfoActivity extends AppCompatActivity {
    /**
     * The drawable that is used in the ImageView of this Activity.
     * If null, all component will be reposition appropriately
     */
    public static Drawable image = null;
    /** An instance of the ImageView of this activity. */
    private ImageView imageView;
    /** An instance of the title TextView of this activity. */
    private TextView titleLabel;
    /** An instance of the description TextView of this activity. */
    private TextView infoLabel;
    /** An instance of the date&time TextView of this activity. */
    private TextView dateLabel;
    /** An instance of the location TextView of this activity. */
    private TextView locationLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);
        initializeComponents();
        parseIntentData();
        resizeComponents();
    }

    /** Initializes all the GUI components in this activity. */
    private void initializeComponents () {
        imageView = (ImageView) findViewById(R.id.ISImageView);
        titleLabel = (TextView) findViewById(R.id.ISTitleLabel);
        infoLabel = (TextView) findViewById(R.id.ISDescriptionLabel);
        dateLabel = (TextView) findViewById(R.id.ISDateLabel);
        locationLabel = (TextView) findViewById(R.id.ISLocationLabel);
    }

    /** Retrieves and assigns all the information that was passed to this activity. */
    private void parseIntentData () {
        Intent intent = getIntent();
        imageView.setImageDrawable(image);
        titleLabel.setText(intent.getStringExtra("title"));
        infoLabel.setText(intent.getStringExtra("info"));
        dateLabel.setText(intent.getStringExtra("date"));
        locationLabel.setText(intent.getStringExtra("location"));
    }

    /** Re-sizes and repositions the GUI component as needed. */
    private void resizeComponents () {
        // Create an instance of a screen size
        Point size = getScreenSize();
        // Declare the content view of this activity
        RelativeLayout contentView = (RelativeLayout) findViewById(R.id.ISContentView);
        // If there is no image to display
        if (imageView.getDrawable() == null) {
            imageView.setEnabled(false);
            RelativeLayout.LayoutParams titleSize = (RelativeLayout.LayoutParams) titleLabel.getLayoutParams();    // Create an instance of the titleLabel attributes size
            titleSize.width = RelativeLayout.LayoutParams.MATCH_PARENT;    // Resize its width
            titleSize.height = RelativeLayout.LayoutParams.WRAP_CONTENT;     // Resize its height
            titleSize.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            Log.i ("Rules", "" + titleSize.debug("Rules and what not"));
            titleLabel.setLayoutParams(titleSize);                      // Apply the new size attributes
        }
        // Create an instance of the dateLabel's and locationLabel's size attributes
        ViewGroup.LayoutParams dateSize = dateLabel.getLayoutParams();
        ViewGroup.LayoutParams locationSize = locationLabel.getLayoutParams();
        // Resize them to be equal
        dateSize.width = (size.x/2) - contentView.getPaddingLeft() - 4;
        locationSize.width = (size.x/2) - contentView.getPaddingRight() - 4;
        // Apply the new size attributes
        dateLabel.setLayoutParams(dateSize);
        locationLabel.setLayoutParams(locationSize);
    }

    /**
     * Retrieves the current screen size from the system and returns it as a Point.
     * @return A Point representing the current screen size.
     */
    private Point getScreenSize () {
        // Declare the display object of the current device
        Display display = getWindowManager().getDefaultDisplay();
        // Declare the instance of the size of the display
        Point size = new Point();
        // Instantiate the size instance
        display.getSize(size);
        return size;
    }
}
