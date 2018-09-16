package com.example.deepaksharma.authgoogle2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sociallogin.Enum.LoginType;
import com.example.sociallogin.auth.FacebookAuth;
import com.example.sociallogin.auth.GoogleAuth;
import com.example.sociallogin.Preference.PreferenceClass;
import com.example.sociallogin.SocialLogin;
import com.example.sociallogin.Twitter.TwitterAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    TextView txt_loginType, txt_LoginDetails;
    Button btn_Logout;
    String chekLoginType;
    ImageView img_UserImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        txt_loginType = findViewById(R.id.txt_loginType);
        txt_LoginDetails = findViewById(R.id.txt_LoginDetails);
        img_UserImg = findViewById(R.id.img_UserImg);
        chekLoginType = PreferenceClass.getValue("loginType");

//        PreferenceClass.setValue("loginType", SocialLogin.getloginType().toString());
        isUserLogin();
    }

    private void isUserLogin() {
        if (SocialLogin.isUserLogin() && chekLoginType != null && chekLoginType.length() > 0) {
            FirebaseUser userDetail = SocialLogin.getFirebaseAuth().getCurrentUser();

            txt_LoginDetails.setText("Uid -> " + userDetail.getUid() + "\n\n Name -> " + userDetail.getDisplayName() +
                    "\n\n GetEmail -> " + userDetail.getEmail() + "\n\n GetPhoneNumber -> " + userDetail.getPhoneNumber() +
                    "\n\n GetPhotoUrl -> " + userDetail.getPhotoUrl() + "\n\n GetProviderId  -> " + userDetail.getProviderId() +
                    "\n provider details -> " + userDetail.getProviderData().get(0).getDisplayName() +
                    "\n email -> " + userDetail.getProviderData().get(0).getEmail() +
                    "\n phone no-> " + userDetail.getProviderData().get(0).getPhoneNumber() +
                    "\n\n\n\n Name -> " + PreferenceClass.getValue("name") +
                    "\n Email id - >" + PreferenceClass.getValue("email") +
                    "\n mobile -> " + PreferenceClass.getValue("mobile") +
                    "\n provider -> " + PreferenceClass.getValue("provider") +
                    "\n id -> " + PreferenceClass.getValue("id") +
                    "\n token -> " + PreferenceClass.getValue("token") +
                    "\n image -> " + PreferenceClass.getValue("image") +
                    "\n dob -> " + PreferenceClass.getValue("dob"));

            Glide.with(this).load(PreferenceClass.getValue("image")).into(img_UserImg);
        }
    }

    public void onSetPreference(View v) {
        PreferenceClass.setValue("name", "Deepak Sharma");
        Toast.makeText(this, "name is Deepak Sharma", Toast.LENGTH_SHORT).show();
    }

    public void onGetPreference(View v) {
        Toast.makeText(this, "" + PreferenceClass.getValue("name"), Toast.LENGTH_SHORT).show();
    }

    public void onLogout(View v) {
        if (chekLoginType != null && chekLoginType.length() > 0) {
            if (chekLoginType.equals(LoginType.GOOGLE)) {
                GoogleAuth.logOut(this);
            } else if (chekLoginType.equals(LoginType.FACEBOOK)) {
                FacebookAuth.logOut();
            } else if (chekLoginType.equals(LoginType.TWITTER)) {
                TwitterAuth.logOut(this);
            }
            PreferenceClass.removeValue("loginType");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
