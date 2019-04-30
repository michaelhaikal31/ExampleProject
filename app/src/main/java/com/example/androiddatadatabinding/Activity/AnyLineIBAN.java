package com.example.androiddatadatabinding.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import io.anyline.plugin.ScanResultListener;
import io.anyline.plugin.ocr.OcrScanResult;
import io.anyline.plugin.ocr.OcrScanViewPlugin;
import io.anyline.view.ScanView;

public class AnyLineIBAN extends AppCompatActivity implements AnylineDebugListener {
    private static final String TAG = AnyLineIBAN.class.getSimpleName();
    private ScanView scanView;
    private TextView textView_result;
    private LinearLayout linearLayout;
    private ConstraintLayout constraintLayout;
    private ImageView imageView;
    private View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_any_line_iban);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(TAG);
        }

        linearLayout = findViewById(R.id.bottom_sheet);
        linearLayout.setVisibility(LinearLayout.INVISIBLE);

        textView_result = findViewById(R.id.tv_ktp);
        imageView = findViewById(R.id.image_ktp);
        view = findViewById(R.id.border_anyline);
        constraintLayout = findViewById(R.id.constraintLayoutktp);
        init();


    }

    void init() {
        scanView = (ScanView) findViewById(R.id.scanview);

        AnylineOcrConfig anylineOcrConfig = new AnylineOcrConfig();
        anylineOcrConfig.setLanguages("TrainedModels/USNr.any");
        anylineOcrConfig.setCharWhitelist("1234567890");
        anylineOcrConfig.setScanMode(AnylineOcrConfig.ScanMode.LINE);
       /* anylineOcrConfig.setMinCharHeight(25);
        anylineOcrConfig.setMaxCharHeight(85);
        anylineOcrConfig.setMinConfidence(80);
       */ anylineOcrConfig.setValidationRegex("^-?\\d{16}$");

        scanView.setScanConfig("iban_view_config.json");

        OcrScanViewPlugin scanViewPlugin = new OcrScanViewPlugin(getApplicationContext(),getString(R.string.anyline_license_key), anylineOcrConfig, scanView.getScanViewPluginConfig(), "OCR" );
        scanView.setScanViewPlugin(scanViewPlugin);

        scanViewPlugin.addScanResultListener(new ScanResultListener<OcrScanResult>() {
            @Override
            public void onResult(OcrScanResult result) {
                String ibanResult = result.getResult();
                String path = setupImagePath(result.getFullImage());

                FileInputStream fi = null;
                try {
                    fi = new FileInputStream(path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(fi);
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);

                float koefX = (float)bitmap.getWidth() / (float)constraintLayout.getWidth();
                float koefY = (float)bitmap.getHeight() / (float)constraintLayout.getHeight();

                int x1 = view.getLeft();
                int y1 = view.getTop();
                int x2 = view.getWidth();
                int y2 = view.getHeight();

                int cropStartX = Math.round(x1 * koefX);
                int cropStartY = Math.round(y1 * koefY);
                int cropWidthX = Math.round(x2 * koefX);
                int cropHeightY = Math.round(y2 * koefY);

                if(cropStartX + cropWidthX <= bitmap.getWidth() && cropStartY + cropHeightY <= bitmap.getHeight()){
                    bitmap = Bitmap.createBitmap(bitmap, cropStartX, cropStartY, cropWidthX, cropHeightY);
                }
                imageView.setImageBitmap(bitmap);
                /*String path = setupImagePath(result.getCutoutImage());
                startScanResultIntent(getResources().getString(R.string.title_iban), getIbanResult(ibanResult), path);

                setupScanProcessView(ScanIbanActivity.this, result, getScanModule());*/
                linearLayout.setVisibility(LinearLayout.VISIBLE);
                view.setVisibility(View.INVISIBLE);
                textView_result.setText(result.getResult().toString().trim());

            }

        });

        scanViewPlugin.setDebugListener(this);
    }
    protected String setupImagePath(AnylineImage anylineImage){
        String imagePath="";
        Long time = System.currentTimeMillis();
        try {
            if(this.getExternalFilesDir(null)!=null){
                imagePath =
                        this.getExternalFilesDir(null)
                                .toString()+ "/results/" + "mrz_image" + time;
            }else if(this.getFilesDir() != null){
                imagePath = this.getFilesDir().toString()+ "/results/" + "mrz_image" + time;
            }
            File fullFile = new File(imagePath);
            //create the directory
            fullFile.mkdirs();
            anylineImage.save(fullFile, 100);
        }catch (IOException e){
            e.printStackTrace();
        }
        catch (Exception e){
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
        } else if(AnylineDebugListener.DEVICE_SHAKE_WARNING_VARIABLE_NAME.equals(s)){
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
            view.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(LinearLayout.INVISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }
}
