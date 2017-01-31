package com.wlmac.lyonsden2_android.resourceActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;

public class InformationFormActivity extends AppCompatActivity {
    private EditText IFSName, IFTName, IFTEmail;
    private Spinner IFSGrade, IFTDepartment;
    private Button IFSCompleteButton, IFTCompleteButton;


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
        IFSGrade = (Spinner) findViewById(R.id.IFSGradeSpinner);
        IFSCompleteButton = (Button) findViewById(R.id.IFSSCompleteButton);

        //Teacher Information View Layout
        IFTName = (EditText) findViewById(R.id.IFSTNameField);
        IFTDepartment = (Spinner) findViewById(R.id.IFSDepartmentSpinner);
        IFTEmail = (EditText) findViewById(R.id.IFSEmailField);
        IFTCompleteButton = (Button) findViewById(R.id.IFSTCompleteButton);
    }

    private void setFonts () {
        int[] components ={R.id.IFSSNameLabel, R.id.IFSSNameField, R.id.IFSGradeLabel, R.id.IFSSCompleteButton, R.id.IFSTNameLabel, R.id.IFSTNameField, R.id.IFSDepartmentLabel, R.id.IFSEmailLabel, R.id.IFSEmailField, R.id.IFSTCompleteButton};
        for (int h = 0; h < components.length; h ++)
            ((TextView) findViewById(components[h])).setTypeface(Retrieve.typeface(this));
        // TODO: Set Spinner typeface
    }
}
