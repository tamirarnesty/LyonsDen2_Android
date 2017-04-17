package com.wlmac.lyonsden2_android.resourceActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.onesignal.OneSignal;
import com.wlmac.lyonsden2_android.HomeActivity;
import com.wlmac.lyonsden2_android.R;

public class GuideActivity extends AppCompatActivity {
    private PageViewer pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_activity);

        // Create each guide page
        int[] drawables = {R.drawable.den_logo, -1, R.drawable.guide_image1, R.drawable.guide_image2, R.drawable.guide_image3, R.drawable.guide_image4, R.drawable.guide_image5, -1};
        int[] texts = {R.string.Page1, R.string.Page2, R.string.Page3, R.string.Page4, R.string.Page5, R.string.Page6, R.string.Page7, R.string.Page8};
        final int[] pageTypes = {GuidePageFragment.pageFirst, GuidePageFragment.pageSecond, GuidePageFragment.pageMain, GuidePageFragment.pageMain, GuidePageFragment.pageMain, GuidePageFragment.pageMain, GuidePageFragment.pageMain, GuidePageFragment.pageLast};

        Log.d("Guide Activity", "Loading Pages into Page Viewer Adapter!");
        // Configure the page viewer
        pager = (PageViewer) findViewById(R.id.GSPager);
        pager.setAdapter(new PageViewerAdapter(getSupportFragmentManager(), drawables, texts, pageTypes, getResources()));

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == pageTypes.length - 1) {
                    pager.setPagingEnabled(false);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                            }


                            GuideActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(GuideActivity.this, HomeActivity.class);
                                    GuideActivity.this.startActivity(intent);
                                    promptNotification();
                                }
                            });
                        }
                    }).start();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void promptNotification() {
        final boolean [] notificationsChosen = {false};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Announcement Notifications");
        alertBuilder.setMessage("Do you wish to receive notifications?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OneSignal.setSubscription(true);
                        notificationsChosen[0] = true;
                        dialog.cancel();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OneSignal.setSubscription(false);
                notificationsChosen[0] = false;
                dialog.cancel();
            }
        });
        alertBuilder.create().show();
    }

}

class PageViewer extends ViewPager {
    private boolean isPagingEnabled = true;

    public PageViewer (Context context) {
        super(context);
    }

    public PageViewer (Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }

    public boolean isPagingEnabled() {
        return isPagingEnabled;
    }
}

class PageViewerAdapter extends FragmentPagerAdapter {
    private int[] images;
    private int[] texts;
    private int[] types;
    private Resources res;

    public PageViewerAdapter(FragmentManager fm, int[] drawables, int[] texts, int[] pageTypes, Resources res) {
        super(fm);
        this.images = drawables;
        this.texts = texts;
        this.types = pageTypes;
        this.res = res;
    }

    @Override
    public Fragment getItem(int position) {
        GuidePageFragment page = new GuidePageFragment();
        Drawable image = null;
        if (images[position] != -1) {
            image = res.getDrawable(images[position]);
        }

        page.configureFragmentTo(image, res.getString(texts[position]), types[position]);

        return page;
    }

    @Override
    public int getCount() {
        return types.length;
    }
}
