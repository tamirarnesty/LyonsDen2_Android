package com.wlmac.lyonsden2_android.resourceActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;
import com.wlmac.lyonsden2_android.HomeActivity;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;

public class InformationFormActivity extends AppCompatActivity {
    private EditText IFSName, IFTName, IFTEmail;
    private Spinner IFSGrade, IFTDepartment;
    private Button IFSCompleteButton, IFCompleteButton;
    private boolean isStudent = true;

    private DatabaseReference targetRef = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_form_activity);

        instantiateComponents();
        setFonts();
    }

    private void instantiateComponents () {
        //Student Information View Layout
        IFSName = (EditText) findViewById(R.id.IFSSNameField);
        IFSName.setOnKeyListener(new View.OnKeyListener() {
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
        IFSGrade = (Spinner) findViewById(R.id.IFSGradeSpinner);
//        IFSCompleteButton = (Button) findViewById(R.id.IFSSCompleteButton);

        //Teacher Information View Layout
        IFTName = (EditText) findViewById(R.id.IFSTNameField);
        IFTDepartment = (Spinner) findViewById(R.id.IFSDepartmentSpinner);
        IFTEmail = (EditText) findViewById(R.id.IFSEmailField);
        IFTEmail.setOnKeyListener(new View.OnKeyListener() {
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
        IFCompleteButton = (Button) findViewById(R.id.IFCompleteButton);

        TextView label = (TextView) findViewById(R.id.IFSTitleLabel);
        assert label != null;
        label.setText(getIntent().getStringExtra("IFTitle"));
        if (label.getText().equals("Teacher Information Form")) {                   // teacher
            isStudent = false;
            findViewById(R.id.IFSSLayout).setVisibility(LinearLayout.GONE);
            findViewById(R.id.IFSTLayout).setVisibility(LinearLayout.VISIBLE);
            findViewById(R.id.IFNote).setVisibility(LinearLayout.VISIBLE);
        } else {                                                                    // student
            isStudent = true;
            findViewById(R.id.IFSTLayout).setVisibility(LinearLayout.GONE);
            findViewById(R.id.IFSSLayout).setVisibility(LinearLayout.VISIBLE);
            findViewById(R.id.IFNote).setVisibility(LinearLayout.GONE);
        }
    }

    private void setFonts () {
        // re add IFSCompleteButton
        int[] components ={R.id.IFSSNameLabel, R.id.IFSSNameField, R.id.IFSGradeLabel, R.id.IFCompleteButton, R.id.IFSTNameLabel, R.id.IFSTNameField, R.id.IFSDepartmentLabel, R.id.IFSEmailLabel, R.id.IFSEmailField};
        for (int h = 0; h < components.length; h ++)
            ((TextView) findViewById(components[h])).setTypeface(Retrieve.typeface(this));
        // TODO: Set Spinner typeface
    }

    public boolean fieldsAreValid() {
        boolean valid = true;
        EditText [] fields = (isStudent) ? new EditText[]{IFSName} : new EditText[]{IFTName, IFTEmail} ;

        for (EditText field : fields) {
            if (field.getText() == null || field.getText().toString().equals("")) {
                field.setBackgroundResource(R.drawable.text_field_bottom_border_invalid);
                valid = false;
            } else {
                field.setBackgroundResource(R.drawable.text_field_bottom_border);
                valid = true;
            }
        }

        if (!valid) {
            return valid;
        }

        // check email format
        final String email = String.valueOf(fields[0].getText());
        if (email.contains("@")) {
            valid = true;
            final String domain = email.substring(email.indexOf("@") + 1);
            if (domain.contains(".")) {
                valid = true;
                final String domain2 = domain.substring(domain.indexOf(".") +1);
                if (domain2.contains("@") || domain2.contains(".")) {
                    valid = false;
                    Log.d("Login Activity", "invalid email format");
                    Toast.makeText(getApplicationContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return valid;
    }

    public void studentSubmission() {
        if (fieldsAreValid()) {
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference user = (targetRef != null) ? targetRef.push() : FirebaseDatabase.getInstance().getReference("users").child("students").child(userID);

            user.child("name").setValue(IFSName.getText().toString());
            user.child("grade").setValue(IFSGrade.getSelectedItem().toString());
            user.child("accessLevel").setValue("Student");

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(IFSName.getText().toString())
                    .build();
            assert firebaseUser != null;
            firebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Information Form", "User profile updated.");
                            }
                        }
                    });
            finish();
            performIntent();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to process", Toast.LENGTH_SHORT).show();
        }

    }

    public void teacherSubmission() {
        if (fieldsAreValid()) {
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference user = (targetRef != null) ? targetRef.push() : FirebaseDatabase.getInstance().getReference("users").child("teachers").child(userID);

            user.child("name").setValue(IFTName.getText().toString());
            user.child("department").setValue(IFTDepartment.getSelectedItem().toString());
            user.child("email").setValue(IFTEmail.getText().toString());
            user.child("accessLevel").setValue("Teacher");

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(IFTName.getText().toString())
                    .build();
            assert firebaseUser != null;
            firebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Information Form", "User profile updated.");
                            }
                        }
                    });
            finish();
            performIntent();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to process", Toast.LENGTH_SHORT).show();
        }
    }

    public void buttonPressed (View view) {
        Toast.makeText(getApplicationContext(), "Processing", Toast.LENGTH_SHORT).show();
        if (Retrieve.isInternetAvailable(this)) {
            Log.d("Information Form", String.valueOf(Retrieve.isInternetAvailable(this)));
            if (isStudent) {
                studentSubmission();
            } else {
                teacherSubmission();
            }
        }
    }

    private void performIntent() {
        Intent intent = new Intent(InformationFormActivity.this, GuideActivity.class);
        startActivity(intent);
        finish();
    }

}
