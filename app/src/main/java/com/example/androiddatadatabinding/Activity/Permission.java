package com.example.androiddatadatabinding.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androiddatadatabinding.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;

public class Permission extends AppCompatActivity {
    private static final String TAG = Permission.class.getSimpleName().toString();
    private Button btnKontak, btnStorage;
    private final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1122;
    private Switch switch_camera, switch_contact, switch_storage, switch_fingerprint;
    TextView textViewfinger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(TAG);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        textViewfinger = (TextView) findViewById(R.id.tv_fingerprint);
        /*SetSupportActionBar (toolbar);
        SupportActionBar.SetDisplayHomeAsUpEnabled (true);
        SupportActionBar.SetHomeButtonEnabled (true);
        */
        switch_fingerprint = findViewById(R.id.switch_fingerprint);
        switch_fingerprint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    requestFingerPrintPermission();
                } else {
                    return;
                }
            }
        });
        switch_contact = findViewById(R.id.switch_contact);
        switch_contact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    requestContactPermission();
                } else {
                    return;
                }
            }
        });
        switch_camera = findViewById(R.id.switch_camera);
        switch_camera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    requestCameraPermission();
                } else {
                    return;
                }
            }
        });
        switch_storage = findViewById(R.id.switch_storage);
        switch_storage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    requestStoragePermission();
                } else {
                    return;
                }
            }
        });


    }

    private void requestFingerPrintPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (!fingerprintManager.isHardwareDetected()) {
                textViewfinger.setText("Fingerprint Scanner not Detected in Device");
                Toast.makeText(getBaseContext(), "Fingerprint Scanner not Detected in Device", Toast.LENGTH_LONG).show();
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                textViewfinger.setText("Permission not grantef to use FingerPrint ");
                Toast.makeText(getBaseContext(),
                        "Permission not grantef to use FingerPrint ", Toast.LENGTH_LONG).show();
            } else if (!keyguardManager.isKeyguardSecure()) {
                textViewfinger.setText("Add Look to your Phone is Settings ");

                Toast.makeText(getBaseContext(),
                        "Add Look to your Phone is Settings ", Toast.LENGTH_LONG).show();
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                textViewfinger.setText("You should add atles 1 Fingerprint to use this feature ");

                Toast.makeText(getBaseContext(),
                        "You should add atles 1 Fingerprint to use this feature", Toast.LENGTH_LONG).show();
            } else {
                textViewfinger.setText("lace your Finger on Scnner to access ");

                Toast.makeText(getBaseContext(),
                        "Place your Finger on Scnner to access ", Toast.LENGTH_LONG).show();
                FingerprintHandler fingerprintHandler = new FingerprintHandler(getBaseContext());
                fingerprintHandler.startAuth(fingerprintManager, null);
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    public void showtext(String textView1, boolean b) {
        textViewfinger.setText(textView1);
        if (b == false) {
            textViewfinger.setTextColor(R.color.colorAccent);
        } else {
            textViewfinger.setTextColor(R.color.bluelight);
        }
    }

    private void requestContactPermission() {
        if (ContextCompat.checkSelfPermission(Permission.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getBaseContext());
                alert.setTitle("Permission Request");
                alert.setMessage("Dengan mengizinkan kami mengakses kontak anda akan memudahkan kami untuk memberikan fitur pencarian nomor telepon yang lebih baik");

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(), "Tampil", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alert.create();
                alert.show();
            } else {

                ActivityCompat.requestPermissions(Permission.this,
                        new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivity(intent);
        }
    }

    private void requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    private void requestCameraPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        openCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            showSettingDialog();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getBaseContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                    }

                })
                .onSameThread()
                .check();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 100);

    }

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Permission.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This App need permissions to use this featur, you can grant them in app setting");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSetting();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}

