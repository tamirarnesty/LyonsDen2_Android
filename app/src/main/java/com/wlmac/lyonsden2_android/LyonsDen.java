package com.wlmac.lyonsden2_android;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

/**
 * Created by sketch204 on 2017-02-24.
 */

public class LyonsDen extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("Lyons Den", "I CALLED MUTHERFUCKERS!!!!!!!!!!!");

        OneSignal.startInit(this).setNotificationOpenedHandler(new OneSignal.NotificationOpenedHandler() {
            @Override
            public void notificationOpened(OSNotificationOpenResult result) {
                // This will be called when the notification is opened
            }
        }).setNotificationReceivedHandler(new OneSignal.NotificationReceivedHandler() {
            @Override
            public void notificationReceived(OSNotification notification) {
                // This will be called when the notif is received
            }
        }).init();
//        OneSignal.startInit(this).
        OneSignal.setSubscription(true);
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