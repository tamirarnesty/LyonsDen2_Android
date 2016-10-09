package com.wlmac.lyonsden2_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by sketch204 on 16-08-09.
 */
public class SplashActivity extends AppCompatActivity {
    static String email = "";
    static String password = "";
    SharedPreferences sharedPreferences = this.getSharedPreferences("com.wlmac.lyonsden2_android", Context.MODE_PRIVATE);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final boolean[] performIntent = {false};

        attemptLogIn();

        final Handler splashDuration = new Handler();
        Runnable r = null;
        final Runnable finalR = r;
        splashDuration.postDelayed(r = new Runnable() {

            int timer = 0;

            @Override
            public void run() {
                timer += 1000;

                if (timer == 3000) {
                    performIntent[0] = true;
                    splashDuration.removeCallbacks(finalR);
                }
            }
        }, 1000);
        // TODO: AutoLogin
        // TODO: Resize the Splash Screen graphic
        if (performIntent[0]) {
        Intent intent = new Intent (this, LoginActivity.class);
        startActivity(intent);
        finish(); }
    }

    private void attemptLogIn () {
        email = sharedPreferences.getString("username", "");
        password = sharedPreferences.getString("password", "");

        
    }
}
