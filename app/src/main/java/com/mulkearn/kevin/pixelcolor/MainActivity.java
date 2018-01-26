package com.mulkearn.kevin.pixelcolor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    RelativeLayout mainView;
    Button searchButton;
    ImageView imageView;
    TextView locationText, pixelText, colorDisplay;
    private int REQUEST_CODE = 1;
    int x = 0, y = 0;
    int pixel, redValue, greenValue, blueValue;
    int height, width;
    Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        mainView = (RelativeLayout) findViewById(R.id.mainView);
        searchButton = (Button) findViewById(R.id.searchButton);
        imageView = (ImageView) findViewById(R.id.imageView);
        locationText = (TextView) findViewById(R.id.locationText);
        pixelText = (TextView) findViewById(R.id.pixelText);
        colorDisplay = (TextView) findViewById(R.id.colorDisplay);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        searchButton.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View v){
               Intent intent = new Intent();
               intent.setType("image/*");
               startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
           }
        });

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        x = (int)event.getX();
        if(x < 0){
            x = 0;
        } else if(x >= width) {
            x = width-1;
        }
        y = (int)event.getY();
        if(y < 0){
            y = 0;
        } else if(y >= height) {
            y = height-1;
        }
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//            case MotionEvent.ACTION_MOVE:
//            case MotionEvent.ACTION_UP:
//        }

        mainView.setDrawingCacheEnabled(true);
        mainView.buildDrawingCache();
        bm = mainView.getDrawingCache();

        pixel = bm.getPixel(x,y);

        redValue = Color.red(pixel);
        blueValue = Color.blue(pixel);
        greenValue = Color.green(pixel);

        locationText.setText("X:" + x + " Y:" + y);
        pixelText.setText("Red:" + redValue + "Green:" + blueValue + "Blue:" + greenValue);

        colorDisplay.setBackgroundColor(pixel);

        return false;//??check if this should be true??
    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data){
        super.onActivityResult(requestcode, resultcode, data);

        if(requestcode == REQUEST_CODE && resultcode == RESULT_OK && data != null && data.getData() != null){
            Uri uri = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
