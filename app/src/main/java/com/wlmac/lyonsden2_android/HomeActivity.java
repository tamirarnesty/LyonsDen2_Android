package com.wlmac.lyonsden2_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wlmac.lyonsden2_android.otherClasses.LyonsCalendar;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;
import com.wlmac.lyonsden2_android.otherClasses.WebCalendar;
import com.wlmac.lyonsden2_android.resourceActivities.CourseActvity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
// TODO: IMPLEMENT ANDROID PROGRESS INDICATORS WHERE NEEDED
// TODO: Implement Error codes where necessary

/**
 * The activity that will be used to display the home screen. The home screen consists of a label for
 * today's day, a timetable that highlights the current period and a list of the most recent announcements.
 *
 * @author sketch204
 * @version 1, 2016/07/30
 */
public class HomeActivity extends AppCompatActivity {
    public static String sharedPreferencesName = "com.wlmac.lyonsden2_android";

    /** Holds the current day's value (1 or 2) */
    private TextView dayLabel;
    /** Declared merely because it must be set to a custom font */
    private TextView todayIsDay;
    /** An array of 4 RelativeLayout each of which represent a period in the timetable*/
    private LinearLayout[] periods = new LinearLayout[4];
    /** The ListView that contains the announcements */
    private ListView listView;
    /** The contents of the announcement ListView */
    private ArrayList<String> announcements = new ArrayList<>();
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

//        initializeContent();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Instantiate all UI components
        initializeComponents();
        timeTable = assembleTimeTable();

        final Handler periodUpdater = new Handler();
        periodUpdater.postDelayed(new Runnable(){

            @Override
            public void run() {
                updatePeriods();
            }
        }, 60000);

        Retrieve.drawerSetup(this, drawerList, rootLayout, drawerToggle);

        // Resize the announcement ListView to fit the screen. DOES NOT WORK ATM!!!
        listView.setMinimumHeight(Retrieve.screenSize(this).y);
        // Set the custom font of the TextLabels
        dayLabel.setTypeface(Retrieve.typeface(this));
        todayIsDay.setTypeface(Retrieve.typeface(this));

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

// MARK: LATE START & DAY OF THE DAY RETRIEVAL

        // Retrieve info from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(HomeActivity.sharedPreferencesName, 0);
        String dayDictionary = sharedPreferences.getString(LyonsCalendar.keyDayDictionary, "Dictionary Not Found"),
               lateStartDicitonary = sharedPreferences.getString(LyonsCalendar.keyLateStartDictionary, "Dictionary Not Found");

        // If dictionaries do not exists a.k.a. Calendar has never been opened
        if (dayDictionary.equals("Dictionary Not Found") || lateStartDicitonary.equals("Dictionary Not Found")) {
            // Reload Dictionary
            WebCalendar.downloadInto(new LyonsCalendar(), getApplicationContext(), WebCalendar.actionCacheOnly);

            dayDictionary = sharedPreferences.getString(LyonsCalendar.keyDayDictionary, "Dictionary Not Found");
            lateStartDicitonary = sharedPreferences.getString(LyonsCalendar.keyLateStartDictionary, "Dictionary Not Found");
        }

        // Checks if there is a school day today, will set to appropriate value
        String dayOfTheDay = (Retrieve.dayFromDictionary(dayDictionary, new Date()).equals("-1")) ? "X" : Retrieve.dayFromDictionary(dayDictionary, new Date());
        // True if today is a late start, false otherwise.
        boolean lateStartStatus = Retrieve.isLateStartDay(lateStartDicitonary, new Date()); // This retrieve method has not been tested
    }

//    private void initializeContent () {
//        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(manager);
//
//
//    }

    private void updatePeriods () {
        Calendar lyonsCalendar = Calendar.getInstance();
//        Calendar periodOne = Calendar.getInstance().set(lyonsCalendar.YEAR, lyonsCalendar.MONTH, lyonsCalendar.DAY_OF_WEEK, 08, 45);
//        Calendar periodTwo = Calendar.getInstance().set(lyonsCalendar.YEAR, lyonsCalendar.MONTH, lyonsCalendar.DAY_OF_WEEK, 10, 10);
//        Calendar periodThree = Calendar.getInstance().set(lyonsCalendar.YEAR, lyonsCalendar.MONTH, lyonsCalendar.DAY_OF_WEEK, 12, 30);
//        Calendar periodFour = Calendar.getInstance().set(lyonsCalendar.YEAR, lyonsCalendar.MONTH, lyonsCalendar.DAY_OF_WEEK, 13, 50);

    }

    /** Instantiates all GUI components */
    private void initializeComponents () {
        dayLabel = (TextView) findViewById(R.id.HSDayLabel);
        todayIsDay = (TextView) findViewById(R.id.HSTodayIsDay);
        listView = (ListView) findViewById(R.id.HSList);
        rootLayout = (DrawerLayout) findViewById(R.id.HDLayout);
        drawerList = (ListView) findViewById(R.id.HDList);
        drawerToggle = Retrieve.drawerToggle(this, rootLayout);

        periods[0] = (LinearLayout) findViewById(R.id.HSPeriod0);
        periods[1] = (LinearLayout) findViewById(R.id.HSPeriod1);
        periods[2] = (LinearLayout) findViewById(R.id.HSPeriod2);
        periods[3] = (LinearLayout) findViewById(R.id.HSPeriod3);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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