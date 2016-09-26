package com.wlmac.lyonsden2_android.otherClasses;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CalendarHelper;
import com.wlmac.lyonsden2_android.CalendarActivity;
import com.wlmac.lyonsden2_android.R;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import hirondelle.date4j.DateTime;

/**
 * This class will act as a customized version of CaldroidFragment, which is a calendar for android.
 * This customized version supports the handling of events.
 *
 * @author sketch204
 * @version 1, 2016/08/15
 */
public class LyonsCalendar extends CaldroidFragment {
    /** A HashMap of Events, each linked to its Date key. */
    private HashMap<Date, ArrayList<Event>> eventBank = new HashMap<>();
    /** The web address from which to download the calendar file */
    private final String calendarURL = "https://calendar.google.com/calendar/ical/wlmacci%40gmail.com/public/basic.ics";

    /**
     * Loads the events into the calendar database.
     * @param events The events to be loaded.
     */
    public void loadEvents (Event[] events) {
        // If there are no events to load, then return
        if (events.length < 1)
            return;
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
    }

    /**
     * Converts the given date to an appropriate key, by transforming the date into 'date-only' format.
     * Thereby clearing its 'time' value.
     * @param date The date to be converted.
     * @return The created 'key'.
     */
    private Date convertToKey (Date date) {
        // Safety first!
        if (date == null) return null;
        // Create a dateTime instance of the date
        DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
        // Re-create the date while leaving out all 'time-specific' fields
        return CalendarHelper.convertDateTimeToDate(new DateTime(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(), 0, 0, 0, 0));
    }

