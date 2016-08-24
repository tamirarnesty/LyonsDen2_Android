package com.wlmac.lyonsden2_android.resourceActivities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.R;

/**
 * This activity is used for viewing an announcement or an event. The data that should be passed to
 * this activity is: a title with key 'title', description with key 'info', a date&time with key 'date'
 * and a location with key 'location'. The image that would be displayed in this activity must have a
 * Drawable object assigned to the static member <i>image</i>. If image is null (default value) then
 * the components will be resized and repositioned appropriately.
 *
 * @author sketch204
 * @version 1, 2016/08/05
 */
public class InfoActivity extends AppCompatActivity {
    /**
     * The drawable that is used in the ImageView of this Activity.
     * If null, all component will be reposition appropriately
     */
    public static Drawable image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

        Intent intent = getIntent();

        // An instance of the ImageView of this activity
        ImageView imageView = (ImageView) findViewById(R.id.ISImageView);
        imageView.setImageDrawable(image);
        if (imageView.getDrawable() == null) {
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageParams.weight = 0;

            imageView.setLayoutParams(imageParams);
        }

        // An instance of the title TextView of this activity
        TextView titleLabel = (TextView) findViewById(R.id.ISTitleLabel);
        titleLabel.setText(intent.getStringExtra("title"));

        // An instance of the description TextView of this activity
        TextView infoLabel = (TextView) findViewById(R.id.ISDescriptionLabel);
        infoLabel.setText(intent.getStringExtra("info"));

        // An instance of the date&time TextView of this activity
        TextView dateLabel = (TextView) findViewById(R.id.ISDateLabel);

        // 20160906240000
        String dateTime = intent.getStringExtra("date").substring(8, 12);
        dateTime = dateTime.substring(0, 2) + ":" + dateTime.substring(2);
        if (dateTime.equals("24:00")) dateTime = "All day";
        dateLabel.setText(dateTime);

        // An instance of the location TextView of this activity
        TextView locationLabel = (TextView) findViewById(R.id.ISLocationLabel);
        locationLabel.setText(intent.getStringExtra("location"));
    }
}
