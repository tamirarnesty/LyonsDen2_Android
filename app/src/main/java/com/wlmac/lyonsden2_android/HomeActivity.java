package com.wlmac.lyonsden2_android;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.Visibility;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.wlmac.lyonsden2_android.lyonsLists.ClubList;
import com.wlmac.lyonsden2_android.lyonsLists.EventList;
import com.wlmac.lyonsden2_android.otherClasses.CourseDialog;
import com.wlmac.lyonsden2_android.otherClasses.ObservableScrollView;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;
import com.wlmac.lyonsden2_android.resourceActivities.CourseActvity;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
// TODO: IMPLEMENT ANDROID PROGRESS INDICATORS WHERE NEEDED

/**
 * The activity that will be used to display the home screen. The home screen consists of a label for
 * today's day, a timetable that highlights the current period and a list of the most recent announcements.
 *
 * @author sketch204
 * @implemented Ademir Gotov
 * @version 1, 2016/07/30
 */
public class HomeActivity extends AppCompatActivity {



    public static String sharedPreferencesName = "com.wlmac.lyonsden2_android";
    /** Holds the current day's value (1 or 2) */
    private TextView dayLabel;
    /** Declared merely because it must be set to a custom font */
    private TextView todayIsDay;
    /** An array of 4 RelativeLayout each of which represent a period in the timetable*/
    private RelativeLayout[] periods = new RelativeLayout[4];
    /** The ListView that contains the announcements */
    private ListView listView;
    /** The contents of the announcement ListView */
    private ArrayList<String> announcements = new ArrayList<>();
    /** A list of item that will be displayed in the every drawer list of this program. */
    private static String[] drawerContent = {"Home", "Calendar", "Announcements", "Clubs", "Contact", "Me"};  // Just trying things out :)
    /** An instance of the root layout of this activity. */
    private DrawerLayout rootLayout;
    /** An instance of the ListView used in this activity's navigation drawer. */
    private ListView drawerList;
    /** The drawer toggler used this activity. */
    private ActionBarDrawerToggle drawerToggle;
    //containers for courses
    private RelativeLayout [] containers = new RelativeLayout[4];


    private String[][] timeTable;


    //private View holderView;
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
        initializeTimeTable();
        updatePeriods();

        final Handler periodUpdater = new Handler();
        periodUpdater.postDelayed(new Runnable(){

            @Override
            public void run() {
                updatePeriods();
            }
        }, 60000);
        setupDrawer(this, drawerList, rootLayout, drawerToggle);

        // Resize the announcement ListView to fit the screen. DOES NOT WORK ATM!!!
        listView.setMinimumHeight(Retrieve.screenSize(this).y);
        // Set the custom font of the TextLabels
        dayLabel.setTypeface(Retrieve.typeface(this));
        todayIsDay.setTypeface(Retrieve.typeface(this));

