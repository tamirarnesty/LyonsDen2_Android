package com.wlmac.lyonsden2_android.resourceActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.R;

// TODO: FIX TIME FORMATTING

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        Intent intent = getIntent();

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
