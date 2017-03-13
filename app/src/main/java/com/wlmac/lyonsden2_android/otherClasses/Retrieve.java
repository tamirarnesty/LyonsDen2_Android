package com.wlmac.lyonsden2_android.otherClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.wlmac.lyonsden2_android.CalendarActivity;
import com.wlmac.lyonsden2_android.ContactActivity;
import com.wlmac.lyonsden2_android.HomeActivity;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.UserActivity;
import com.wlmac.lyonsden2_android.lyonsLists.ClubList;
import com.wlmac.lyonsden2_android.lyonsLists.EventList;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * This class contains <u>black box</u> methods that will occur throughout the app multiple times.
 * Its purpose is to make the app run more efficiently.
 *
 * Created by sketch204 on 2016-11-15.
 */

public class Retrieve {

    public static void eventData (final Context context, DatabaseReference ref, final ArrayList<String[]> target, final ListDataHandler handler) {
        Log.d("Event Parser", "Commencing Parse!");
        ref.orderByChild("dateTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String[] keys = {"title", "description", "dateTime", "location", "creator"};
                if (dataSnapshot.exists()) {
                    target.clear();

                    for (DataSnapshot event : dataSnapshot.getChildren()) {
                        target.add(new String[5]);
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
                    Toast.makeText(context,
                            "Another Error Occurred!\n Error #" + context.getResources().getInteger(R.integer.DatabaseDirectoryNonExistent),
                            Toast.LENGTH_LONG).show();
                    handler.handle(null);
                    Log.d("Event Retriever", "Reference does not exist!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context,
                        "Another Error Occurred!\n Error #" + context.getResources().getInteger(R.integer.DatabaseOperationCancelled),
                        Toast.LENGTH_LONG).show();
                handler.handle(null);
                Log.d("Event Retriever", "Request Cancelled!");
            }
        });
    }

