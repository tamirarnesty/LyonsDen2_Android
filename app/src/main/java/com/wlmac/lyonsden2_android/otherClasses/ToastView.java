package com.wlmac.lyonsden2_android.otherClasses;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.wlmac.lyonsden2_android.R;

/**
 * Created by sketch204 on 2016-11-18.
 */

public class ToastView extends DialogFragment {
    View toastView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        toastView = getActivity().getLayoutInflater().inflate(R.layout.toast_view, null);

        builder.setView(toastView);
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(300, 280);
    }
}