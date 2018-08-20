package com.example.deepaksharma.authgoogle2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sociallogin.Facebook.FacebookAuth;
import com.example.sociallogin.GoogleLogin.GoogleAuth;
import com.example.sociallogin.Interface.OnLoginListener;
import com.example.sociallogin.Model.LoginResponse;
import com.example.sociallogin.Preference.PreferenceClass;
import com.example.sociallogin.SocialLogin;
import com.example.sociallogin.Twitter.TwitterAuth;

public class MainActivity extends AppCompatActivity implements OnLoginListener {
    String TAG = MainActivity.class.getSimpleName();
    String chekLoginType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: " + FacebookAuth.getHashKey(this));
        chekLoginType = PreferenceClass.getValue("loginType");
        if (SocialLogin.isUserLogin() && chekLoginType != null && chekLoginType.length() > 0) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    /**
     * this is refrence of activity
     * this refrence of OnLoginListener interface
     * client_id  default client id provide by fcm
     */
    public void onGoogle(View v) {
        GoogleAuth.googleLogin(this, this, getString(R.string.default_web_client_id));
    }

    public void facebookLogin(View v) {
        FacebookAuth.fbLogin(MainActivity.this, this);
    }

    public void twitterLogin(View v) {
        TwitterAuth.twitterLogin(this, this, getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret));
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
            switch (SocialLogin.getloginType()) {
                case GOOGLE:
                    GoogleAuth.activityResult(this, requestCode, resultCode, data);
                    break;
                case FACEBOOK:
                    FacebookAuth.activityResult(requestCode, resultCode, data);
                    break;
                case TWITTER:
                    TwitterAuth.activityResult(requestCode, resultCode, data);
                    break;
            }
//        } else {
//            Log.d(TAG, "onActivityResult: login Failed");
//        }
    }

    @Override
    public void onSuccess(LoginResponse loginResponse) {
        userInfo(loginResponse);
    }

    @Override
    public void onSuccess(String success) {
        Toast.makeText(this, "" + success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(String failure) {
        Log.d(TAG, "onResponse onFailure: " + failure);
        Toast.makeText(this, "" + failure, Toast.LENGTH_SHORT).show();
    }

    public void userInfo(LoginResponse userLogin) {
        Toast.makeText(this, "success login", Toast.LENGTH_SHORT).show();
        PreferenceClass.setValue("loginType", SocialLogin.getloginType().toString());
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

}