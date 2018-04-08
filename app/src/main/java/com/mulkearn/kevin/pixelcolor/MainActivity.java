package com.mulkearn.kevin.pixelcolor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //EnableRuntimePermission();
    }

    public void startImageActivity(View view) {
        Intent i_image = new Intent(this, ImageViewActivity.class);
        startActivity(i_image);
    }
}
