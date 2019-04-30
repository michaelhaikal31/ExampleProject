package com.example.androiddatadatabinding.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androiddatadatabinding.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageFragment extends Fragment {
    private Bitmap bitmap;

    @BindView(R.id.res_photo)
    ImageView resPhoto;

    @BindView(R.id.res_photo_size)
    TextView resPhotoSize;

    public void imageSetupFragment(Bitmap bitmap) {
        if (bitmap != null) {
            this.bitmap = bitmap;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_image, container, false);
        ButterKnife.bind(this, view);
        //check if bitmap exist, set to ImageView
        if (bitmap != null) {
            resPhoto.setImageBitmap(bitmap);
            String info = "image with:" + bitmap.getWidth() + "\n" +
                    "image height:" + bitmap.getHeight();
            resPhotoSize.setText(info);
        }
        return view;
    }
}
