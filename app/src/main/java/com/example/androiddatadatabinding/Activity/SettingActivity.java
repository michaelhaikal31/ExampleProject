package com.example.androiddatadatabinding.Activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.androiddatadatabinding.R;
import com.example.androiddatadatabinding.Util.SharedPref;

public class SettingActivity extends AppCompatActivity {
    private SharedPref sharedPref;
    private TextView tvVersionApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setPrefConfig();
        setVersionAppConfig();
    }

    private void setVersionAppConfig() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvVersionApp = findViewById(R.id.valueversion);
        tvVersionApp.setText("Version "+packageInfo.versionName);
    }

    private void setPrefConfig() {
        sharedPref = new SharedPref(this);
        if(sharedPref.loadNightModeState() == true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        Switch swith = findViewById(R.id.swith);
        if(sharedPref.loadNightModeState() == true) swith.setChecked(true);
        swith.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sharedPref.setNightModeState(true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    //getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    recreate();
                }else {
                    sharedPref.setNightModeState(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                    //getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    recreate();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
