package com.wlmac.lyonsden2_android.otherClasses;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.R;

/**
 * Created by sketch204 on 16-08-27.
 */
public class LyonsAlert extends DialogFragment {
    private String titleToSet = "Title";
    private String subtitleToSet = "Subtitle";
    private String leftButtonTitle = "Cancel";
    private String rightButtonTitle = "Ok";
    private View.OnClickListener leftButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            return;
        }
    };
    private View.OnClickListener rightButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            return;
        }
    };
    private EditText inputField;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View alertView = getActivity().getLayoutInflater().inflate(R.layout.lyons_alert_layout, null);

        ((TextView) alertView.findViewById(R.id.LATitle)).setText(titleToSet);
        ((TextView) alertView.findViewById(R.id.LASubtitle)).setText(subtitleToSet);
        ((Button) alertView.findViewById(R.id.LAButtonLeft)).setText(leftButtonTitle);
        ((Button) alertView.findViewById(R.id.LAButtonRight)).setText(rightButtonTitle);
        alertView.findViewById(R.id.LAButtonLeft).setOnClickListener(leftButtonOnClick);
        alertView.findViewById(R.id.LAButtonRight).setOnClickListener(rightButtonOnClick);

        inputField = (EditText) alertView.findViewById(R.id.LAInput);


        builder.setView(alertView);
        return builder.create();
    }

    public String getInputText () {
        return (inputField != null && inputField.getText().toString() != null) ? inputField.getText().toString() : null;
    }

    public void setTitle(String title) {
        titleToSet = title;
    }

    public void setSubtitle (String subtitle) {
        subtitleToSet = subtitle;
    }

    public void configureLeftButton (String title, View.OnClickListener onClick) {
        leftButtonTitle = title;
        leftButtonOnClick = onClick;
    }

    public void configureRightButton (String title, View.OnClickListener onClick) {
        rightButtonTitle = title;
        rightButtonOnClick = onClick;
    }
}
