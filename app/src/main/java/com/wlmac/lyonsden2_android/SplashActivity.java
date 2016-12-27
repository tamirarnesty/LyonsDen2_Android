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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This class contains the Splash Screen of this program
 *
 * Created by sketch204 on 16-08-09.
 */
public class SplashActivity extends AppCompatActivity {
    static String email = "";
    static String password = "";

    // TODO: I THINK THIS IS INSTANTIATING TOO EARLY!!!!
    SharedPreferences sharedPreferences;
    FirebaseAuth authenticator;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final boolean[] performIntent = {false};

        // Gota initialize things here mate
        sharedPreferences = this.getSharedPreferences(HomeActivity.sharedPreferencesName, Context.MODE_PRIVATE);

        authenticator = FirebaseAuth.getInstance();

        attemptLogIn();

//        final Handler splashDuration = new Handler();
//        Runnable r = null;
//        final Runnable finalR = r;
//        splashDuration.postDelayed(r = new Runnable() {
//
//            int timer = 0;
//
//            @Override
//            public void run() {
//                timer += 1000;
//
//                if (timer == 3000) {
//                    performIntent[0] = true;
//                    splashDuration.removeCallbacks(finalR);
//                }
//            }
//        }, 1000);

        // ^ This put me in an infinite loop, idk what you were trying to do but...

        // Here's a 3 Second delay (I think its 3)
        int tick = 0;
        while (tick < 3) {
            tick ++;
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
        }

        // TODO: AutoLogin
        // TODO: Resize the Splash Screen graphic
//        if (performIntent[0]) {
            Intent intent = new Intent (this, LoginActivity.class);
            startActivity(intent);
            finish();
//        }
    }

    private void attemptLogIn () {
        // My shared preferences wiped for some reason, and i'm testing offline things so this was out temporarily

//        email = sharedPreferences.getString("username", "sketch204@gmail.com");
//        password = sharedPreferences.getString("password", "Pok3monG0");
//        if (email.equals("") || password.equals(""))

        //Temporal placeholders
        email = "g@gmail.com";
        password = "Pok3monG0";
        authenticator.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Splash Screen", "signInWithEmail:onComplete:" + task.isSuccessful());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.commit();
                    }
                });
    }
}
