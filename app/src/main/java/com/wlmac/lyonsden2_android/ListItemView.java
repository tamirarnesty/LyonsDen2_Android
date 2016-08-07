package com.wlmac.lyonsden2_android;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class ListItemView extends View {
    private String titleString = null;
    private float titleTextSize = 20;
//    private float titleTextWidth;
    private float titleTextHeight;
    private TextPaint titleTextPainter;

    private String infoString = null;
    private float infoTextSize = 15;
//    private float infoTextWidth;
    private float infoTextHeight;
    private TextPaint infoTextPainter;

    private int textColor = Color.GRAY;
    private Drawable image = getResources().getDrawable(R.drawable.inal);
    // The preset padding used in during the drawing cycles.
    private int paddingLeft = getPaddingLeft(), paddingTop = getPaddingTop(), paddingRight = getPaddingRight(), paddingBottom = getPaddingBottom();

    public ListItemView(Context context) {
        super(context);
        init(null, 0);
    }

    public ListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ListItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public ListItemView (Context context, AttributeSet attrs, int defStyle, String title, String info, @Nullable Drawable image) {
        super(context, attrs);
        this.titleString = title;
        this.infoString = info;
        this.image = image;
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        // 'a' will be used to access all the attributes declared for this view, in its attributes file.
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ListItemView, defStyle, 0);

        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with values that should fall on pixel boundaries.
        titleTextSize = a.getDimension(R.styleable.ListItemView_titleTextSize, titleTextSize);
        infoTextSize = a.getDimension(R.styleable.ListItemView_infoTextSize, infoTextSize);
        textColor = a.getColor(R.styleable.ListItemView_textColor, textColor);

        a.recycle();
        // 'a' should not be used from this point on

        // Give the strings a default value, to prevent errors, just in case.
        titleString = (titleString == null || titleString.trim().equals("")) ? "Title here" : titleString.trim();
        infoString = (infoString == null) ? "" : infoString.trim();

        // Set up TextPainters for title
        titleTextPainter = new TextPaint();
        titleTextPainter.setFlags(Paint.ANTI_ALIAS_FLAG);
        titleTextPainter.setTextAlign(Paint.Align.LEFT);
        // Set up TextPainters for info
        infoTextPainter = new TextPaint();
        infoTextPainter.setFlags(Paint.ANTI_ALIAS_FLAG);
        infoTextPainter.setTextAlign(Paint.Align.LEFT);
        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    // Finalize the setup of the textPainters
    private void invalidateTextPaintAndMeasurements() {
        titleTextPainter.setTextSize(titleTextSize);                // Set text size
        titleTextPainter.setColor(textColor);                       // Set text color
        titleTextPainter.setTypeface(HomeActivity.hapnaMonoLight);  // Set text font
//        titleTextWidth = titleTextPainter.measureText(titleString); // Set text width
        Paint.FontMetrics titleMetrics = titleTextPainter.getFontMetrics(); // Create text baseline measuring tool
        titleTextHeight = titleMetrics.ascent;  // Set height to distance from baseline to top of text, as if it is a single line text.

        infoTextPainter.setTextSize(infoTextSize);                  // Set text size
        infoTextPainter.setColor(textColor);                        // Set text color
        infoTextPainter.setTypeface(HomeActivity.hapnaMonoLight);   // Set text font
//        infoTextWidth = infoTextPainter.measureText(infoString);    // Set text width
        Paint.FontMetrics infoMetric = infoTextPainter.getFontMetrics();    // Create text baseline measuring tool
        infoTextHeight = infoMetric.ascent;     // Set height to distance from baseline to top of text, as if it is a single line text.
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Super call
        super.onDraw(canvas);
        // This will hold the x position for text
        int xPos;
        // If an image exists
        if (image != null) {
            // Size the image
            image.setBounds(paddingLeft,                    // Set its left bound
                            paddingTop,                     // Set its top bound
                            paddingLeft + (getWidth() / 6), // Set its right bound
                            paddingTop + (getHeight() - paddingBottom - paddingTop));   // Set its bottom bound
            xPos = paddingLeft + image.getBounds().width() + 8; // Shift the text over to the right
            image.draw(canvas);     // Draw the image on the canvas
        } else {    // Otherwise
            xPos = paddingLeft;     // Keep the text on the left edge
        }

        // I don't quite understand the y positioning but, the 8 is the distance between title and info. If heights will be removed, the strings will be drawn at their baselines

        // Draw the title at the specified (x, y) with the specified TextPainter
        canvas.drawText(titleString, xPos, paddingTop - titleTextHeight, titleTextPainter);
        // Draw the info at the specified (x, y) with the specified TextPainter
        canvas.drawText(infoString, xPos, paddingTop - titleTextHeight - infoTextHeight + 8, infoTextPainter);
    }

    public String getTitleString() {
        return titleString;
    }

    public void setTitleString(String titleString) {
        this.titleString = titleString;
    }

    public String getInfoString() {
        return infoString;
    }

    public void setInfoString(String infoString) {
        this.infoString = infoString;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}