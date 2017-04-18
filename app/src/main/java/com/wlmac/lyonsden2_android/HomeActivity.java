package com.wlmac.lyonsden2_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Handler;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * The activity that will be used to display the home screen. The home screen consists of a label for
 * today's day, a timetable that highlights the current period and a list of the most recent announcements.
 *wh
 * @author Ademir Gotov
 * @version 1, 2016/07/30
 */
public class HomeActivity extends AppCompatActivity {
    public static boolean didUpdateDataset = false;

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
    /** A list of item that will be displayed in the every drawer list of this program. */
    private static String[] drawerContent = {"Home", "Calendar", "Announcements", "Clubs", "Contact", "Me"};  // Just trying things out :)
    /** An instance of the root layout of this activity. */
    private DrawerLayout rootLayout;
    /** An instance of the ListView used in this activity's navigation drawer. */
    private ListView drawerList;
    /** The drawer toggle used this activity. */
    private ActionBarDrawerToggle drawerToggle;
    private TextView lateStartLabel;
    /** Containers for courses */
    private RelativeLayout [] containers = new RelativeLayout[4];
    /** Date Format */
    private SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm:ss", Locale.CANADA);

    private SwipeRefreshLayout refreshLayout;


    /** Timetable selection backgrounds */
    Drawable drawableSelect;
    Drawable drawableBlack;
    Drawable drawableLeft;
    Drawable drawableRight;
    Drawable drawableMostLeft;
    Drawable drawableMostRight;

    private TextView noInternet;
    private boolean isLateStart;
    private boolean isSpecSchedule = false;
    private String[] normalDay;
    private String[] lateDay;
    private long[] normalTime;
    private long[] lateTime;
    private boolean isOnlineLogInShown;
    private ListAdapter adapter;
    SharedPreferences sharedPreferences;

// MARK: Parallax Fields
    /** A reference to the instance of a container that contains the Day label + Timetable as its child views. */
    private RelativeLayout topViews;
    private Toolbar toolbar;
    private boolean didCalculateSpacer = false;
    private int topViewsHeight = 0;
    private int lastOffset = 0;

    private String[][] timeTable;


    //private View holderView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Super call
        super.onCreate(savedInstanceState);
        // Declare the associated xml layout file
        setContentView(R.layout.home_activity);

        Retrieve.oneSignalStatus();

        // Declare time stamps for all periods in dedicated arrays
        initializeTimeStamps();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Instantiate all UI components
        initializeComponents();
        initializeTimeTable();

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

