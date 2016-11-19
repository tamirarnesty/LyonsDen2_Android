package com.wlmac.lyonsden2_android;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wlmac.lyonsden2_android.otherClasses.Event;
import com.wlmac.lyonsden2_android.otherClasses.LoadingLabel;
import com.wlmac.lyonsden2_android.otherClasses.LyonsCalendar;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;
import com.wlmac.lyonsden2_android.otherClasses.WebCalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// First time launch
// Calendar shows, hidden
// Loading animation starts
// Calendar starts loading
// Once calendar is loaded, display calendar

// Post-First time launch
// Last calendar is shown
// New Calendar is downloaded in the background
// Once downloaded, the current visual calendar is updated

/**
 * The activity to that will display the calendar and its events.
 * Display a scrollable GridVIew of dates. If a date is selected then the list
 * at the bottom of the calendar will update to display the events associated with
 * the selected date.
 *
 * @author sketch204
 * @version 1, 2016/08/08
 */
public class CalendarActivity extends AppCompatActivity {
    /** A {@link String} representation of the Day Dictionary. */
    private String dayDictionary;
    /** The drawer toggler used this activity. */
    private ActionBarDrawerToggle drawerToggle;
    /** The Calendar View.  */
    private LyonsCalendar calendarView = new LyonsCalendar();
    /** The date label under the calendar. */
    private TextView dateLabel;
    /** The event list that contains all the {@link Event}s of a selected date. */
    private LinearLayout eventList;
    /** A holder for the last selected date in the grid. */
    private Date lastSelectedDate;
    /** A holder for today's date. */
    private Date today = new Date();
    /** A calendar event listener/handler. */
    private CaldroidListener listener = new CaldroidListener() {
        /**
         * Notifies the client that, CaldroidFragment has been created and its child views
         * are no longer null. This method is useful for customization of views directly,
         * although all visual customization should be made through the associated
         * (provided/created) calendar style. */
        @Override
        public void onCaldroidViewCreated() {
            // Move the UICalendar to today's date
            calendarView.setCalendarDate(today);

            // Change today's cell to "today's cell"
            setCellForDate(4, today);
        }

        /**
         * Notifies the client that a the user selected a date.
         * @param date The Date object associated with the date that the user selected.
         * @param view The View object associated with the date that the user selected.
         */
        @Override
        public void onSelectDate(Date date, View view) {
    // MARK: DATE SELECTION
            int cellCode;

            if (lastSelectedDate != null && lastSelectedDate.equals(date))
                // If the user selected an already selected date, then do nothing
                return;
            else if (lastSelectedDate == null)
                // If this is the first date to be selected, then ignore the operations that would be done to lastSelectedDate.
                lastSelectedDate = date;
            else {
                if (lastSelectedDate.toString().substring(0, 10).equals(today.toString().substring(0, 10)))
                    // If the last selected date is today's cell, then, set it to a default "today's cell"
                    cellCode = 4;
                else
                    // Otherwise set it to a "default cell"
                    cellCode = 0;
                // Set the appropriate cell
                setCellForDate(cellCode, lastSelectedDate);
            }

            if (date.toString().substring(0, 10).equals(today.toString().substring(0, 10))) {
                // If the selected date is today's date, then, set it to "today's cell selected"
                cellCode = 6;
            } else {
                // Otherwise highlight it as a "default cell selected"
                cellCode = 2;
            }
            // Set the appropriate cell
            setCellForDate(cellCode, date);
            // Update the last selected date
            lastSelectedDate = date;

            // Declare the text of the date label
            String dateLabelText = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA).format(date);
            if (dayDictionary == null) {        // If a Day Dictionary is not available then
                // Try to load it
                dayDictionary = getSharedPreferences(HomeActivity.sharedPreferencesName, 0).getString(LyonsCalendar.keyDayDictionary, null);
                if (dayDictionary != null) {    // If it worked then get the day of the selected date and
                    // Set it
                    String day = getDayFromDictionary(dayDictionary, date);
                    dateLabelText += (!day.equals("-1")) ? " Day " + day : "";
                }
            } else {    // If a Day Dictionary is available then
                // Set the day of the selected date
                String day = getDayFromDictionary(dayDictionary, date);
                dateLabelText += (!day.equals("-1")) ? " Day " + day : "";
            }
            // Update the date label
            dateLabel.setText(dateLabelText);
    // MARK: DISPLAY EVENT
            // Clear the current eventList of all past events.
            eventList.removeAllViews();

            // If there are no events associated with the selected date, then you shall not pass!
            if (!calendarView.isDateEvent(date))
                return;

            // Retrieve the events associated with the selected date
            Event[] eventBank = calendarView.getEventForDate(date);
            // For as many times as there are events
            for (int h = 0; h < eventBank.length; h ++) {
                // Create a divider View for the list, to create space between event views.
                View dividerView = new View(getApplicationContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(0, 3, 0, 3);  // This is the actual size
                dividerView.setLayoutParams(lp);
                dividerView.setBackgroundColor(getResources().getColor(R.color.background));   // Divider color
                // Create and add the eventView to the event list scroll view
                eventList.addView(createEventView(eventBank[h]));
                // And also add the divider
                eventList.addView(dividerView);
            }
        }

        /**
         * This ethod creates the Event View and resizes it to only hold the displayed information.
         * @param event The {@link Event} associated with this {@link View}.
         */
        private View createEventView (Event event) {
            // Create a view out of the "event_view.xml" layout file
            View eventView = getLayoutInflater().inflate(R.layout.event_layout, null);

            // The following piece of code will hide the rows of the view that hold no text

            // Configure Title
            if (event.getTitle() == null || event.getTitle().isEmpty())   // If there is no text in title
                // Hide Title
                eventView.findViewById(R.id.EVTitleLabel).setVisibility(View.GONE);
            else    // Otherwise
                // Set the event view's title
                ((TextView) eventView.findViewById(R.id.EVTitleLabel)).setText(event.getTitle());


            // Configure Description
            if (event.getDescription() == null || event.getDescription().isEmpty())   // If there is no text in title
                // Hide Description
                eventView.findViewById(R.id.EVInfoLabel).setVisibility(View.GONE);
            else    // Otherwise
                // Set the event view's description
                ((TextView) eventView.findViewById(R.id.EVInfoLabel)).setText(event.getDescription());

            // Configure the Date/Time and Location
            if (event.getStartDate() == null && (event.getLocation() == null || event.getLocation().isEmpty()))  // If both of the fields are empty
                // Hide the bottom labels
                eventView.findViewById(R.id.EVBottomLabels).setVisibility(View.GONE);
            else {  // Otherwise
                // Parse and set the event view's dateTime
                String dateOfEvent = event.getStartDate().toString().substring(10, 16);
                ((TextView) eventView.findViewById(R.id.EVDateLabel)).setText((dateOfEvent.contains("00:00")) ? "All day" : dateOfEvent);
                // Set the event view's location
                ((TextView) eventView.findViewById(R.id.EVLocationLabel)).setText(event.getLocation());
            }

            return eventView;
        }
    };
    private LoadingLabel loadingLabel;
    private ProgressBar loadingCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Super call
        super.onCreate(savedInstanceState);
        // Associate the appropriate layout file with this activity
        setContentView(R.layout.calendar_activity);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

