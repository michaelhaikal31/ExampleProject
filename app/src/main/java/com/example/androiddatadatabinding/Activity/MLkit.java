package com.example.androiddatadatabinding.Activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androiddatadatabinding.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;


public class MLkit extends AppCompatActivity {
    private static final String TAG = MLkit.class.getSimpleName().toString();
    ProgressDialog progressDialog;
    CameraView mCameraView;
    LinearLayout linearLayout;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mlkit);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(TAG);
        }
        view = findViewById(R.id.border_CameraView);
        linearLayout = findViewById(R.id.perview_cameraview);
        FirebaseApp.initializeApp(this);

        mCameraView = findViewById(R.id.camView);
        mCameraView.setFocusable(true);
        mCameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                Bitmap bitmap = cameraKitImage.getBitmap();

                bitmap = Bitmap.createScaledBitmap(bitmap,
                        mCameraView.getWidth(), mCameraView.getHeight(), false);
                mCameraView.stop();

                float koefX = (float)bitmap.getWidth() / (float)linearLayout.getWidth();
                float koefY = (float)bitmap.getHeight() / (float)linearLayout.getHeight();

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

                /*Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setData()*/

                runTextRecognition(bitmap);

            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
        TextView textView =findViewById(R.id.confirmation_button);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(MLkit.this);
                progressDialog.setMessage("Processing..");
                progressDialog.setCancelable(true);
                progressDialog.show();
                mCameraView.start();
                mCameraView.setCropOutput(true);
                mCameraView.captureImage();
            }
        });
    }

    private void runTextRecognition(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
        Task<FirebaseVisionText> result = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        processTextRecognitionResult(firebaseVisionText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void processTextRecognitionResult(FirebaseVisionText text) {
        List<FirebaseVisionText.Block> blocks = text.getBlocks();
        StringBuilder recognisedText = new StringBuilder("");
        for (int i = 0; i < blocks.size(); i++) {
            recognisedText.append(blocks.get(i).getText() + "\n");
        }
        //Snackbar.make(getCon,recognisedText, Snackbar.LENGTH_SHORT).show();
        //d.setText(recognisedText);
        Toast.makeText(getBaseContext(),recognisedText,Toast.LENGTH_LONG).show();
        /*if(blocks.size() == 0){
            Log.d("TAG", "No text found");
            return;
        }
        mGraphicOverlay.clear();
        for(int i=0; i< blocks.size(); i++){
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j=0; j < lines.size(); j++){
                List<FirebaseVisionText.Element>elements = lines.get(j).getElements();
                for (int k=0; k<elements.size();k++){
                    GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
                    mGraphicOverlay.add(textGraphic);
                    System.out.println(textGraphic.toString());
                }
            }
        }
        */
        progressDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();

//        surfaceView.getLocationInWindow(location);
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            mCameraView.stop();
            mCameraView.start();
        }

        return super.onOptionsItemSelected(item);
    }
}
