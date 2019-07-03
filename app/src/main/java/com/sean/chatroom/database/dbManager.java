package com.sean.chatroom.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sean.chatroom.bean.ChatHistoryItem;
import com.sean.chatroom.bean.FriendItem;
import com.sean.chatroom.bean.UserInfoItem;
import com.sean.chatroom.bean.Message;
import com.sean.chatroom.bean.UserData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;


public class dbManager implements dbFunction {
    private static dbManager instance = null;

    public SQLiteDatabase db;
    private DBHelper dbHelper;

    public static dbManager getInstance(Context context) {
        if (instance == null) {
            synchronized (dbManager.class) {
                if (instance == null) {
                    instance = new dbManager(context);
                }
            }
        }
        return instance;
    }

    private dbManager(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void writeOpen() {
        db = dbHelper.getWritableDatabase();
    }

    public void readeOpen() {
        db = dbHelper.getReadableDatabase();
    }

    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    //新增user的資料到sqlite
    @Override
    public Observable<Boolean> insertUserData(final UserData userData) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                ContentValues contentValues = new ContentValues();
                contentValues.put("userID", userData.getUserID());
                contentValues.put("name", userData.getName());
                contentValues.put("sticker", userData.getSticker());
                contentValues.put("background", userData.getBackground());
                contentValues.put("id", userData.getId());
                contentValues.put("id_search", userData.getId_search());
                contentValues.put("qr_code", userData.getQrcode());
                contentValues.put("phone", userData.getPhone());
                if (db.insert("user", null, contentValues) != -1) {
                    emitter.onNext(true);
                    emitter.onComplete();
                } else {
                    emitter.onNext(false);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    //新增Friend的資料到sqlite
    @Override
    public Observable<Boolean> insertFriendData(final FriendItem friendItem) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                ContentValues contentValues = new ContentValues();
                contentValues.put("userID", friendItem.getUserID());
                contentValues.put("name", friendItem.getName());
                contentValues.put("sticker", friendItem.getSticker());
                contentValues.put("background", friendItem.getBackground());
                contentValues.put("ship", friendItem.getShip());
                contentValues.put("remark", "");
                contentValues.put("update_time", friendItem.getUpdate_time());
                if (db.insert("friend", null, contentValues) != -1) {
                    emitter.onNext(true);
                    emitter.onComplete();
                } else {
                    emitter.onNext(false);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    //新增聊天紀錄到sqlite
    @Override
    public Observable<Boolean> insertChat(final Message message, final String room) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                ContentValues contentValues = new ContentValues();
                contentValues.put("room", room);
                contentValues.put("sender", message.getSender());
                contentValues.put("receiver", message.getReceiver());
                contentValues.put("message", message.getMessage());
                contentValues.put("msgID", message.getMsgID());
                contentValues.put("createtime", message.getCreatetime());
                contentValues.put("delivertime", message.getDeliverime());
                contentValues.put("read", 0);
                contentValues.put("message_type", message.getType());
                if (db.insert("chat", null, contentValues) != -1) {
                    emitter.onNext(true);
                    emitter.onComplete();
                } else {
                    emitter.onNext(false);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io());

    }

    //從sqlite獲取user的資料
    @Override
    public Observable<List<UserInfoItem>> queryUserData() {
        return Observable.create(new ObservableOnSubscribe<List<UserInfoItem>>() {
            @Override
            public void subscribe(ObservableEmitter<List<UserInfoItem>> emitter) throws Exception {
                Cursor cursor = db.rawQuery("select * from user", null);
                List<UserInfoItem> list = new ArrayList<>();
                while (cursor.moveToNext()) {
                    list.add(new UserInfoItem("姓名", cursor.getString(cursor.getColumnIndex("name"))));
                    list.add(new UserInfoItem("電話號碼", cursor.getString(cursor.getColumnIndex("phone"))));
                    list.add(new UserInfoItem("ID", cursor.getString(cursor.getColumnIndex("id"))));
                    list.add(new UserInfoItem("加入好友", "-------", (cursor.getInt(cursor.getColumnIndex("id_search")) == 1) ? true : false));
                    list.add(new UserInfoItem("行動條碼", cursor.getString(cursor.getColumnIndex("qr_code"))));
                }
                emitter.onNext(list);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    //從sqlite獲取Friend的資料
    @Override
    public Observable<List<FriendItem>> queryFriend() {
        return Observable.create(new ObservableOnSubscribe<List<FriendItem>>() {
            @Override
            public void subscribe(ObservableEmitter<List<FriendItem>> emitter) throws Exception {
                Cursor cursor = db.rawQuery("select * from friend", null);
                List<FriendItem> list = new ArrayList<>();
                while (cursor.moveToNext()) {
                    list.add(new FriendItem(
                            cursor.getString(cursor.getColumnIndex("userID")),
                            cursor.getString(cursor.getColumnIndex("name")),
                            cursor.getString(cursor.getColumnIndex("sticker")),
                            cursor.getString(cursor.getColumnIndex("background")),
                            cursor.getInt(cursor.getColumnIndex("ship")),
                            cursor.getString(cursor.getColumnIndex("remark")),
                            Long.parseLong(cursor.getString(cursor.getColumnIndex("update_time")))
                    ));
                }
                emitter.onNext(list);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    //從sqlite獲取朋友邀請的資料
    @Override
    public Observable<List<FriendItem>> queryFriendInvite() {
        return Observable.create(new ObservableOnSubscribe<List<FriendItem>>() {
            @Override
            public void subscribe(ObservableEmitter<List<FriendItem>> emitter) throws Exception {
                Cursor cursor = db.rawQuery("select * from friend", null);
                List<FriendItem> list = new ArrayList<>();
                while (cursor.moveToNext()) {
                    if (cursor.getInt(cursor.getColumnIndex("ship")) == 0) {
                        list.add(new FriendItem(
                                cursor.getString(cursor.getColumnIndex("userID")),
                                cursor.getString(cursor.getColumnIndex("name")),
                                cursor.getString(cursor.getColumnIndex("sticker"))
                        ));
                    }

                }
                emitter.onNext(list);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    //從sqlite獲取聊天紀錄
    @Override
    public Observable<List<Message>> queryChat(final String room) {
        return Observable.create(new ObservableOnSubscribe<List<Message>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Message>> emitter) throws Exception {
                Cursor cursor = db.rawQuery("select * from chat where room = ?", new String[]{room});
                List<Message> list = new ArrayList<>();
                while (cursor.moveToNext()) {
                    list.add(new Message(
                            cursor.getString(cursor.getColumnIndex("sender")),
                            cursor.getString(cursor.getColumnIndex("receiver")),
                            cursor.getString(cursor.getColumnIndex("message")),
                            Long.parseLong(cursor.getString(cursor.getColumnIndex("createtime"))),
                            Long.parseLong(cursor.getString(cursor.getColumnIndex("delivertime"))),
                            cursor.getString(cursor.getColumnIndex("msgID")),
                            cursor.getInt(cursor.getColumnIndex("read")),
                            cursor.getInt(cursor.getColumnIndex("message_type"))
                    ));
                }
                emitter.onNext(list);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    //從sqlite獲取最後的聊天紀錄
    @Override
    public Observable<List<ChatHistoryItem>> queryChathistory() {
        return Observable.create(new ObservableOnSubscribe<List<ChatHistoryItem>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ChatHistoryItem>> emitter) throws Exception {
                Cursor cursor = db.rawQuery("select * from chathistory", null);
                List<ChatHistoryItem> list = new ArrayList<>();
                while (cursor.moveToNext()) {
                    list.add(new ChatHistoryItem(
                            cursor.getString(cursor.getColumnIndex("room")),
                            cursor.getString(cursor.getColumnIndex("sticker")),
                            cursor.getString(cursor.getColumnIndex("name")),
                            cursor.getString(cursor.getColumnIndex("message")),
                            cursor.getString(cursor.getColumnIndex("time"))
                    ));
                }
                emitter.onNext(list);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    //更新sqlite裡user的資料
    @Override
    public Observable<Boolean> updateUserData(final String userID, final String type, final String value) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                ContentValues contentValues = new ContentValues();
                switch (type) {
                    case "name":
                        contentValues.put("name", value);
                        break;
                    case "phone":
                        contentValues.put("phone", value);
                        break;
                    case "id":
                        contentValues.put("id", value);
                        break;
                    case "id_search":
                        contentValues.put("id_search", Integer.parseInt(value));
                        break;
                    case "sticker":
                        contentValues.put("sticker", value);
                        break;
                    case "background":
                        contentValues.put("background", value);
                        break;
                }
                String[] args = {userID};
                if (db.update("user", contentValues, "userID=?", args) != -1) {
                    emitter.onNext(true);
                    emitter.onComplete();
                } else {
                    emitter.onNext(false);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    //更新sqlite裡的聊天紀錄
    @Override
    public Observable<Boolean> updateChat(final String msgID) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                ContentValues contentValues = new ContentValues();
                contentValues.put("read", 1);
                String[] args = {msgID};
                if (db.update("chat", contentValues, "msgID=?", args) != -1) {
                    emitter.onNext(true);
                    emitter.onComplete();
                } else {
                    emitter.onNext(false);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    //更新sqlite裡朋友的關係
    @Override
    public Observable<Boolean> updatFriendShip(final String userID) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                ContentValues contentValues = new ContentValues();
                contentValues.put("ship", 1);
                String[] args = {userID};
                if (db.update("friend", contentValues, "userID=?", args) != -1) {
                    emitter.onNext(true);
                    emitter.onComplete();
                } else {
                    emitter.onNext(false);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    //更新sqlite裡Friend的資料
    @Override
    public Observable<Boolean> updatFriend(final String userID, final List<String> type, final List<String> value) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                ContentValues contentValues = new ContentValues();
                for (int i = 0; i < type.size(); i++) {
                    contentValues.put(type.get(i), value.get(i));
                }
                String[] args = {userID};
                if (db.update("friend", contentValues, "userID=?", args) != -1) {
                    emitter.onNext(true);
                    emitter.onComplete();
                } else {
                    emitter.onNext(false);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    //檢查Fiend是否已存在sqlite裡
    @Override
    public Observable<Boolean> isFriendExistence(final String userID) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                Cursor cursor = db.rawQuery("select * from friend where userID=?", new String[]{userID});
                if (cursor.moveToNext()) {
                    emitter.onNext(true);
                } else {
                    emitter.onNext(false);
                }
                emitter.onComplete();
            }
        });
    }

    //檢查網路搜尋到的人物是不是自己
    @Override
    public Observable<Boolean> isMyself(final String userID) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                Cursor cursor = db.rawQuery("select * from user where userID = ?", new String[]{userID});
                if (cursor.moveToNext()) {
                    emitter.onNext(true);
                } else {
                    emitter.onNext(false);
                }
                emitter.onComplete();
            }
        });
    }

    //新增最後的聊天紀錄到sqlite
    @Override
    public Observable<Boolean> insertChatHistory(final String room, final String sticker, final String name,
                                                 final String msg, final String time) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                ContentValues contentValues = new ContentValues();
                contentValues.put("room", room);
                contentValues.put("sticker", sticker);
                contentValues.put("name", name);
                contentValues.put("message", msg);
                contentValues.put("time", String.valueOf(time));
                if (db.insert("chathistory", null, contentValues) != -1) {
                    emitter.onNext(true);
                    emitter.onComplete();
                } else {
                    emitter.onNext(false);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    //更新sqlite裡最後的聊天紀錄
    @Override
    public Observable<Boolean> updateChatHistory(final String room, final String msg, final String time) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                ContentValues contentValues = new ContentValues();
                contentValues.put("message", msg);
                contentValues.put("time", String.valueOf(time));
                String[] args = {room};
                if (db.update("chathistory", contentValues, "room=?", args) != -1) {
                    emitter.onNext(true);
                    emitter.onComplete();
                } else {
                    emitter.onNext(false);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    //檢查最後的聊天紀錄是否已存在sqlite裡
    @Override
    public Observable<Boolean> HistoryExist(final String room) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                Cursor cursor = db.rawQuery("select * from chathistory where room = ?", new String[]{room});
                if (cursor.moveToNext()) {
                    emitter.onNext(true);
                } else {
                    emitter.onNext(false);
                }
                emitter.onComplete();
            }
        });
    }

    //更新sqlite裡最後聊天紀錄的Sticker
    @Override
    public Observable<Boolean> updateChatHistorySticker(final String room, final String sticker) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                ContentValues contentValues = new ContentValues();
                contentValues.put("sticker", sticker);
                String[] args = {room};
                if (db.update("chathistory", contentValues, "room=?", args) != -1) {
                    emitter.onNext(true);
                    emitter.onComplete();
                } else {
                    emitter.onNext(false);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    //更新sqlite裡Friend的備註
    @Override
    public Observable<Boolean> updateFriendRemark(final String userID, final String remark) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                ContentValues contentValues = new ContentValues();
                contentValues.put("remark", remark);
                String[] args = {userID};
                if (db.update("friend", contentValues, "userID=?", args) != -1) {
                    emitter.onNext(true);
                    emitter.onComplete();
                } else {
                    emitter.onNext(false);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}