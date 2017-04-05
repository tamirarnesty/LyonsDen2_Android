package com.wlmac.lyonsden2_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wlmac.lyonsden2_android.otherClasses.LyonsAlert;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;

// TODO: IMPLEMENT CLUB CODE CHECKER
// TODO: MAKE A LOADING INDICATOR FOR WHEN CHECKING THE CLUB CODE
// TODO: MAKE KEYBOARD GO AWAY WHEN !editing

/**
 * This activity is used for viewing User information with options to add, change or remove data.
 *
 * @author Ademir Gotov
 * @version 1, 2016/08/05
 */
public class UserActivity extends AppCompatActivity {
    /** The drawer toggler used this activity. */
    private ActionBarDrawerToggle drawerToggle;
    private TableLayout extraButtonsContainer;
    private TextView displayName;
    private TextView email;
    private TextView accessLevel;
    private Button toggleButton;
    private boolean isShowingExtraButtons = true;
    SharedPreferences sharedPreferences;

    private boolean editing = false;
    private String accessLevelString = "Unavailable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        instantiateComponents();
        setFonts();
        extraButtonsContainer = (TableLayout) findViewById(R.id.USContainer);
        toggleButton = (Button) findViewById(R.id.USToggleButton);

        // Drawer setup
        DrawerLayout rootLayout = (DrawerLayout) findViewById(R.id.NDLayout);
        ListView drawerList = (ListView) findViewById(R.id.NDList);
        drawerToggle = Retrieve.drawerToggle(this, rootLayout);
        Retrieve.drawerSetup(this, drawerList, rootLayout, drawerToggle);

