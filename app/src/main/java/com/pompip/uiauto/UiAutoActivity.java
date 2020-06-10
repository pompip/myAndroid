package com.pompip.uiauto;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.bonree.myapplication.R;

public class UiAutoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_auto);

        By.desc("");

    }
}