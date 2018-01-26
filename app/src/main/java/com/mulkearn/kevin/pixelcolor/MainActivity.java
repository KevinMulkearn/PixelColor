package com.mulkearn.kevin.pixelcolor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    RelativeLayout mainView;
    Button searchButton, captureButton;
    ImageView imageView;
    TextView hexText, rgbText, hsvText, colorDisplay;
    //private int REQUEST_CODE = 1;
    private int REQUEST_CODE = 0;
    int x = 0, y = 0;
    int pixel, redValue, greenValue, blueValue;
    int height, width;
    Bitmap mainViewBitmap, imageBitmap = null;
    Uri uri;
    public  static final int RequestPermissionCode  = 1 ;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        mainView = (RelativeLayout) findViewById(R.id.mainView);
        searchButton = (Button) findViewById(R.id.searchButton);
        captureButton = (Button) findViewById(R.id.captureButton);
        imageView = (ImageView) findViewById(R.id.imageView);
        hexText = (TextView) findViewById(R.id.hexText);
        rgbText = (TextView) findViewById(R.id.rgbText);
        hsvText = (TextView) findViewById(R.id.hsvText);
        colorDisplay = (TextView) findViewById(R.id.colorDisplay);

        uri = Uri.parse("android.resource://com.mulkearn.kevin.pixelcolor/"+R.drawable.color_home_screen);
        imageView.setImageURI(null);
        imageView.setImageURI(uri);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        EnableRuntimePermission();

//        searchButton.setOnClickListener(new View.OnClickListener(){
//           @Override
//            public void onClick(View v){
//               Intent intent = new Intent();
//               intent.setType("image/*");
//               startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE);
//           }
//        });

    }

    public void EnableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA))
        {
            Toast.makeText(MainActivity.this,"CAMERA permission allowed", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        //(key,value)
        outState.putParcelable("uri", uri);
    }

    //reset the saved state values
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        Uri newUri = savedInstanceState.getParcelable("uri");
        uri = newUri;
        imageView.setImageURI(null);
        imageView.setImageURI(uri);
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
        mainViewBitmap = mainView.getDrawingCache();

        pixel = mainViewBitmap.getPixel(x,y);

        redValue = Color.red(pixel);
        blueValue = Color.blue(pixel);
        greenValue = Color.green(pixel);

        hexText.setText("Hex: " + rgbToHex(redValue, blueValue, greenValue));
        rgbText.setText("rgb(" + redValue + ", " + blueValue + ", " + greenValue + ")");
        hsvText.setText(getHSVValue(redValue, blueValue, greenValue));

        colorDisplay.setBackgroundColor(pixel);

        return false;//??check if this should be true??
    }

    public String rgbToHex(int r, int g, int b){
        return String.format("#%02X%02X%02X",r, g, b);
    }

    public String getHSVValue(int r, int g, int b){
        float[] hsv = new float[3];
        Color.RGBToHSV(r, g, b, hsv);
        float h = hsv[0];
        float s = hsv[1] * 100;
        float v = hsv[2] * 100;
        String hue = Integer.toString((int) h) + "\u00b0";
        String sat = Integer.toString((int) s) + "%";
        String val = Integer.toString((int) v) + "%";

        return "hsv(" + hue + ", " + sat + ", " + val + ")";
    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data){
        super.onActivityResult(requestcode, resultcode, data);

        if(requestcode == 1 && resultcode == RESULT_OK && data != null && data.getData() != null){
            uri = data.getData();
            try{
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //////
        else if (requestcode == 7 && resultcode == RESULT_OK) {
            //Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageBitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(imageBitmap);
        }
        ///////
    }

//    public void onCaptureClick(View view) {
//        Intent i = new Intent(this, CameraCapture.class);
//        startActivity(i);
//    }

    public void onOpenClick(View view) {
        REQUEST_CODE = 1;
        intent = new Intent();
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE);
    }

    public void onCaptureClick(View view) {
        REQUEST_CODE = 7;
        intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE);
    }





    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {
            case RequestPermissionCode:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,"Starting Camera", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this,"Camera Permission Denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }



}
