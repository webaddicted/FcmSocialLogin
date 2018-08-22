package com.example.sociallogin.Twitter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;


import com.example.sociallogin.Enum.LoginType;
import com.example.sociallogin.Interface.OnLoginListener;
import com.example.sociallogin.Model.LoginResponse;
import com.example.sociallogin.SocialLogin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

public class TwitterAuth {
    private static String TAG = TwitterAuth.class.getSimpleName();
    private static @NonNull
    FirebaseAuth mAuth = SocialLogin.getFirebaseAuth();
    private static TwitterAuthClient mTwitterAuthClient;
    private static OnLoginListener mLoginResponse;

    /**
     * @param activity                referance of activity
     * @param loginResponse           is describe user login status
     * @param twitter_consumer_key    provided by twitter
     * @param twitter_consumer_secret is also provide by twitter
     */
    public static void twitterLogin(@NonNull final Activity activity, @NonNull OnLoginListener loginResponse, @NonNull String twitter_consumer_key, @NonNull String twitter_consumer_secret) {
        SocialLogin.loginType(LoginType.TWITTER);
        mLoginResponse = loginResponse;
        TwitterAuthConfig authConfig = new TwitterAuthConfig(twitter_consumer_key, twitter_consumer_secret);
        TwitterConfig twitterConfig = new TwitterConfig.Builder(activity).twitterAuthConfig(authConfig).build();
        Twitter.initialize(twitterConfig);
        mTwitterAuthClient = new TwitterAuthClient();
        mTwitterAuthClient.authorize(activity, new com.twitter.sdk.android.core.Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                handleTwitterSession(activity, twitterSessionResult.data);
            }

            @Override
            public void failure(TwitterException e) {
                mLoginResponse.onFailure("Twitter authentication failed " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * receive onActivityResult response
     */
    public static void activityResult(@NonNull int requestCode, @NonNull int resultCode, @NonNull Intent data) {
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * get user info from FCM
     *
     * @param activity referance of activity
     * @param session  get all user info.
     */
    private static void handleTwitterSession(@NonNull final Activity activity, @NonNull TwitterSession session) {
        AuthCredential credential = TwitterAuthProvider.getCredential(session.getAuthToken().token, session.getAuthToken().secret);
        mAuth.signInWithCredential(credential).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    getUserInfo(mAuth.getCurrentUser());
                } else {
                    mLoginResponse.onFailure("Twitter Authentication failed " + task.getException());
                    // If sign in fails, display a message to the user.
                }
            }
        });
    }

    /**
     * @param firebaseUser is current login user
     */
    private static void getUserInfo(@NonNull FirebaseUser firebaseUser) {
        String strEmailId;
        String strPhoneNo;
        if (firebaseUser.getEmail() != null) strEmailId = firebaseUser.getEmail();
        else strEmailId = firebaseUser.getProviderData().get(0).getEmail();

        if (firebaseUser.getPhoneNumber() != null) strPhoneNo = firebaseUser.getPhoneNumber();
        else strPhoneNo = firebaseUser.getProviderData().get(0).getPhoneNumber();
        LoginResponse loginResponse = new LoginResponse(firebaseUser.getUid(),
                firebaseUser.getDisplayName(),
                strEmailId,
                strPhoneNo,
                firebaseUser.getPhotoUrl().toString(),
                firebaseUser.getProviderId()
        );
        mLoginResponse.onSuccess(loginResponse);
    }
public static boolean logOut() {
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (twitterSession != null) {
            clearTwitterCookies(mActivity);
            Twitter.getSessionManager().clearActiveSession();
            Twitter.logOut();
            return true;
        }else
            return false;
    }


    private static void clearTwitterCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
//     public static boolean logOut() {
//         CookieSyncManager.createInstance(SocialLogin.getInstance());
//         CookieManager cookieManager = CookieManager.getInstance();
//         cookieManager.removeSessionCookie();
//         TwitterCore.getInstance().getSessionManager().clearActiveSession();
//         return true;
//     }
}
