package com.example.sociallogin.GoogleLogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.sociallogin.Enum.LoginType;
import com.example.sociallogin.Interface.OnLoginListener;
import com.example.sociallogin.Model.LoginResponse;
import com.example.sociallogin.SocialLogin;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.app.Activity.RESULT_OK;

/**
 * Created by deepaksharma on 4/6/18.
 */

public class GoogleAuth {
    public static final int RC_SIGN_IN = 234;
    private static GoogleSignInClient mGoogleSignInClient;
    private static @NonNull
    FirebaseAuth mAuth = SocialLogin.getFirebaseAuth();
    private static String TAG = GoogleAuth.class.getSimpleName();
    private static OnLoginListener mLoginResponse;

    /**
     * @param activity      referance of activity
     * @param loginResponse is describe user login status
     * @param clientId      is default_web_client_id
     */
    public static void googleLogin(@NonNull Activity activity, @NonNull OnLoginListener loginResponse, @NonNull String clientId) {
        mLoginResponse = loginResponse;
        SocialLogin.loginType(LoginType.GOOGLE);
        if (mAuth != null) {
            if (mAuth.getCurrentUser() == null) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(clientId)
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                activity.startActivityForResult(signInIntent, RC_SIGN_IN);
            } else {
                getUserInfo(mAuth.getCurrentUser());
            }
        }
    }

    /**
     * @param activity    referance of activity
     * @param requestCode is google sigin login request.
     * @param resultCode  is RESULT_OK
     * @param data        have google signin information
     */
    public static void activityResult(@NonNull Activity activity, @NonNull int requestCode, @NonNull int resultCode, @NonNull Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(activity, account);
            } catch (ApiException e) {
                mLoginResponse.onFailure("Authentication failed : " + e);
            }
        } else {
            mLoginResponse.onFailure("Authentication failed.");
        }
    }

    /**
     * get user info from FCM
     *
     * @param activity referance of activity
     * @param account  is contain all user info.
     */
    private static void firebaseAuthWithGoogle(@NonNull final Activity activity, @NonNull GoogleSignInAccount account) {
        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    getUserInfo(mAuth.getCurrentUser());
                } else {
                    mLoginResponse.onFailure("SignIn Authentication failed.");
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

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut();
    }
}
