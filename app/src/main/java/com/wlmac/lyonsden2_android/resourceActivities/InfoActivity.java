package com.wlmac.lyonsden2_android.resourceActivities;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.R;

public class InfoActivity extends AppCompatActivity {
    public static Drawable image = null;
    private ImageView imageView;
    private TextView titleLabel;
    private TextView infoLabel;
    private TextView dateLabel;
    private TextView locationLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

        initializeComponents();
        parseIntentData();
        resizeComponents();
    }

    private void initializeComponents () {
        imageView = (ImageView) findViewById(R.id.ISImageView);
        titleLabel = (TextView) findViewById(R.id.ISTitleLabel);
        infoLabel = (TextView) findViewById(R.id.ISDescriptionLabel);
        dateLabel = (TextView) findViewById(R.id.ISDateLabel);
        locationLabel = (TextView) findViewById(R.id.ISLocationLabel);
    }

    private void parseIntentData () {
        Intent intent = getIntent();
        imageView.setImageDrawable(image);
        titleLabel.setText(intent.getStringExtra("title"));
        infoLabel.setText(intent.getStringExtra("info"));
        dateLabel.setText(intent.getStringExtra("date"));
        locationLabel.setText(intent.getStringExtra("location"));
    }

    private void resizeComponents () {
        Point size = getScreenSize();
        RelativeLayout contentView = (RelativeLayout) findViewById(R.id.ISContentView);

        if (imageView.getDrawable() == null) {
            ViewGroup.LayoutParams titleSize = titleLabel.getLayoutParams();
            titleSize.width = (size.x/2) - contentView.getPaddingLeft() - contentView.getPaddingRight();
            titleSize.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            titleLabel.setLayoutParams(titleSize);
        }

        ViewGroup.LayoutParams dateSize = dateLabel.getLayoutParams();
        ViewGroup.LayoutParams locationSize = locationLabel.getLayoutParams();

        dateSize.width = (size.x/2) - contentView.getPaddingLeft() - 4;
        locationSize.width = (size.x/2) - contentView.getPaddingRight() - 4;

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
