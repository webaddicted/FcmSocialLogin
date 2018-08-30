package com.example.sociallogin.Model;

public class LoginResponse {
    private String userid;
    private String userName;
    private String userEmailId;
    private String userMobileno;
    private String userImage;
    private String dob;
    private String userProvider;

    public LoginResponse(String userid, String userName, String userEmailId, String userMobileno, String userImage, String dob, String userProvider) {
        this.userid = userid;
        this.userName = userName;
        this.userEmailId = userEmailId;
        this.userMobileno = userMobileno;
        this.userImage = userImage;
        this.dob = dob;
        this.userProvider = userProvider;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmailId() {
        return userEmailId;
    }

    public void setUserEmailId(String userEmailId) {
        this.userEmailId = userEmailId;
    }

    public String getUserMobileno() {
        return userMobileno;
    }

    public void setUserMobileno(String userMobileno) {
        this.userMobileno = userMobileno;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getUserProvider() {
        return userProvider;
    }

    public void setUserProvider(String userProvider) {
        this.userProvider = userProvider;
    }
}