        // TEMPORARY!!!
        for (int h = 0; h < 50; h++) {
            announcements.add("Title " + (h+1));
        }
        // Declare and set the ArrayAdapter for filling the ListView with content
        //          Type of content                      |Source|Type of ListView layout            | Data source array
        //                                               |Object|                                   |
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, announcements);
        listView.setAdapter(adapter);


        // Declare a listener for whenever an item has been clicked in the ListVew
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override//             |The ListView        |The item  |The item's   |The item's
            //                      |                    |clicked   |position     |ID
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(parent.getContext(), "This is wrong", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(parent.getContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //    private void initializeContent () {
//        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(manager);
//
//
//    }

    public void updatePeriods () {
        timeTable = assembleTimeTable();
        //Calendar lyonsCalendar = Calendar.getInstance();
//        Calendar periodOne = Calendar.getInstance().set(lyonsCalendar.YEAR, lyonsCalendar.MONTH, lyonsCalendar.DAY_OF_WEEK, 08, 45);
//        Calendar periodTwo = Calendar.getInstance().set(lyonsCalendar.YEAR, lyonsCalendar.MONTH, lyonsCalendar.DAY_OF_WEEK, 10, 10);
//        Calendar periodThree = Calendar.getInstance().set(lyonsCalendar.YEAR, lyonsCalendar.MONTH, lyonsCalendar.DAY_OF_WEEK, 12, 30);
//        Calendar periodFour = Calendar.getInstance().set(lyonsCalendar.YEAR, lyonsCalendar.MONTH, lyonsCalendar.DAY_OF_WEEK, 13, 50);

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
                ((TextView) view.findViewById(android.R.id.text1)).setTextColor(parent.getResources().getColor(R.color.whiteText));
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
        } else if (activity == 2) {
            target = EventList.class;
        } else if ( activity == 3) {
            target = ClubList.class;
        } else if (activity == 4) {
            target = ContactActivity.class;
        } else if (activity == 5) {
            target = UserActivity.class;
        }
        Intent intent = new Intent (initiator, target);
        initiator.startActivity(intent);
    }

    /** Instantiates all GUI components */
    private void initializeComponents () {
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

    private void initializeTimeTable() {
        int [] tempContainer = {R.id.HSPeriod0, R.id.HSPeriod1, R.id.HSPeriod2, R.id.HSPeriod3};
        for (int i = 0; i < containers.length; i++) {
            containers[i] = (RelativeLayout) findViewById(tempContainer[i]);
            containers[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int i = Integer.parseInt(v.getTag().toString());
                    //Toast.makeText(getApplicationContext(), "_" + i + "_", Toast.LENGTH_SHORT).show();
                    periodClicked(i + 1, timeTable[i][0], timeTable[i][1], timeTable[i][2], timeTable[i][3]);
                    return true;
                }
            });
        }
    }

    // You will probably want to change this to set the time table to whatever it retrieved from permanent storage
    public String[][] assembleTimeTable() {

        // An instance of the time table that will be returned and assigned to a global variable
        String[][] timeTable = new String[4][4];
        // A bank of IDs each refering to an individual piece of the timetable
        int[][] idBank = {{R.id.HSCourseName0, R.id.HSCourseCode0, R.id.HSTeacherName0, R.id.HSRoomNumber0},    // Period 1
                          {R.id.HSCourseName1, R.id.HSCourseCode1, R.id.HSTeacherName1, R.id.HSRoomNumber1},    // Period 2
                          {R.id.HSCourseName2, R.id.HSCourseCode2, R.id.HSTeacherName2, R.id.HSRoomNumber2},    // Period 3
                          {R.id.HSCourseName3, R.id.HSCourseCode3, R.id.HSTeacherName3, R.id.HSRoomNumber3}};   // Period 4
        int[] spares = {R.id.HSSpare0, R.id.HSSpare1, R.id.HSSpare2, R.id.HSSpare3};
        // A nested loop that will fill up the timetable
        SharedPreferences pref = getSharedPreferences(HomeActivity.sharedPreferencesName, 0);
        for (int h = 0; h < timeTable.length; h ++) {
            boolean check = pref.getBoolean("Period " + (h+1), false);
            if (check == false) {
                for (int j = 0; j < timeTable[h].length; j++) {
                    // A instance of a timetable peice (made without assigning to a variable)
                    //                                                       You might use set text here to set to data from

                    //                                                       permanent storage
                    String s;
                    switch (j) {
                        case 0:
                            s = "Course Name";
                            break;
                        case 1:
                            s = "Course Code";
                            break;
                        case 2:
                            s = "Teacher Name";
                            break;
                        case 3:
                            s = "Room Number";
                            break;
                        default:
                            s = "Incorrect value";
                    }
                    if (findViewById(spares[h]).getVisibility() == View.VISIBLE) {
                        (findViewById(spares[h])).setVisibility(View.INVISIBLE);
                    }
                    (findViewById(idBank[h][j])).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(idBank[h][j])).setText(pref.getString("Period " + (h + 1) + " " + j, s));
                    timeTable[h][j] = ((TextView) findViewById(idBank[h][j])).getText().toString();
                    //toString because it return an Editable type
                }
            } else {
            //    if (findViewById(spares[h]).getVisibility() == View.INVISIBLE) {
                    for (int j = 0; j < timeTable[h].length; j++) {
                        String s;
                        switch (j) {
                            case 0:
                                s = "Course Name";
                                break;
                            case 1:
                                s = "Course Code";
                                break;
                            case 2:
                                s = "Teacher Name";
                                break;
                            case 3:
                                s = "Room Number";
                                break;
                            default:
                                s = "Incorrect value";
                        }
                        (findViewById(idBank[h][j])).setVisibility(View.INVISIBLE);
                        ((TextView) findViewById(idBank[h][j])).setText(pref.getString("Period " + (h + 1) + " " + j, s));
                        timeTable[h][j] = ((TextView) findViewById(idBank[h][j])).getText().toString();
                    }
                (findViewById(spares[h])).setVisibility(View.VISIBLE);
                //    }
            }
        }
        return timeTable;
        // P.S. I have no clue how permanent storage works in android :)
    }

    public void periodClicked (int index, String name, String code, String teacher, String room) {
        CourseDialog courseDialog = new CourseDialog();
        courseDialog.setPeriod("Period " + index, name, code, teacher, room);
        courseDialog.show(getFragmentManager(), "");

        /*Intent intent = new Intent (this, CourseActvity.class);

        // Each view container has a tag representing its index
        String[] periodData = new String[5];
        periodData[0] = "" + (index + 1);
        for (int h = 0; h < timeTable[index].length; h++)
            periodData[h+1] = timeTable[index][h];

        intent.putExtra("periodData", periodData);
        startActivity(intent);
    */}

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


/*

        //THIS IS TEST NOT WORKING YET AND SHOULD BE IN onCreate method

        holderView = findViewById(R.id.topViews);

        final View listHeader = getLayoutInflater().inflate(R.layout.list_header, null);
        listView.addHeaderView(listHeader);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,int totalItemCount)
            {
                  if (listView.getFirstVisiblePosition() == 0) {
                    View firstChild = listView.getChildAt(0);
                    int topY = 0;
                    if (firstChild != null) {
                        topY = firstChild.getTop();
                    }
                    holderView.setY(topY * 0.5f);
                }
            }
        });
        //LIKE YOU KNOW!

*/