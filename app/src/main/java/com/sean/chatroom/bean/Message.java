package com.sean.chatroom.bean;

import java.io.File;
import java.util.Calendar;


public class Message {
    public static final int TYPE_MESSAGE_SENT = 525;
    public static final int TYPE_IMAGE_SENT = 526;
    public static final int TYPE_FILE_SENT = 527;
    public static final int TYPE_AUDIO_SENT = 528;
    public static final int TYPE_FACETIME_SENT = 529;
    public static final int TYPE_MESSAGE_RECEIVED = 725;
    public static final int TYPE_IMAGE_RECEIVED = 726;
    public static final int TYPE_FILE_RECEIVED = 727;
    public static final int TYPE_AUDIO_RECEIVED = 728;
    public static final int TYPE_FACETIME_RECEIVED = 729;

    private String sender;
    private String receiver;
    private String message;
    private long createtime;
    private long deliverime;
    private String msgID;
    private int read;
    private int type;
    private boolean showDate;
    private File imageFile;

    //發送訊息使用的建構子
    public Message(String sender, String receiver, String message, int type) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.msgID = makeMsgID(50);
        this.createtime = getlocltime();
        this.deliverime = 0;
        this.read = 0;
        this.type = type;
        this.showDate = true;
    }

    //發送檔案訊息使用的建構子
    public Message(String sender, String receiver, String message, File imageFile, int type) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.msgID = makeMsgID(50);
        this.createtime = getlocltime();
        this.deliverime = 0;
        this.read = 0;
        this.imageFile = imageFile;
        this.type = type;
        this.showDate = true;
    }

    //接收訊息使用的建構子
    public Message(String sender, String receiver, String message, long createtime, long deliverime, String msgID, int read, int type) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.createtime = createtime;
        this.deliverime = deliverime;
        this.msgID = msgID;
        this.read = read;
        this.type = type;
        this.showDate = true;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public long getDeliverime() {
        return deliverime;
    }

    public void setDeliverime(long deliverime) {
        this.deliverime = deliverime;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

    private long getlocltime() {
        Calendar cal = Calendar.getInstance();
        int zoneoffset = cal.get(Calendar.ZONE_OFFSET);
        long utc = cal.getTimeInMillis() - zoneoffset;
        return utc;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    private String makeMsgID(int Max) {
        String Big[] = new String[91 - 65];
        String Small[] = new String[123 - 97];
        String Number[] = new String[10 - 0];
        String MsgID = "";
        for (int i = 90; i > 64; i--) {
            Big[i - 64 - 1] = Character.toString((char) i);
        }

        for (int i = 122; i > 96; i--) {
            Small[i - 96 - 1] = Character.toString((char) i);
        }

        for (int i = 9; i >= 0; i--) {
            Number[i - 0] = String.valueOf(i);
        }

        String TypeContainer[][] = {Big, Small, Number};

        for (int i = 0; i < Max; i++) {
            int SelectType = (int) Math.floor(Math.random() * 3);
            int SelectChar;
            if (SelectType == 2) {
                SelectChar = (int) Math.floor(Math.random() * 10);
            } else {
                SelectChar = (int) Math.floor(Math.random() * 26);
            }
            MsgID += TypeContainer[SelectType][SelectChar];
        }
        return MsgID;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
