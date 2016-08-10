package com.wlmac.lyonsden2_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by sketch204 on 16-08-09.
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: AutoLogin
        // TODO: Resize the Splash Screen graphic
        Intent intent = new Intent (this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
