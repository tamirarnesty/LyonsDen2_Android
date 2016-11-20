package com.wlmac.lyonsden2_android.contactActivities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wlmac.lyonsden2_android.ContactActivity;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.LoadingLabel;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;
import com.wlmac.lyonsden2_android.otherClasses.ToastView;

import java.util.Calendar;

// TODO: CREATE LOADING WHEEL FOR WHEN APPROVING AND SUBMITTING THE ANNOUNCEMENT

/**
 * The activity to that will be used for constructing, approving and uploading announcemnt to the
 * database.
 *
 * @author sketch204
 * @version 1, 2016/08/06
 */
public class AnnouncementActivity extends AppCompatActivity {
    /** An instance of the title input field. */
    private EditText titleField;
    /** An instance of the description input field. */
    private EditText descriptionField;
    /** An instance of the date input field. */
    private TextView dateField;
    /** An instance of the time input field. */
    private TextView timeField;
    /** An instance of the location input field. */
    private EditText locationField;
    /** An instance of the teacher login input field. */
    private EditText teacherLogin;

    private Button approveButton;

    private Button submitButton;

    private LinearLayout contentView;

    private boolean isProposalLocked = false;

    private ToastView loadingToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.announcement_activity);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        instantiateComponents();
//        setFonts();
    }

    /**
     * This method is called whenever the "Pick a Date" button is pressed. It displays a date picking
     * dialog to the user, and transfers the picked date to the appropriate text field.
     * @param view The view that initiated this method.
     */
    public void pickDate (View view) {
        Log.i("Date", "has been clicked");

        // Create an instance of the current date
        final Calendar cal = Calendar.getInstance();
        int curYear = cal.get(Calendar.YEAR);
        int curMonth = cal.get(Calendar.MONTH);
        int curDay = cal.get(Calendar.DAY_OF_MONTH);

        // Create date picker, set to the current date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Set the the date field to the current date
                        String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        dateField.setText(date);
                    }
                }, curYear, curMonth, curDay);
        // Display date picker
        datePickerDialog.show();
    }

    /**
     * This method is called whenever the "Pick a Time" button is pressed. It displays a time picking
     * dialog to the user, and transfers the picked time to the appropriate text field.
     * @param view The view that initiated this method.
     */
    public void pickTime (View view) {
        Log.i("Time", "has been clicked");

        // Create an instance of the current time
        final Calendar cal = Calendar.getInstance();
        int curHour = cal.get(Calendar.HOUR_OF_DAY);
        int curMinute = cal.get(Calendar.MINUTE);

        // Create time picker, set to the current time
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = hourOfDay + ":" + minute;
                        timeField.setText(time);
                    }
                }, curHour, curMinute, false);
        // Display the time picker
        timePickerDialog.show();
    }

    /** Instantiates all GUI components. Also sets the initial date/time for the date and time fields. */
    private void instantiateComponents () {
        titleField = (EditText) findViewById(R.id.APSTitleField);
        descriptionField = (EditText) findViewById(R.id.APSDescriptionField);
        locationField = (EditText) findViewById(R.id.APSLocationField);
        teacherLogin = (EditText) findViewById(R.id.APSTeacherLogin);
        approveButton = (Button) findViewById(R.id.APSApproveButton);
        submitButton = (Button) findViewById(R.id.APSSubmitButton);
//        loadingCircle = (ProgressBar) findViewById(R.id.APSLoadingWheel);
//        loadingLabel = new LoadingLabel(((TextView) findViewById(R.id.APSLoadingLabel)), this);
        loadingToast = new ToastView(this);
        contentView = (LinearLayout) findViewById(R.id.APSContentView);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        loadingToast.getView().setLayoutParams(params);
        contentView.addView(loadingToast.getView());//, params);

//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//        contentView.addView(loadingToast.getView(), params);

        final Calendar cal = Calendar.getInstance();
        // An Array of fields that should be formatted
        String [] fields = {"" + cal.get(Calendar.MONTH), "" + cal.get(Calendar.DAY_OF_MONTH), "" + cal.get(Calendar.HOUR_OF_DAY), "" + cal.get(Calendar.MINUTE)};
        // For each field that is in the array
        for (int h = 0; h < fields.length; h ++) {
            if (fields[h].length() < 2) {   // If a 0 must be added to the value, then do it
                fields[h] = "0" + fields[h];
            }
        }
        // Instantiate the date field
        dateField = (TextView) findViewById(R.id.APSDateField);
        // Create its default value
        String date = "" + cal.get(Calendar.YEAR) + "-" + fields[0] + "-" + fields[1];
        // Assign its default value
        dateField.setText(date);

        // Instantiate the date field
        timeField = (TextView) findViewById(R.id.APSTimeField);
        // Create its default value
        String time = fields[2] + ":" + fields[3];
        // Assign its default value
        timeField.setText(time);
    }

    private void toggleLoadingComponents () {
        if (loadingToast.isVisible()) {
            loadingToast.hide();

            Log.d("AnnouncementActivity", "Hiding Toast!");
        } else {
            loadingToast.show();
            Log.d("AnnouncementActivity", "Displaying Toast!");
        }
    }

    public void submitAnnouncement (View view) {
        toggleLoadingComponents();

        if (Retrieve.isInternetAvailable(this)) {
            DatabaseReference announcement = FirebaseDatabase.getInstance().getReference("announcements").push();
            String dateTime = dateField.getText().toString().replaceAll("-", "") + timeField.getText().toString().replaceAll(":", "") + "00";

//            announcement.child("title").setValue(titleField.getText().toString());
//            announcement.child("description").setValue(descriptionField.getText().toString());
//            announcement.child("dateTime").setValue(dateTime);
//            announcement.child("location").setValue(locationField.getText().toString());

            // Notify User about success!
            Intent intent = new Intent(this, ContactActivity.class);
            intent.putExtra("afterProposal", true);
            startActivity(intent);
        } else {    // No Internet
            Toast.makeText(getApplicationContext(), "Submission Failed!\nNo Internet!", Toast.LENGTH_LONG).show();
        }
        toggleLoadingComponents();
    }

    private void lockUnlockProposal() {
        titleField.setEnabled(!isProposalLocked);
        descriptionField.setEnabled(!isProposalLocked);
        ((Button) findViewById(R.id.APSDateButton)).setEnabled(!isProposalLocked);
        ((Button) findViewById(R.id.APSTimeButton)).setEnabled(!isProposalLocked);
        locationField.setEnabled(!isProposalLocked);
        teacherLogin.setEnabled(!isProposalLocked);

        submitButton.setVisibility((isProposalLocked) ? View.VISIBLE : View.GONE );
        approveButton.setText((isProposalLocked) ? "Unlock" : "Approve");
    }

    /** Called whenever the teacher credential has been validated and the submission process should begin. */
    public void approveProposal (View view) {
        // If unlocking proposal
        if (isProposalLocked) { lockUnlockProposal(); }
//        // If fields are invalid
//        if (fieldsAreInvalid()) { return; }

        toggleLoadingComponents();
        // Perform internet check
        boolean internetAvailable = Retrieve.isInternetAvailable(this);

        if (internetAvailable) {
            Retrieve.teacherApproval(teacherLogin.getText().toString(), new Retrieve.StatusHandler() {
                @Override
                public void handle(boolean status) {
                    isProposalLocked = status;
                    if (isProposalLocked) {
                        Toast.makeText(getApplicationContext(), "Approval Success!", Toast.LENGTH_LONG).show();
                        lockUnlockProposal();
                        toggleLoadingComponents();
                    } else {
                        Toast.makeText(getApplicationContext(), "Approval Failed!\nWrong Key!", Toast.LENGTH_LONG).show();
                        toggleLoadingComponents();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Approval Failed!\nNo Internet!", Toast.LENGTH_LONG).show();
            toggleLoadingComponents();
        }
    }

    /**
     * Checks the comparison validity of every EditText on screen.
     * @return Return true if, and only if, all the input fields on screen are valid.
     */
    private boolean fieldsAreInvalid() {
        // States whether the fields are invalid or not
        boolean fieldsAreInvalid = false;
        // Check that fields are not empty
        EditText[] fields = {titleField, descriptionField, teacherLogin};
        for (EditText field : fields) {
            if (field.getText() == null || field.getText().toString().equals("")) {
                // If the current field is invalid then highlight it, and state that some fields are invalid
                field.setBackgroundResource(R.drawable.input_field_invalid);
                fieldsAreInvalid = true;
            } else {
                // If it is valid, then de-highlight it, just in case
                field.setBackgroundResource(R.drawable.input_field_default);
            }
        }
        return fieldsAreInvalid;
    }
}
