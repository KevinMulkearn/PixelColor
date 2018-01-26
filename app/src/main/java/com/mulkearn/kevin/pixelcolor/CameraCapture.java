package com.mulkearn.kevin.pixelcolor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraCapture extends AppCompatActivity {

    Button capture_button;
    ImageView camera_image;
    Intent intent ;
    public  static final int RequestPermissionCode  = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_camera);

        capture_button = (Button)findViewById(R.id.capture_button);
        camera_image = (ImageView)findViewById(R.id.camera_image);

        EnableRuntimePermission();

        capture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 7);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 7 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            camera_image.setImageBitmap(bitmap);
        }
    }

    public void EnableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(CameraCapture.this, Manifest.permission.CAMERA))
        {
            Toast.makeText(CameraCapture.this,"CAMERA permission allowed", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(CameraCapture.this,new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {
            case RequestPermissionCode:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(CameraCapture.this,"Starting Camera", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CameraCapture.this,"Camera Permission Denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

}

