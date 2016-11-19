package com.wlmac.lyonsden2_android.otherClasses;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.concurrent.ExecutionException;

/**
 * Created by sketch204 on 2016-11-15.
 */

public class Retrieve {

    public static void eventData (DatabaseReference ref, final EventDataHandler handler) {
        Log.d("Event Parser", "Commencing Parse!");
        ref.orderByChild("dateTime").addListenerForSingleValueEvent(new ValueEventListener() {
            ArrayList<String[]> eventData = new ArrayList<>();
            private String[] keys = {"title", "description", "dateTime", "location"};

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot event : dataSnapshot.getChildren()) {
                        eventData.add(new String[4]);
                        for (int h = 0; h < keys.length; h ++) {
                            try {
                                if (h == 2) { eventData.get(eventData.size() - 1)[h] = convertToDate(event.child(keys[h]).getValue(String.class)); }
                                else { eventData.get(eventData.size() - 1)[h] = event.child(keys[h]).getValue(String.class); }
                            } catch (DatabaseException e) {
                                eventData.get(eventData.size() - 1)[h] = event.child(keys[h]).getValue(Long.class).toString();
                            }
                        }
                    }
                    String[][] typeHolder = new String[0][0];
                    handler.handle(reverse(eventData.toArray(typeHolder)));
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

    public static void clubData (DatabaseReference ref, final ArrayList<String[]> target, final ClubDataHandler handler) {
        ref.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
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

// MARK: HELPER METHODS

    private static String convertToDate (String input) {
        String output = input.substring(0, 4) + "-" + input.substring(4, 6) + "-" + input.substring(6, 8);;
        if (!input.substring(8, 12).equals("0000") && !input.substring(8, 12).equals("2400"))
            output += " " + input.substring(8, 10) + ":" + input.substring(10, 12);
        return output;
    }

    private static String[][] reverse (String[][] input) {
        String[] tempHold;
        for (int h = 0; h < input.length/2; h ++) {
            tempHold = input[h];
            input[h] = input[input.length - 1 - h];
            input[input.length - 1 - h] = tempHold;
        }
        return input;
    }

    private static String convertToTimeStamp (String input) {
        return null;
    }

// MARK: HANDLER INTERFACES

    public interface EventDataHandler {
        void handle (String[][] eventData);
    }

    public interface ClubDataHandler {
        void handle (ArrayList<String[]> clubData);
    }

    public interface StatusHandler {
        void handle (boolean status);
    }

    public interface OneSignalHandler {
        void handle (String[] receivers);
    }
}