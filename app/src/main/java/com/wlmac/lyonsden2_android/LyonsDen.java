package com.wlmac.lyonsden2_android;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONException;

/**
 * Created by sketch204 on 2017-02-24.
 */

public class LyonsDen extends Application {
    public static String keySharedPreferences = "com.wlmac.lyonsden2_android";
    public static String keyEmail = "username";
    public static String keyPass = "password";
    public static String dayKey = "dayLabel";
    public static String sparesKey = "timeTableSpares";

    @Override
    public void onCreate() {
        super.onCreate();

        initializeOneSignal(true);
//        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getApplicationContext());
//
//        alertBuilder.setTitle("Hold on!");
//
//        alertBuilder.setMessage("Is this a spare?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                initializeOneSignal(true);
//                dialog.cancel();
//            }
//        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                initializeOneSignal(false);
//                dialog.cancel();
//            }
//        });
//        AlertDialog alertDialog = alertBuilder.create();
//        alertDialog.show();
    }

    public void initializeOneSignal(boolean response) {
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new OneSignal.NotificationOpenedHandler() {
            @Override
            public void notificationOpened(OSNotificationOpenResult result) {
                if (result.toJSONObject().has("actionSelected")) {
                    try {
                        Log.d("OneSignalExample", "OneSignal notification button with id " + result.toJSONObject().getString("actionSelected") + " pressed");
                        Log.d("OneSignalExample", "Full additionalData:\n" + result.toJSONObject().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                // This will be called when the notification is opened
            }
        }).setNotificationReceivedHandler(new OneSignal.NotificationReceivedHandler() {
            @Override
            public void notificationReceived(OSNotification notification) {
                // This will be called when the notif is received
            }
        }).inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .init();
        OneSignal.setSubscription(response);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}