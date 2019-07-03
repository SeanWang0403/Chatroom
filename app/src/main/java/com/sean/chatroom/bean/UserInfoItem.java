package com.sean.chatroom.bean;

public class UserInfoItem {
    private String Title;
    private String data;
    private boolean check=false;

    public UserInfoItem(String title, String data) {
        this.Title = title;
        this.data = data;
    }
    public UserInfoItem(String title, String data,boolean check) {
        this.Title = title;
        this.data = data;
        this.check=check;
    }
    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
