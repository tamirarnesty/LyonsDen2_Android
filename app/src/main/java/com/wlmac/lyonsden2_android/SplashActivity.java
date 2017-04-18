package com.wlmac.lyonsden2_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;
import com.wlmac.lyonsden2_android.resourceActivities.GuideActivity;

import java.util.Calendar;

/**
 * This class contains the Splash Screen of this program
 *
 * Created by sketch204 on 16-08-09.
 */
public class SplashActivity extends AppCompatActivity {
    static String email;
    static String password;
    final boolean[] performIntent = {false, false};

    SharedPreferences sharedPreferences;
    FirebaseAuth authenticator;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        try {
            ((TextView) findViewById(R.id.SSCopyright)).setText("Copyright Tamir Arnesty & Inal Gotov © " + Calendar.getInstance().get(Calendar.YEAR));
            ((TextView) findViewById(R.id.SSCopyright)).setTypeface(Retrieve.typeface(this));
            findViewById(R.id.SSCopyright).bringToFront();
        } catch (NullPointerException e) {}

        sharedPreferences = this.getSharedPreferences(LyonsDen.keySharedPreferences, Context.MODE_PRIVATE);
        authenticator = FirebaseAuth.getInstance();

        email = sharedPreferences.getString(LyonsDen.keyEmail, "");
        password = sharedPreferences.getString(LyonsDen.keyPass, "");
        int tick = 0;
        while (tick < 4) {
            tick ++;
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
        }

        attemptOfflineLogIn();
    }

    private void attemptOfflineLogIn() {

        if (Retrieve.isInternetAvailable(this)) {
            Log.d("Splash Screen", "internet available");
            attemptLogIn();
        } else {
            offlineOccurrence();
        }
    }

    private void offlineOccurrence () {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d("Splash Screen", "internet not available. going home");
            Intent intent = new Intent (SplashActivity.this, HomeActivity.class);
            intent.putExtra("isInternetAvailable", false);
            startActivity(intent);
            finish();
        } else {
            Log.d("Splash Screen", "internet not available. going to log in");
            Intent intent = new Intent (SplashActivity.this, LoginActivity.class);
            intent.putExtra("isInternetAvailable", false);
            startActivity(intent);
            finish();
        }

    }

    private void attemptLogIn () {
        if (email.equals("") || password.equals("")) { // log in
            Log.d("Splash Screen", email);
            Log.d("Splash Screen", password);
            Log.d("Splash Screen", "no credentials saved on device");
            performIntent[0] = true;
            performIntent[1] = false;
            performIntent();
        } else {
            authenticator.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("Splash Screen", "signInWithEmail:onComplete:" + task.isSuccessful());
                    if (task.isSuccessful()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(LyonsDen.keyEmail, email);
                        editor.putString(LyonsDen.keyPass, password);
                        editor.apply();
                        performIntent[0] = false;
                        performIntent[1] = true;
                    } else {
                        if (!task.isSuccessful()) {
                            performIntent[0] = true;
                            performIntent[1] = false;
                        }
                    }
                    performIntent();
                }
            });
        }
    }

    private void performIntent () {
        Intent intent; // 0 = log in 1 = home
        if (performIntent[0]) {
            Log.d("Splash Screen", "segue to log in");
            intent = new Intent (SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (performIntent[1]) {
                Log.d("Splash Screen", "segue to home");
                intent = new Intent (SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
