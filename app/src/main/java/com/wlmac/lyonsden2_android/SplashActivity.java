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
    final boolean[] performIntent = {false, false};

    // TODO: I THINK THIS IS INSTANTIATING TOO EARLY!!!!
    SharedPreferences sharedPreferences;
    FirebaseAuth authenticator;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = this.getSharedPreferences(HomeActivity.sharedPreferencesName, Context.MODE_PRIVATE);

        authenticator = FirebaseAuth.getInstance();

        attemptLogIn();

        // Here's a 3 Second delay (I think its 3)
        int tick = 0;
        while (tick < 3) {
            tick ++;
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
        }

        // TODO: AutoLogin
        // TODO: Resize the Splash Screen graphic
    }

    private void performIntent () {
        // 0 == home 1 == log in
        if (performIntent[0]) {
            Intent intent = new Intent (this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (performIntent[1]) {
                Intent intent = new Intent (this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void attemptLogIn () {
        //Temporal placeholders
//        email = "g@gmail.com";
//        password = "Pok3monG0";
        email = sharedPreferences.getString("email", "default");
        password = sharedPreferences.getString("password", "defaultPass");
        if ((email != "default") && (password != "defaultPass")) {
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
            performIntent[0] = true;
            performIntent[1] = false;
        } else {
            performIntent[0] = false;
            performIntent[1] = true;
        }
        performIntent();
    }
}
