package com.wlmac.lyonsden2_android;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.otherClasses.Event;
import com.wlmac.lyonsden2_android.otherClasses.LyonsCalendar;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Calendar;
import java.util.Date;

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
    /** The drawer toggler used this activity. */
    private ActionBarDrawerToggle drawerToggle;
    /** The UICalendar.  */
    private LyonsCalendar calendarView = new LyonsCalendar();
    /** The date label under the calendar. */
    private TextView dateLabel;
    /** The event list that contains all the events for a selected date. */
    private LinearLayout eventList;
    /** A holder for the last selected date. */
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
            // Update the date label
            dateLabel.setText(date.toString().substring(0, 10));

        // MARK: DISPLAY EVENT

            // Clear the current eventList of all past events.
            eventList.removeAllViews();

            // If there are no events associated with the selected date, then you shall not pass!
            if (!calendarView.isDateEvent(date)) return;

            // Retrieve the events associated with the selected date
            Event[] eventsForDate = calendarView.getEventForDate(date);
            // For as many times as there are events
            for (int h = 0; h < eventsForDate.length; h ++) {
                // Create a view out of the "event_view.xml" layout file
                View eventView = getLayoutInflater().inflate(R.layout.event_layout, null);
                // Create a divider View for the list
                View dividerView = new View(getApplicationContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(0, 3, 0, 3);  // This is the actual size
                dividerView.setLayoutParams(lp);
                dividerView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));   // Divider color

                // Set the event view's title
                ((TextView) eventView.findViewById(R.id.EVTitleLabel)).setText(eventsForDate[h].getTitle());
                // Set the event view's description
                ((TextView) eventView.findViewById(R.id.EVInfoLabel)).setText(eventsForDate[h].getDescription());
                // Parse and set the event view's dateTime
                String dateOfEvent = eventsForDate[h].getStartDate().toString().substring(10, 16);
                ((TextView) eventView.findViewById(R.id.EVDateLabel)).setText((dateOfEvent.equals("00:00")) ? "All day" : dateOfEvent);
                // Set the event view's location
                ((TextView) eventView.findViewById(R.id.EVLocationLabel)).setText(eventsForDate[h].getLocation());
                // Add the created views to the event list
                eventList.addView(eventView);
                eventList.addView(dividerView);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Super call
        super.onCreate(savedInstanceState);
        // Associate the appropriate layout file with this activity
        setContentView(R.layout.calendar_activity);

    // MARK: DRAWER

        // An instance of the root layout of this activity
        DrawerLayout rootLayout = (DrawerLayout) findViewById(R.id.CalDLayout);
        // An instance of the ListView used in this activity's navigation drawer
        ListView drawerList = (ListView) findViewById(R.id.CalDList);
        // Instantiate the drawer for thi activity
        drawerToggle = HomeActivity.initializeDrawerToggle(this, rootLayout);
        HomeActivity.setupDrawer(this, drawerList, rootLayout, drawerToggle);

    // MARK: UI INITIALIZATION

        // Instantiate all of the UI components
        dateLabel = (TextView) findViewById(R.id.CalSDateLabel);
        eventList = (LinearLayout) findViewById(R.id.CalSEventList);

    // MARK: CALENDAR INITIALIZATION

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
        // Pass the instantiation parameters to the UICalendar
        calendarView.setArguments(args);
        // Display the UICalendar on screen, by replacing an existing 'holder View' on screen with the UICalendar
        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.CalSCalendar, calendarView);
        t.commit();
    }

    private void setCellForDate (int cellCode, Date date) {
        if (calendarView.isDateEvent(date))
            cellCode++;

        Drawable drawable = getResources().getDrawable(R.drawable.calendar_cell);
        try { drawable.setLevel(cellCode); } catch (NullPointerException e) { Log.d("Calendar Activity:", "The calendar_cell.xml file seems to missing or corrupted."); }
        calendarView.setBackgroundDrawableForDate(drawable, date);
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