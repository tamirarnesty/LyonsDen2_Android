package com.wlmac.lyonsden2_android;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth authenticator;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        authListener = new FirebaseAuth.AuthStateListener() {
            // Called whenever changes are made to the login state of the current user.
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    // signed in
                    Intent intent = new Intent (getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    Log.i("Firebase Auth", "Log in Success!");
                } else {
                    // signed out
                    Log.i("Firebase Auth", "Log in Failed!");
                }
            }
        };
        // Create the authenticator
        authenticator = FirebaseAuth.getInstance();
        // Don't use this, you got your own, i'm just too lazy to make it proper :)
        authenticator.signInWithEmailAndPassword("sketch204@gmail.com", "Pok3monG0");
    }

    public void logIn (View view) {
        Intent intent = new Intent (this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        // To start the listener
        authenticator.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        // To stop the listener
        if (authListener != null) {
            authenticator.removeAuthStateListener(authListener);
        }
    }
}
