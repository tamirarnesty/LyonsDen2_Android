package com.wlmac.lyonsden2_android.otherClasses;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.wlmac.lyonsden2_android.LyonsDen;
import com.wlmac.lyonsden2_android.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous calendar loading on a separate handler thread.
 *
 * @author sketch204
 * @version 1, 2016/10/08
 */
public class WebCalendar extends IntentService {
    /** Web-link to the google calendar. */
    private static final String webAddress = "https://calendar.google.com/calendar/ical/wlmacci%40gmail.com/public/basic.ics";
    /** An instance holder of a {@link LyonsCalendar}, into which the web calendar will be loaded. */
    private static LyonsCalendar targetCalendar;
    /** Number of reconnect attempts, once reached 3 will stop attempting. */
    private int numberOfAttempts = 1;

    /** An action definition for downloading and loading the calendar without creating a Day Dictionary Cache. */
    public static final String actionDownload = "downloadNoDayDictionaryCaching";
    /** An action definition for downloading and loading the calendar while also creating a Day Dictionary Cache. */
    public static final String actionDownloadWithCache = "downloadWithDayDictionaryCaching";
    /** An action definition for downloading the calendar and create a Day Dictionary Cache out of it. */
    public static final String actionCacheOnly = "downloadForDayDictionaryCacheOnly";
    /** An action definition for loading the last saved instance of the calendar. */
    public static final String actionLoadOffline = "loadOfflineCalendar";

    /** The default constructor of this Service. */
    public WebCalendar() {
        super("WebCalendar");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void downloadInto(LyonsCalendar localCalendar, Context context, String action) {
        Log.i("WebCalendar", "Received Request for " + action);
        targetCalendar = localCalendar;
        Intent intent = new Intent(context, WebCalendar.class);
        intent.setAction(action);
        context.startService(intent);
    }

// MARK: DOWNLOADING
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("WebCalendar", "Handling Request...");
        if (intent != null && !intent.getAction().equals(actionLoadOffline)) {
            String webData = "";        // The string that will hold the web data

            Log.i("WebCalendar", "Initiating Download");
            try {
                // Declare the calendar url
                URL url = new URL(webAddress);
                // Create and setup the HTTP session
                HttpURLConnection session = (HttpURLConnection) url.openConnection();
                session.setReadTimeout(10000);          // Set its reading timeout
                session.setConnectTimeout(15000);       // Set its connecting timeout
                session.setRequestMethod("GET");        // Set its connection type
                session.setDoInput(true);               // State whether it will accept input
                try {
                    // Start the session
                    session.connect();
                    webData = Retrieve.stringFromStream(session.getInputStream());
                } catch (IOException e) {   // If connection Failed
                    Log.i("WebCalendar", "Recording Process Failed!");
                    Log.i("WebCalendar", "Trying to Reconnect");
                    if (numberOfAttempts < 4) {
                        numberOfAttempts++;
                        onHandleIntent(intent);     // Will perform a recursive call to self and hopefully
                        return;                     // Return from all of the 'caller' instances
                    } else {                        // Haven't actually tested this, but the theory checks out
                        Toast.makeText(getApplicationContext(), "Connection Lost!\nError #" + getResources().getInteger(R.integer.ConnectionAttemptsAmountExceeded), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Failed to Connect to Servers!\nError #" + getResources().getInteger(R.integer.FailedToOpenConnection), Toast.LENGTH_LONG).show();
                Log.i("WebCalendar", "Calendar Download Failed!");
                Log.i("WebCalendar", e.toString());
            }
            Log.i("WebCalendar", "Download of Data Success!");

            // Perform the necessary operations based on the necessary actions
            if (targetCalendar != null && intent.getAction().equals(actionDownload))                // Action 1
                targetCalendar.loadEvents(targetCalendar.parseCalendar(webData, null));
            else if (targetCalendar != null && intent.getAction().equals(actionDownloadWithCache))  // Action 2
                targetCalendar.loadEvents(targetCalendar.parseCalendar(webData, this));
            else if (targetCalendar != null && intent.getAction().equals(actionCacheOnly))       // Action 3
                targetCalendar.parseCalendar(webData, this);

            // Cache the calendar file
            SharedPreferences.Editor cache = getSharedPreferences(LyonsDen.keySharedPreferences, 0).edit();
            cache.putString(LyonsCalendar.keyCalendarCache, webData);
            cache.apply();
            // If the request was to load the calendar offline, from last cached file
        } else if (intent.getAction().equals(actionLoadOffline)) {      // Action 4
            String offlineCalendar = getSharedPreferences(LyonsDen.keySharedPreferences, 0).getString(LyonsCalendar.keyCalendarCache, null);
            targetCalendar.loadEvents(targetCalendar.parseCalendar(offlineCalendar, null));
        }
    }
}
