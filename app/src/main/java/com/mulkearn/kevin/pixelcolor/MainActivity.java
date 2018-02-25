package com.mulkearn.kevin.pixelcolor;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    RelativeLayout mainView;
    Button searchButton, captureButton;
    ImageView imageView;
    TextView hexText, rgbText, hsvText, colorDisplay;
    Bitmap mainViewBitmap, imageBitmap = null;
    Uri uri, photoURI;
    public  static final int RequestPermissionCode  = 1 ;

    private int REQUEST_CODE = 0;
    int x = 0, y = 0;
    int pixel, redValue, greenValue, blueValue, height, width;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        //Get references
        mainView = (RelativeLayout) findViewById(R.id.mainView);
        searchButton = (Button) findViewById(R.id.searchButton);
        captureButton = (Button) findViewById(R.id.captureButton);
        imageView = (ImageView) findViewById(R.id.imageView);
        hexText = (TextView) findViewById(R.id.hexText);
        rgbText = (TextView) findViewById(R.id.rgbText);
        hsvText = (TextView) findViewById(R.id.hsvText);
        colorDisplay = (TextView) findViewById(R.id.colorDisplay);

        //Get screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        //Set imageView image
        uri = Uri.parse("android.resource://com.mulkearn.kevin.pixelcolor/" + R.drawable.color_home_screen);
        imageView.setImageURI(null);
        imageView.setImageURI(uri);

        EnableRuntimePermission();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelable("uri", uri); //(key,value)
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        uri = savedInstanceState.getParcelable("uri");
        if(uri != null){
            imageView.setImageURI(uri);
        }
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

        mainView.setDrawingCacheEnabled(true);
        mainView.buildDrawingCache();
        mainViewBitmap = mainView.getDrawingCache();

        pixel = mainViewBitmap.getPixel(x,y);
        redValue = Color.red(pixel);
        blueValue = Color.blue(pixel);
        greenValue = Color.green(pixel);

        hexText.setText("Hex: " + rgbToHex(redValue, greenValue, blueValue));
        rgbText.setText("rgb(" + redValue + ", " + greenValue + ", " + blueValue + ")");
        hsvText.setText(getHSVValue(redValue, greenValue, blueValue));
        colorDisplay.setBackgroundColor(pixel);

        return false;
    }

    public void onOpenClick(View view) {
        REQUEST_CODE = 1;
        Intent i_open = new Intent();
        i_open.setType("image/*");
        i_open.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i_open, "Select Image"), REQUEST_CODE);
    }

    public void onCaptureClick(View view) {
        REQUEST_CODE = 2;
        Intent i_capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (i_capture.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(MainActivity.this, "Error Saving", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.mulkearn.kevin.fileprovider", photoFile);
                i_capture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                uri = photoURI;
                startActivityForResult(i_capture, REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){ //Get device image
            uri = data.getData();
            try{
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Unexpected Error", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) { //Get thumbnail image
            setPic();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName, ".jpg", storageDir); // prefix, suffix, directory
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(imageBitmap);
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
        String hue = Math.round(h) + "\u00b0";
        String sat = Math.round(s) + "%";
        String val = Math.round(v) + "%";

        return "hsv(" + hue + ", " + sat + ", " + val + ")";
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

}
