package com.wlmac.lyonsden2_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;
import com.wlmac.lyonsden2_android.lyonsLists.ListAdapter;
import com.wlmac.lyonsden2_android.otherClasses.CourseDialog;
import com.wlmac.lyonsden2_android.otherClasses.LyonsAlert;
import com.wlmac.lyonsden2_android.otherClasses.LyonsCalendar;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;
import com.wlmac.lyonsden2_android.otherClasses.WebCalendar;
import com.wlmac.lyonsden2_android.resourceActivities.GuideActivity;
import com.wlmac.lyonsden2_android.resourceActivities.InfoActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * The activity that will be used to display the home screen. The home screen consists of a label for
 * today's day, a timetable that highlights the current period and a list of the most recent announcements.
 *wh
 * @author Ademir Gotov
 * @version 1, 2016/07/30
 */
public class HomeActivity extends AppCompatActivity {
    public static boolean didUpdateDataset = false;
    private static int timeTrackerMessageCode = 1;

    /** Holds the current day's value (1 or 2) */
    private TextView dayLabel;
    /** Declared merely because it must be set to a custom font */
    private TextView todayIsDay;
    /** An array of 4 RelativeLayout each of which represent a period in the timetable*/
    private RelativeLayout[] periods = new RelativeLayout[4];
    /** The ListView that contains the announcements */
    private ListView listView;
    /** The contents of the announcement ListView */
    private ArrayList<String[]> announcements = new ArrayList<>();
    /** An instance of the root layout of this activity. */
    private DrawerLayout rootLayout;
    /** An instance of the ListView used in this activity's navigation drawer. */
    private ListView drawerList;
    /** The drawer toggle used this activity. */
    private ActionBarDrawerToggle drawerToggle;
    private TextView lateStartLabel;

    private SwipeRefreshLayout refreshLayout;

    Handler timeTracker;

    private TextView noInternet;
    private boolean isLateStart;
    private boolean isSpecSchedule = false;
    private ListAdapter adapter;
    SharedPreferences sharedPreferences;

// MARK: Parallax Fields
    /** A reference to the instance of a container that contains the Day label + Timetable as its child views. */
    private RelativeLayout topViews;
    private Toolbar toolbar;
    private boolean didCalculateSpacer = false;
    private int topViewsHeight = 0;
    private int lastOffset = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Super call
        super.onCreate(savedInstanceState);
        // Declare the associated xml layout file
        setContentView(R.layout.home_activity);

        Retrieve.oneSignalStatus();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Instantiate all UI components
        initializeComponents();
        setFonts();

        // Setup drawer
        Retrieve.drawerSetup(this, drawerList, rootLayout, drawerToggle);

        sharedPreferences = this.getSharedPreferences(LyonsDen.keySharedPreferences, Context.MODE_PRIVATE); // Initialize share preferences

        // Set day of the day
        if (getSharedPreferences(LyonsDen.keySharedPreferences, 0).getString(LyonsCalendar.keyDayDictionary, null) != null)
            updateDay();
        else if (Retrieve.isInternetAvailable(this)) {
            // Download cal and try again
            Log.d("Home", "Commencing Calendar Caching!");
            WebCalendar.downloadInto(new LyonsCalendar(), this, WebCalendar.actionCacheOnly);
            Log.d("Home", "Download Initiated!");
            updateDay();
            Log.d("Home", "Updated Day Label!");
        } else {
            dayLabel.setText("X");
            todayIsDay.setText("No Internet Available");
        }

        if (dayLabel.getText().toString().equals("X")) {
            Toast.makeText(getApplicationContext(), "No day available.\nThere is no school today.\nIt may be a weekend.", Toast.LENGTH_LONG).show();
        }

        isLateStart = Retrieve.isLateStartDay(getSharedPreferences(LyonsDen.keySharedPreferences, 0).getString(LyonsCalendar.keyLateStartDictionary, ""), new Date());

        if (isLateStart) lateStartLabel.setVisibility(View.VISIBLE);

        initiateTimeTableSetup();

        // Declare a listener for whenever an item has been clicked in the ListVew
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override//             |The ListView        |The item  |The item's   |The item's
            //                      |                    |clicked   |position     |ID
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Home", "You clicked on item " + (position - 1));

