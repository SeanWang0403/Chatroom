package com.sean.chatroom.bean;

public class FriendItem {
    private String userID;
    private String name;
    private String sticker;
    private String background;
    private int ship;
    private String remark;
    private long update_time;

    //好友列表使用的建構子
    public FriendItem(String userID, String name, String sticker, String background, int ship, String remark,long update_time) {
        this.userID = userID;
        this.name = name;
        this.sticker = sticker;
        this.background = background;
        this.ship = ship;
        this.remark = remark;
        this.update_time=update_time;
    }
    //好友邀請列表使用的建構子
    public FriendItem(String userID, String name, String sticker) {
        this.userID = userID;
        this.name = name;
        this.sticker = sticker;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getShip() {
        return ship;
    }

    public void setShip(int ship) {
        this.ship = ship;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }
}
