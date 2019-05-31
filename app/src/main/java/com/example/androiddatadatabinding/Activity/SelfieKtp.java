package com.example.androiddatadatabinding.Activity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.androiddatadatabinding.GraphicUtils.GraphicOverlay;
import com.example.androiddatadatabinding.R;
import com.example.androiddatadatabinding.Util.FaceGraphic;
import com.example.androiddatadatabinding.Util.FrameMetadata;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SelfieKtp extends AppCompatActivity {
    private static final String TAG = "FaceDetectionProcessor";
    private CameraView mCameraView;
    private GraphicOverlay graphicOverlay;
    private FrameMetadata frameMetadata;
    private Bitmap overlayBitmap;

    private ImageView imageView;
    private View view5;
    private Dialog dialog;
    private ConstraintLayout constraintLayoutSelfi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie_ktp);
        imageView = findViewById(R.id.imagecapture);
        dialog = new Dialog(SelfieKtp.this);
        view5 = findViewById(R.id.view5);
        constraintLayoutSelfi = findViewById(R.id.constraintLayoutSelfi);
        graphicOverlay = findViewById(R.id.graphic_overlay);
        mCameraView = findViewById(R.id.SelfiSurfaceView);
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
                Bitmap  bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, mCameraView.getWidth(), mCameraView.getHeight(), false);
                setButtonCapture(bitmap);
//                runFaceRecognition(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
        mCameraView.start();

        /*FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .setLandmarkType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .build();
        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
        overlayBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_call_black_24dp);
*/
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                graphicOverlay.clear();
                mCameraView.captureImage();
            }
        });
    }

    private void setButtonCapture(Bitmap bitmap) {
        if(bitmap != null){
            dialog.setContentView(R.layout.dialog_selfiektp);

                float KoefX = (float) bitmap.getWidth()/(float)constraintLayoutSelfi.getWidth();
                float KoefY = (float)bitmap.getHeight() / (float)constraintLayoutSelfi.getHeight();
                int x1 = view5.getLeft();
                int y1 = view5.getTop();
                int x2 = view5.getWidth();
                int y2 = view5.getHeight();
                int cropStartX = Math.round(x1*KoefX);
                int cropStartY = Math.round(y1 * KoefY);
                int cropWidthX = Math.round(x2 * KoefX);
                int cropHeightY = Math.round(y2 * KoefY);
                if(cropStartX + cropWidthX <= bitmap.getWidth() && cropStartX+cropHeightY<= bitmap.getHeight()){
                    bitmap = Bitmap.createBitmap(bitmap, cropStartX, cropStartY,cropWidthX,cropHeightY);
                }
            CreateImageFile(bitmap);
            ImageView imageView = dialog.findViewById(R.id.imageView3);
            imageView.setImageBitmap(bitmap);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }

    private void CreateImageFile(final Bitmap bitmap) {
       File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
       String timeStamp = new SimpleDateFormat("MMdd_HHmmssSSS").format(new Date());
       String imageFileName = "Capture_"+timeStamp+".jpg";
       final File file = new File(path,imageFileName);

       if(path.mkdir()){
           Toast.makeText(getBaseContext() , "Not Exist :"+path.getName(),Toast.LENGTH_LONG).show();
       }
        try {
            OutputStream OutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, OutputStream);
            OutputStream.flush();
            OutputStream.close();
            MediaScannerConnection.scanFile(getBaseContext(),
                    new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
            Toast.makeText(getBaseContext() , file.getName(),Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runFaceRecognition(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector();
        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> faces) {
                        processFaceRecognition(faces);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.getStackTrace();
                    }
                });
    }

    private void processFaceRecognition(List<FirebaseVisionFace> faces) {
        graphicOverlay.clear();
        for (int i = 0; i < faces.size(); ++i) {
            FirebaseVisionFace face = faces.get(i);

            int cameraFacing =
                    frameMetadata != null ? frameMetadata.getCameraFacing() :
                            Camera.CameraInfo.CAMERA_FACING_BACK;
            FaceGraphic faceGraphic = new FaceGraphic(graphicOverlay, face, cameraFacing, overlayBitmap);
            graphicOverlay.add(faceGraphic);
        }
        graphicOverlay.postInvalidate();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mCameraView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
            if (mCameraView.isFacingFront()) {
                mCameraView.stop();
                mCameraView.setFacing(CameraKit.Constants.FACING_BACK);
                mCameraView.start();
            } else if (mCameraView.isFacingBack()) {
                mCameraView.stop();
                mCameraView.setFacing(CameraKit.Constants.FACING_FRONT);
                mCameraView.start();
            }
        }
        if (id == R.id.action_capture) {

        }

        return super.onOptionsItemSelected(item);
    }
}
