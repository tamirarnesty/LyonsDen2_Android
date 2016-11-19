package com.wlmac.lyonsden2_android.otherClasses;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.R;

/**
 * Created by sketch204 on 2016-11-18.
 */

public class ToastView {
    private View toastView;
    private LoadingLabel loadingLabel;
    private boolean isVisible = false;
//    private ProgressBar loadingCircle;

    public ToastView(Activity initiator) {
        toastView = initiator.getLayoutInflater().inflate(R.layout.toast_view, null);
        loadingLabel = new LoadingLabel(((TextView) toastView.findViewById(R.id.TVLoadingLabel)), initiator);
//        loadingCircle = (ProgressBar) toastView.findViewById(R.id.TVLoadingWheel);
        toastView.setVisibility(View.VISIBLE);
    }

    public View getView() {
        return toastView;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void show() {
        toastView.setVisibility(View.VISIBLE);
        isVisible = true;
        loadingLabel.startCycling();
    }

    public void hide() {
        toastView.setVisibility(View.GONE);
        isVisible = false;
        loadingLabel.stopCycling();
    }
}