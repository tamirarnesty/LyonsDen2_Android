package com.wlmac.lyonsden2_android.otherClasses;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CalendarHelper;
import com.roomorama.caldroid.CellView;
import com.wlmac.lyonsden2_android.CalendarActivity;
import com.wlmac.lyonsden2_android.HomeActivity;
import com.wlmac.lyonsden2_android.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hirondelle.date4j.DateTime;

/**
 * This class will act as a customized version of CaldroidFragment, which is a calendar for android.
 * This customized version supports the handling of events.
 *
 * @author sketch204
 * @version 1, 2016/08/15
 */
public class LyonsCalendar extends CaldroidFragment {
    /** {@link SharedPreferences} link to the Calendar Cache. */
    public static final String keyCalendarCache = "calendarFileCache";
    /** {@link SharedPreferences} link to the Day Dictionary Cache. */
    public static final String keyDayDictionary = "dayDictionaryFile";
    /** {@link SharedPreferences} link to the Late Start Dictionary Cache. */
    public static final String keyLateStartDictionary = "lateStartDictionaryFile";  // To Be Implemented


    /** A HashMap of Events, each linked to its Date key. */
    private HashMap<Date, ArrayList<Event>> eventBank = new HashMap<>();
    /** States whether this calendar is empty. True when no calendar is available. */
    private boolean isEmpty = false;
    /** States whether this calendar is offline. */
    private boolean isOffline = false;


    /**
     * Loads the events into the calendar.
     * @param events The events to be loaded.
     */
    public void loadEvents (Event[] events) {
        if (events == null) return;
        // If there are no events to load, then return
        if (events.length < 1) return;
        // Otherwise update the appropriate date cell in the calendar
        updateEventUI(events);
        // For as many times as there are events
        for (int h = 0; h < events.length; h ++) {
            // Create a key for the curent event
            Date curKey = convertToKey(events[h].getStartDate());
            if (eventBank.containsKey(curKey)) {
                // If events for the given key already exist, then add the current event to the party
                eventBank.get(curKey).add(events[h]);
            } else {
                // Otherwise create a nwe entry for the given date
                eventBank.put(curKey, new ArrayList<Event>());
                eventBank.get(curKey).add(events[h]);
            }
        }

        ((CalendarActivity) getActivity()).onEventsLoaded();
    }

    /**
     * Converts the given {@link Date} to an appropriate key, by transforming the date into 'date-only' format.
     * Thereby clearing any of its 'time' values.
     * @param date The {@link Date} to be converted.
     * @return The created {@link Date} 'key'.
     */
    public static Date convertToKey (Date date) {
        // Safety first!
        if (date == null) return null;
        // Create a dateTime instance of the date
        DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
        // Re-create the date while leaving out all 'time-specific' fields
        return CalendarHelper.convertDateTimeToDate(new DateTime(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(), 0, 0, 0, 0));
    }

    /**
     * Changes the appropriate cells to 'event cells'.
     * @param events The events containing the {@link Date} to update.
     */
    private void updateEventUI (Event[] events) {
        // For as many times as there are events
        for (int h = 0; h < events.length; h ++) {
            // Create the cell image
            Drawable drawable;
            if ((CalendarActivity)getActivity() != null) {
                drawable = ((CalendarActivity)getActivity()).getResources().getDrawable(R.drawable.calendar_cell);
            } else {
                drawable = getResources().getDrawable(R.drawable.calendar_cell);
                Toast.makeText(getContext(), "A UI Error Occurred!\nError #" + getResources().getInteger(R.integer.DrawableCreationFailure), Toast.LENGTH_LONG).show();
            }

            // If date is today
            if (events[h].getStartDate().toString().substring(0, 10).equals(new Date().toString().substring(0, 10)))
                // If the date is today, then set it to 'today event cell'
                drawable.setLevel(5);
            else
                // Otherwise set it to 'default event cell'
                drawable.setLevel(1);
            // Change the background of the cell
            this.setBackgroundDrawableForDate(drawable, events[h].getStartDate());
        }
    }

    /**
     * States whether the given {@link Date} contains any events.
     * @param date The {@link Date} to check.
     * @return True if, and only if, the given {@link Date} contains events.
     */
    public boolean isDateEvent (Date date) {
        return eventBank.containsKey(convertToKey(date));
    }

