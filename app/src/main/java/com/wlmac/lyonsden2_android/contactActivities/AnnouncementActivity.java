package com.wlmac.lyonsden2_android.contactActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.HomeActivity;
import com.wlmac.lyonsden2_android.R;

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
    /** An instance of the teacher login input field. */
    private EditText teacherLogin;
    /** An instance of the teacher password input field. */
    private EditText teacherPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.announcement_activity);

        instantiateComponents();
        setFonts();
    }

    /** Instantiates all GUI components */
    private void instantiateComponents () {
        titleField = (EditText) findViewById(R.id.APSTitleField);
        descriptionField = (EditText) findViewById(R.id.APSDescriptionField);
        teacherLogin = (EditText) findViewById(R.id.APSTeacherLogin);
        teacherPass = (EditText) findViewById(R.id.APSTeacherPass);
    }

    /** Sets the appropriate font for each text label on this screen. */
    private void setFonts () {
        // Create instances of all labels on screen
        TextView[] labels = {(TextView) findViewById(R.id.APSTitleLabel), (TextView) findViewById(R.id.APSDescriptionLabel), (TextView) findViewById(R.id.APSTeacherLabel)};
        // Set their typefaces
        for (int h = 0; h < labels.length; h ++) {
            labels[0].setTypeface(HomeActivity.hapnaMonoLight);
        }
    }

    /**
     * Called whenever the submit button is pressed. Sends the proposed announcement to the database.
     * @param view The view that called this method.
     */
    public void submitAnnouncement (View view) {
        // If any of the fields are invalid, then
        if (!fieldsValid()) {
            return;     // Return from this method
        }
        // TODO: Announcement submition
    }

    /**
     * Called whenever the teacher's 'Approve' button is pressed. Check the validity of the entered credentials and disables further editing of the announcement.
     * @param view The view that called this method.
     */
    public void validateProposal (View view) {
        // If the teacher login is not valid, then
        if (!fieldsValid() || isTeacherValid(teacherLogin.getText().toString(), teacherPass.getText().toString())) {
            return; // Return from this method.
        }
        // TODO: Announcement validation
    }

    /**
     * Checks the comparison validity of every EditText on screen.
     * @return Return true if, and only if, all the input fields on screen are valid.
     */
    private boolean fieldsValid () {
        if (titleField == null || titleField.getText() == null || titleField.getText().toString().trim().equals("")) {
            return false;   // If the title input field is invalid, then return false.
        } else if (descriptionField == null || descriptionField.getText() == null || descriptionField.getText().toString().trim().equals("")) {
            return false;   // If the description input field is invalid, then return false.
        } else if (teacherLogin == null || teacherLogin.getText() == null || teacherLogin.getText().toString().trim().equals("")) {
            return false;   // If the teacher login input field is invalid, then return false.
        } else if (teacherPass == null || teacherPass.getText() == null || teacherPass.getText().toString().trim().equals("")) {
            return false;   // If the teacher password input field is invalid, then return false.
        } else {
            return true;    // Otherwise return true.
        }
    }

    /** Checks the validity of the entered teacher's credentials. */
    private boolean isTeacherValid (String username, String password) {
        // TODO: Teacher Validity Check
        return false;
    }
}
