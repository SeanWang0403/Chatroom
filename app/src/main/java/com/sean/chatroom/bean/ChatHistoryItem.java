package com.sean.chatroom.bean;

public class ChatHistoryItem {
    private String Room;
    private String Sticker;
    private String Name;
    private String Message;
    private String Time;

    public ChatHistoryItem(String room, String sticker, String name, String message, String time) {
        Room = room;
        Sticker = sticker;
        Name = name;
        Message = message;
        Time = time;
    }

    public String getRoom() {
        return Room;
    }

    public void setRoom(String room) {
        Room = room;
    }

    public String getSticker() {
        return Sticker;
    }

    public void setSticker(String sticker) {
        Sticker = sticker;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
