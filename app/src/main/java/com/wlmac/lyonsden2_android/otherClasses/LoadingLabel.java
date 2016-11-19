package com.wlmac.lyonsden2_android.otherClasses;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by sketch204 on 2016-10-13.
 */

public class LoadingLabel {
    private String[] labelBank = {"UnLoading", "Just Loading", "Making sure you're on time", "Loading, just for you", "I, am your loader!",
            "Take some time to ROAR!!!", "Adding a touch of blue and yellow", "Charging to OVER 9000!!!",
            "Trying to do your homework for you", "Yes, this list is almost infinite", "This app is awesome!",
            "Assembling calendar events into order", "Adding a bit of quadratic equations to the mix", "Deciphering Mr. Wong's lessons",
            "Doing Techy stuff", "ROARing like a champ!", "Hacking the Pentagon (shhh...)", "Filling up water bottles",
            "Cleaning your locker", "Coming up with your career", "Solving your problems", "Saving lives", "Loading announcements",
            "Finding the Bay Harbour Butcher", "Aligning Components", "Aligning cells", "Formatting text", "Parsing web data",
            "Paying your debts", "Curing cancer", "Bribing teachers", "Coming up with the next joke", "Failing to come up with a joke",
            "Composing tests", "Stealing answers from teachers", "I forgot what to say", "Trying to fit into a locker", "Doing laps"};

    private String curLabel = "Loading";
    private LabelUpdater animator;
    private TextView target;
    private Activity initiator;

    public LoadingLabel(TextView target, Activity initiator) {
        this.target = target;
        this.initiator = initiator;
        onLabelUpdated();
    }

    private void onLabelUpdated () {
        initiator.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (curLabel.endsWith(".")) {
                    float oldPosition = target.getX();
                    target.setText(curLabel);
                    target.setX(oldPosition);
                } else {
                    Point screenSize = new Point();
                    TextPaint paint = target.getPaint();
                    Rect textSize = new Rect();
                    paint.getTextBounds(curLabel, 0, curLabel.length(), textSize);
                    initiator.getWindowManager().getDefaultDisplay().getSize(screenSize);
                    target.setText(curLabel);
                    target.setX((screenSize.x/2) - textSize.width()/2);
                }
            }
        });
    }

    public void startCycling () {
        if (animator == null) {
            animator = new LabelUpdater();
            animator.start();
        }
    }

    public void stopCycling () {
        if (animator != null && animator.isUpdating()) {
            animator.contentLoaded();
            animator = null;
        }
    }

    public void show() {
        initiator.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                target.setVisibility(View.VISIBLE);
            }
        });
        startCycling();
    }

    public void dismiss () {
        stopCycling();
        target.setVisibility(View.GONE);
    }

    private class LabelUpdater extends Thread {
        private boolean updating = false;

        private void updateLabel () {
            if (curLabel.endsWith("...")) {
                curLabel = labelBank[new Random().nextInt(labelBank.length)];
            } else {
                curLabel += ".";
            }
            onLabelUpdated();
        }

        public boolean isUpdating() {
            return updating;
        }

        public void contentLoaded () {
            Log.d("Loading Label", "Content Loaded!");
            try {
                updating = false;
                Log.d("Loading Label", "Joining Thread!");
                join();
                Log.d("Loading Label", "Thread Joined!");
            } catch (InterruptedException e) {
                Log.d("Loading Label", "Thread.join() Failed!");
            }
        }

        @Override
        public void run() {
            updating = true;
            while (updating == true) {
                updateLabel();
                Log.d("Loading Label", "Updating Label! State: " + updating);
                try { Thread.sleep(500); } catch (InterruptedException e) {}
            }
        }
    }
}
