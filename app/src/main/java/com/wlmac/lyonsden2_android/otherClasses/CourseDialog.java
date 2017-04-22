package com.wlmac.lyonsden2_android.otherClasses;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wlmac.lyonsden2_android.HomeActivity;
import com.wlmac.lyonsden2_android.LyonsDen;
import com.wlmac.lyonsden2_android.R;

/**
 * Created by Ademir on 1/11/2017.
 */

// Shared Preferences Keys
// Format: [periodKey][periodIndex]:[fieldIndex]
// Where: [periodKey] - the static period key belonging in CourseDialog
//        [periodIndex] - One of 0/1/2/3, in relation to one of the periods
//        [fieldIndex] - One of 0/1/2/3, in relation to the field on the period
// Example:
// periodKey + 2 + ":" + 4 = Refers to the Room Number field of period 3

public class CourseDialog extends DialogFragment {
    public static String spareKey = "SPARE";
    public static String periodKey = "Period ";

    private EditText[] periodFields;
    private String[] periodData;
    private int periodIndex;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.course_actvity, null);

        initializePeriodData();
        configureContentView(view);

        builder.setView(view);
        return builder.create();
    }

    private void initializePeriodData() {
        SharedPreferences preferences = getActivity().getSharedPreferences(LyonsDen.keySharedPreferences, Context.MODE_PRIVATE);
        String curPeriodKey = periodKey + periodIndex;
        periodData = new String[4];

        for (int h = 0; h < 4; h ++) {
            periodData[h] = preferences.getString(curPeriodKey + ":" + h, "");
            if (periodData[h].equals(spareKey)) {
                periodData[h] = "";
            }
        }
    }

    private void configureContentView(View view) {
        periodFields = new EditText[]{(EditText) view.findViewById(R.id.CourseSNameField),
                                      (EditText) view.findViewById(R.id.CourseSCodeField),
                                      (EditText) view.findViewById(R.id.CourseSTeacherField),
                                      (EditText) view.findViewById(R.id.CourseSRoomField)};

        for (int h = 0; h < periodFields.length; h ++) {
            periodFields[h].setText(periodData[h]);
            periodFields[h].setTypeface(Retrieve.typeface(getActivity()));
        }

        int[] ids = {R.id.CourseSNameLabel, R.id.CourseSCodeLabel, R.id.CourseSTeacherLabel, R.id.CourseSRoomLabel, R.id.CourseSPeriodLabel, R.id.CourseSSubmit, R.id.CourseSSpare};
        for (int id : ids) {
            ((TextView) view.findViewById(id)).setTypeface(Retrieve.typeface(getActivity()));
        }

        ((TextView) view.findViewById(R.id.CourseSPeriodLabel)).setText(periodKey + (periodIndex + 1));

        view.findViewById(R.id.CourseSSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int emptyCounter = 0;

                for (EditText field: periodFields) {
                    if (field.getText().toString().isEmpty()) {
                        emptyCounter ++;
                    }
                }

                if (emptyCounter != 4) {    // Prompt for spare confirmation
                    commitChanges(false);
                    updateInitiator();
                    CourseDialog.this.dismiss();
                } else {
                    Toast.makeText(getActivity(), "You must fill in at least one field", Toast.LENGTH_LONG).show();
                }
            }
        });

        view.findViewById(R.id.CourseSSpare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitChanges(true);
                updateInitiator();
                CourseDialog.this.dismiss();
            }
        });
    }

    private void commitChanges(boolean isSpare) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(LyonsDen.keySharedPreferences, Context.MODE_PRIVATE).edit();
        if (isSpare) {
            for (int h = 0; h < periodFields.length; h++) {
                editor.putString(periodKey + periodIndex + ":" + h, spareKey);
            }
        } else {
            for (int h = 0; h < periodFields.length; h++) {
                editor.putString(periodKey + periodIndex + ":" + h, periodFields[h].getText().toString());
            }
        }
        editor.apply();
    }

    private void updateInitiator() {
        // Update Initiator Time Table
        final HomeActivity activity = (HomeActivity) getActivity();
        activity.repopulateTimeTable();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (activity.getScheduleDay() > 0) {
                    activity.updateTimeTableUI();
                }
            }
        });
    }

    public void setPeriodIndex(int periodIndex) {
        this.periodIndex = periodIndex;
    }
}
