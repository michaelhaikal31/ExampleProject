package com.example.androiddatadatabinding.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences sharedPref;
    public SharedPref(Context context){
        sharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }
    //this method will save the nightMode states : True or False
    public void setNightModeState(Boolean state){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("NightMode", state);
        editor.commit();
    }
    //this method will load the Night Mode State
    public Boolean loadNightModeState(){
        Boolean state = sharedPref.getBoolean("NightMode", false);
        return state;
    }
}
