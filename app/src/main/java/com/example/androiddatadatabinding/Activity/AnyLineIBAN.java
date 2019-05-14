package com.example.androiddatadatabinding.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.androiddatadatabinding.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import at.nineyards.anyline.AnylineDebugListener;
import at.nineyards.anyline.core.RunFailure;
import at.nineyards.anyline.models.AnylineImage;
import at.nineyards.anyline.modules.ocr.AnylineOcrConfig;
import butterknife.ButterKnife;
import io.anyline.plugin.ScanResultListener;
import io.anyline.plugin.ocr.OcrScanResult;
import io.anyline.plugin.ocr.OcrScanViewPlugin;
import io.anyline.view.ScanView;

public class AnyLineIBAN extends AppCompatActivity implements AnylineDebugListener {
    private static final String TAG = AnyLineIBAN.class.getSimpleName();
    int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    boolean flagPermissions = false;
    String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA
    };
    private LinearLayout linearLayout;

    private ScanView scanView;

    private TextView textView_result;

    private ImageView imageNomorKtp;

    private ImageView imageFullKtp, ImageBorder;

    private View view;
    private ConstraintLayout constraintLayout;

    private Context mContext ;
    BottomSheetDialog mBottomSheetDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBar();
        ButterKnife.bind(this);
        setContentView(R.layout.activity_any_line_iban);
        linearLayout = findViewById(R.id.bottom_sheet);
        scanView = findViewById(R.id.scanview);
       /* textView_result = findViewById(R.id.tv_ktp);
        imageNomorKtp = findViewById(R.id.image_ktp);
        imageFullKtp = findViewById(R.id.imagefull_ktp);*/
        ImageBorder = findViewById(R.id.borderImageKtp);
        view = findViewById(R.id.border_anyline);
        constraintLayout = findViewById(R.id.constraintLayoutktp);
        mBottomSheetDialog = new BottomSheetDialog(this);

        View sheetView = this.getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        imageNomorKtp =sheetView.findViewById(R.id.image_ktp);
        imageFullKtp = sheetView.findViewById(R.id.imagefull_ktp);
        textView_result =sheetView.findViewById(R.id.tv_ktp);
        mBottomSheetDialog.setContentView(sheetView);

