package com.wlmac.lyonsden2_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);


    }

    public void logIn (View view) {
        Intent intent = new Intent (this, HomeActivity.class);
        startActivity(intent);
    }
}
