package com.example.sociallogin.Interface;

import com.example.sociallogin.Model.LoginResponse;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by deepaksharma on 4/6/18.
 */

public interface OnLoginListener {
    void onSuccess(LoginResponse loginResponse);
    void onSuccess(String success);
    void onFailure(String failure);
}