//        linearLayout.setVisibility(LinearLayout.INVISIBLE);
        init();
        TextView txtclose = (TextView)findViewById(R.id.txtclose);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void changeStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    void init() {
        AnylineOcrConfig anylineOcrConfig = new AnylineOcrConfig();
        anylineOcrConfig.setLanguages("TrainedModels/USNr.any");
        anylineOcrConfig.setCharWhitelist("1234567890");
        anylineOcrConfig.setScanMode(AnylineOcrConfig.ScanMode.AUTO);
        anylineOcrConfig.setValidationRegex("^-?\\d{16}$");
        scanView.setScanConfig("iban_view_config.json");
        OcrScanViewPlugin scanViewPlugin = new OcrScanViewPlugin(getApplicationContext(), getString(R.string.anyline_license_key), anylineOcrConfig, scanView.getScanViewPluginConfig(), "OCR");
        scanView.setScanViewPlugin(scanViewPlugin);
        scanViewPlugin.addScanResultListener(new ScanResultListener<OcrScanResult>() {
            @Override
            public void onResult(OcrScanResult result) {
                Bitmap bitmapNomorKtp = getBitmap(setupImagePath(result.getThresholdedImage()));
                Bitmap bitmapFullKtp = getBitmap(setupImagePath(result.getFullImage()));

                float koefX = (float) bitmapFullKtp.getWidth() / (float) constraintLayout.getWidth();
                float koefY = (float) bitmapFullKtp.getHeight() / (float) constraintLayout.getHeight();
                int x1 = view.getLeft();
                int y1 = view.getTop();
                int x2 = view.getWidth();
                int y2 = view.getHeight();
                int cropStartX = Math.round(x1 * koefX);
                int cropStartY = Math.round(y1 * koefY);
                int cropWidthX = Math.round(x2 * koefX);
                int cropHeightY = Math.round(y2 * koefY);
                if (cropStartX + cropWidthX <= bitmapFullKtp.getWidth() && cropStartY + cropHeightY <= bitmapFullKtp.getHeight()) {
                    bitmapFullKtp = Bitmap.createBitmap(bitmapFullKtp, cropStartX, cropStartY, cropWidthX, cropHeightY);
                }
                /*Settingan ThresholdImage*/
                /*Mat imageMat = new Mat();
                Utils.bitmapToMat(bitmapFullKtp, imageMat);
                Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGR2GRAY);
                Imgproc.threshold(imageMat, imageMat, 120, 235, Imgproc.THRESH_BINARY);
                Utils.matToBitmap(imageMat, bitmapFullKtp);*/

                imageNomorKtp.setImageBitmap(bitmapNomorKtp);
                imageFullKtp.setImageBitmap(bitmapFullKtp);

               /* linearLayout.setVisibility(LinearLayout.VISIBLE);
                view.setVisibility(View.INVISIBLE);
                ImageBorder.setVisibility(View.INVISIBLE);*/
                textView_result.setText(result.getResult().toString().trim());

                mBottomSheetDialog.show();
                scanView.start();
            }

        });
        scanViewPlugin.setDebugListener(this);
    }

    private Bitmap getBitmap(String path) {
        Bitmap bitmap = null;
        try {
            FileInputStream fi = new FileInputStream(path);
            Bitmap bitmapImage = BitmapFactory.decodeStream(fi);
            bitmap = Bitmap.createScaledBitmap(bitmapImage, bitmapImage.getWidth() * 2, bitmapImage.getHeight() * 2, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    protected String setupImagePath(AnylineImage anylineImage) {
        String imagePath = "";
        Long time = System.currentTimeMillis();
        try {
            if (this.getExternalFilesDir(null) != null) {
                imagePath = this.getExternalFilesDir(null).toString() + "/results/" + "mrz_image" + time;
            } else if (this.getFilesDir() != null) {
                imagePath = this.getFilesDir().toString() + "/results/" + "mrz_image" + time;
            }
            File fullFile = new File(imagePath);
            //create the directory
            fullFile.mkdirs();
            anylineImage.save(fullFile, 100);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanView.stop();
        scanView.releaseCameraInBackground();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onDebug(String s, Object o) {
        if (AnylineDebugListener.BRIGHTNESS_VARIABLE_NAME.equals(s) &&
                (AnylineDebugListener.BRIGHTNESS_VARIABLE_CLASS.equals(o.getClass()) ||
                        AnylineDebugListener.BRIGHTNESS_VARIABLE_CLASS.isAssignableFrom(o.getClass()))) {
            switch (scanView.getBrightnessFeedBack()) {
                case TOO_BRIGHT:
                    //  Toast.makeText(getApplicationContext(), "Terlalu Terang", Toast.LENGTH_LONG).show();
                    break;
                case TOO_DARK:
                    // Toast.makeText(getApplicationContext(), "Terlalu Gelap", Toast.LENGTH_LONG).show();
                    break;
                case OK:
                    // Toast.makeText(getApplicationContext(), "Oke, Sesuai", Toast.LENGTH_LONG).show();
                    break;
            }
        } else if (AnylineDebugListener.DEVICE_SHAKE_WARNING_VARIABLE_NAME.equals(s)) {
            System.out.println("asd to shake");
        }
    }

    @Override
    public void onRunSkipped(RunFailure runFailure) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the
        getMenuInflater().inflate(R.menu.menu_mlkit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_camera) {
            scanView.stop();
            scanView.start();
            mBottomSheetDialog.dismiss();
           /* view.setVisibility(View.VISIBLE);
            ImageBorder.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(LinearLayout.INVISIBLE);*/
        }

        return super.onOptionsItemSelected(item);
    }
}
