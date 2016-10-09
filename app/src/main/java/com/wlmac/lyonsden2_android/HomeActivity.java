package com.wlmac.lyonsden2_android;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wlmac.lyonsden2_android.resourceActivities.CourseActvity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

// TODO: IMPLEMENT ANDROID PROGRESS INDICATORS WHERE NEEDED

/**
 * The activity that will be used to display the home screen. The home screen consists of a label for
 * today's day, a timetable that highlights the current period and a list of the most recent announcements.
 *
 * @author sketch204
 * @version 1, 2016/07/30
 */
public class HomeActivity extends AppCompatActivity {
    public static String sharedPreferencesName = "LyonsPrefs";

    /** Holds the current day's value (1 or 2) */
    private TextView dayLabel;
    /** Declared merely because it must be set to a custom font */
    private TextView todayIsDay;
    /** The custom font used with most TextViews, this is where it should always be accessed from. */
    public static Typeface hapnaMonoLight;
    /** An array of 4 RelativeLayout each of which represent a period in the timetable*/
    private RelativeLayout[] periods = new RelativeLayout[4];
    /** The ListView that contains the announcements */
    private ListView listView;
    /** The contents of the announcement ListView */
    private ArrayList<String> announcements = new ArrayList<>();
    /** A list of item that will be displayed in the every drawer list of this program. */
    private static String[] drawerContent = {"Home", "Calendar", "Announcements", "Events", "Clubs", "Contact", "Me"};  // Just trying things out :)
    /** An instance of the root layout of this activity. */
    private DrawerLayout rootLayout;
    /** An instance of the ListView used in this activity's navigation drawer. */
    private ListView drawerList;
    /** The drawer toggler used this activity. */
    private ActionBarDrawerToggle drawerToggle;

    private String[][] timeTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Super call
        super.onCreate(savedInstanceState);
        // Declare the associated xml layout file
        setContentView(R.layout.home_activity);
        // Instantiate all UI components
        initializeComponents();
        timeTable = assembleTimeTable();

        setupDrawer(this, drawerList, rootLayout, drawerToggle);

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
        drawerList.setAdapter(new ArrayAdapter<String>(initiator, android.R.layout.simple_selectable_list_item, HomeActivity.drawerContent) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);
                ((TextView) view.findViewById(android.R.id.text1)).setTextColor(parent.getResources().getColor(R.color.accentColor));
                return view;
            }
        });
        // Set the drawer list's item click listener
        drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

    public static void performDrawerSegue (Context initiator, int activity) {
        Class target = null;
        if (activity == 0) {
            target = HomeActivity.class;
        } else if (activity == 1) {
            target = CalendarActivity.class;
        } else if (activity == 2 || activity == 3 || activity == 4) {
            ListActivity.displayContent = activity - 1;
            target = ListActivity.class;
        } else if (activity == 5) {
            target = ContactActivity.class;
        } else if (activity == 6) {
            target = UserActivity.class;
        }
        Intent intent = new Intent (initiator, target);
        initiator.startActivity(intent);
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

        periods[0] = (RelativeLayout) findViewById(R.id.HSPeriod0);
        periods[1] = (RelativeLayout) findViewById(R.id.HSPeriod1);
        periods[2] = (RelativeLayout) findViewById(R.id.HSPeriod2);
        periods[3] = (RelativeLayout) findViewById(R.id.HSPeriod3);
    }

    // You will probably want to change this to set the time table to whatever it retrieved from permanent storage
    private String[][] assembleTimeTable() {
        // An instance of the time table that will be returned and assigned to a global variable
        String[][] timeTable = new String[4][4];
        // A bank of IDs each refering to an individual piece of the timetable
        int[][] idBank = {{R.id.HSCourseCode0, R.id.HSCourseName0, R.id.HSTeacherName0, R.id.HSRoomNumber0},    // Period 1
                          {R.id.HSCourseCode1, R.id.HSCourseName1, R.id.HSTeacherName1, R.id.HSRoomNumber1},    // Period 2
                          {R.id.HSCourseCode2, R.id.HSCourseName2, R.id.HSTeacherName2, R.id.HSRoomNumber2},    // Period 3
                          {R.id.HSCourseCode3, R.id.HSCourseName3, R.id.HSTeacherName3, R.id.HSRoomNumber3}};   // Period 4
        // A nested loop that will fill up the timetable
        for (int h = 0; h < timeTable.length; h ++) {
            for (int j = 0; j < timeTable[h].length; j ++) {
                                // A instance of a timetable peice (made without assigning to a variable)
                //                                                       You might use set text here to set to data from
                //                                                       permanent storage
                timeTable[h][j] = ((TextView) findViewById(idBank[h][j])).getText().toString();
                //                                                                  toString because it return an Editable type
            }
        }
        return timeTable;
        // P.S. I have no clue how permanent storage works in android :)
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

    public void periodClicked (View view) {
        Intent intent = new Intent (this, CourseActvity.class);

        // Each view container has a tag representing its index
        int index = Integer.parseInt(view.getTag().toString());
        String[] periodData = new String[5];
        periodData[0] = "" + (index + 1);
        for (int h = 0; h < timeTable[index].length; h++)
            periodData[h+1] = timeTable[index][h];

        intent.putExtra("periodData", periodData);
        startActivity(intent);
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