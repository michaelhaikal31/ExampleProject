package com.example.androiddatadatabinding.Activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.androiddatadatabinding.Fragment.ImageFragment;
import com.example.androiddatadatabinding.Fragment.PhotoFragment;
import com.example.androiddatadatabinding.R;

import butterknife.ButterKnife;

public class SavePhotoRecord extends AppCompatActivity  implements PhotoFragment.OnFragmentInteractionListener{
    Button cke;
    int PERMISSION_ALL = 1;
    boolean flagPermissions = false;

    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_photo_record);
        ButterKnife.bind(this);
        checkPermissions();

        cke = findViewById(R.id.make_photo_button);
        cke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check permissions
                if (!flagPermissions) {
                    checkPermissions();
                    return;
                }
                //start photo fragment
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.res_photo_layout, new PhotoFragment())
                        .addToBackStack(null)
                        .commit();

            }
        });
    }

    /*@OnClick(R.id.make_photo_button)
    void onClickScanButton() {
            }*/
    void checkPermissions() {
        if (!hasPermissions(this, PERMISSIONS)) {
            requestPermissions(PERMISSIONS,PERMISSION_ALL);
            flagPermissions = false;
        }
        flagPermissions = true;

    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onFragmentInteraction(Bitmap bitmap) {
        if (bitmap != null) {
            ImageFragment imageFragment = new ImageFragment();
            imageFragment.imageSetupFragment(bitmap);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.res_photo_layout, imageFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
