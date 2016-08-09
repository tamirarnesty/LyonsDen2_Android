package com.wlmac.lyonsden2_android;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * The activity that will be used to display the home screen. The home screen consists of a label for
 * today's day, a timetable that highlights the current period and a list of the most recent announcements.
 *
 * @author sketch204
 * @version 1, 2016/07/30
 */
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
    /** A list of item that will be displayed in the every drawer list of this program. */
    private static String[] drawerContent = {"Home", "Announcements", "Calendar", "Clubs", "Events", "Contact"};
    /** An instance of the root layout of this activity. */
    private DrawerLayout rootLayout;
    /** An instance of the ListView used in this activity's navigation drawer. */
    private ListView drawerList;
    /** The drawer toggler used this activity. */
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Super call
        super.onCreate(savedInstanceState);
        // Declare the associated xml layout file
        setContentView(R.layout.home_activity);
        // Instantiate all UI components
        initializeComponents();

        setupDrawer(this, drawerList, rootLayout, drawerToggle);

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
                Toast.makeText(parent.getContext(), "BAH BAH", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * The method used for a quick and easy setup of a default drawer in the current view.
     * @param initiator The activity that is calling this method. ('this' arguement will work most of the time)
     * @param drawerList The drawer list that is bind to the initiating activity.
     * @param rootLayout The root layout of the initiating activity.
     * @param drawerToggle The drawer toggle of the initiating activity.
     */
    public static void setupDrawer (AppCompatActivity initiator, ListView drawerList, DrawerLayout rootLayout, ActionBarDrawerToggle drawerToggle) {
        // Declare the drawer list adapter, to fill the drawer list
        drawerList.setAdapter(new ArrayAdapter<String>(initiator, android.R.layout.simple_selectable_list_item, HomeActivity.drawerContent));
        // Set the drawer list's item click listener
        drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 1)  // If its not 'Announcements' that is selected then
                    HomeActivity.performDrawerSegue(parent.getContext(), position); // Segue into the appropriate Activity
            }
        });
        // Display the drawer indicator
        initiator.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initiator.getSupportActionBar().setHomeButtonEnabled(true);
        // Enable the drawer indicator
        drawerToggle.setDrawerIndicatorEnabled(true);
        // Add the drawer toggler to the current layout
        rootLayout.addDrawerListener(drawerToggle);
    }

    /**
     * Returns a fully setup ActionBarDrawerToggle object, redy for use with a drawer.
     * @param initiator The activity that is calling this method. ('this' arguement will work most of the time)
     * @param rootLayout The root layout of the initiating activity.
     */
    public static ActionBarDrawerToggle initializeDrawerToggle (AppCompatActivity initiator, DrawerLayout rootLayout) {
        final AppCompatActivity finalInitiator = initiator;
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(finalInitiator, rootLayout, R.string.drawerOpen, R.string.drawerClose) {
            @Override
            public void onDrawerClosed(View drawerView) {   // When the drawer is closed
                super.onDrawerClosed(drawerView);           // Super Call
                finalInitiator.getSupportActionBar().setTitle(finalInitiator.getTitle()); // Set the app title to the drawer's title
                finalInitiator.invalidateOptionsMenu();                    // State that the drawer should be redrawn
            }

            @Override
            public void onDrawerOpened(View drawerView) {   // When the drawer is opened
                super.onDrawerOpened(drawerView);           // Super call
                finalInitiator.getSupportActionBar().setTitle("Menu");     // Set the app title to the drawer's title
                finalInitiator.invalidateOptionsMenu();                    // State that the drawer should be redrawn
            }
        };
        return  drawerToggle;
    }

    /** Instantiates all GUI components */
    private void initializeComponents () {
        hapnaMonoLight = Typeface.createFromAsset(getAssets(), "fonts/HapnaMono-Light.otf");
        dayLabel = (TextView) findViewById(R.id.HSDayLabel);
        todayIsDay = (TextView) findViewById(R.id.HSTodayIsDay);
        listView = (ListView) findViewById(R.id.HSList);
        rootLayout = (DrawerLayout) findViewById(R.id.HDLayout);
        drawerList = (ListView) findViewById(R.id.HDList);
        drawerToggle = initializeDrawerToggle(this, rootLayout);
    }

    public static void performDrawerSegue (Context initiator, int activity) {
        Class target = null;
        if (activity == 0) {
            target = HomeActivity.class;
        } else if (activity == 2) {
            target = CalendarActivity.class;
        } else if (activity == 3) {
            ListActivity.showingClubs = true;
            target = ListActivity.class;
        } else if (activity == 4) {
            ListActivity.showingClubs = false;
            target = ListActivity.class;
        } else if (activity == 5) {
            target = ContactActivity.class;
        }
        Intent intent = new Intent (initiator, target);
        initiator.startActivity(intent);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
}