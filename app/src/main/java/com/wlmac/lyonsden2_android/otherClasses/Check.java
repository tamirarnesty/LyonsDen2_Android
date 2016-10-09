package com.wlmac.lyonsden2_android.otherClasses;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class can be used to check for an existing connection to the internet
 * This class is made very easy to use, the following line will return true only if a
 * <b>perfect</b> connection to a google server exists, otherwise return false.
 *
 * <i>new Check().execute(this)</i>
 *
 * Created by sketch204 on 2016-10-09.
 */

public class Check extends AsyncTask <Activity, Integer, Boolean> {
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
                connection.setConnectTimeout(5000); //choose your own timeframe
                connection.setReadTimeout(3000); //choose your own timeframe
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