    public static void clubData (final Context context, DatabaseReference ref, final ArrayList<String[]> target, final ListDataHandler handler) {
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
                    Toast.makeText(context,
                            "Another Error Occurred!\n Error #" + context.getResources().getInteger(R.integer.DatabaseDirectoryNonExistent),
                            Toast.LENGTH_LONG).show();
                    Log.d("Club Retriever", "Reference does not exist!");
                    handler.handle(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context,
                        "Another Error Occurred!\n Error #" + context.getResources().getInteger(R.integer.DatabaseOperationCancelled),
                        Toast.LENGTH_LONG).show();
                Log.d("Club Retriever", "Request Cancelled!");
                handler.handle(null);
            }
        });
    }

    public static void teacherApproval (final Context context, final String key, final StatusHandler handle) {
        FirebaseDatabase.getInstance().getReference("users").child("teacherIDs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot teacher : dataSnapshot.getChildren()) {
                        if (encrypted(key).equals(teacher.getValue(String.class))) {
                            handle.handle(true);
                            return;
                        }
                    }
                    handle.handle(false);
                } else {
                    Toast.makeText(context,
                            "Another Error Occurred!\n Error #" + context.getResources().getInteger(R.integer.DatabaseDirectoryNonExistent),
                            Toast.LENGTH_LONG).show();
                    handle.handle(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context,
                        "Another Error Occurred!\n Error #" + context.getResources().getInteger(R.integer.DatabaseOperationCancelled),
                        Toast.LENGTH_LONG).show();
                handle.handle(false);
            }
        });
    }

    public static String encrypted (String key) {
        return key;
    }

    public static void isUserTeacher(final Context context, final String key, final StatusHandler handler) {
        FirebaseDatabase.getInstance().getReference("users/teachers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d.getKey().equals(key)) {
                        handler.handle(true);
                        return;
                    }
                }
                handler.handle(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context,
                        "Another Error Occurred!\n Error #" + context.getResources().getInteger(R.integer.DatabaseOperationCancelled),
                        Toast.LENGTH_LONG).show();
                handler.handle(false);
                Log.d("Event Retriever", "Request Cancelled!");
            }
        });
    }

    // Used for retrieving a list of OneSignal recievers' IDs for notification
    public static void oneSignalIDs (final OneSignalHandler handle) {
        FirebaseDatabase.getInstance().getReference("/users/notificationIDs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> output = new ArrayList<String>(dataSnapshot.getValue(new GenericTypeIndicator<Map<String, String>>() {}).values());
                    handle.handle(output);
                } else {
                    handle.handle(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void oneSignalStatus () {
        Log.d("OneSignalStatusChecker", "Checking Status...");
        Retrieve.oneSignalIDs(new OneSignalHandler() {
            @Override
            public void handle(final ArrayList<String> receivers) {
                OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                    @Override
                    public void idsAvailable(String userId, String registrationId) {
                        if (userId != null && !receivers.contains(userId)) {
                            Log.d("OneSignalStatusChecker", "One Signal ID Does not exist. Creating...");
                            String firUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            if (firUID != null) {
                                FirebaseDatabase.getInstance().getReference("users/notificationIDs/" + firUID).setValue(userId);
                            } else {
                                Log.d("OneSignalStatusChecker", "Creation Failed! Not authenticated with Firebase!");
                            }
                        } else Log.d("OneSignalStatusChecker", "One Signal ID Exists.");
                    }
                });
            }
        });
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

    public static int heightForText (String text, Activity initiator, int textSize) {
        TextView textView = new TextView(initiator.getApplicationContext());
        textView.setTypeface(typeface(initiator));
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textView.measure(View.MeasureSpec.makeMeasureSpec(screenSize(initiator).x, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return textView.getMeasuredHeight();
    }

    public static int dpFromInt (int input, Resources resources) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, input, resources.getDisplayMetrics());
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

    public static Typeface typeface (Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Light.ttf");
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
        int index = dictionary.indexOf(key);

        if (index != -1) {
            index += key.length() + 1;
            try {
                return "" + dictionary.charAt(index);
            } catch (IndexOutOfBoundsException e) {
                return "-1";
            }
        } else {
            return "-1";
        }
        //date:1;20160203:2;
    }

    public static boolean isLateStartDay (String dictionary, Date date) {
        String key = LyonsCalendar.convertToKey(date).toString();
        return dictionary.contains(key);
    }

    /** A helper method that creates a {@link String} representation out of the contents of the passed {@link java.io.InputStream} */
    public static String stringFromStream (java.io.InputStream is) {
        if (is == null) return "";
        // Tell the scanner to convert the whole string into a single token
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


    /** A list of item that will be displayed in the every drawer list of this program. */
    private static String[] drawerContent = {"Home", "Calendar", "Announcements", "Clubs", "Contact", "Me"};

    /**
     * The method used for a quick and easy setup of a default drawer in the current view.
     * @param initiator The activity that is calling this method. ('this' arguement will work most of the time)
     * @param drawerList The drawer list that is bind to the initiating activity.
     * @param rootLayout The root layout of the initiating activity.
     * @param drawerToggle The drawer toggle of the initiating activity.
     */
    public static void drawerSetup(final AppCompatActivity initiator, ListView drawerList, DrawerLayout rootLayout, ActionBarDrawerToggle drawerToggle) {
        // Declare the drawer list adapter, to fill the drawer list
        drawerList.setAdapter(new ArrayAdapter<String>(initiator, android.R.layout.simple_selectable_list_item, Retrieve.drawerContent) {
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
                Retrieve.drawerSegue(initiator, position); // Segue into the appropriate Activity
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
    public static ActionBarDrawerToggle drawerToggle(AppCompatActivity initiator, DrawerLayout rootLayout) {
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

    public static void drawerSegue(AppCompatActivity initiator, int activity) {
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
        initiator.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        ((DrawerLayout) initiator.findViewById(R.id.NDLayout)).closeDrawers();
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
        void handle (ArrayList<String> receivers);
    }
}