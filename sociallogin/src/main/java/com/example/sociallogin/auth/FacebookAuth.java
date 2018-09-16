package com.example.sociallogin.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.example.sociallogin.Enum.LoginType;
import com.example.sociallogin.Interface.OnLoginListener;
import com.example.sociallogin.Model.FbResponse;
import com.example.sociallogin.Model.LoginResponse;
import com.example.sociallogin.SocialLogin;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class FacebookAuth {
    private static String TAG = FacebookAuth.class.getSimpleName();
    private static List<String> READ_PERMISSION = Arrays.asList("public_profile", "email", "user_birthday", "user_friends", "user_photos");
    private static CallbackManager callbackManager;
    private static @NonNull
    FirebaseAuth mAuth = SocialLogin.getFirebaseAuth();
    private static OnLoginListener mLoginResponse;
    private static LoginManager loginManager;

    /**
     * @param activity      referance of activity
     * @param loginResponse is describe user login status
     */
    public static void fbLogin(@NonNull final Activity activity, @NonNull OnLoginListener loginResponse) {
        SocialLogin.loginType(LoginType.FACEBOOK);
        loginManager = LoginManager.getInstance();
        mLoginResponse = loginResponse;
        if (getAccessToken() == null) loginAuth(activity);
        else handleFacebookAccessToken(getAccessToken());
    }

    private static void loginAuth(Activity activity) {
        loginManager.logInWithReadPermissions(activity, READ_PERMISSION);
        callbackManager = CallbackManager.Factory.create();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Print user’s ID and Auth Token
                fcmAuth(activity, loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                mLoginResponse.onFailure("Facebook Authentication failed.");
            }

            @Override
            public void onError(FacebookException exception) {
                mLoginResponse.onFailure("Facebook Authentication error " + exception.getMessage());
            }
        });
    }

    private static void fcmAuth(Activity activity, AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) {
                handleFacebookAccessToken(accessToken);
            } else {
                mLoginResponse.onFailure("Authentication failed " + task.getException());
            }
        });
    }

    /**
     * Receive onActivityResult response
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public static void onActivityResult(@NonNull int requestCode, @NonNull int resultCode, @NonNull Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Get user info from fb token
     * @param token fb token
     */
    private static void handleFacebookAccessToken(@NonNull AccessToken token) {
        GraphRequest request = GraphRequest.newMeRequest(token, (object, response) -> {
            FbResponse fbResponse = fromJson(object.toString(), FbResponse.class);
            Log.d(TAG, "onCompleted: str_facebookname -> " + fbResponse.getName() +
                    "\n str_facebookemail -> " + fbResponse.getEmail() +
                    "\n str_facebookid -> " + fbResponse.getId() +
                    "\n str_birthday -> " + fbResponse.getBirthday() +
                    "\n strPhoto -> " + fbResponse.getPicture().getData().getUrl()+
            "\n token-> "+token.getToken());
            getUserInfo(fbResponse, mAuth.getCurrentUser(), token.getToken());
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,last_name,gender,birthday,email,cover,picture.type(large),photos");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .create().fromJson(json, clazz);
    }

    /**
     * @param fbResponse
     * @param firebaseUser is current login user
     * @param token
     */
    private static void getUserInfo(FbResponse fbResponse, @NonNull FirebaseUser firebaseUser, String token) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserid(firebaseUser.getUid());
        loginResponse.setTokenId(token);
        loginResponse.setUserName(fbResponse.getName());
        loginResponse.setUserEmailId(fbResponse.getEmail());
        loginResponse.setDob(fbResponse.getBirthday());
        loginResponse.setUserImage(fbResponse.getPicture().getData().getUrl());
        loginResponse.setProvider(firebaseUser.getProviderId());
        mLoginResponse.onSuccess(loginResponse);
    }

    /**
     * get keyhash for facebook
     *
     * @param context referance of activity
     */
    public static String getHashKey(@NonNull Context context) {
        String keyhash = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                keyhash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyhash;
    }

    public static boolean logOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        return true;
    }

    public static boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public static AccessToken getAccessToken() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken;
    }
}