                Intent intent = new Intent(HomeActivity.this, InfoActivity.class);
                String[] list = announcements.get(position - 1);
                intent.putExtra("tag", "announcement");
                intent.putExtra("initiator", "home");
                intent.putExtra("announcement", list);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (didCalculateSpacer && listView.getChildAt(0) != null) {
                    Log.d("Parallax!!!!!!", "      ");
                    int childTopY = Math.abs(listView.getChildAt(0).getTop()) + (firstVisibleItem * listView.getChildAt(0).getHeight());
                    if (firstVisibleItem != 0) {
                        childTopY += topViewsHeight + getSupportActionBar().getHeight();
                    }
                    int deltaOffset = lastOffset - childTopY;

                    // I know it is a very long condition :( In short its
                    //  (((movingUp)        && (canStillMoveTopViewsUp)) ||
                    if ((((deltaOffset < 0) && (Math.abs(toolbar.getY()) < (topViewsHeight + getSupportActionBar().getHeight()))) ||
                    //  ((movingDown)      && (canStillMoveTopViewsDown))) &&
                        ((deltaOffset > 0) && (toolbar.getY() + (topViews.getY() - getSupportActionBar().getHeight()) <= 0))) &&
                    //  (listViewHasChildren)
                        (listView.getChildAt(0) != null)) {

                        toolbar.setY(-childTopY * 0.5f);
                        topViews.setY((-childTopY * 0.5f) + getSupportActionBar().getHeight());
                    }

                    lastOffset = childTopY;
                }

