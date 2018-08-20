package com.example.sociallogin;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.sociallogin.Enum.LoginType;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SocialLogin {
    private static FirebaseAuth mAuth;
    private static LoginType mLoginType;
    private static Context mContext;
    private static String preferenceKey;

    /**
     * initialize Firebase
     */
    public static FirebaseAuth init(@NonNull Context context) {
        mContext = context;
        if (mAuth == null) {
            FirebaseApp.initializeApp(context);
            mAuth = FirebaseAuth.getInstance();
        }
        return mAuth;
    }

    public static FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    /**
     * check user signIn status
     */
    public static boolean isUserLogin() {
        if (mAuth != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) return false;
            else return true;
        }
        return false;
    }

    public static void loginType(LoginType loginType) {
        mLoginType = loginType;
    }

    /**
     * Check LoginType
     */
    public static LoginType getloginType() {
        return mLoginType;
    }

    public static Context getInstance() {
        return mContext;
    }

    public static void setPreferenceKey(String key) {
        preferenceKey = key;
    }

    public static String getPreferenceKey() {
        return preferenceKey;
    }

}
