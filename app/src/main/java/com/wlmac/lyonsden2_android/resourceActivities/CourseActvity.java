package com.wlmac.lyonsden2_android.resourceActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.R;

public class CourseActvity extends AppCompatActivity {
    private TextView periodLabel;
    private EditText code;
    private EditText name;
    private EditText teacher;
    private EditText room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_actvity);
        //                    The Intent  Retrieve array with key "periodData"
        String[] periodData = getIntent().getStringArrayExtra("periodData");

        periodLabel = (TextView) findViewById(R.id.CourseSPeriodLabel);
        periodLabel.setText("Period " + periodData[0]);

        code = (EditText) findViewById(R.id.CourseSCodeField);
        code.setText(periodData[1]);

        name = (EditText) findViewById(R.id.CourseSNameField);
        name.setText(periodData[2]);

        teacher = (EditText) findViewById(R.id.CourseSTeacherField);
        teacher.setText(periodData[3]);

        room = (EditText) findViewById(R.id.CourseSRoomField);
        room.setText(periodData[4]);
    }

    public void submitChanges (View view) {
        Log.d("Course Activity", "Let's pretend that I actually submitted your changes to the database? or saved them to the phone");
        // SharedPreferences is the way to store things, use the static in HS to access the proper Preferences, or create your own, up to you
    }
}
