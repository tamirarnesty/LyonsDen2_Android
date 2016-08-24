package com.wlmac.lyonsden2_android.contactActivities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.wlmac.lyonsden2_android.ContactActivity;
import com.wlmac.lyonsden2_android.R;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;

// I know the submission system is a bit overcomplicated, but in my defense I had bigger plans for it
// and I'm still planning on implementing them, just a bit later.

// TODO: CREATE LOADING WHEEL FOR WHEN APPROVING AND SUBMITTING THE ANNOUNCEMENT
// TODO: VISUALIZE THE DATE PICKERS, ANY WAY POSSIBLE
// TODO: FIGURE OUT ERROR HANDLING. LIKE NO INTERNET
// TODO: FIX AUTO GENERATED DATE/TIME FORMATTING (IN DATE/TIME FIELDS)

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
    /** States whether the proposal has been validated. */
    private boolean proposalValidated = false;
    /** Hold a cache of the teacher's IDs for more efficient internet usage. (Download only once) */
    private HashMap<String, String> teacherIDCache = null;
    /** Holds an instance of the database that is used with this activity. */
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.announcement_activity);

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
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,// R.style.DatePickerDialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Set the the date field to the current date
                        String date = year + "-" + monthOfYear + "-" + dayOfMonth;
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

//    /** Sets the appropriate font for each text label on this screen. */
//    private void setFonts () {
//        // Create instances of all labels on screen
//        TextView[] labels = {(TextView) findViewById(R.id.APSTitleLabel), (TextView) findViewById(R.id.APSDescriptionLabel), (TextView) findViewById(R.id.APSTeacherLabel)};
//        // Set their typefaces
//        for (int h = 0; h < labels.length; h ++) {
//            labels[0].setTypeface(HomeActivity.hapnaMonoLight);
//        }
//    }

    public void submitAnnouncement (View view) {
        Toast.makeText(this, "The real method is missing! 8(", Toast.LENGTH_LONG).show();
    }

    /** Called whenever the teacher credential has been validated and the submission process should begin. */
    private void approveProposal () {
        proposalValidated = true;
        // Notify the user about approval success
        Toast.makeText(this, "Approved!", Toast.LENGTH_SHORT).show();
        // Continue to submission
//        submitAnnouncement(null);
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
