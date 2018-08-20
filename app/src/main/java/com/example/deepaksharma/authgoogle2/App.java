package com.example.deepaksharma.authgoogle2;

import android.app.Application;
import com.example.sociallogin.SocialLogin;

//import app.com.tasociallogin.network.TASocialLogin;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        init login  library
        SocialLogin.init(this);
        SocialLogin.setPreferenceKey("Login");
    }
}
