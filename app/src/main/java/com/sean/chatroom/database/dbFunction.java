package com.sean.chatroom.database;

import com.sean.chatroom.bean.ChatHistoryItem;
import com.sean.chatroom.bean.FriendItem;
import com.sean.chatroom.bean.UserInfoItem;
import com.sean.chatroom.bean.Message;
import com.sean.chatroom.bean.UserData;

import java.util.List;

import io.reactivex.Observable;

public interface dbFunction {
    Observable<Boolean> insertUserData(UserData userData);

    Observable<Boolean> insertFriendData(FriendItem friendItem);

    Observable<Boolean> isFriendExistence(String userID);

    Observable<Boolean> isMyself(String userID);

    Observable<Boolean> insertChat(Message message, String room);

    Observable<Boolean> insertChatHistory(String room, String sticker, String name, String msg, String time);

    Observable<List<UserInfoItem>> queryUserData();

    Observable<List<FriendItem>> queryFriend();

    Observable<List<FriendItem>> queryFriendInvite();

    Observable<List<Message>> queryChat(String room);

    Observable<List<ChatHistoryItem>> queryChathistory();

    Observable<Boolean> updateUserData(String userID, String type, String value);

    Observable<Boolean> updatFriend(String userID, List<String> type, List<String> value);

    Observable<Boolean> updatFriendShip(String userID);

    Observable<Boolean> updateChat(String msgID);

    Observable<Boolean> updateChatHistory(String room, String msg, String time);

    Observable<Boolean> updateFriendRemark(String userID, String remark);

    Observable<Boolean> updateChatHistorySticker(String room, String sticker);

    Observable<Boolean> HistoryExist(String room);


}
