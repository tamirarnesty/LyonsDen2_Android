package com.wlmac.lyonsden2_android.contactActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.HomeActivity;
import com.wlmac.lyonsden2_android.R;

public class AnnouncementActivity extends AppCompatActivity {
    private EditText titleField;
    private EditText descriptionField;
    private EditText teacherLogin;
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

    /** Sets the appropriate font for each text label on this screen */
    private void setFonts () {
        TextView[] labels = {(TextView) findViewById(R.id.APSTitleLabel), (TextView) findViewById(R.id.APSDescriptionLabel), (TextView) findViewById(R.id.APSTeacherLabel)};
        for (int h = 0; h < labels.length; h ++) {
            labels[0].setTypeface(HomeActivity.hapnaMonoLight);
        }
    }

    /** Called whenever the submit button is pressed. Sends the proposed announcement to the database. */
    public void submitAnnouncement (View view) {
        if (!fieldsValid()) {
            return;
        }
        // TODO: Announcement submition
    }

    /** Called whenever the teacher's 'Approve' button is pressed. Check the validity of the entered credentials and disables further editing of the announcement. */
    public void validateProposal (View view) {
        if (!fieldsValid() || isTeacherValid(teacherLogin.getText().toString(), teacherPass.getText().toString())) {
            return;
        }
        // TODO: Announcement validation
    }

    /** Checks the comparison validity of every EditText on screen. */
    private boolean fieldsValid () {
        if (titleField == null || titleField.getText() == null || titleField.getText().toString().trim().equals("")) {
            return false;
        } else if (descriptionField == null || descriptionField.getText() == null || descriptionField.getText().toString().trim().equals("")) {
            return false;
        } else if (teacherLogin == null || teacherLogin.getText() == null || teacherLogin.getText().toString().trim().equals("")) {
            return false;
        } else if (teacherPass == null || teacherPass.getText() == null || teacherPass.getText().toString().trim().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    /** Checks the validity of the entered teacher's credentials. */
    private boolean isTeacherValid (String username, String password) {
        // TODO: Teacher Validity Check
        return false;
    }
}
