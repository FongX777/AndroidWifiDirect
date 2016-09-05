package com.example.fongxuan.myapplication;

/**
 * Created by fongxuan on 8/29/16.
 */
public class User {
    private String userName;
    private String userStatus;
    private String userIconLocation;


    public User(){
        userName = "none";
        userIconLocation="mimap/icon1";
        userStatus="unavailible";
    }
    public User(String userName, String userIconLocation){
        this.userName = userName;
        this.userIconLocation = userIconLocation;
    }


    public String getUserName(){
        return userName;
    }
    public String getUserIconLocation(){
        return getUserIconLocation();
    }
    public String getUserStatus(){return userStatus;}

    public void setUserName(String userName){
        this.userName = userName;
    }
    public void setUserIconLocation(String userIconLocation){
        this.userIconLocation = userIconLocation;
    }
    public void setUserStatus(String userStatus){
        this.userStatus = userStatus;
    }
}