                if (!didCalculateSpacer && topViews.getHeight() != 0) {
                    didCalculateSpacer = true;
                    calculateSpacer();
                }
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadAnnouncements();
            }
        });
        refreshLayout.setColorSchemeResources(R.color.navigationBar);

        toolbar.bringToFront();
        topViews.bringToFront();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If you must prompt for notifications, prompt!
        if (getIntent().getBooleanExtra(GuideActivity.keyNotif, false)) {
            promptNotification();
        }

        if (didUpdateDataset) {
            loadAnnouncements();
            didUpdateDataset = false;
        }

        if (getScheduleDay() > 0) {
            updateTimeTableUI();
            createTimeTracker();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getScheduleDay() > 0) {
            timeTracker.removeMessages(timeTrackerMessageCode);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void lateStartLabelPressed(View view) {
        LyonsAlert alert = new LyonsAlert();
        alert.setScheduleView(new LyonsAlert.SchedulePopulator() {
            @Override
            public void populateContent(String[] startTimes, String[] endTimes) {
                String[] localSTimes = {"10:00", "11:10", "12:10", "1:00", "2:05"};
                String[] localETimes = {"11:05", "12:10", "1:00", "2:00", "3:05"};
                for (int h = 0; h < localSTimes.length; h ++) {
                    startTimes[h] = localSTimes[h];
                }
                for (int h = 0; h < localETimes.length; h ++) {
                    endTimes[h] = localETimes[h];
                }
            }
        });
        alert.show(getSupportFragmentManager(), "Late start schedule");
    }

    private void calculateSpacer() {
        View listHeader = getLayoutInflater().inflate(R.layout.home_activity_list_header, null);
        View topViewsSpacer = listHeader.findViewById(R.id.SpacerTopViews);
        ViewGroup.LayoutParams params = topViewsSpacer.getLayoutParams();
        params.height = topViews.getHeight();
        topViewsHeight = topViews.getHeight();
        topViewsSpacer.setLayoutParams(params);

        listHeader.setOnClickListener(null);
        listHeader.setClickable(false);

        listView.addHeaderView(listHeader, null, false);

        adapter = new ListAdapter(this, announcements, false, true);
        listView.setAdapter(adapter);

        loadAnnouncements();

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true);
        if (typedValue.resourceId != 0) {
            int offset = (int)getResources().getDimension(typedValue.resourceId);
            refreshLayout.setProgressViewOffset(true, offset + topViewsHeight, offset + topViewsHeight + Retrieve.pxFromDpInt(32, getResources()));
        }
    }

    private void loadAnnouncements() {
        if (Retrieve.isInternetAvailable(HomeActivity.this)) {
            Retrieve.eventData(this, FirebaseDatabase.getInstance().getReference("announcements"), announcements, new Retrieve.ListDataHandler() {
                @Override
                public void handle(ArrayList<String[]> listData) {
                    adapter.notifyDataSetChanged();
                    lastOffset = (listView.getChildAt(0) != null) ? listView.getChildAt(0).getTop() : 0;
                    refreshLayout.setRefreshing(false);
                }
            });
            listView.setVisibility(View.VISIBLE);
            noInternet.setVisibility(View.INVISIBLE);
        } else {
            listView.setVisibility(View.INVISIBLE);
            noInternet.setVisibility(View.VISIBLE);
        }


        // TEMPORARY!!!!!!!
//        for (int h = 0; h < 50; h ++) {
//            String[] tempHold = new String[4];
//            for (int j = 0; j < 4; j ++) {
//                tempHold[j] = "Something " + h;
//            }
//            announcements.add(tempHold);
//        }
    }

    private void updateDay () {
        String day = Retrieve.dayFromDictionary(getSharedPreferences(LyonsDen.keySharedPreferences, 0).getString(LyonsCalendar.keyDayDictionary, ""), new Date());
        if (day.equals("-1"))
            day = "X";
        dayLabel.setText(day);
        sharedPreferences.edit().putString(LyonsDen.dayKey, dayLabel.getText().toString()).apply();
    }

    /** Instantiates all GUI components */
    private void initializeComponents () {
        listView = (ListView) findViewById(R.id.HSList);
        dayLabel = (TextView) findViewById(R.id.HSDayLabel);
        todayIsDay = (TextView) findViewById(R.id.HSTodayIsDay);
        rootLayout = (DrawerLayout) findViewById(R.id.NDLayout);
        drawerList = (ListView) findViewById(R.id.NDList);
        drawerToggle = Retrieve.drawerToggle(this, rootLayout);
        noInternet = (TextView) findViewById(R.id.HSNoInternet);
        lateStartLabel = (TextView) findViewById(R.id.HSLateStartLabel);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.HSListRefresh);

        topViews = (RelativeLayout) findViewById(R.id.HSTopViews);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        periods[0] = (RelativeLayout) findViewById(R.id.HSPeriod0);
        periods[1] = (RelativeLayout) findViewById(R.id.HSPeriod1);
        periods[2] = (RelativeLayout) findViewById(R.id.HSPeriod2);
        periods[3] = (RelativeLayout) findViewById(R.id.HSPeriod3);

        for (int h = 0; h < periods.length; h++) {
            final int curIndex = h;
            periods[h].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    requestTimeTableChange(curIndex);
                    return true;
                }
            });
        }

        resetTimeTableUI();
    }

    private void setFonts() {
        // Set the custom font of the TextLabels
        dayLabel.setTypeface(Retrieve.typeface(this));
        todayIsDay.setTypeface(Retrieve.typeface(this));
    }

    private void promptNotification() {
        final boolean [] notificationsChosen = {false};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Announcement Notifications");
        alertBuilder.setMessage("Do you wish to receive notifications?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OneSignal.setSubscription(true);
                        sharedPreferences.edit().putBoolean(UserActivity.keyNotification, true).apply();
                        dialog.cancel();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OneSignal.setSubscription(false);
                sharedPreferences.edit().putBoolean(UserActivity.keyNotification, false).apply();
                dialog.cancel();
            }
        });
        alertBuilder.create().show();
    }

    public int getScheduleDay() {
        try {
            return Integer.parseInt(dayLabel.getText().toString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void displayHelpDialog () {
        final LyonsAlert alert = new LyonsAlert();
        alert.hideInput();
        alert.hideLeftButton();
        alert.setTitle("Home Screen Help");
        alert.setSubtitle("The Day label provides information about the current time table schedule, whether 1 or 2." +
                "\n\nX indicates days with no school. The time table you see below the Day label is for your personal schedule." +
                "\n\nThe current period is outlined in white, and the course reflects your 1st, 2nd, 3rd & 4th period classes, in order from left to right." +
                "\n\nTo input your courses, press, hold, and release the block you choose and enter the respective course for the respective period, keeping in mind the current day." +
                "\n\nIf you have a spare, leave the information blank and press Done.", Gravity.CENTER_HORIZONTAL);
        alert.configureRightButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.show(getSupportFragmentManager(), "HelpDialog");
    }


// MARK: Time Table Implementation

    private void requestTimeTableChange (int period) {
        CourseDialog dialog = new CourseDialog();
        int periodIndex = period;

        // Day 1/2 accommodations
        if (getScheduleDay() == 2 && (periodIndex > 2)) {
            periodIndex = periodIndex + ((periodIndex == 4) ? -1 : 1);
        }

        dialog.setPeriodIndex(periodIndex);
        dialog.show(getFragmentManager(), "");
    }

    /** This is the method that you must call to initiate time table setup. It will handle the rest. */
    private void initiateTimeTableSetup() {
        repopulateTimeTable();
    }


    // MARK: Time Table UI Methods

    /**
     * This method retrieves data from Shared Preferences and uses it to populate the time table
     * This method does account for the day of the day.
     * This is the method you must call to update time table contents.
     */
    public void repopulateTimeTable() {
        Log.d("Home Activity", "Populating Time Table");
        int[][] idBank = {{R.id.HSCourseName0, R.id.HSCourseCode0, R.id.HSTeacherName0, R.id.HSRoomNumber0},    // Period 1
                          {R.id.HSCourseName1, R.id.HSCourseCode1, R.id.HSTeacherName1, R.id.HSRoomNumber1},    // Period 2
                          {R.id.HSCourseName2, R.id.HSCourseCode2, R.id.HSTeacherName2, R.id.HSRoomNumber2},    // Period 3
                          {R.id.HSCourseName3, R.id.HSCourseCode3, R.id.HSTeacherName3, R.id.HSRoomNumber3}};   // Period 4
        int[] spares = {R.id.HSSpare0, R.id.HSSpare1, R.id.HSSpare2, R.id.HSSpare3};
        String[] defaultPeriodValue = {getResources().getString(R.string.HSCourseNameDefault),
                                       getResources().getString(R.string.HSCourseCodeDefault),
                                       getResources().getString(R.string.HSTeacherNameDefault),
                                       getResources().getString(R.string.HSRoomNumberDefault)};

        String[][] timeTable = new String[4][4];

        // Retrieve time table contents from shared preferences
        // For each period
        for (int h = 0; h < 4; h ++) {
            String curPeriodKey = CourseDialog.periodKey + h;   // Shared Preferences period data key
            int emptyCounter = 0;       // Counter for empty field
            // For each period field
            for (int j = 0; j < 4; j ++) {
                Log.d("Home Activity", "Retrieving Field " + j + " for key:" + curPeriodKey);
                timeTable[h][j] = sharedPreferences.getString(curPeriodKey + ":" + j, "");
                if (timeTable [h][j].isEmpty()) {
                    emptyCounter ++;
                }
            }
            // If the period is empty (never initialized) replace the contents with default values
            if (emptyCounter > 3) {
                for (int j = 0; j < 4; j ++) {
                    timeTable[h][j] = defaultPeriodValue[j];
                }
            }
        }
        Log.d("Home Activity", "Retrieved Time Table Data: " + Arrays.deepToString(timeTable));

        // Switch periods if it is a day two
        if (getScheduleDay() == 2) {
            String [] tempHold = timeTable[2];
            timeTable[2] = timeTable[3];
            timeTable[3] = tempHold;
        }

        // Populate contents into the UI & configure label visibilities
        // For each period
        for (int h = 0; h < 4; h ++) {
            if (timeTable[h][0].equalsIgnoreCase("SPARE")) {
                findViewById(spares[h]).setVisibility(View.VISIBLE);
                for (int id : idBank[h]) {
                    findViewById(id).setVisibility(View.INVISIBLE);
                }
            } else {
                findViewById(spares[h]).setVisibility(View.INVISIBLE);
                // For each period field
                for (int j = 0; j < 4; j++) {
                    ((TextView) findViewById(idBank[h][j])).setText(timeTable[h][j]);
                    findViewById(idBank[h][j]).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * This method will handle the UI updates.
     * This method does account for the schedule day
     * This is the method that you must call to update the highlighting part of the UI if necessary */
    public void updateTimeTableUI () {
        // Logic! The comment indents matter here!

        // Retrieve index number of from identifyPeriodState()

        // If the index number is an odd number, then we are currently in a period
            // To get period index from given index: perIndex = Math.floor(index/2)

        // If the index number is an even number then we are currently in-between periods
            // To get right-side period index from the given index: perIndex = index/2
                // Apply left side border to this index
            // To get left-side period index from the given index; perIndex = index/2 - 1
                // Apply right side border to this index

            // This part must be error trapped, since if the given index is 0 or 8, you may have IndexOutOfBounds issues
        // If this is confusing, ask me for a diagram and it will all make sense

        // ComputerScience!

        Log.d("HomeActivity:UIUpdater", "Updating Time Table UI...");

        // Clean up before starting
        resetTimeTableUI();

        // Get teh current time state
        int curTimeState = identifyPeriodState(System.currentTimeMillis(), getTimeStamps()) - 1;

        // Do this because identify method was configured for a different purpose, therefore if you subtract 1 and get a negative number, you really are supposed to have the 'post-period-4' state,
        if (curTimeState < 0) {
            curTimeState = getTimeStamps().length - 1;
        }

        if (curTimeState % 2 != 0) {    // We are in a period
            periods[(int) Math.floor(curTimeState/2)].getBackground().setLevel(0);  // Set the period as selected
        } else {    // We are in between periods
            int rightSidePeriod = curTimeState/2;       // Get the index of the right side period
            int leftSidePeriod = rightSidePeriod - 1;   // Get the index of the left side period

            if (leftSidePeriod > 0) {   // If we are within bounds, put a thin border on the right side of the left side period
                periods[leftSidePeriod].getBackground().setLevel(3);
            } else {    // If we are out-of-bounds, i.e. 'pre-period-1' state, put a thicker left side border on (most likely) period 1
                periods[rightSidePeriod].getBackground().setLevel(4);
            }

            if (rightSidePeriod < periods.length - 1) { // If we are within bounds, put a thin border on the left side of the right side period
                periods[rightSidePeriod].getBackground().setLevel(2);
            } else {    // If we are out-of-bounds, i.e. 'post-period-4' state, put a thicker right side border on (most likely) period 4
                periods[leftSidePeriod].getBackground().setLevel(5);
            }
        }
    }

    private void resetTimeTableUI () {
        // Reset period borders
        for (RelativeLayout period : periods) {
            period.getBackground().setLevel(1);
        }
    }


    // MARK: Time Table UI Auto-Update Methods

    /**
     * This method will update the highlighting of the current period and make sure that the current
     * time state is always highlighted on the time table.
     * This is the method you must call to initiate UI Auto-Updates. */
    private void createTimeTracker () {
        timeTracker = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.d("HomeActivity:Handler", "Received Time Table UI Update request! Updating...");
                HomeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTimeTableUI();
                    }
                });
                return false;
            }
        });
        // Start timeTracker
        calculateTimeToNextPeriod();
    }


    // MARK: TimeTracker Helper Methods Implementation

    /**
     * This method calculates the amount of millisecond until the start of the next time state.
     * Once calculated it will initiate a delayed message for timeTracker, that will be sent after the calculated amount of time
     * This method accounts for late start schedule on its own, based on the isLateStart property.
     */
    private void calculateTimeToNextPeriod() {
        // Calculate time in millis until next period, based on the system clock's current time
        String[] timeStamps = getTimeStamps();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        // Declare the current time so that all helper methods are in sync
        long curTime = System.currentTimeMillis();
        // Determine which timestamp represents the next period
        String nextTimeStamp = timeStamps[identifyPeriodState(curTime, timeStamps)];

        long timeStampMillis = 0;
        try {
            Date timeStampDate = formatter.parse(nextTimeStamp);
            Calendar todayCal = Calendar.getInstance();
            Calendar timeStampCalendar = Calendar.getInstance();
            timeStampCalendar.setTime(timeStampDate);
            timeStampCalendar.set(todayCal.get(Calendar.YEAR), todayCal.get(Calendar.MONTH), todayCal.get(Calendar.DAY_OF_MONTH));
            timeStampMillis = timeStampCalendar.getTimeInMillis();
        } catch (ParseException e) {
            Toast.makeText(this, "An Internal Time Table Error Occurred!\n Error #" + getResources().getInteger(R.integer.TimeParsingError), Toast.LENGTH_LONG).show();
        }


        long timeToNextPeriod = timeStampMillis - curTime;
        // if we are after period 4
        if (timeToNextPeriod < 0) {
            // Then you get the current time, subtract it from 24:00:00 to get the time window to the end of the day
            timeToNextPeriod = ((long) 8.64e7) + timeToNextPeriod;
        }


        // Declare Handler Message and configure its message code, so that it can later be removed from que if necessary
        Message msg = new Message();
        msg.what = timeTrackerMessageCode;

        // Must give an additional 1 second delay, or else it calls updateTimeTableUI too early and UI doesn't update as it should
        timeTracker.sendMessageDelayed(msg, timeToNextPeriod + 1000);

    }

    /**
     * Identifies the current period state (pre-period-one, period-four, etc...) based on the current time.
     *
     * @return The index of the next chronological time stamp, relative to the current time.
     */
    private int identifyPeriodState(long curMillisTime, String[] timeStamps) {
        // Retrieve a string representation of the current time (24-hour format)
        String curTime = new SimpleDateFormat("HH:mm:ss").format(new Date(curMillisTime));

        for (int h = 0; h < timeStamps.length; h++) {
            int curResult = curTime.compareTo(timeStamps[h]);

            if (curResult <= 0) {
                return h;
            }
        }
        return 0;
    }

    private String[] getTimeStamps() {
        return (!isLateStart) ? new String[]{"00:00:00", "08:45:00", "10:05:00", "10:10:00", "11:30:00", "12:30:00", "13:45:00", "13:50:00", "15:05:00"}
                              : new String[]{"00:00:00", "10:00:00", "11:05:00", "11:10:00", "12:10:00", "13:00:00", "13:55:00", "14:00:00", "15:05:00"};
    }


// MARK: Misc Default Overrides Implementation

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;
        if (item.getItemId() == R.id.helpAction) {
            displayHelpDialog();
            return true;
        }
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
