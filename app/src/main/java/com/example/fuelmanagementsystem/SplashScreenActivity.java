package com.example.fuelmanagementsystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    IntentFilter intentFilter;

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sp_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp_login = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        if (sp_login.getBoolean("logged", false)) {
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        }else{
            startActivity(new Intent(SplashScreenActivity.this, UserLoginActivity.class));
        }

        finish();
    }
}
