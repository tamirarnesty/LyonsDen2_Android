package com.wlmac.lyonsden2_android;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// TODO: MAKE METHODS SWITACHBLE BASED ON PLATFORM VERSION (GET RID OF DEPRECATED METHOD)

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth authenticator;
    private FirebaseAuth.AuthStateListener authListener;
    private EditText emailField;
    private EditText passField;
    private EditText codeField;
    private boolean signUpSelected = true;
    private Button[] segmentedButtons = new Button[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        emailField = (EditText) findViewById(R.id.LSEmailField);
        passField = (EditText) findViewById(R.id.LSPassField);
        codeField = (EditText) findViewById(R.id.LSCodeField);
        segmentedButtons[0] = (Button) findViewById(R.id.LSSignUpButton);
        segmentedButtons[1] = (Button) findViewById(R.id.LSLoginButton);
        Drawable signUp = getResources().getDrawable(R.drawable.segmented_button);
        try {
            signUp.setLevel(1);
            segmentedButtons[0].setBackgroundDrawable(signUp);
            segmentedButtons[0].setTextColor(getResources().getColor(R.color.accentColor));
        } catch (NullPointerException e) {
            Log.d("Login Activity:", "The segmented_button.xml file seems to missing or corrupted.");
        }

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

    // TODO: MAKE METHODS SWITCHABLE BASED ON PLATFORM VERSION (GET RID OF DEPRECATED METHOD)
    public void toggleSegmentedControl (View view) {
        if (view.getTag().toString().equals("signup") && !signUpSelected) { // Switch to signup
            signUpSelected = !signUpSelected;
            codeField.setVisibility(View.VISIBLE);
        } else if (view.getTag().toString().equals("login") && signUpSelected){ // Switch to login
            signUpSelected = !signUpSelected;
            codeField.setVisibility(View.GONE);
        } else {
            Log.d("LoginActivity", "toggleSegmentedControl() called from unknown source");
            return;
        }
        Drawable signUp = getResources().getDrawable(R.drawable.segmented_button);
        Drawable login = getResources().getDrawable(R.drawable.segmented_button);
        try {
            signUp.setLevel((signUpSelected) ? 1 : 0);
            segmentedButtons[0].setBackgroundDrawable(signUp);
            segmentedButtons[0].setTextColor(getResources().getColor((signUpSelected) ? R.color.accentColor : R.color.backgroundColor));
            login.setLevel((!signUpSelected) ? 1 : 0);
            segmentedButtons[1].setBackgroundDrawable(login);
            segmentedButtons[1].setTextColor(getResources().getColor((!signUpSelected) ? R.color.accentColor : R.color.backgroundColor));
        } catch (NullPointerException e) {
            Log.d("Login Activity:", "The segmented_button.xml file seems to missing or corrupted.");
        }
    }

    public void logIn (View view) {
        if (codeField.getVisibility() == View.GONE)
            codeField.setVisibility(View.VISIBLE);
        else if (codeField.getVisibility() == View.VISIBLE)
            codeField.setVisibility(View.GONE);
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
