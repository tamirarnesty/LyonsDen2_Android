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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;
import com.wlmac.lyonsden2_android.ContactActivity;
import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.LoadingLabel;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;
import com.wlmac.lyonsden2_android.otherClasses.ToastView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

    private boolean isProposalLocked = false;

    private ToastView loadingToast;

    private DatabaseReference targetRef = null;

    private String intentData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.announcement_activity);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        instantiateComponents();
        setFonts();
        intentData = getIntent().getStringExtra("announcementId");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private void setFonts() {
        int[] component = {R.id.APSTitleLabel, R.id.APSDescriptionLabel, R.id.APSLocationLabel, R.id.APSTeacherLabel, R.id.APSTitleField, R.id.APSDescriptionField, R.id.APSDateField, R.id.APSTimeField, R.id.APSLocationField, R.id.APSTeacherLogin, R.id.APSDateButton, R.id.APSTimeButton, R.id.APSApproveButton, R.id.APSSubmitButton};

        for (int h = 0; h < component.length; h ++)
            ((TextView) findViewById(component[h])).setTypeface(Retrieve.typeface(this));
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
                        String month = (monthOfYear < 10) ? "0" + (monthOfYear + 1) : "" + (monthOfYear + 1);
                        String day = (dayOfMonth < 10) ? "0" + dayOfMonth : "" + dayOfMonth;
                        String date = year + "-" + month + "-" + day;
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
                        String hour = (hourOfDay < 10) ? "0" + hourOfDay : "" + hourOfDay;
                        String min = (minute < 10) ? "0" + minute : "" + minute;
                        String time = hour + ":" + min;
                        timeField.setText(time);
                    }
                }, curHour, curMinute, false);
        // Display the time picker
        timePickerDialog.show();
    }

    /** Instantiates all GUI components. Also sets the initial date/time for the date and time fields. */
    private void instantiateComponents () {
        if (!getIntent().getStringExtra("clubKey").equals("no-key"))
            targetRef = FirebaseDatabase.getInstance().getReference("clubs").child(getIntent().getStringExtra("clubKey")).child("announcements");
        titleField = (EditText) findViewById(R.id.APSTitleField);
        descriptionField = (EditText) findViewById(R.id.APSDescriptionField);
        locationField = (EditText) findViewById(R.id.APSLocationField);
        teacherLogin = (EditText) findViewById(R.id.APSTeacherLogin);
        approveButton = (Button) findViewById(R.id.APSApproveButton);
        submitButton = (Button) findViewById(R.id.APSSubmitButton);
        loadingToast = new ToastView();

        final Calendar cal = Calendar.getInstance();
        // An Array of fields that should be formatted
        String [] fields = {"" + (cal.get(Calendar.MONTH) + 1), "" + cal.get(Calendar.DAY_OF_MONTH), "" + cal.get(Calendar.HOUR_OF_DAY), "" + cal.get(Calendar.MINUTE)};
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

    public void submitAnnouncement (View view) {
        loadingToast.show(getFragmentManager(), "SomeDialog");

        if (Retrieve.isInternetAvailable(this)) {
            DatabaseReference announcement = (targetRef != null) ? targetRef.push() :FirebaseDatabase.getInstance().getReference("announcements").push();
            String dateTime = dateField.getText().toString().replaceAll("-", "") + timeField.getText().toString().replaceAll(":", "") + "00";

            announcement.child("title").setValue(titleField.getText().toString());
            announcement.child("description").setValue(descriptionField.getText().toString());
            announcement.child("dateTime").setValue(dateTime);
            announcement.child("location").setValue(locationField.getText().toString());
            announcement.child("creator").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

            if (!intentData.equals("clubsAnnouncement"))
                pushNotification(titleField.getText().toString(), descriptionField.getText().toString());

            Toast.makeText(getApplicationContext(), "Announcement Posted!", Toast.LENGTH_SHORT).show();

            finish();
        } else {    // No Internet
            Toast.makeText(getApplicationContext(), "Submission Failed!\nNo Internet!", Toast.LENGTH_LONG).show();
        }
        loadingToast.dismiss();
    }

    public void pushNotification(final String title, final String message) {
        final String[] userID = {""};
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(final String userId, String registrationId) {
                userID[0] = userId;
                Log.d("Propose Announcement", userId);
            // idsAvailable completed.
                // Begin sending
                Retrieve.oneSignalIDs(new Retrieve.OneSignalHandler() {
                    @Override
                    public void handle(ArrayList<String> receivers) {
                        ArrayList<String> formattedReceivers = new ArrayList<String>();
                        for (int i = 0; i < receivers.size(); i++) {
                            formattedReceivers.add(i, "'" + receivers.get(i) + "'");
                            Log.d("RECEIVERS------------", formattedReceivers.get(i));
                        }

                        if (formattedReceivers.contains("'" + userId + "'")) {
                            formattedReceivers.remove("'" + userId + "'");
                        }

                        try {
                            OneSignal.postNotification(new JSONObject("{" +
                                            "'headings': {'en':'" + title + "'}, " +
                                            "'contents': {'en':'" + message + "'}, " +
                                            "'include_player_ids':" + formattedReceivers + "}"),
                                    new OneSignal.PostNotificationResponseHandler() {
                                        @Override
                                        public void onSuccess(JSONObject response) {
                                            Log.d("AnnouncementActivity", "postNotification Success: " + response.toString());
                                        }

                                        @Override
                                        public void onFailure(JSONObject response) {
                                            Log.d("AnnouncementActivity", "postNotification Failure: " + response.toString());
                                        }
                                    });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void lockUnlockProposal() {
        titleField.setEnabled(!isProposalLocked);
        descriptionField.setEnabled(!isProposalLocked);
        ((Button) findViewById(R.id.APSDateButton)).setEnabled(!isProposalLocked);
        ((Button) findViewById(R.id.APSTimeButton)).setEnabled(!isProposalLocked);
        dateField.setEnabled(!isProposalLocked);
        timeField.setEnabled(!isProposalLocked);
        locationField.setEnabled(!isProposalLocked);
        teacherLogin.setEnabled(!isProposalLocked);

        submitButton.setVisibility((isProposalLocked) ? View.VISIBLE : View.GONE );
        approveButton.setText((isProposalLocked) ? "Unlock" : "Approve");
    }

    /** Called whenever the teacher credential has been validated and the submission process should begin. */
    public void approveProposal (View view) {
        // If unlocking proposal
        if (isProposalLocked) {
            isProposalLocked = false;
            lockUnlockProposal();
            return;
        }
        // If fields are invalid
        if (fieldsAreInvalid()) {
            Toast.makeText(this, "Some Fields are invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingToast.show(getFragmentManager(), "SomeDialog");

        // Perform internet check
        boolean internetAvailable = Retrieve.isInternetAvailable(this);

        if (internetAvailable) {
            Retrieve.teacherApproval(this, teacherLogin.getText().toString(), new Retrieve.StatusHandler() {
                @Override
                public void handle(boolean status) {
                    isProposalLocked = status;
                    if (isProposalLocked) {
                        Log.d("Announcement Proposal", "Approval Success!");
                        Toast.makeText(getApplicationContext(), "Approval Success!", Toast.LENGTH_LONG).show();
                        teacherLogin.setText("");
                        lockUnlockProposal();
                        loadingToast.dismiss();
                    } else {
                        Log.d("Announcement Proposal", "Approval Failed! Wrong Password!");
                        Toast.makeText(getApplicationContext(), "Approval Failed!\nWrong Key!", Toast.LENGTH_LONG).show();
                        loadingToast.dismiss();
                    }
                }
            });
        } else {
            Log.d("Announcement Proposal", "Approval Failed! No Internet!");
            Toast.makeText(getApplicationContext(), "Approval Failed!\nNo Internet!", Toast.LENGTH_LONG).show();
            loadingToast.dismiss();
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
