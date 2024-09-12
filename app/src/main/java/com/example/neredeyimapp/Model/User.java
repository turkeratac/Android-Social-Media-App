package com.example.neredeyimapp.Model;

public class User{
    public String userID;
    public String userName;
    public String userPassword;
    public String imageRefNo;
    public User(String userID, String userName, String userPassword, String imageRefNo) {
        this.userID = userID;
        this.userName = userName;
        this.userPassword = userPassword;
        this.imageRefNo = imageRefNo;
    }
    public User() {
    }
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }


    public String getImageRefNo() {
        return imageRefNo;
    }

    public void setImageRefNo(String imageRefNo) {
        this.imageRefNo = imageRefNo;
    }

}