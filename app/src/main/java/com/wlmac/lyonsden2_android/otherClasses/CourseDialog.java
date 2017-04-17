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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.HomeActivity;
import com.wlmac.lyonsden2_android.LyonsDen;
import com.wlmac.lyonsden2_android.R;

/**
 * Created by Ademir on 1/11/2017.
 */

public class CourseDialog extends DialogFragment {
    private EditText name, code, teacher, room;
    private Button submit;
    private TextView period;
    private String periodString = "Period", nameString, codeString, teacherString, roomString;
    private int periodInteger;
    String day = "";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.course_actvity ,null);

        period = (TextView) view.findViewById(R.id.CourseSPeriodLabel);
        name = (EditText) view.findViewById(R.id.CourseSNameField);
        code = (EditText) view.findViewById(R.id.CourseSCodeField);
        teacher = (EditText) view.findViewById(R.id.CourseSTeacherField);
        room = (EditText) view.findViewById(R.id.CourseSRoomField);
        room.setRawInputType(InputType.TYPE_CLASS_PHONE);

        submit = (Button) view.findViewById(R.id.USUpdate);

        period.setText(periodString);
        name.setText(nameString);
        code.setText(codeString);
        teacher.setText(teacherString);
        room.setText(roomString);
//        periodInteger = periodString.charAt(periodString.length()-1);
//        if (day.equals("2") && periodInteger == 3) {
//            periodString = periodString.substring(0, periodString.length()-1) + (periodInteger +1);
//        } else if (day.equals("2") && periodInteger == 4) {
//            periodString = periodString.substring(0, periodString.length()-1) + (periodInteger -1);
//        }
            submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty() && code.getText().toString().isEmpty() && teacher.getText().toString().isEmpty() && room.getText().toString().isEmpty()) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

                    alertBuilder.setTitle("Hold on!");

                    alertBuilder.setMessage("Is this a spare?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LyonsDen.keySharedPreferences, 0);
                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            edit.putBoolean(periodString, true);
                            edit.putString(periodString + " 0", name.getText().toString());
                            edit.putString(periodString + " 1", code.getText().toString());
                            edit.putString(periodString + " 2", teacher.getText().toString());
                            edit.putString(periodString + " 3", room.getText().toString());
                            edit.apply();
                            ((HomeActivity) getActivity()).updatePeriods();
                            getDialog().dismiss();
                            dialog.cancel();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = alertBuilder.create();
                    alertDialog.show();
                } else {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LyonsDen.keySharedPreferences, 0);
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putBoolean(periodString, false);
                    edit.putString(periodString + " 0", name.getText().toString());
                    edit.putString(periodString + " 1", code.getText().toString());
                    edit.putString(periodString + " 2", teacher.getText().toString());
                    edit.putString(periodString + " 3", room.getText().toString());
                    edit.apply();
                    ((HomeActivity) getActivity()).updatePeriods();
                    getDialog().dismiss();
                }
            }
        });
        builder.setView(view);
        return builder.create();
    }

    public void setPeriod(String period, String nameString, String codeString, String teacherString, String roomString, String dayLabel) {
        this.periodString = period;
        this.day = dayLabel;
        if (nameString.equals("Course Name"))
            this.nameString = "";
        else
            this.nameString = nameString;

        if (codeString.equals("Course Code"))
            this.codeString = "";
        else
            this.codeString = codeString;

        if (teacherString.equals("Teacher Name"))
            this.teacherString = "";
        else
            this.teacherString = teacherString;

        if (roomString.equals("Room Number"))
            this.roomString = "";
        else
            this.roomString = roomString;
    }
}
