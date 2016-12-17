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

public class UserActivity extends AppCompatActivity {
    /** An instance of the root layout of this activity. */
    private DrawerLayout rootLayout;
    /** An instance of the ListView used in this activity's navigation drawer. */
    private ListView drawerList;
    /** The drawer toggler used this activity. */
    private ActionBarDrawerToggle drawerToggle;
    private TableLayout extraButtonsContainer;
    private EditText displayName;
    private EditText email;
    private EditText accessLevel;
    private Button toggleButton;
    private boolean isShowingExtraButtons = true;
    SharedPreferences sharedPreferences;

    private boolean editing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        instantiateComponents();
        extraButtonsContainer = (TableLayout) findViewById(R.id.USContainer);
        toggleButton = (Button) findViewById(R.id.USToggleButton);
        // Drawer setup
        rootLayout = (DrawerLayout) findViewById(R.id.LDLayout);
        drawerList = (ListView) findViewById(R.id.LDList);
        drawerToggle = HomeActivity.initializeDrawerToggle(this, rootLayout);
        HomeActivity.setupDrawer(this, drawerList, rootLayout, drawerToggle);

        //something entirely different
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference("users").child("students").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    onContentLoad(dataSnapshot.child("name").getValue(String.class), user.getEmail(),
                            dataSnapshot.child("accessLevel").getValue(String.class));
                else Log.d("An error occured", "database directory isn't correct or database does not exist");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int textHeight = Retrieve.heightForText("Reset Password", this, 24) + 32; // Accounts for padding
        extraButtonsContainer.animate().translationYBy(textHeight).setDuration(0).start();
        toggleButton.animate().translationYBy(textHeight).setDuration(0).start();
    }

    public void toggleButtons (View view) {
        int textHeight = Retrieve.heightForText("Reset Password", this, 24) + 32; // Accounts for padding
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
        sharedPreferences = this.getSharedPreferences(HomeActivity.sharedPreferencesName, Context.MODE_PRIVATE);
    }

    private void instantiateComponents () {
        displayName = (EditText) findViewById(R.id.USNameField);
        email = (EditText) findViewById(R.id.USEmailField);
        accessLevel = (EditText) findViewById(R.id.USAccessField);
    }

    public void clubLeadershipApplication (View view) {
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

        alertDialog.show(getSupportFragmentManager(), "ClubCodeDialog");
    }

    public void editAccount (String name, final String email) {
        if (Retrieve.isInternetAvailable(this)) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference nameDatabaseReference = FirebaseDatabase.getInstance().getReference("users").
                    child("students").child(user.getUid()).child("name");
            nameDatabaseReference.setValue(name);
            //Re-authentication because it is required by firebase in order to change information inside authentication like in this case - email
            AuthCredential credential = EmailAuthProvider.getCredential(sharedPreferences.getString("email", null),
                    sharedPreferences.getString("password", null));
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(email, "Re authentication is successful");
                    } else {
                        Log.d(email, "Re authentication is not working");
                        Log.d(email, task.getException().toString());
                    }
                }
            });
            user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(email, "User Email address updated.");
                        } else {
                            Log.d(email, "It did not work!");
                            Log.d(email, task.getException().toString());
                        }
                    }
            });

        }
        else {
            Toast.makeText(UserActivity.this, "No internet connection available", Toast.LENGTH_SHORT).show();
        }
        /*if (view.getId() == R.id.USUpdate) {
            Log.d("User Activity", "Let's pretend that i'm actually updating your info");
        } else if (view.getId() == R.id.USDelete) {
            try {FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("User Activity", "User account deleted.");
                    }
                }
            }); }
            catch (NullPointerException e) {
                Log.d("User Activity", "No user exists");
            }
        } else if (view.getId() == R.id.USSignOut) {
            FirebaseAuth.getInstance().signOut();
            try {Log.d("User Activity", "Signed out " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName()); }
            catch (NullPointerException e) {
                Log.d("User Activity", "No signed in user because it returned null");
            }
        } else {
            Log.d("User Activity", "Let's pretend that i'm actually resetting your password");
        } */
    }

    private void enterEditMode () {
        editing = !editing;

        displayName.setCursorVisible(editing);
        displayName.setFocusableInTouchMode(editing);
        displayName.setFocusable(editing);
        displayName.setEnabled(editing);
        displayName.setClickable(editing);
        displayName.setInputType((editing) ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL);
        displayName.requestFocus();

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
        // TODO: MAKE KEYBOARD GO AWAY WHEN !editing
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (editing) {
            //need new icon for a button
            getMenuInflater().inflate(R.menu.add_menu, menu);
            return true;
        }
        else {
            getMenuInflater().inflate(R.menu.edit_menu, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editAction)
            enterEditMode();
        else if (item.getItemId() == R.id.addAction){
            enterEditMode();
            editAccount(displayName.getText().toString(), email.getText().toString());
            Toast.makeText(UserActivity.this, "Information Updated", Toast.LENGTH_SHORT).show();
        }
        if (drawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
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
            String s = (String) view.getTag();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            switch (s) {
                case "deleteAcc":
                    FirebaseUser user = auth.getCurrentUser();
                    FirebaseDatabase.getInstance().getReference("users").child("students").child(user.getUid()).removeValue();
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else
                                Toast.makeText(UserActivity.this, "An error 1.1 occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case "signOut":
                    auth.signOut();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    break;
                case "resetPass":
                    TextView textView = (TextView) findViewById(R.id.USEmailField);
                    String email = textView.getText().toString();
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Toast.makeText(UserActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(UserActivity.this, "An error 1.2 occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
        } else
            Toast.makeText(this, "No Internet Available", Toast.LENGTH_SHORT).show();
    }

    public void becomeClubLeaderButton(View view) {
    }

    private void checkClubCode (String code) {

    }

}