        // Initialize Period Updater only if it is a school day
        if (getDayLabel().equals("1") || getDayLabel().equals("2")) {
            // Initialize Period Updater
            if (sharedPreferences.getBoolean("isOnlineLogInShown", false)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isOnlineLogInShown", true);
                editor.apply();
                boolean isOnlineLogIn = getIntent().getBooleanExtra("isInternetAvailable", true);
                if (!isOnlineLogIn) {
                    Toast.makeText(getApplicationContext(), "Offline Log In.\nSome features unavailable", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Successful Log In", Toast.LENGTH_LONG).show();
                }
            }
            final Handler periodUpdater = new Handler();
            periodUpdater.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updatePeriods();
                }
            }, 60000);
            Thread thread = createThread();
            // Start period updater
            thread.start();
        }

        // Setup drawer
        Retrieve.drawerSetup(this, drawerList, rootLayout, drawerToggle);

        // Resize the announcement ListView to fit the screen. DOES NOT WORK ATM!!!
        listView.setMinimumHeight(Retrieve.screenSize(this).y);
        // Set the custom font of the TextLabels
        dayLabel.setTypeface(Retrieve.typeface(this));
        todayIsDay.setTypeface(Retrieve.typeface(this));

        if (dayLabel.getText().toString().equals("X")) {
            Toast.makeText(getApplicationContext(), "No day available.\nThere is no school today.\nIt may be a weekend.", Toast.LENGTH_LONG).show();
        }

        updatePeriods();

        isLateStart = Retrieve.isLateStartDay(getSharedPreferences(LyonsDen.keySharedPreferences, 0).getString(LyonsCalendar.keyLateStartDictionary, ""), new Date());

        if (isLateStart) lateStartLabel.setVisibility(View.VISIBLE);

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

        listView.addHeaderView(listHeader, listHeader, false);

        adapter = new ListAdapter(this, announcements, false, true);
        listView.setAdapter(adapter);

        loadAnnouncements();

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true);
        if (typedValue.resourceId != 0) {
            int offset = (int)getResources().getDimension(typedValue.resourceId);
            refreshLayout.setProgressViewOffset(true, offset, offset + Retrieve.pxFromDpInt(32, getResources()));
            Log.d("Home Activity", "" + offset);
        }
    }

    private void initializeTimeStamps() {
        normalDay = new String[]{"00:00:00", "08:44:59", "08:45:00", "10:05:00", "10:05:01", "10:09:59", "10:10:00", "11:30:00", "11:30:01",
                "12:29:59", "12:30:00", "13:45:00", "13:45:01", "13:49:59", "13:50:00", "15:05:00"};
        lateDay = new String[]{"00:00:00", "09:59:59", "10:00:00", "11:05:00", "11:05:01", "11:09:59", "11:10:00", "12:10:00", "12:10:01",
                "12:59:59", "13:00:00", "13:55:00", "13:55:01", "13:59:59", "14:00:00", "15:05:00"};

        normalTime = new long[]{31499999, 36300000, 36599999, 41400000, 45098999, 49500000, 49799999, 54300000, 86399999};
        lateTime = new long[]{35999999, 39900000, 40199999, 43800000, 46898999, 50100000, 50399999, 54300000, 86399999};
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

    public void updatePeriods () {
        timeTable = assembleTimeTable();
    }

    public Thread createThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Date currentDate = new Date();
                        String timeString = timeFormat.format(currentDate);
                        int placeHolder;
                        long time;
                        if (isLateStart) {
                            placeHolder = checkTimes(timeString, normalDay);
                            time = returnTime(placeHolder, normalTime);
                        } else {
                            placeHolder = checkTimes(timeString, lateDay);
                            time = returnTime(placeHolder, lateTime);
                        }
                        final int timeNumber = placeHolder;
                        HomeActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                drawTableTime(timeNumber);
                            }
                        });
                        Thread.sleep(time);
                    } catch (Exception e) {}
                }
            }
        });
    }

    private void updateDay () {
        String day = Retrieve.dayFromDictionary(getSharedPreferences(LyonsDen.keySharedPreferences, 0).getString(LyonsCalendar.keyDayDictionary, ""), new Date());
        if (day.equals("-1"))
            day = "X";
        dayLabel.setText(day);
        sharedPreferences.edit().putString(LyonsDen.dayKey, dayLabel.getText().toString()).apply();
    }

    private int checkTimes(String time, String[] array) {
        if (time.compareTo(array[0]) > 0 && time.compareTo(array[1]) < 0) {
            return 0;
        } else if (time.compareTo(array[2]) > 0 && time.compareTo(array[3]) < 0) {
            return 1;
        } else if (time.compareTo(array[4]) > 0 && time.compareTo(array[5]) < 0) {
            return 2;
        } else if (time.compareTo(array[6]) > 0 && time.compareTo(array[7]) < 0) {
            return 3;
        } else if (time.compareTo(array[8]) > 0 && time.compareTo(array[9]) < 0) {
            return 4;
        } else if (time.compareTo(array[9]) > 0 && time.compareTo(array[11]) < 0) {
            return 5;
        } else if (time.compareTo(array[12]) > 0 && time.compareTo(array[13]) < 0) {
            return 6;
        } else if (time.compareTo(array[14]) > 0 && time.compareTo(array[15]) < 0) {
            return 7;
        } else {
            return 8;
        }
    }

    private long returnTime(int i, long[] array) {
        Calendar c = Calendar.getInstance();
        long now = c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long currentTime = now - c.getTimeInMillis();
        long returnValue = 0;
        switch (i) {
            case 0:
                returnValue = array[0] - currentTime;
                break;
            case 1:
                returnValue = array[1] - currentTime;
                break;
            case 2:
                returnValue = array[2] - currentTime;
                break;
            case 3:
                returnValue = array[3] - currentTime;
                break;
            case 4:
                returnValue = array[4] - currentTime;
                break;
            case 5:
                returnValue = array[5] - currentTime;
                break;
            case 6:
                returnValue = array[6] - currentTime;
                break;
            case 7:
                returnValue = array[7] - currentTime;
                break;
            case 8:
                returnValue = array[8] - currentTime;
                break;
        }
        return returnValue;
    }

    private void drawTableTime(int time) {
        switch (time) {
            case 0:
                containers[0].setBackgroundDrawable(drawableMostLeft);
                containers[1].setBackgroundDrawable(drawableBlack);
                containers[2].setBackgroundDrawable(drawableBlack);
                containers[3].setBackgroundDrawable(drawableBlack);
                break;
            case 1:
                containers[0].setBackgroundDrawable(drawableSelect);
                containers[1].setBackgroundDrawable(drawableBlack);
                containers[2].setBackgroundDrawable(drawableBlack);
                containers[3].setBackgroundDrawable(drawableBlack);
                break;
            case 2:
                containers[0].setBackgroundDrawable(drawableRight);
                containers[1].setBackgroundDrawable(drawableLeft);
                containers[2].setBackgroundDrawable(drawableBlack);
                containers[3].setBackgroundDrawable(drawableBlack);
                break;
            case 3:
                containers[0].setBackgroundDrawable(drawableBlack);
                containers[1].setBackgroundDrawable(drawableSelect);
                containers[2].setBackgroundDrawable(drawableBlack);
                containers[3].setBackgroundDrawable(drawableBlack);
                break;
            case 4:
                containers[0].setBackgroundDrawable(drawableBlack);
                containers[1].setBackgroundDrawable(drawableRight);
                containers[2].setBackgroundDrawable(drawableLeft);
                containers[3].setBackgroundDrawable(drawableBlack);
                break;
            case 5:
                containers[0].setBackgroundDrawable(drawableBlack);
                containers[1].setBackgroundDrawable(drawableBlack);
                containers[2].setBackgroundDrawable(drawableSelect);
                containers[3].setBackgroundDrawable(drawableBlack);
                break;
            case 6:
                containers[0].setBackgroundDrawable(drawableBlack);
                containers[1].setBackgroundDrawable(drawableBlack);
                containers[2].setBackgroundDrawable(drawableRight);
                containers[3].setBackgroundDrawable(drawableLeft);
                break;
            case 7:
                containers[0].setBackgroundDrawable(drawableBlack);
                containers[1].setBackgroundDrawable(drawableBlack);
                containers[2].setBackgroundDrawable(drawableBlack);
                containers[3].setBackgroundDrawable(drawableSelect);
                break;
            case 8:
                containers[0].setBackgroundDrawable(drawableBlack);
                containers[1].setBackgroundDrawable(drawableBlack);
                containers[2].setBackgroundDrawable(drawableBlack);
                containers[3].setBackgroundDrawable(drawableMostRight);
                break;
        }
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
        drawableSelect = getResources().getDrawable(R.drawable.time_table_backgroud);
        drawableBlack = getResources().getDrawable(R.drawable.time_table_backgroud);
        drawableLeft = getResources().getDrawable(R.drawable.time_table_backgroud);
        drawableRight = getResources().getDrawable(R.drawable.time_table_backgroud);
        drawableMostLeft = getResources().getDrawable(R.drawable.time_table_backgroud);
        drawableMostRight = getResources().getDrawable(R.drawable.time_table_backgroud);
        drawableSelect.setLevel(0);
        drawableBlack.setLevel(1);
        drawableLeft.setLevel(2);
        drawableRight.setLevel(3);
        drawableMostLeft.setLevel(4);
        drawableMostRight.setLevel(5);
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
                        notificationsChosen[0] = true;
                        dialog.cancel();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OneSignal.setSubscription(false);
                notificationsChosen[0] = false;
                dialog.cancel();
            }
        });
        alertBuilder.create().show();
    }

    // You will probably want to change this to set the time table to whatever it retrieved from permanent storage
    public String[][] assembleTimeTable() {

        // An instance of the time table that will be returned and assigned to a global variable
        String[][] timeTable = new String[4][4];
        // A bank of IDs each is reference to an individual piece of the timetable
        int[][] idBank = {{R.id.HSCourseName0, R.id.HSCourseCode0, R.id.HSTeacherName0, R.id.HSRoomNumber0},    // Period 1
                          {R.id.HSCourseName1, R.id.HSCourseCode1, R.id.HSTeacherName1, R.id.HSRoomNumber1},    // Period 2
                          {R.id.HSCourseName2, R.id.HSCourseCode2, R.id.HSTeacherName2, R.id.HSRoomNumber2},    // Period 3
                          {R.id.HSCourseName3, R.id.HSCourseCode3, R.id.HSTeacherName3, R.id.HSRoomNumber3}};   // Period 4
        int[] spares = {R.id.HSSpare0, R.id.HSSpare1, R.id.HSSpare2, R.id.HSSpare3};
        // A nested loop that will fill up the timetable
        SharedPreferences pref = getSharedPreferences(LyonsDen.keySharedPreferences, 0);
        for (int h = 0; h < timeTable.length; h ++) {
            boolean check = pref.getBoolean("Period " + (h+1), false);
                for (int j = 0; j < timeTable[h].length; j++) {
                    // A instance of a timetable piece (made without assigning to a variable)
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
                    if (h < 2) {
                        ((TextView) findViewById(idBank[h][j])).setText(pref.getString("Period " + (h + 1) + " " + j, s));
                        timeTable[h][j] = ((TextView) findViewById(idBank[h][j])).getText().toString();
                    } else {
                        if (h == 2) {
                            if (getDayLabel().equals("1") || getDayLabel().equals("X")) {
                                // Period 3
                                ((TextView) findViewById(idBank[2][j])).setText(pref.getString("Period " + (3) + " " + j, s));
                                timeTable[2][j] = ((TextView) findViewById(idBank[2][j])).getText().toString();
                                // Period 4
                                ((TextView) findViewById(idBank[3][j])).setText(pref.getString("Period " + (4) + " " + j, s));
                                timeTable[3][j] = ((TextView) findViewById(idBank[3][j])).getText().toString();
                            } else if (dayLabel.getText().equals("2")) {
                                // Period 3
                                ((TextView) findViewById(idBank[2][j])).setText(pref.getString("Period " + (4) + " " + j, s));
                                timeTable[2][j] = ((TextView) findViewById(idBank[3][j])).getText().toString();
                                // Period 4
                                ((TextView) findViewById(idBank[3][j])).setText(pref.getString("Period " + (3) + " " + j, s));
                                timeTable[3][j] = ((TextView) findViewById(idBank[2][j])).getText().toString();
                            }
                        }
                    }
                }
            for (int j = 0; j < timeTable[h].length; j++) {
                // A instance of a timetable piece (made without assigning to a variable)
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
                ((TextView) findViewById(idBank[h][j])).setText(pref.getString("Period " + (h + 1) + " " + j, s));
                timeTable[h][j] = ((TextView) findViewById(idBank[h][j])).getText().toString();
            }
            if (!check) {
                (findViewById(spares[h])).setVisibility(View.INVISIBLE);
                for (int j = 0; j < timeTable[h].length; j++)
                    (findViewById(idBank[h][j])).setVisibility(View.VISIBLE);
            } else {
                (findViewById(spares[h])).setVisibility(View.VISIBLE);
                for (int j = 0; j < timeTable[h].length; j++)
                    (findViewById(idBank[h][j])).setVisibility(View.INVISIBLE);
            }
        }

        return timeTable;
    }

    public String getDayLabel() {
        return dayLabel.getText().toString();
    }

    public void periodClicked (int index, String name, String code, String teacher, String room) {
        CourseDialog courseDialog = new CourseDialog();
        if (getDayLabel().equals("1")) {
            courseDialog.setPeriod("Period " + index, name, code, teacher, room, getDayLabel());
        } else {
            if (getDayLabel().equals("2")) {
                if (index < 3)
                    courseDialog.setPeriod("Period " + index, name, code, teacher, room, getDayLabel());
                else if (index == 3)
                    courseDialog.setPeriod("Period " + (index+1), name, code, teacher, room, getDayLabel());
                else if (index == 4)
                    courseDialog.setPeriod("Period " + (index-1), name, code, teacher, room, getDayLabel());
            }
        }
        courseDialog.show(getFragmentManager(), "");
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