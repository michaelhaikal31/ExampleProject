package com.example.androiddatadatabinding.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.androiddatadatabinding.R;
import com.example.androiddatadatabinding.Util.Constant;
import com.example.androiddatadatabinding.Util.SharedPref;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import info.androidhive.fontawesome.FontDrawable;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, AdapterView.OnItemClickListener {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUESTS = 1;
    Dialog myDialog;

    private static final Class<?>[] CLASSES = new Class<?>[]{
            FastAdapter.class, Permission.class,
            AndroidCharacterRecognition.class, MLkit.class, SavePhotoRecord.class, AnyLineIBAN.class,
    };
    private static final int[] DESCRIPTION_IDS = new int[]{
            R.string.desc_Fast_Adapter, R.string.desc_Permission,
            R.string.desc_Android_Character_Recognition, R.string.desc_ML_kit, R.string.desc_ML_kit, R.string.desc_ML_kit,
    };

    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            getWindow().setBackgroundDrawableResource(R.drawable.ic_cornered_stairs_night);
        }else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            getWindow().setBackgroundDrawableResource(R.drawable.ic_cornered_stairs_light);
        }

        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setInitUI();
        setButtonFloating();
        checkRootDevice();

    }

    private static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
                "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }
    private static boolean checkRootMethod3(){
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] {"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if(in.readLine() !=  null)
                return true;
            return false;
        } catch (Throwable t) {
            return false;
        }finally {
            if(process != null)process.destroy();
        }

    }
    private void checkRootDevice() {
        if (!isDeviceRooted() == true) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(Constant.MESSAGE);
            builder.setMessage(Constant.MESSAGE_ROOT);
            builder.setPositiveButton(Constant.MESSSGE_OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void setInitUI() {
        ListView listView = (ListView) findViewById(R.id.ActivityListView);
        MyArrayAdapter adapter = new MyArrayAdapter(this, android.R.layout.simple_list_item_2, CLASSES);
        adapter.setDescriptionIds(DESCRIPTION_IDS);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private void setButtonFloating() {
        myDialog = new Dialog(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        FontDrawable drawable = new FontDrawable(this, R.string.fa_paper_plane_solid, true, false);
        drawable.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        fab.setImageDrawable(drawable);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.setContentView(R.layout.profilepopup);
                TextView txtclose = (TextView) myDialog.findViewById(R.id.txtclose);
                txtclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Class<?> clicked = CLASSES[position];
        startActivity(new Intent(this, clicked));
    }

    public static class MyArrayAdapter extends ArrayAdapter<Class<?>> {

        private final Context context;
        private final Class<?>[] classes;
        private int[] descriptionIds;

        public MyArrayAdapter(Context context, int resource, Class<?>[] objects) {
            super(context, resource, objects);
            classes = objects;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(android.R.layout.simple_list_item_2, null);
            }
            ((TextView) view.findViewById(android.R.id.text1)).setText(classes[position].getSimpleName());
            ((TextView) view.findViewById(android.R.id.text2)).setText(descriptionIds[position]);

            return view;
        }

        public void setDescriptionIds(int[] descriptionIds) {
            this.descriptionIds = descriptionIds;
        }
    }
}
