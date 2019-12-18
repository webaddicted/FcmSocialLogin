# SocialLogin
Social Login is complete package of social login integration with fcm.

SocialLoginIntegrationAndroid
-------------------------------

Android Project with helper library for login through facebook, twitter and google.


**I have used following steps:**

<img src="https://github.com/webaddicted/SocialLogin/blob/master/screenshot/login.png" width="400">   <img src="https://github.com/webaddicted/SocialLogin/blob/master/screenshot/facebook.png" width="400">

 <img src="https://github.com/webaddicted/SocialLogin/blob/master/screenshot/home.png" width="400">
 

Steps Follow : 
--------------

**Step 1 : integrate firebase in project.**

**Step 2 : Enable GOOGLE FACEBOOK, TWITTER authentication.**

**Step 3 : create project in developer facebook & app.twitter.com site.**

**Step 4 : get securit key  an fill in firebase auth deshboard and also add key in project.**

**Step 5 : initalize social login library in application class .**

        SocialLogin.init(this);

**Step 6 : add fcm & social login library dependency in gradle file..**

        dependencies {
            implementation 'com.google.firebase:firebase-auth:16.0.1'
            implementation 'com.github.webaddicted:Fcm_SocialLogin:1.0.1'
        }

**Step 7 : Add it in your root build.gradle at the end of repositories:**

        allprojects {
            repositories {
                ...
                maven { url 'https://jitpack.io' }
            }
        }

Step 7 GOOGLE STEPS -
---------------------

**On button click**

         public void onGoogle(View v) {
                GoogleAuth.googleLogin(this, this, getString(R.string.default_web_client_id));
            }

**In Activity Result**

            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if (resultCode == RESULT_OK) {
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
                } else {
                    Log.d(TAG, "onActivityResult: login Failed");
                }
            }

Implements OnLoginListener
--------------------------

**response get in**

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

**check user login status using**

        SocialLogin.isUserLogin();

**Logout login by using**

         public void onLogout(View v) {
                if (chekLoginType != null && chekLoginType.length() > 0) {
                    if (chekLoginType.equals(LoginType.GOOGLE)) {
                        GoogleAuth.signOut();
                    } else if (chekLoginType.equals(LoginType.FACEBOOK)) {
                        FacebookAuth.logOut();
                    } else if (chekLoginType.equals(LoginType.TWITTER)) {
                        TwitterAuth.logOut();
                    }
                  }
               }

  ## Same step for FACEBOOK ur TWITTER

           public void facebookLogin(View v) {
                FacebookAuth.fbLogin(MainActivity.this, this);

            }

            public void twitterLogin(View v) {
                TwitterAuth.twitterLogin(this, this, getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret));
            }

**On Activity result is same for all**

**Logout steps is also same for all**