    /**
     * Returns an event for the given {@link Date} 'key'.
     * @param date The {@link Date} whose events should be returned.
     * @return An Event array containing all events of the given date.
     */
    public Event[] getEventForDate (Date date) {
        Date key = convertToKey(date);
        if (eventBank.containsKey(key)) {
            Event[] outputType = new Event[eventBank.get(key).size()];
            return eventBank.get(key).toArray(outputType);
        } else {
            return null;
        }
    }

    /** States whether this calendar is empty. True when no calendar is available. */
    public boolean isEmpty() {
        return isEmpty;
    }

    /** Sets the 'empty' property of this calendar. Should only be true if there is no calendar database available. */
    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    /** States whether this calendar is offline. */
    public boolean isOffline() {
        return isOffline;
    }

    /** Sets the 'offline' property of this calendar. Should only be true if there is no web calendar available. */
    public void setOffline(boolean offline) {
        isOffline = offline;
    }

// MARK: PARSING
    /**
     * This method is used for parsing raw text calendar data into an array of {@link Event}s.
     * @param rawCalendarData A {@link String} representation of the raw text calendar data.
     * @param initiator The initiating {@link Service} instance, must be null unless dayDictionary caching is necessary.
     * @return The produced array of {@link Event}s.
     */
    public Event[] parseCalendar(String rawCalendarData, Service initiator) {
        Log.i("Calendar Background", "Initiating Parsing of Data");

        // An ArrayList containing the output events
        ArrayList<Event> events = new ArrayList<>();

        String dayDictionary = (initiator != null) ? "" : null;
        String lateStartDictionary = (initiator != null) ? "" : null;

        // An array of search flags, used for finding the proper event information
        // [h][0] - start flag, [h][1] - end flag
        String[][] searchFlag = {{"SUMMARY:", "TRANSP:"},
                {"DESCRIPTION:", "LAST-MODIFIED:"},
                {"DTSTART", "DTEND"},
                {"DTEND", "DTSTAMP"},
                {"LOCATION:", "SEQUENCE:"}};
        // An array of event configuring methods:
        // [0] - Sets the passed event's title
        // [1] - Sets the passed event's description
        // [2] - Sets the passed event's start date
        // [3] - Sets the passed event's end date
        // [4] - Sets the passed event's location
        // Executed as: eventField[h].assign (input, event);
        EventField[] eventField = {new EventField() {
            @Override   // Set Title
            public void assign(String input, Event event) {     // Set Title
                event.setTitle(input);
            }
        }, new EventField() {
            @Override   // Set Description
            public void assign(String input, Event event) {     // Set Description
                event.setDescription(input);
            }
        }, new EventField() {
            @Override   // Set Start Date
            public void assign(String input, Event event) {     // Set Start Date
                event.setStartDate(parseDate(input));               // Assign it to the event
            }
        }, new EventField() {
            @Override   // Set End Date
            public void assign(String input, Event event) {     // Set End Date
                event.setEndDate(parseDate(input));
            }
        }, new EventField() {
            @Override   // Set Location
            public void assign(String input, Event event) {     // Set Location
                event.setLocation(input);
            }
        }};

        // The current calendar contents that are being parsed, the top event gets chopped off once it has been parsed.
        Log.i("Calendar Background", "Commencing Parsing of Data");

        // Will repeat until there are events in "raw data to be parsed" (rawParseData)
        do {
            // Remove the unnecessary text before the search flag (either file header or parsed event)
            rawCalendarData = rawCalendarData.substring((rawCalendarData.indexOf("BEGIN:VEVENT")) + ("BEGIN:VEVENT".length()));
            // A holder for the start index of the current field
            int curIndexFlag = 0;
            // A holder for the current event being parsed
            Event curEvent = new Event();

            // Repeats once for every event field
            for (int h = 0; h < searchFlag.length; h++) {
                // Find the beginning index of the current field's data
                curIndexFlag = rawCalendarData.indexOf(searchFlag[h][0]) + (searchFlag[h][0].length());
                // Pass the current field's data to the appropriate setter method (Gotten from the eventField array)
                eventField[h].assign(rawCalendarData.substring(curIndexFlag, rawCalendarData.indexOf(searchFlag[h][1], curIndexFlag)), curEvent);

            }

            if (curEvent.getTitle().equalsIgnoreCase("LATE START") && lateStartDictionary != null) {
                lateStartDictionary += "" + convertToKey(curEvent.getStartDate()).toString() + ";\n";
            }

            // Once all event fields have been filled
            // Add the created event to the event bank
            if ((curEvent.getTitle().equalsIgnoreCase("DAY 1") || curEvent.getTitle().equalsIgnoreCase("DAY 2"))) {
                if (dayDictionary != null)
                    dayDictionary += "" + convertToKey(curEvent.getStartDate()).toString() + ":" + curEvent.getTitle().charAt(curEvent.getTitle().length() - 1) + ";\n";
                // Else do nothing
            } else {
                events.add(curEvent);
            }
        } while (rawCalendarData.contains("BEGIN:VEVENT"));
        // Create an array to fit the ArrayList
        Event[] outputArray = new Event[events.size()];

        if (dayDictionary != null) {
            SharedPreferences.Editor editor = initiator.getSharedPreferences(HomeActivity.sharedPreferencesName, 0).edit();
            editor.putString(keyDayDictionary, dayDictionary);
            editor.putString(keyLateStartDictionary, lateStartDictionary);
            editor.apply();
        }

        Log.i("Calendar Background", "Parsing of Data Success!");

        // Return the array, filled with the ArrayList's data
        return events.toArray(outputArray);
    }

