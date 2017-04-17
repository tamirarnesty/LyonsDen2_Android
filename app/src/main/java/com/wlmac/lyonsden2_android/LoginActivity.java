package com.wlmac.lyonsden2_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wlmac.lyonsden2_android.otherClasses.LyonsAlert;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;
import com.wlmac.lyonsden2_android.otherClasses.ToastView;
import com.wlmac.lyonsden2_android.resourceActivities.InformationFormActivity;

// TODO: MAKE METHODS SWITACHBLE BASED ON PLATFORM VERSION (GET RID OF DEPRECATED METHOD)
// TODO: Implement Error codes where necessary (Internet unavailability does not require an error code)

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth authenticator;
    private FirebaseAuth.AuthStateListener authListener;
    private Button logInButton;
    private EditText emailField;
    private EditText passField;
    private EditText signUpKeyField;
    private boolean signUpSelected = true;
    private String[] signUpKeys = new String[2];
    private Button[] segmentedButtons = new Button[2];
    private boolean isStudent = true;
    private ToastView loadingToast;
    /** Store data permanently on device */
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        sharedPreferences = this.getSharedPreferences(LyonsDen.keySharedPreferences, Context.MODE_PRIVATE);

        logInButton = (Button) findViewById(R.id.LSLogin);
        emailField = (EditText) findViewById(R.id.LSEmailField);
        passField = (EditText) findViewById(R.id.LSPassField);
        signUpKeyField = (EditText) findViewById(R.id.LSCodeField);
        segmentedButtons[0] = (Button) findViewById(R.id.LSSignUpButton);
        segmentedButtons[1] = (Button) findViewById(R.id.LSLoginButton);
        loadingToast = new ToastView();
        Drawable signUp = getResources().getDrawable(R.drawable.segmented_button);
        try {
            signUp.setLevel(1);
            segmentedButtons[0].setBackgroundDrawable(signUp);
            segmentedButtons[0].setTextColor(getResources().getColor(R.color.accent));
        } catch (NullPointerException e) {
            Log.d("Login Activity:", "The segmented_button.xml file seems to missing or corrupted.");
        }

        logInButton.setEnabled(true);
        emailField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
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

        if (sharedPreferences.getBoolean("isOnlineLogInShown", true)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isOnlineLogInShown", false);
            editor.apply();
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    // TODO: MAKE METHODS SWITCHABLE BASED ON PLATFORM VERSION (GET RID OF DEPRECATED METHOD)
    public void toggleSegmentedControl (View view) {

        if (view.getTag().toString().equals("signup") && !signUpSelected) { // Switch to signup
            signUpSelected = true;
            signUpKeyField.animate().alpha(1).setDuration(300).start();
            signUpKeyField.setVisibility(View.VISIBLE);
            logInButton.setText("Sign Up");
        } else if (view.getTag().toString().equals("login") && signUpSelected){ // Switch to login
            signUpSelected = false;
            signUpKeyField.animate().alpha(0).setDuration(300).start();
            signUpKeyField.setVisibility(View.INVISIBLE);
            logInButton.setText("Log In");
        } else {
            Log.d("LoginActivity", "toggleSegmentedControl() called from unknown source");
            return;
        }
        Drawable signUp = getResources().getDrawable(R.drawable.segmented_button);
        Drawable login = getResources().getDrawable(R.drawable.segmented_button);
        try {
            signUp.setLevel((signUpSelected) ? 1 : 0);
            segmentedButtons[0].setBackgroundDrawable(signUp);
            segmentedButtons[0].setTextColor(getResources().getColor((signUpSelected) ? R.color.accent : R.color.text_view_edit));
            login.setLevel((!signUpSelected) ? 1 : 0);
            segmentedButtons[1].setBackgroundDrawable(login);
            segmentedButtons[1].setTextColor(getResources().getColor((!signUpSelected) ? R.color.accent : R.color.text_view_edit));
        } catch (NullPointerException e) {
            Log.d("Login Activity:", "The segmented_button.xml file seems to missing or corrupted.");
        }
    }


    public void logIn(View view) {
        Log.d("Login Activity:", "button pressed.");
        loadingToast.show(getFragmentManager(), "SomeDialog");
        logInButton.setEnabled(false);
        if (fieldsAreValid()) {
            Log.d("Login Activity:", "fields are valid");
            emailField.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        // Must return true here to consume event
                        return true;
                    }
                    return false;
                }
            });

            if (Retrieve.isInternetAvailable(this)) {
                Log.d("Login Activity:", "to process request");
                this.retrieveKeys();
            } else {
                Toast.makeText(getApplicationContext(), "No internet access!", Toast.LENGTH_LONG).show();
                loadingToast.dismiss();
            }
        } else {
            logInButton.setEnabled(true);
            loadingToast.dismiss();
        }

    }

    private String[] retrieveKeys() {
        FirebaseDatabase.getInstance().getReference("java").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                signUpKeys[0] = dataSnapshot.child("type1").getValue(String.class);
                signUpKeys[1] = dataSnapshot.child("type2").getValue(String.class);
                processRequest();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return signUpKeys;
    }

    private void processRequest(){
        // initialize needed
        final boolean[] performIntent = {false};
        final String[] signUpKeys = this.signUpKeys;

        if (signUpSelected) { // sign up
            if (signUpKeyField.getText().toString().equals(signUpKeys[0]) || signUpKeyField.getText().toString().equals(signUpKeys[1])) {
                this.createNewUser(signUpKeys);
                signUpKeyField.setBackgroundResource(R.drawable.text_field_bottom_border);
            } else {
                Toast.makeText(getApplicationContext(), "Incorrect sign up key", Toast.LENGTH_SHORT).show();
                signUpKeyField.setBackgroundResource(R.drawable.text_field_bottom_border_invalid);
                loadingToast.dismiss();
            }
        } else { // log in
            Log.d("Login Activity", "not sign up");
            if (!signUpSelected) {
                Log.d("Login Activity", "sign up passed");
                Log.d("Login Activity", "Login: " + emailField.getText().toString());
                Log.d("Login Activity", "Login: " + passField.getText().toString());
                authenticator.signInWithEmailAndPassword(emailField.getText().toString(), passField.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("Login Activity", "signInWithEmail:onComplete:" + task.isSuccessful());
                                if (task.isSuccessful()) {
                                    try {   // If task is not successful
                                        Log.d("Login Activity", FirebaseAuth.getInstance().getCurrentUser().toString());
                                    } catch (NullPointerException e) { /* Ra-ta-ta-ta-ta */ }                                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                        performIntent[0] = true;
                                        performIntent("old");
                                        loadingToast.dismiss();
                                    }
                                }

                                if (!task.isSuccessful()) {
                                    logInButton.setEnabled(true);
                                    loadingToast.dismiss();
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthUserCollisionException e) {
                                        Log.d("Login Activity", "User already exists error");
                                        Toast.makeText(getApplicationContext(), "User already exists.", Toast.LENGTH_LONG).show();
                                    } catch (FirebaseAuthWeakPasswordException e) {
                                        Log.d("Login Activity", "Weak password error");
                                        Toast.makeText(getApplicationContext(), "Password entered is too weak.", Toast.LENGTH_LONG).show();
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        Log.d("Login Activity", "Username or password error");
                                        Toast.makeText(getApplicationContext(), "Username or password is incorrect.", Toast.LENGTH_LONG).show();
                                    } catch (FirebaseAuthInvalidUserException e) {
                                        Log.d("Login Activity", "User does not exist");
                                        Toast.makeText(getApplicationContext(), "User does not exist.", Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Log.d("Login Activity", task.toString());
                                        Toast.makeText(getApplicationContext(), task.toString(), Toast.LENGTH_LONG).show();
                                        Log.d("Login Activity", "Log in failure");
                                        Toast.makeText(getApplicationContext(), "An unknown error occurred.", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        });
            }
        }

    }

    private void createNewUser(final String[] signUpKeys) {
        final boolean[] performIntent = {false};

        if (fieldsAreValid()) {
            authenticator.createUserWithEmailAndPassword(emailField.getText().toString(), passField.getText().toString())

                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("Login Activity", "createUserWithEmail:onComplete:" + task.isSuccessful());

                            if (signUpKeyField.getText().toString().equals(signUpKeys[1])) {
                                // store teacher password into database for announcements
                                isStudent = false;
                                String password = Retrieve.encrypted(passField.getText().toString());
                                String userId = "";
                                try {
                                    userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                } catch (NullPointerException e) {
                                    Log.d("Login Activity", "User auth failure. User does not exist");
                                }
                                FirebaseDatabase.getInstance().getReference("users").child("teacherIDs").child(userId).setValue(password);
                            }

                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                performIntent[0] = true;
                                performIntent("new");
                                loadingToast.dismiss();
                            }

                            if (!task.isSuccessful()) {
                                logInButton.setEnabled(true);
                                loadingToast.dismiss();
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    Log.d("Login Activity", "User already exists error");
                                    Toast.makeText(getApplicationContext(), "User already exists.", Toast.LENGTH_LONG).show();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    Log.d("Login Activity", "Weak password error");
                                    Toast.makeText(getApplicationContext(), "Password entered is too weak.", Toast.LENGTH_LONG).show();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Log.d("Login Activity", "Username or password error");
                                    Toast.makeText(getApplicationContext(), "Username or password is incorrect.", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Log.d("Login Activity", "unknown error");
                                    Toast.makeText(getApplicationContext(), "An unknown error occurred.", Toast.LENGTH_LONG).show();
                                }
                                Log.d("Login Activity", task.toString());
                                Toast.makeText(getApplicationContext(), task.toString(), Toast.LENGTH_LONG).show();
                                Log.d("Login Activity", "Create user failure");
                            }
                        }
                    });


        } else {
            Toast.makeText(getApplicationContext(), "Sign up failed", Toast.LENGTH_LONG).show();
            loadingToast.dismiss();
        }
    }

    /**
     * Called when time to segue into another screen.
     */
    private void performIntent(String type) {
        Intent intent = null;
        // store on device
        sharedPreferences.edit().putString(LyonsDen.keyPass, passField.getText().toString()).apply();
        sharedPreferences.edit().putString(LyonsDen.keyEmail, emailField.getText().toString()).apply();
        if (type.equals("new")) {
            intent = new Intent(LoginActivity.this, InformationFormActivity.class);
        } else {
            if (type.equals("old"))
                intent = new Intent(LoginActivity.this, HomeActivity.class);
        }
        String IFTitle = (isStudent) ? "Student Information Form" : "Teacher Information Form";
        intent.putExtra("IFTitle", IFTitle);
        startActivity(intent);
        finish();
    }

    /**
     * @return boolean true if all field data is correct.
     */
    private boolean fieldsAreValid() {
        boolean [] valid = {true, true};

        EditText[] fields = (signUpSelected) ? new EditText[]{emailField, passField, signUpKeyField} : new EditText[]{emailField, passField};
        for (EditText field : fields) {
            if (field.getText() == null || field.getText().toString().equals("")) {
                field.setBackgroundResource(R.drawable.text_field_bottom_border_invalid);
                valid[0] = false;
            } else {
                field.setBackgroundResource(R.drawable.text_field_bottom_border);
                valid[0] = true;
            }
        }

        // check email format
        final String email = String.valueOf(fields[0].getText());
        if (email.contains("@")) {
            valid[1] = true;
            final String domain = email.substring(email.indexOf("@") + 1);
            if (domain.contains(".")) {
                valid[1] = true;
                final String domain2 = domain.substring(domain.indexOf(".") +1);
                if (domain2.contains("@") || domain2.contains(".")) {
                    valid[1] = false;
                    Log.d("Login Activity", "invalid email format");
                    Toast.makeText(getApplicationContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
                }
            }
        }
        boolean result = (valid[0] && valid[1]);
        logInButton.setEnabled(true);
        return result;
    }

    public void resetPassword(View view) {
        final boolean [] userExists = {true};
        Log.d("Login Activity", "Reset password requested.");
        // confirm request
        final LyonsAlert confirmRequestAlert = new LyonsAlert();
        confirmRequestAlert.setTitle("Lyon's Den");
        confirmRequestAlert.setSubtitle("Forgot Password?");
        confirmRequestAlert.hideInput();
        confirmRequestAlert.configureLeftButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmRequestAlert.dismiss();
            }
        });

        // create reset alert view
        confirmRequestAlert.configureRightButton("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmRequestAlert.dismiss();

                final LyonsAlert handleRequestAlert = new LyonsAlert();
                handleRequestAlert.setTitle("Reset Password");
                handleRequestAlert.setSubtitle("Enter your account email.");
                handleRequestAlert.setEmailKeyboard();
                handleRequestAlert.configureLeftButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleRequestAlert.dismiss();
                    }
                });
                handleRequestAlert.configureRightButton("Submit", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().fetchProvidersForEmail(handleRequestAlert.getInputText()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                Log.d("Login Activity", "HAPPENED MAN");
                                if (task.getResult().getProviders().isEmpty()) {
                                    userExists[0] = false;
                                } else {
                                    if (!task.getResult().getProviders().isEmpty()) {
                                        userExists[0] = true;
                                    }
                                }
                                if (!task.isSuccessful()) {
                                    Log.d("Login Activity", "not successful");
                                    Log.d("Login Activity", String.valueOf(task.getResult().getProviders()));
                                } else {
                                    Log.d("Login Activity", "successful");
                                    Log.d("Login Activity", String.valueOf(task.getResult().getProviders()));
                                }
                                if (userExists[0]) {
                                    FirebaseAuth.getInstance().sendPasswordResetEmail(handleRequestAlert.getInputText())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("Login Activity", "Email sent.");
                                                        Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_LONG).show();
                                                    } else if (!task.isSuccessful()) {
                                                        try {
                                                            throw task.getException();
                                                        } catch (FirebaseException e) {
                                                            Log.d("Login Activity", task.toString());
                                                            Toast.makeText(getApplicationContext(), "Email failed to send.", Toast.LENGTH_LONG).show();
                                                            Log.d("Login Activity", "Email failed to send.");
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(getApplicationContext(), "Email is not registered to\nan account.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        handleRequestAlert.dismiss();
                    }
                });
                handleRequestAlert.show(getSupportFragmentManager(), "ResetPasswordInputDialog");
            }
        });

        confirmRequestAlert.show(getSupportFragmentManager(), "ResetPasswordDialog");
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
