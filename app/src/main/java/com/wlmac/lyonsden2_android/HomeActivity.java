package com.wlmac.lyonsden2_android;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    /** Holds the current day's value (1 or 2) */
    private TextView dayLabel;
    /** Declared merely because it must be set to a custom font */
    private TextView todayIsDay;
    /** The custom font used with most TextViews, this is where it should always be called from. */
    public static Typeface hapnaMonoLight;
    /** An array of 4 RelativeLayout each of which represent a period in the timetable*/
    private RelativeLayout[] periods;

    private ListView listView;

    private ArrayList<String> announcements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        dayLabel = (TextView) findViewById(R.id.dayLabel);
        todayIsDay = (TextView) findViewById(R.id.todayIsDay);
        periods = createPeriods();

        listView = (ListView) findViewById(R.id.homeScreenList);
        listView.setMinimumHeight(getScreenSize().y);

        hapnaMonoLight = Typeface.createFromAsset(getAssets(), "fonts/HapnaMono-Light.otf");
        dayLabel.setTypeface(hapnaMonoLight);
        todayIsDay.setTypeface(hapnaMonoLight);

        for (int h = 0; h < 50; h ++) {
            announcements.add("Title " + (h+1));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, announcements);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                segueIntoContact();
            }
        });
    }

    private void segueIntoContact () {
        Intent intent = new Intent (this, ContactActivity.class);
        this.startActivity(intent);
        // For passing data
//      intent.putExtra("key", value); //Optional parameters

    }

    /** Creates the period instances and resizes them to fit on the screen. */
    private RelativeLayout[] createPeriods () {
        // TODO: Must figure out a way to scale font size depending on screen size

        // Declare the periods place holder
        RelativeLayout[] output = {(RelativeLayout) findViewById(R.id.period0), (RelativeLayout) findViewById(R.id.period1),
                                   (RelativeLayout) findViewById(R.id.period2), (RelativeLayout) findViewById(R.id.period3)};
        // Declare the instance of the size of the display
        Point size = getScreenSize();
        // Resize each 'period'
        for (int h = 0; h < output.length; h ++) {
            RelativeLayout.LayoutParams periodSize = (RelativeLayout.LayoutParams) output[h].getLayoutParams();
            periodSize.width = size.x/4;
            output[h].setLayoutParams(periodSize);
        }
        // Return the created periods
        return output;
    }

    public Point getScreenSize () {
        // Declare the display object of the current device
        Display display = getWindowManager().getDefaultDisplay();
        // Declare the instance of the size of the display
        Point size = new Point();
        // Instantiate the size instance
        display.getSize(size);
        return size;
    }
}