    /**
     * This method parses a given {@link String} date taken from the calendar file, into a valid {@link Date} object.
     * The following are the formats that this method can parse:
     * - ;TZID=America/Toronto:20161028T204534
     * - :20161028T204534Z
     * - ;VALUE=DATE:20161028
     * @param input The {@link String} to parse.
     * @return The generate {@link Date} object.
     */
    private Date parseDate (String input) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");   // Declare the date formatter

        if (input.startsWith(";TZID")) {                            // Example ;TZID=America/Toronto:20120928T100000
            input = input.substring(input.indexOf(':'));            // Get rid of the timezone setting
        }   // Afterwards the next block should be able to handle it

        if (input.startsWith(":")) {                                // Example :20130410T230000Z
            input = input.replace("T", "");                         // Remove the 'T' in the middle
            input = input.replace("Z\r\n", "");                     // Remove the 'Z\r\n' at the end
            input = input.substring(input.indexOf(':') + 1);        // Remove the ':' at the beginning
            formatter = new SimpleDateFormat("yyyyMMddHHmmss");     // Create an appropriate date formatter
        } else if (input.startsWith(";")) {                         // Example ;VALUE=DATE:20170609
            // Remove the ';VALUE=DATE:' and the beginning as well as the '\r\n' at the end
            input = input.substring(input.indexOf(':') + 1, input.length() - 1);
        } else {
            input = "19970101";
            Toast.makeText(getContext(), "A Loading Error Occurred!\nError #" + getResources().getInteger(R.integer.DateParsingError), Toast.LENGTH_LONG).show();
        }

        Date output = null;
        try {
            output = formatter.parse(input);     // Create the date
        } catch (ParseException e) {                // If an exception is produced then log it
            Log.d("Date Parse Error", "Bad Date");
            Toast.makeText(getContext(), "A Loading Error Occurred!\nError #" + getResources().getInteger(R.integer.DateParsingError), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        return output;
    }

    /**
     * An Interface that is used to allow all the individual {@link Event} setter method to be
     * accessed in an Array like manner.
     */
    private interface EventField {
        void assign(String input, Event event);
    }

    // This override is to make the calendar use the Custom CaldroidGridAdapter.
    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        return new LyonsAdapter(getActivity(), month, year,
                getCaldroidData(), extraData);
    }
}

/** The GridViewAdapter that disables out-of-month dates. */
class LyonsAdapter extends CaldroidGridAdapter {
    private ArrayList<DateTime> outOfBoundDates = new ArrayList<>();

    public LyonsAdapter (Context context, int month, int year, Map<String, Object> caldroidData, Map<String, Object> extraData) {
        super(context, month, year, caldroidData, extraData);
    }

    @Override
    protected void customizeTextView(int position, CellView cellView) {
        super.customizeTextView(position, cellView);

        // Get dateTime of this cell
        DateTime dateTime = this.datetimeList.get(position);

        cellView.setVisibility(View.VISIBLE);

        // Set color of the visibility in previous / next month
        if (dateTime.getMonth() != month) {
            cellView.setVisibility(View.GONE);
            outOfBoundDates.add(dateTime);
        }

        if (position == getCount()) {
            setDisableDates(outOfBoundDates);
        }
    }
}