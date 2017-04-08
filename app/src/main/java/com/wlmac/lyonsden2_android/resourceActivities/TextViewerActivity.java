package com.wlmac.lyonsden2_android.resourceActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.wlmac.lyonsden2_android.R;
import com.wlmac.lyonsden2_android.otherClasses.Retrieve;

import java.io.IOException;
import java.io.InputStream;

public class TextViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_viewer_activity);

        // Configure the textView
        ((TextView) findViewById(R.id.TVSText)).setText(Html.fromHtml(parseFile(getIntent().getStringExtra("filename"))));
        ((TextView) findViewById(R.id.TVSText)).setTypeface(Retrieve.typeface(this));
    }

    private String parseFile (String filename) {
        InputStream is = null;
        try { is = getAssets().open(filename); } catch (IOException e) {
            Log.d("TextViewActivity", "An IOException Occurred");
            Log.d("TextViewActivity", e.getMessage());
            Log.d("TextViewActivity", e.toString());
        }

        return Retrieve.stringFromStream(is);
    }
}