    // MARK: CALENDAR VIEW INITIALIZATION

        calendarView.setCaldroidListener(listener);
        // Create the medium for transferring the instantiation parameters for the UICalendar
        Bundle args = new Bundle();
        // A necessary aspect of parameter passing
        Calendar cal = Calendar.getInstance();
        // Setup the instantiation parameters for the UICalendar to retrieve upon instantiation
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        args.putInt(CaldroidFragment.THEME_RESOURCE, R.style.LyonsCalendar);
        args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, false);
        args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, false);
        // Pass the instantiation parameters to the UICalendar
        calendarView.setArguments(args);
        // Display the UICalendar on screen, by replacing an existing 'holder View' on screen with the UICalendar
        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.CalSCalendar, calendarView);
        t.commit();

    // MARK: DRAWER INITIALIZATION

        // An instance of the root layout of this activity
        DrawerLayout rootLayout = (DrawerLayout) findViewById(R.id.CalDLayout);
        // An instance of the ListView used in this activity's navigation drawer
        ListView drawerList = (ListView) findViewById(R.id.CalDList);
        // Instantiate the drawer for thi activity
        drawerToggle = HomeActivity.initializeDrawerToggle(this, rootLayout);
        HomeActivity.setupDrawer(this, drawerList, rootLayout, drawerToggle);

    // MARK: DAY DICTIONARY INITIALIZATION

        SharedPreferences cache = getSharedPreferences(HomeActivity.sharedPreferencesName, 0);
        dayDictionary = cache.getString(LyonsCalendar.keyDayDictionary, null);
        String downloadAction = (dayDictionary == null) ? WebCalendar.actionDownloadWithCache : WebCalendar.actionDownload;

    // MARK: CALENDAR INITIALIZATION
        // Check for internet availability
        if (Retrieve.isInternetAvailable(this)) {  // Speaks for it self
            // Download action may be different, depending on circumstances
            WebCalendar.downloadInto(calendarView, getApplicationContext(), downloadAction);
            showLoadingComponents();

        } else {                // If internet is not available then
            String toastText = "Unable to connect to calendar"; // Will display this message if offline and no cache
            // If a Cached Calendar exists then use that and tell the user about it
            if (getSharedPreferences(HomeActivity.sharedPreferencesName, 0).getString(LyonsCalendar.keyCalendarCache, null) != null) {
                calendarView.setOffline(true);
                showLoadingComponents();
                toastText += "\nLoading last cached version";   // Will display that plus this message if offline but with cache
            } else {    // If not then too bad so sad
                calendarView.setEmpty(true);
            }
            Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
        }

    // MARK: UI INITIALIZATION

        dateLabel = (TextView) findViewById(R.id.CalSDateLabel);
        eventList = (LinearLayout) findViewById(R.id.CalSEventList);
        if (!calendarView.isEmpty()) {      // If a calendar will load then, hide the components of this activity
            dateLabel.setVisibility(View.GONE);
            eventList.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (calendarView.isOffline()) {     // If a cached calendar must be loaded
            WebCalendar.downloadInto(calendarView, this, WebCalendar.actionLoadOffline);
        }   // This is done here and not in onCreate(), to give the calendar fragment time to attach itself to the activity

        // This is to make sure that the calendar is hidden when any sort of calendar is available
        if (!calendarView.isEmpty()) {
            try {
                calendarView.getView().setVisibility(View.GONE);
            } catch (NullPointerException e) {
                Toast.makeText(this, "A UI Error Occurred!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Called when the events are loaded into the calendar. */
    public void onEventsLoaded () {
        // Display all components of this activity
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadingLabel != null && loadingCircle != null) {
                    loadingLabel.dismiss();
                    loadingCircle.setVisibility(View.GONE);
                }

                try {
                    calendarView.getView().setVisibility(View.VISIBLE);
                } catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "A UI Error Occurred!", Toast.LENGTH_SHORT).show();
                }
                dateLabel.setVisibility(View.VISIBLE);
                eventList.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setCellForDate (int cellCode, Date date) {
        if (calendarView.isDateEvent(date))
            cellCode++;

        Drawable drawable = getResources().getDrawable(R.drawable.calendar_cell);
        try { drawable.setLevel(cellCode); } catch (NullPointerException e) { Log.d("Calendar Activity:", "The calendar_cell.xml file seems to missing or corrupted."); }
        calendarView.setBackgroundDrawableForDate(drawable, date);
    }

    private void showLoadingComponents () {
        loadingLabel = new LoadingLabel(((TextView) findViewById(R.id.CalSLoadingLabel)), this);
        loadingLabel.startCycling();
        loadingCircle = (ProgressBar) findViewById(R.id.CalSLoadingWheel);
    }

    /**
     * This method is used to get a {@link String} representation of the day of the given {@link Date} from
     * the given {@link String} Day Dictionary.
     * @param dictionary A {@link String} instance of the Day Dictionary to use.
     * @param date The date to look for.
     * @return A {@link String} representation of the day. Will only return the number of the day. If no day is available will return -1.
     */
    public static String getDayFromDictionary (String dictionary, Date date) {
        // Key to look for in the dictionary
        String key = LyonsCalendar.convertToKey(date).toString();
        // Index of the day
        int index = dictionary.indexOf(key) + key.length() + 1;

        try {
            return "" + dictionary.charAt(index);
        } catch (IndexOutOfBoundsException e) {
            return "-1";
        }
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