        //something entirely different
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference("users").child("students").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    onContentLoad(dataSnapshot.child("name").getValue(String.class), user.getEmail(),
                            dataSnapshot.child("accessLevel").getValue(String.class));
                     accessLevelString = dataSnapshot.child("accessLevel").getValue(String.class);
                    instantiateComponents();
                }
                else Log.d("An error occured", "database directory isn't correct or database does not exist");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        float textHeight = (Retrieve.heightForText("Sign out", this, 12) + (Retrieve.dpFromInt(16, getResources()))) * 2; // Accounts for padding

        Log.d("User Activity", "" + textHeight);

        extraButtonsContainer.animate().translationYBy(textHeight).setDuration(0).start();
        toggleButton.animate().translationYBy(textHeight).setDuration(0).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void toggleButtons (View view) {
        float textHeight = (Retrieve.heightForText("Sign out", this, 12) + (Retrieve.dpFromInt(16, getResources()))) * 2; // Accounts for padding
        textHeight = (isShowingExtraButtons) ? textHeight * -1 : textHeight;
        extraButtonsContainer.animate().translationYBy(textHeight).setDuration(300).start();
        toggleButton.animate().translationYBy(textHeight).setDuration(300).start();
        toggleButton.setBackgroundResource((isShowingExtraButtons) ? R.drawable.arrow_down_48dp : R.drawable.arrow_up_48dp);
        isShowingExtraButtons = !isShowingExtraButtons;
    }

    private void onContentLoad(String name, String email, String accessLevel) {
        this.displayName.setText(name);
        this.email.setText(email);
        this.accessLevel.setText(accessLevel);
        sharedPreferences = this.getSharedPreferences(LyonsDen.keySharedPreferences, Context.MODE_PRIVATE);
    }

    private void instantiateComponents () {
        displayName = (TextView) findViewById(R.id.USNameField);
        email = (TextView) findViewById(R.id.USEmailField);
        accessLevel = (TextView) findViewById(R.id.USAccessField);

        try {
            displayName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            accessLevel.setText(accessLevelString);
        } catch (NullPointerException e) {}
    }

    private void setFonts() {
        int[] ids = {R.id.USNameLabel, R.id.USNameField, R.id.USEmailLabel, R.id.USEmailField, R.id.USAccessLabel, R.id.USAccessField,
                    R.id.USLeaderButton, R.id.USDelete, R.id.USSignOut, R.id.USReset};
        for (int id : ids)
            ((TextView) findViewById(id)).setTypeface(Retrieve.typeface(this));
    }

    public void editAccount (String name, final String email) {
        if (Retrieve.isInternetAvailable(this)) {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            // Update Display name in database
            FirebaseDatabase.getInstance().getReference("users/students/" + user.getUid() + "/name").setValue(name);

//            Log.d("UserActivity", "email: " + sharedPreferences.getString(LyonsDen.keyEmail, ""));
//            Log.d("UserActivity", "pass: " + sharedPreferences.getString(LyonsDen.keyPass, ""));

            //Re-authentication because it is required by firebase in order to change auth info like it is in this case - email
            AuthCredential credential = EmailAuthProvider.getCredential(sharedPreferences.getString(LyonsDen.keyEmail, ""),
                    sharedPreferences.getString(LyonsDen.keyPass, ""));
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(email, "Re authentication is successful");
                        Log.d(email, "Updating Email");
                        user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(email, "User Email address updated.");
                                    SharedPreferences.Editor prefs = getSharedPreferences(LyonsDen.keySharedPreferences, Context.MODE_PRIVATE).edit();
                                    prefs.putString(LyonsDen.keyEmail, email);
                                    prefs.apply();
                                } else {
                                    Log.d(email, "It did not work!");
                                    Log.d(email, task.getException().toString());
                                }
                            }
                        });
                    } else {
                        Log.d(email, "Re authentication is not working");
                        Log.d(email, task.getException().toString());
                    }
                }
            });
        } else {
            Toast.makeText(UserActivity.this, "No internet connection available", Toast.LENGTH_SHORT).show();
        }
    }

    private void enterEditMode () {
        editing = !editing;

        int bgResource = (editing) ? R.drawable.text_view_edit_underline : R.drawable.text_view_default;

        displayName.setBackgroundResource(bgResource);
        displayName.setCursorVisible(editing);
        displayName.setFocusableInTouchMode(editing);
        displayName.setFocusable(editing);
        displayName.setEnabled(editing);
        displayName.setClickable(editing);
        displayName.setInputType((editing) ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL);
        displayName.requestFocus();

        email.setBackgroundResource(bgResource);
        email.setCursorVisible(editing);
        email.setFocusableInTouchMode(editing);
        email.setFocusable(editing);
        email.setEnabled(editing);
        email.setClickable(editing);
        email.setInputType((editing) ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL);

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (editing) {
            getMenuInflater().inflate(R.menu.done_menu, menu);
            return true;
        } else {
            getMenuInflater().inflate(R.menu.edit_menu, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editAction)
            enterEditMode();
        else if (item.getItemId() == R.id.doneAction) {
            enterEditMode();
            editAccount(displayName.getText().toString(), email.getText().toString());
            Toast.makeText(UserActivity.this, "Information Updated", Toast.LENGTH_SHORT).show();
        }
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public void UAButtons (View view) {
        if (Retrieve.isInternetAvailable(this)) {
            switch ((String) view.getTag()) {
                case "deleteAcc":
                    final LyonsAlert deleteAlert = new LyonsAlert();
                    deleteAlert.setTitle("Are you sure?");
                    deleteAlert.setSubtitle("Are you sure you want to delete your account?\nAn account is required to use the app.");
                    deleteAlert.hideInput();
                    deleteAlert.configureLeftButton("Cancel", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteAlert.dismiss();
                        }
                    });
                    deleteAlert.configureRightButton("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteAlert.dismiss();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            FirebaseDatabase.getInstance().getReference("users").child("students").child(user.getUid()).removeValue();
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UserActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else
                                        Toast.makeText(UserActivity.this, "Error 1.1 occurred", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    deleteAlert.show(getSupportFragmentManager(), "DeleteAccountDialog");
                    break;
                case "signOut":
                    final LyonsAlert signOutAlert = new LyonsAlert();
                    signOutAlert.setTitle("Are you sure?");
                    signOutAlert.setSubtitle("Are you sure you want to delete your account?\nAn account is required to use the app.");
                    signOutAlert.hideInput();
                    signOutAlert.configureLeftButton("Cancel", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            signOutAlert.dismiss();
                        }
                    });
                    signOutAlert.configureRightButton("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            signOutAlert.dismiss();
                            FirebaseAuth.getInstance().signOut();
                            SharedPreferences.Editor prefs = getSharedPreferences(LyonsDen.keySharedPreferences, Context.MODE_PRIVATE).edit();
                            prefs.remove(LyonsDen.keyEmail);
                            prefs.remove(LyonsDen.keyPass);
                            prefs.apply();

                            Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    signOutAlert.show(getSupportFragmentManager(), "DeleteAccountDialog");

                    break;
                case "resetPass":
                    final String email = this.email.getText().toString();

                    // Dialog goes here!
                    final LyonsAlert resetAlert = new LyonsAlert();
                    resetAlert.setTitle("Reset Password");
                    resetAlert.setSubtitle("We will send an email to " + email + " with instructions on how to reset your password.");
                    resetAlert.hideInput();
                    resetAlert.configureLeftButton("Cancel", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            resetAlert.dismiss();
                        }
                    });
                    resetAlert.configureRightButton("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        Toast.makeText(UserActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                                    else {
                                        Toast.makeText(UserActivity.this, "Error 1.2 occurred", Toast.LENGTH_SHORT).show();
                                        Log.d("UserActivity", "Password Reset Failed!");
                                        Log.d("UserActivity", task.getException().getMessage());
                                    }
                                }
                            });
                            resetAlert.dismiss();
                        }
                    });
                    resetAlert.show(getSupportFragmentManager(), "ResetPasswordDialog");
                    break;
            }
        } else
            Toast.makeText(this, "No Internet Available", Toast.LENGTH_SHORT).show();
    }

