package com.example.krzysztofjdrusiak.altodom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class OswietlenieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oswietlenie);
    }

    public void onNextClickBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onNextClickBtnStairs(View view) {
        Intent intent = new Intent(this, SchodyActivity.class);
        startActivity(intent);
    }
}
