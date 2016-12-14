package com.wlmac.lyonsden2_android;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private String displayNameString, emailString, accessLevelString;
    private EditText displayName;
    private EditText email;
    private EditText accessLevel;

    private boolean editing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        instantiateComponents();

        // Drawer setup
        rootLayout = (DrawerLayout) findViewById(R.id.LDLayout);
        drawerList = (ListView) findViewById(R.id.LDList);
        drawerToggle = HomeActivity.initializeDrawerToggle(this, rootLayout);
        HomeActivity.setupDrawer(this, drawerList, rootLayout, drawerToggle);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        emailString = user.getEmail();
        FirebaseDatabase.getInstance().getReference("users").child("students").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    onContentLoad(dataSnapshot.child("name").getValue(String.class), emailString,
                            dataSnapshot.child("accessLevel").getValue(String.class));
                else Log.d("An error occured", "database directory isn't correct or database does not exist");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void onContentLoad(String name, String email, String accessLevel) {
        this.displayName.setText(name);
        this.email.setText(email);
        this.accessLevel.setText(accessLevel);
    }

    private void instantiateComponents () {
        displayName = (EditText) findViewById(R.id.USNameField);
        email = (EditText) findViewById(R.id.USEmailField);
        accessLevel = (EditText) findViewById(R.id.USAccessField);
    }

    private void checkClubCode (String code) {

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

    public void editAccount (View view) {
        if (view.getId() == R.id.USUpdate) {

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
        }
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

        // TODO: MAKE KEYBOARD GO AWAY WHEN !editing
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editAction)
            enterEditMode();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
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
}


