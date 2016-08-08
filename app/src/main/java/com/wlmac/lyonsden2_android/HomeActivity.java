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
    /** The custom font used with most TextViews, this is where it should always be accessed from. */
    public static Typeface hapnaMonoLight;
    /** An array of 4 RelativeLayout each of which represent a period in the timetable*/
    private RelativeLayout[] periods;
    /** The ListView that contains the announcements */
    private ListView listView;
    /** The contents of the announcement ListView */
    private ArrayList<String> announcements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Super call
        super.onCreate(savedInstanceState);
        // Declare the associated xml layout file
        setContentView(R.layout.home_activity);
        // Instantiate all UI components
        this.instantiateComponents();
        // Create and resize each period on the timetable to fit the screen
        periods = createPeriods();
        // Resize the announcement ListView to fit the screen. DOES NOT WORK ATM!!!
        listView.setMinimumHeight(getScreenSize().y);
        // Set the custom font of the TextLabels
        dayLabel.setTypeface(hapnaMonoLight);
        todayIsDay.setTypeface(hapnaMonoLight);

        // TEMPORARY!!!
        for (int h = 0; h < 50; h ++) {
            announcements.add("Title " + (h+1));
        }
        // Declare and set the ArrayAdapter for filling the ListView with content
        //          Type of content                      |Source|Type of ListView layout            | Data source array
        //                                               |Object|                                   |
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, announcements);
        listView.setAdapter(adapter);

        // Declare a listener for whenever an item has been clicked in the ListVew
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override//             |The ListView        |The item  |The item's   |The item's
            //                      |                    |clicked   |position     |ID
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                segueIntoContact();
            }
        });
    }

    private void instantiateComponents () {
        dayLabel = (TextView) findViewById(R.id.HSDayLabel);
        todayIsDay = (TextView) findViewById(R.id.HSTodayIsDay);
        listView = (ListView) findViewById(R.id.HSList);
        hapnaMonoLight = Typeface.createFromAsset(getAssets(), "fonts/HapnaMono-Light.otf");
    }

    private void segueIntoContact () {
        Intent intent = new Intent (this, ContactActivity.class);
        this.startActivity(intent);
        // For passing data
//      intent.putExtra("key", value); //Optional parameters
    }

    /**
     * Creates the period instances and resizes them to fit on the screen.
     * @return An array of RelativeLayout objects representing periods on the timetable.
     */
    private RelativeLayout[] createPeriods () {
        // TODO: Must figure out a way to scale font size depending on screen size

        // Declare the periods place holder
        RelativeLayout[] output = {(RelativeLayout) findViewById(R.id.HSPeriod0), (RelativeLayout) findViewById(R.id.HSPeriod1),
                                   (RelativeLayout) findViewById(R.id.HSPeriod2), (RelativeLayout) findViewById(R.id.HSPeriod3)};
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