    /**
     * Changes the appropriate cell to 'event cells'.
     * @param events The events containing the dates to check.
     */
    private void updateEventUI (Event[] events) {
        // TODO: Change this to have only date, checking each event is inefficient.

        // For as many times as there are events
        for (int h = 0; h < events.length; h ++) {
            // Create the cell image
            Drawable drawable = ((CalendarActivity)getActivity()).getResources().getDrawable(R.drawable.calendar_cell);
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

    // This method is overriden only to load the events into the calendar, AFTER they have been downloaded
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LoadEventsFromWeb eventLoader = new LoadEventsFromWeb();
        try {
            this.loadEvents(eventLoader.execute(calendarURL).get());
        } catch (InterruptedException e) {} catch (ExecutionException e) {}
    }

    /**
     * States whether the given date contains any events.
     * @param date The date to check.
     * @return True if, and only if, the given date contains events.
     */
    public boolean isDateEvent (Date date) {
        return eventBank.containsKey(convertToKey(date));
    }

    /**
     * Returns an event for the given key.
     * @param date The date whose events should be returned.
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
}

/**
 * The class used for downloading the event bank in the background.
 *
 * @author sketch204
 * @version 1, 2016/08/10
 */
class LoadEventsFromWeb extends AsyncTask<String, Integer, Event[]> {

    @Override
    protected Event[] doInBackground(String... params) {
        try {
            return downloadAndParse(params[0]);
        } catch (IOException e) {
            Log.i("Calendar Data:", "Unable to record data");
        }
        return null;
    }

    /**
     * Downloads the calendar from the given web address, and parses it into
     * and array of Event objects to be loaded into LyonsCalendar.
     * @param webAddress The URL from which to download the calendar file.
     * @return An array of Event objects, each representing an event from the downloaded calendar.
     * @throws IOException Thrown if there are any problem during the downloading of the file.
     */
    private Event[] downloadAndParse (String webAddress) throws IOException {
        // An ArrayList containing the output events
        ArrayList<Event> events = new ArrayList<>();
        // An InputStream that is used to read data from the web
        InputStreamReader webContent = null;
        // The string that will hold the web data
        String webData = "";

        // MARK: DOWNLOADING

        // Declare the url object
        URL url = new URL(webAddress);
        // Create and setup the HTTP session
        HttpURLConnection session = (HttpURLConnection) url.openConnection();
        session.setReadTimeout(10000);      // Set its reading timeout
        session.setConnectTimeout(15000);   // Set its connecting timeout
        session.setRequestMethod("GET");    // Set its connection type
        session.setDoInput(true);           // State whether it will accept input
        // Execute the session
        session.connect();
        // Record and Log the response
        int response = session.getResponseCode();
        Log.d("HTTP Session", "Response Code " + response);
        // Create the web data reader
        webContent = new InputStreamReader(session.getInputStream());

        try {
            // Holder for the int version of the last read character
            int rawChar = webContent.read();
            // Holder for the last read character
            char currentChar;
            // While there is data
            while (rawChar != -1) {
                // Transform from integer to character
                currentChar = (char) rawChar;
                // Record character
                webData += currentChar;
                // Read integer character
                rawChar = webContent.read();
                // Repeat
            }
        } finally {     // Whether completed or not
            if (webContent != null) {
                // Close an open input stream
                webContent.close();
            }
        }

        // MARK: PARSING

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
        // [2] - Sets the passed event's start date (must be either in ":yyyymmddThhmmssZ\r\n" format or ";SOME_STRING:yyyymmdd\r\n" format
        // [3] - Sets the passed event's end date (must be either in ":yyyymmddThhmmssZ\r\n" format or ";SOME_STRING:yyyymmdd\r\n" format
        // [4] - Sets the passed event's location
        // Executed as: eventField[h].assign (input, event);
        EventField[] eventField = {new EventField() {
            @Override   // Set Title
            public void assign(String input, Event event) {
                event.setTitle(input);
            }
        }, new EventField() {
            @Override   // Set Description
            public void assign(String input, Event event) {
                event.setDescription(input);
            }
        }, new EventField() {
            @Override   // Set Start Date
            public void assign(String input, Event event) {
                SimpleDateFormat formatter;     // Declare the date formatter
                if (input.startsWith(":")) {    // If passed format is :yyyymmddThhmmssZ\r\n
                    input = input.replace("T", "");         // Remove the 'T' in the middle
                    input = input.replace("Z\r\n", "");     // Remove the 'Z\r\b' at the end
                    input = input.substring(input.indexOf(':') + 1);    // Remove the ':' at the beginning
                    formatter = new SimpleDateFormat("yyyyMMddHHmmss"); // Create an appropriate date formatter
                } else {    // If passed format is ;SOME_STRING:yyyymmdd\r\n
                    input = input.substring(input.indexOf(':') + 1, input.length() - 2);    // Remove the ';SOME_STRING:' and the beginning as well as the '\r\n' at the end
                    formatter = new SimpleDateFormat("yyyyMMdd");       // Create an appropriate date formatter
                }
                try {
                    Date date = formatter.parse(input);     // Create the date
                    event.setStartDate(date);               // Assign it to the event
                } catch (ParseException e) {                // If an exception is produced then log it
                    Log.d("Date Parse Error", "Start Date");
                    e.printStackTrace();
                }
            }
        }, new EventField() {
            @Override   // Set End Date
            public void assign(String input, Event event) {
                SimpleDateFormat formatter;     // Declare the date formatter
                if (input.startsWith(":")) {    // If passed format is :yyyymmddThhmmssZ\r\n
                    input = input.replace("T", "");         // Remove the 'T' in the middle
                    input = input.replace("Z\r\n", "");     // Remove the 'Z\r\b' at the end
                    input = input.substring(input.indexOf(':') + 1);    // Remove the ':' at the beginning
                    formatter = new SimpleDateFormat("yyyyMMddHHmmss"); // Create an appropriate date formatter
                } else {    // If passed format is ;SOME_STRING:yyyymmdd\r\n
                    input = input.substring(input.indexOf(':') + 1, input.length() - 2);    // Remove the ';SOME_STRING:' and the beginning as well as the '\r\n' at the end
                    formatter = new SimpleDateFormat("yyyyMMdd");       // Create an appropriate date formatter
                }
                try {
                    Date date = formatter.parse(input);     // Create the date
                    event.setEndDate(date);                 // Assign it to the event
                } catch (ParseException e) {                // If an exception is produced then log it
                    Log.d("Date Parse Error", "End Date");
                    e.printStackTrace();
                }
            }
        }, new EventField() {
            @Override   // Set Location
            public void assign(String input, Event event) {
                event.setLocation(input);
            }
        }};

        // The current calendar contents that are being parsed, the top event gets chopped off once it has been parsed.
        String rawParseData = webData;
        // Will repeat until there are events in "raw data to be parsed" (rawParseData)
        do {
            // Chop off the unnecessary data at the top (either file header or parsed event)
            rawParseData = rawParseData.substring((rawParseData.indexOf("BEGIN:VEVENT")) + ("BEGIN:VEVENT".length()));
            // A holder for the start index of the current field
            int curIndexFlag = 0;
            // A holder for the current event being parsed
            Event curEvent = new Event();
            // Repeats once for every event field
            for (int h = 0; h < searchFlag.length; h ++) {
                // Find the beginning index of the current field's data
                curIndexFlag = rawParseData.indexOf(searchFlag[h][0]) + (searchFlag[h][0].length());
                // Pass the current field's data to the appropriate setter method (Gotten from the eventField array)
                eventField[h].assign(rawParseData.substring(curIndexFlag, rawParseData.indexOf(searchFlag[h][1], curIndexFlag)), curEvent);
            }
            // Once all event fields have been filled
            // Add the created event to the event ArrayList
            events.add(curEvent);
        } while (rawParseData.contains("BEGIN:VEVENT"));
        // Create an array to fit the ArrayList
        Event[] outputArray = new Event[events.size()];
        // Return the array, filled with the ArrayList's data
        return events.toArray(outputArray);
    }

    // An interface that is used to bind all the setter method into an array
    private interface EventField {
        void assign(String input, Event event);
    }
}