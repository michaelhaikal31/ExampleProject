package com.example.androiddatadatabinding.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androiddatadatabinding.R;
import com.example.androiddatadatabinding.Util.SharedPref;

import info.androidhive.fontawesome.FontDrawable;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUESTS = 1;
    Dialog myDialog;

    private static final Class<?>[] CLASSES = new Class<?>[]{
            FastAdapter.class,
            Permission.class,
            AndroidCharacterRecognition.class,
            MLkit.class,
            SavePhotoRecord.class,
            AnyLineIBAN.class,
            SelfieKtp.class,
            PrintBloetooth.class,
            KotlinExperience.class
    };
    private static final int[] DESCRIPTION_IDS = new int[]{
            R.string.descFastAdapter,
            R.string.descPermission,
            R.string.descACR,
            R.string.descMLkit,
            R.string.descCropPhoto,
            R.string.descOcrAnyLine,
            R.string.descSelfyCamera,
            R.string.descPrintBluetootch,
            R.string.descKotlinExperience

    };

    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setInitUI();
        setButtonFloating();

    }

    private void setInitUI() {
        ListView listView = (ListView) findViewById(R.id.ActivityListView);
        MyArrayAdapter adapter = new MyArrayAdapter(this, android.R.layout.simple_list_item_2, CLASSES);
        adapter.setDescriptionIds(DESCRIPTION_IDS);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i;
                switch (position) {
                    case 0:
                        i = new Intent(MainActivity.this, FastAdapter.class);
                        startActivity(i);
                        break;
                    case 1:
                        i = new Intent(MainActivity.this, Permission.class);
                        startActivity(i);
                        break;
                    case 2:
                        i = new Intent(MainActivity.this, AndroidCharacterRecognition.class);
                        startActivity(i);
                        break;
                    case 3:
                        i = new Intent(MainActivity.this, MLkit.class);
                        startActivity(i);
                        break;
                    case 4:
                        i = new Intent(MainActivity.this, SavePhotoRecord.class);
                        startActivity(i);
                        break;
                    case 5:
                        checkPermissions();
                        break;
                    case 6:
                        i = new Intent(MainActivity.this, SelfieKtp.class);
                        startActivity(i);
                        break;
                    case 7 :i = new Intent(MainActivity.this, PrintBloetooth.class);
                        startActivity(i);
                        break;
                    case 8 :i = new Intent(MainActivity.this, KotlinExperience.class);
                        startActivity(i);
                        break;
                    default:
                        break;
                }

            }
        });
    }

    void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    100);

        } else {
            Intent i = new Intent(MainActivity.this, AnyLineIBAN.class);
            startActivity(i);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(MainActivity.this, AnyLineIBAN.class);
                startActivity(i);
            } else {
                Toast.makeText(this, "bnla", Toast.LENGTH_LONG).show();
            }

        }

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

    /* @Override
     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         Class<?> clicked = CLASSES[position];
         startActivity(new Intent(this, clicked));
     }
 */
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
