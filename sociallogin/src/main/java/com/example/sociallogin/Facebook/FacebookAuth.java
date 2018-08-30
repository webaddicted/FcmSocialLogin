package com.example.sociallogin.Facebook;

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
import com.example.sociallogin.GoogleLogin.GoogleAuth;
import com.example.sociallogin.Interface.OnLoginListener;
import com.example.sociallogin.Model.FbResponse;
import com.example.sociallogin.Model.LoginResponse;
import com.example.sociallogin.SocialLogin;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class FacebookAuth {
    private static String TAG = FacebookAuth.class.getSimpleName();
    private static CallbackManager callbackManager;
    private static @NonNull
    FirebaseAuth mAuth = SocialLogin.getFirebaseAuth();
    private static List<String> READ_PERMISSION = Arrays.asList("public_profile", "email", "user_birthday", "user_friends", "user_photos");
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
        loginManager.logInWithReadPermissions(activity, READ_PERMISSION);
        callbackManager = CallbackManager.Factory.create();
        // Register your callback//
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Print the user’s ID and the Auth Token to Android Studio’s Logcat Monitor//
                handleFacebookAccessToken(activity, loginResult.getAccessToken(), mAuth);
            }

            @Override
            public void onCancel() {
                mLoginResponse.onFailure("Facebook Authentication cancel.");
            }

            @Override
            public void onError(FacebookException exception) {
                mLoginResponse.onFailure("Facebook Authentication error " + exception.getMessage());
            }
        });
    }

    /**
     * receive onActivityResult response
     */
    public static void activityResult(@NonNull int requestCode, @NonNull int resultCode, @NonNull Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * get user info from FCM
     *
     * @param activity referance of activity
     * @param mAuth    get all user info.
     */
    private static void handleFacebookAccessToken(@NonNull final Activity activity, @NonNull AccessToken token, @NonNull final FirebaseAuth mAuth) {
        GraphRequest request = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        FbResponse fbResponse = fromJson(object.toString(), FbResponse.class);
                        Log.d(TAG, "onCompleted: str_facebookname -> " + fbResponse.getName() +
                                "\n str_facebookemail -> " + fbResponse.getEmail() +
                                "\n str_facebookid -> " + fbResponse.getId() +
                                "\n str_birthday -> " + fbResponse.getBirthday() +
                                "\n strPhoto -> " + fbResponse.getPicture().getData().getUrl());

                        LoginResponse userModel = new LoginResponse(fbResponse.getId(), fbResponse.getName(), fbResponse.getEmail(), "", fbResponse.getPicture().getData().getUrl(), fbResponse.getBirthday(), "fb");
                        mLoginResponse.onSuccess(userModel);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name,first_name,last_name,email,gender,birthday,picture");
        request.setParameters(parameters);
        request.executeAsync();
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(credential).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    getUserInfo(mAuth.getCurrentUser());
//                } else {
//                    mLoginResponse.onFailure("Authentication failed " + task.getException());
//                }
//            }
//        });
    }
    public static <T> T fromJson(String json, Class<T> clazz) {
        return new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .create().fromJson(json, clazz);
    }

    /**
     * @param firebaseUser is current login user
     */
//    private static void getUserInfo(@NonNull FirebaseUser firebaseUser) {
//        String strEmailId;
//        String strPhoneNo;
//        if (firebaseUser.getEmail() != null) strEmailId = firebaseUser.getEmail();
//        else strEmailId = firebaseUser.getProviderData().get(0).getEmail();
//
//        if (firebaseUser.getPhoneNumber() != null) strPhoneNo = firebaseUser.getPhoneNumber();
//        else strPhoneNo = firebaseUser.getProviderData().get(0).getPhoneNumber();
//        LoginResponse loginResponse = new LoginResponse(firebaseUser.getUid(),
//                firebaseUser.getDisplayName(),
//                strEmailId,
//                strPhoneNo,
//                firebaseUser.getPhotoUrl().toString(),
//                firebaseUser.getProviderId()
//        );
//        mLoginResponse.onSuccess(loginResponse);
//    }

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

    public static void logOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }
}
