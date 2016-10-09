package com.wlmac.lyonsden2_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.wlmac.lyonsden2_android.otherClasses.LyonsAlert;

// TODO: MAKE METHODS SWITACHBLE BASED ON PLATFORM VERSION (GET RID OF DEPRECATED METHOD)

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth authenticator;
    private FirebaseAuth.AuthStateListener authListener;
    private EditText emailField;
    private EditText passField;
    private EditText signUpKeyField;
    private boolean signUpSelected = true;
    private Button[] segmentedButtons = new Button[2];
    /** Store data permanently on device */
    SharedPreferences sharedPreferences = this.getSharedPreferences("com.wlmac.lyonsden2_android", Context.MODE_PRIVATE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        emailField = (EditText) findViewById(R.id.LSEmailField);
        passField = (EditText) findViewById(R.id.LSPassField);
        signUpKeyField = (EditText) findViewById(R.id.LSCodeField);
        segmentedButtons[0] = (Button) findViewById(R.id.LSSignUpButton);
        segmentedButtons[1] = (Button) findViewById(R.id.LSLoginButton);
        Drawable signUp = getResources().getDrawable(R.drawable.segmented_button);
        try {
            signUp.setLevel(1);
            segmentedButtons[0].setBackgroundDrawable(signUp);
            segmentedButtons[0].setTextColor(getResources().getColor(R.color.accent));
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
        //authenticator.signInWithEmailAndPassword("sketch204@gmail.com", "Pok3monG0");
    }

    // TODO: MAKE METHODS SWITCHABLE BASED ON PLATFORM VERSION (GET RID OF DEPRECATED METHOD)
    public void toggleSegmentedControl (View view) {
        if (view.getTag().toString().equals("signup") && !signUpSelected) { // Switch to signup
            signUpSelected = true;
            signUpKeyField.setVisibility(View.VISIBLE);
        } else if (view.getTag().toString().equals("login") && signUpSelected){ // Switch to login
            signUpSelected = false;
            signUpKeyField.setVisibility(View.GONE);
        } else {
            Log.d("LoginActivity", "toggleSegmentedControl() called from unknown source");
            return;
        }
        Drawable signUp = getResources().getDrawable(R.drawable.segmented_button);
        Drawable login = getResources().getDrawable(R.drawable.segmented_button);
        try {
            signUp.setLevel((signUpSelected) ? 1 : 0);
            segmentedButtons[0].setBackgroundDrawable(signUp);
            segmentedButtons[0].setTextColor(getResources().getColor((signUpSelected) ? R.color.accent : R.color.segmentedButtonUnselected));
            login.setLevel((!signUpSelected) ? 1 : 0);
            segmentedButtons[1].setBackgroundDrawable(login);
            segmentedButtons[1].setTextColor(getResources().getColor((!signUpSelected) ? R.color.accent : R.color.segmentedButtonUnselected));
        } catch (NullPointerException e) {
            Log.d("Login Activity:", "The segmented_button.xml file seems to missing or corrupted.");
        }
    }

    public void logIn (View view) {
        // initialize needed
        final boolean[] performIntent = {false};
        final String [] signUpKeys = {FirebaseDatabase.getInstance().getReference("java").child("type1").getKey(),
                                FirebaseDatabase.getInstance().getReference("java").child("type2").getKey()};

        if (signUpSelected) { // sign up
            if (signUpKeyField.getText().toString().equals(signUpKeys[0]) || signUpKeyField.getText().toString().equals(signUpKeys[1])) {
                authenticator.createUserWithEmailAndPassword(emailField.getText().toString(), passField.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("Login Activity", "createUserWithEmail:onComplete:" + task.isSuccessful());

                                if (signUpKeyField.getText().toString().equals(signUpKeys[1])) {
                                    // store teacher password into database for announcements
                                    FirebaseDatabase.getInstance().getReference("users").child("teacherIDs").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(passField.getText().toString());
                                }

                                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                    performIntent[0] = true;
                                }

                                if (!task.isSuccessful()) {

                                    Log.d("Login Activity", "Create user failure");
                                }
                            }
                });

            }
        } else if (!signUpSelected) { // log in
            authenticator.signInWithEmailAndPassword(emailField.getText().toString(), passField.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("Login Activity", "signInWithEmail:onComplete:" + task.isSuccessful());

                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                performIntent[0] = true;
                            }
                        }
                    });
        } else {
            Log.d("LoginActivity", "Log in or sign up attempt failed terribly.");
        }
        if (performIntent[0]) {
            // store on device
            sharedPreferences.edit().putString("password", passField.getText().toString()).apply();
            sharedPreferences.edit().putString("uID", emailField.getText().toString()).apply();
            // segue
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);

        }
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
