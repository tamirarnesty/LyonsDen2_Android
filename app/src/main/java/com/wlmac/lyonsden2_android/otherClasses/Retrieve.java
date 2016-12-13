package com.wlmac.lyonsden2_android.otherClasses;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by sketch204 on 2016-11-15.
 */

public class Retrieve {

    public static void eventData (DatabaseReference ref, final ArrayList<String[]> target, final ListDataHandler handler) {
        Log.d("Event Parser", "Commencing Parse!");
        ref.orderByChild("dateTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String[] keys = {"title", "description", "dateTime", "location"};
                if (dataSnapshot.exists()) {
                    target.clear();

                    for (DataSnapshot event : dataSnapshot.getChildren()) {
                        target.add(new String[4]);
                        for (int h = 0; h < keys.length; h ++) {
                            try {
                                if (h == 2) { target.get(target.size() - 1)[h] = convertToDate(event.child(keys[h]).getValue(String.class)); }
                                else { target.get(target.size() - 1)[h] = event.child(keys[h]).getValue(String.class); }
                            } catch (DatabaseException e) {
                                target.get(target.size() - 1)[h] = event.child(keys[h]).getValue(Long.class).toString();
                            }
                        }
                    }
                    handler.handle(reverse(target));
                    Log.d("Event Retriever", "Retrieval Success!");
                } else {
                    handler.handle(null);
                    Log.d("Event Retriever", "Reference does not exist!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                handler.handle(null);
                Log.d("Event Retriever", "Request Cancelled!");
            }
        });
    }

    public static void clubData (DatabaseReference ref, final ArrayList<String[]> target, final ListDataHandler handler) {
        ref.orderByChild("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String[] keys = {"title", "description", "leads"};

                if (dataSnapshot.exists()) {
                    target.clear();
                    for (DataSnapshot club : dataSnapshot.getChildren()) {
                        target.add(new String[4]);
                        target.get(target.size() - 1)[3] = club.getKey();
                        for (int h = 0; h < keys.length; h ++) {
                            target.get(target.size() - 1)[h] = club.child(keys[h]).getValue(String.class);
                        }
                    }
                    handler.handle(target);
                    Log.d("Club Retriever", "Retrieval Success!");
                } else {
                    Log.d("Club Retriever", "Reference does not exist!");
                    handler.handle(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Club Retriever", "Request Cancelled!");
                handler.handle(null);
            }
        });
    }

    public static void teacherApproval (final String key, final StatusHandler handle) {
        FirebaseDatabase.getInstance().getReference("users").child("teacherIDs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot teacher : dataSnapshot.getChildren()) {
                    if (encrypted(key).equals(teacher.getValue(String.class))) {
                        handle.handle(true);
                        return;
                    }
                }
                handle.handle(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static String encrypted (String key) {
        return key;
    }

    public static void oneSignalIDs (OneSignalHandler handle) {

    }

    public static boolean isInternetAvailable(Activity initiator) {
        class Check extends AsyncTask<Activity, Integer, Boolean> {
            /** The initiating activity. Used to get the proper instance of a ConnectivityManager. */
            private Activity initiator;

            @Override
            protected Boolean doInBackground(Activity... params) {
                initiator = params[0];
                return isInternetAvailable();
            }

            /**
             * The method checks for internet connectivity. It first check if the device is connected to a network, afterwards
             * it attempts to connect to <i>http://www.google.ca</i>, if a connection was established then it will return true.
             * Otherwise it will return false.
             * @return true if a connection to <i>http://www.google.ca</i> was possible, false otherwise.
             */
            public boolean isInternetAvailable () {
                ConnectivityManager cm = (ConnectivityManager) initiator.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();

                boolean isConnectedToNetwork = netInfo != null && netInfo.isConnected();

                if (isConnectedToNetwork) {
                    try {
                        HttpURLConnection connection = (HttpURLConnection) (new URL("http://www.google.ca").openConnection());
                        connection.setRequestProperty("User-Agent", "Test");
                        connection.setRequestProperty("Connection", "close");
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(3000);
                        connection.connect();
                        return (connection.getResponseCode() == 200);
                    } catch (IOException e) {
                        return (false);  //connectivity exists, but no internet.
                    }
                } else {
                    return false;  //no connectivity
                }
            }
        }

        try {
            return new Check().execute(initiator).get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    public static void oneSignalStatus () {

    }

    public static int heightForText (String text, Activity initiator, int textSize) {
        TextView textView = new TextView(initiator.getApplicationContext());
        textView.setTypeface(typeface(initiator));
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textView.measure(View.MeasureSpec.makeMeasureSpec(screenSize(initiator).x, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return textView.getMeasuredHeight();
    }

    /**
     * Retrieves the current screen size from the system and returns it as a Point.
     * @return A Point representing the current screen size.
     */
    public static Point screenSize(Activity initiator) {
        // Declare the display object of the current device
        Display display = initiator.getWindowManager().getDefaultDisplay();
        // Declare the instance of the size of the display
        Point size = new Point();
        // Instantiate the size instance
        display.getSize(size);
        return size;
    }

    public static Typeface typeface (Activity initiator) {
        return Typeface.createFromAsset(initiator.getAssets(), "fonts/OpenSans-Light.ttf");
    }

    /**
     * This method is used to get a {@link String} representation of the day of the given {@link Date} from
     * the given {@link String} Day Dictionary.
     * @param dictionary A {@link String} instance of the Day Dictionary to use.
     * @param date The date to look for.
     * @return A {@link String} representation of the day. Will only return the number of the day. If no day is available will return -1.
     */
    public static String dayFromDictionary (String dictionary, Date date) {
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

// MARK: HELPER METHODS

    private static ArrayList<String[]> reverse (ArrayList<String[]> input) {
        String[] tempHold;
        for (int h = 0; h < input.size()/2; h ++) {
            tempHold = input.get(h);
            input.set(h, input.get(input.size() - 1 - h));
            input.set(input.size() - 1 - h, tempHold);
        }
        return input;
    }

    private static String convertToDate (String input) {
        String output = input.substring(0, 4) + "-" + input.substring(4, 6) + "-" + input.substring(6, 8);;
        if (!input.substring(8, 12).equals("0000") && !input.substring(8, 12).equals("2400"))
            output += " " + input.substring(8, 10) + ":" + input.substring(10, 12);
        return output;
    }

    private static String convertToTimeStamp (String input) {
        return null;
    }

// MARK: HANDLER INTERFACES

    public interface ListDataHandler {
        void handle (ArrayList<String[]> listData);
    }

    public interface StatusHandler {
        void handle (boolean status);
    }

    public interface OneSignalHandler {
        void handle (String[] receivers);
    }
}