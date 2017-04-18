package com.wlmac.lyonsden2_android.otherClasses;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
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
    private boolean inputShouldBeSecure = false;
    private boolean inputShouldBeHidden = false;
    private boolean leftButtonShouldBeHiiden = false;
    private boolean rightButtonShouldBeHiiden = false;
    private int titleGravity = Gravity.TOP | Gravity.START;
    private int subtitleGravity = Gravity.TOP | Gravity.START;
    private String[] startTimes = null;
    private String[] endTimes = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View alertView;

        if (startTimes == null && endTimes == null) {
            alertView = getActivity().getLayoutInflater().inflate(R.layout.lyons_alert_layout, null);
            ((TextView) alertView.findViewById(R.id.LATitle)).setText(titleToSet);
            ((TextView) alertView.findViewById(R.id.LATitle)).setGravity(titleGravity);
            ((TextView) alertView.findViewById(R.id.LASubtitle)).setText(subtitleToSet);
            ((TextView) alertView.findViewById(R.id.LASubtitle)).setGravity(subtitleGravity);
            ((Button) alertView.findViewById(R.id.LAButtonLeft)).setText(leftButtonTitle);
            ((Button) alertView.findViewById(R.id.LAButtonRight)).setText(rightButtonTitle);
            alertView.findViewById(R.id.LAButtonLeft).setOnClickListener(leftButtonOnClick);
            alertView.findViewById(R.id.LAButtonRight).setOnClickListener(rightButtonOnClick);

            inputField = (EditText) alertView.findViewById(R.id.LAInput);
            if (inputShouldBeSecure) {
                inputField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            if (inputShouldBeHidden) {
                inputField.setVisibility(View.GONE);
            }

            if (leftButtonShouldBeHiiden) {
                (alertView.findViewById(R.id.LAButtonLeft)).setVisibility(View.GONE);
            }

            if (rightButtonShouldBeHiiden) {
                (alertView.findViewById(R.id.LAButtonRight)).setVisibility(View.GONE);
            }
        } else {
            alertView = getActivity().getLayoutInflater().inflate(R.layout.late_start_schedule_layout, null);
            int[] startIDs = {R.id.LateSStart1, R.id.LateSStart2, R.id.LateSStartL, R.id.LateSStart3, R.id.LateSStart4};
            int[] endIDs = {R.id.LateSEnd1, R.id.LateSEnd2, R.id.LateSEndL, R.id.LateSEnd3, R.id.LateSEnd4};
            for (int h = 0; h < startIDs.length; h ++) {
                ((TextView) alertView.findViewById(startIDs[h])).setText(startTimes[h]);
            }
            for (int h = 0; h < endIDs.length; h ++) {
                ((TextView) alertView.findViewById(endIDs[h])).setText(endTimes[h]);
            }
        }

        setFonts(alertView);

        builder.setView(alertView);
        return builder.create();
    }

    private void setFonts(View target) {
        int[] components;
        if (endTimes == null && startTimes == null) {
            components = new int[] {R.id.LATitle, R.id.LASubtitle, R.id.LAButtonLeft, R.id.LAButtonRight, R.id.LAInput};
        } else {
            components = new int[] {R.id.LateSPerLabel1, R.id.LateSPerLabel2, R.id.LateSPerLabelL, R.id.LateSPerLabel3, R.id.LateSPerLabel4,
                                    R.id.LateSStart1, R.id.LateSStart2, R.id.LateSStartL, R.id.LateSStart3, R.id.LateSStart4,
                                    R.id.LateSPerSep1, R.id.LateSPerSep2, R.id.LateSPerSepL, R.id.LateSPerSep3, R.id.LateSPerSep4,
                                    R.id.LateSEnd1, R.id.LateSEnd2, R.id.LateSEndL, R.id.LateSEnd3, R.id.LateSEnd4,
                                    R.id.LateSTitle};
        }

        for (int h = 0; h < components.length; h ++) {
            ((TextView) target.findViewById(components[h])).setTypeface(Retrieve.typeface(getContext()));
        }
    }

    public String getInputText () {
        return (inputField != null && inputField.getText().toString() != null) ? inputField.getText().toString() : null;
    }

    // Must be called before dialog is shown
    public void makeInputSecure () {
        inputShouldBeSecure = true;
    }

    // Must be called before dialog is shown
    public void hideInput () {
        inputShouldBeHidden = true;
    }

    public void hideLeftButton () {
        leftButtonShouldBeHiiden = true;
    }

    public void hideRightButton () {
        rightButtonShouldBeHiiden = true;
    }

    public void setTitle(String title) {
        titleToSet = title;
    }

    public void setTitle(String title, int textGravity) {
        titleToSet = title;
        titleGravity = textGravity;
    }

    public void setSubtitle (String subtitle) {
        subtitleToSet = subtitle;
    }

    public void setSubtitle (String subtitle, int textGravity) {
        subtitleToSet = subtitle;
        subtitleGravity = textGravity;
    }

    public void configureLeftButton (String title, View.OnClickListener onClick) {
        leftButtonTitle = title;
        leftButtonOnClick = onClick;
    }

    public void configureRightButton (String title, View.OnClickListener onClick) {
        rightButtonTitle = title;
        rightButtonOnClick = onClick;
    }

    public void setEmailKeyboard() {
        if (inputField != null)
            inputField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

    public void setScheduleView(SchedulePopulator populator) {
        startTimes = new String[5];
        endTimes = new String[5];
        populator.populateContent(startTimes, endTimes);
    }

    public interface SchedulePopulator {
        void populateContent (String[] startTimes, String[] endTimes);
    }
}
