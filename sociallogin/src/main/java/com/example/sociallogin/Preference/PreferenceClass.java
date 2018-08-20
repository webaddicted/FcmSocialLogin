package com.example.sociallogin.Preference;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.sociallogin.SocialLogin;

/**
 * Created by Anish Sharma on 31-12-2017.
 */

public class PreferenceClass {
    public static final String PREFS_NAME = SocialLogin.getPreferenceKey();
    public static final Context context = SocialLogin.getInstance();

    public static void setValue(String key, String value) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getValue(String key) {
        SharedPreferences settings;
        String text;
        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        text = settings.getString(key, null);
        return text;
    }

    public static void clearSharedPreference() {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    public static void removeValue(String key) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.remove(key);
        editor.commit();
    }

}