// MARK: CLUB METHODS

    /**
     * Prompts the user to enter a club code. Called when the 'Become Club Leader' button
     * is pressed. <u>Async method.</u>
     * @param view The view that called this method
     */
    public void promptClubCode (View view) {
        final LyonsAlert alertDialog = new LyonsAlert();
        alertDialog.setTitle("Club Code");
        alertDialog.setSubtitle("Please enter the club code");
        alertDialog.configureLeftButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.configureRightButton("Submit", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkClubCode(alertDialog.getInputText());
                alertDialog.dismiss();
            }
        });
        alertDialog.makeInputSecure();  // Make textField, secure entry

        alertDialog.show(getSupportFragmentManager(), "ClubCodeDialog");
    }

    /**
     * Performs finishing operations if a valid club key has been found. Will Modify database
     * access level, on screen access level and database club editors directory. <u>Async method.</u>
     * @param clubName The database key for the club to be modified.
     */
    private void onCheckClubCodeStatus(String clubName) {
        if (clubName == null) {     // If the club code was not recognized
            Toast.makeText(this, "The entered club code is incorrect", Toast.LENGTH_LONG).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set access level user property
        if (!this.accessLevel.getText().toString().equalsIgnoreCase("club leader")) {
            // Determine which directory to access in database
            String userKey = (this.accessLevel.getText().toString().equalsIgnoreCase("student")) ? "students" : "teachers";
            // Update property in database
            FirebaseDatabase.getInstance().getReference("users/" + userKey + "/" + uid + "/accessLevel").setValue("Club Leader");
            // Update property in UI
            this.accessLevel.setText("ClubLeader");
        }

        // Add user as editor in the club
        FirebaseDatabase.getInstance().getReference("clubs/" + clubName + "/editors").push().setValue(uid);

        Toast.makeText(this, "Club Key Found!", Toast.LENGTH_SHORT).show();
    }

    /** Will check if the clubKey that the passed key is valid. Will call a handler method
     * onCheckClubCodeStatus, to handle the result. <u>Async method.</u>
     * @param code The entered code to be checked.
     */
    private void checkClubCode (String code) {
        FirebaseDatabase.getInstance().getReference("clubKeys/" + code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    onCheckClubCodeStatus(dataSnapshot.getValue(String.class));
                } else {
                    onCheckClubCodeStatus(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}