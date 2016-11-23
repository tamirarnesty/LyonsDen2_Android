package com.wlmac.lyonsden2_android.resourceActivities;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

        //Student Information View Layout
        IFSName = (EditText) findViewById(R.id.IFSName);
        IFSGrade = (Spinner) findViewById(R.id.IFSGrade);
        IFSCompleteButton = (Button) findViewById(R.id.IFSCompleteButton);
        IFSCompleteButton.setTypeface(Retrieve.typeface(this));

        //Teacher Information View Layout
        IFTName = (EditText) findViewById(R.id.IFTName);
        IFTDepartment = (Spinner) findViewById(R.id.IFTDepartment);
        IFTEmail = (EditText) findViewById(R.id.IFTEmail);
        IFTCompleteButton = (Button) findViewById(R.id.IFTCompleteButton);
        IFTCompleteButton.setTypeface(Retrieve.typeface(this));

    }
}
