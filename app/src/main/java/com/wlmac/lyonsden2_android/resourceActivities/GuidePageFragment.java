package com.wlmac.lyonsden2_android.resourceActivities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GuidePageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GuidePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuidePageFragment extends Fragment {
    public static int pageFirst = 0;
    public static int pageSecond = 1;
    public static int pageMain = 2;
    public static int pageLast = 3;

    private String text;
    private Drawable image;
    private int pageType;

    public GuidePageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GuidePageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public void configureFragmentTo(Drawable image, String text, int pageType) {
        this.text = text;
        this.image = image;
        this.pageType = pageType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_page, container, false);

        // Configure the layout to display the proper and configured page
        if (pageType == pageMain) { // Main page will be displayed
            view.findViewById(R.id.FGPFirst).setVisibility(View.GONE);  // Hide the non-main page layout
            view.findViewById(R.id.FGPLast).setVisibility(View.GONE);  // Hide the last page layout

            // Configure the page
            ((ImageView) view.findViewById(R.id.FGPMainImageView)).setImageDrawable(image);
            ((TextView) view.findViewById(R.id.FGPMainText)).setText(text);
            ((TextView) view.findViewById(R.id.FGPMainText)).setTypeface(Retrieve.typeface(getContext()));
        } else if (pageType == pageLast) {
            view.findViewById(R.id.FGPMain).setVisibility(View.GONE);   // Hide the main page layout
            view.findViewById(R.id.FGPFirst).setVisibility(View.GONE);  // Hide the non-main page layout
        }else {    // A non-main page will be displayed
            view.findViewById(R.id.FGPMain).setVisibility(View.GONE);   // Hide the main page layout
            view.findViewById(R.id.FGPLast).setVisibility(View.GONE);  // Hide the last page layout

            if (pageType == pageSecond) {       // If a second or last page is displayed, hdie the image view
                view.findViewById(R.id.FGPFirstImageView).setVisibility(View.GONE);
            }

            // Configure the page
            ((ImageView) view.findViewById(R.id.FGPFirstImageView)).setImageDrawable(image);
            ((TextView) view.findViewById(R.id.FGPFirstText)).setText(text);
            ((TextView) view.findViewById(R.id.FGPFirstText)).setTypeface(Retrieve.typeface(getContext()));
            ((TextView) view.findViewById(R.id.FGPBottomText)).setTypeface(Retrieve.typeface(getContext()));
        }

        return view;
    }
}
