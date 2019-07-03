package com.sean.chatroom.api;

import com.sean.chatroom.bean.FriendItem;
import com.sean.chatroom.bean.LoginData;
import com.sean.chatroom.bean.RegisterData;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;


public interface ApiService {
    @POST("/Login")
    @FormUrlEncoded
    Observable<LoginData> getLogin(@Field("account") String account, @Field("password") String password);

    @POST("/Register")
    @FormUrlEncoded
    Observable<RegisterData> getRegister(@Field("name") String name, @Field("account") String account, @Field("password") String password,
                                         @Field("mail") String mail, @Field("phone") String phone);

    @POST("/getfriend")
    @FormUrlEncoded
    Observable<List<FriendItem>> getFriendData(@Field("userID") String userID);

    @POST("/searchfriend")
    @FormUrlEncoded
    Observable<FriendItem> searchFriend(@Field("type") String type, @Field("user") String user);

    @POST("/addfriend")
    @FormUrlEncoded
    Observable<ResponseBody> addfriend(@Field("myID") String myID, @Field("otherID") String otherID);


    @POST("/UpdateUserData")
    @FormUrlEncoded
    Observable<ResponseBody> UpdateUserData(@Field("userID") String userID, @Field("type") String type,
                                            @Field("value") String value);

    @POST("/updateFriendShip")
    @FormUrlEncoded
    Observable<ResponseBody> UpdateFriendShip(@Field("myUserID") String myUserID, @Field("otherUserID") String uotherUserID);

    @Multipart
    @POST("/UpdatePhoto")
    Observable<ResponseBody> updateUserPhoto(@Part("userID") String userID, @Part("photoType") String photoType,
                                             @Part("photoName") String photoName, @Part MultipartBody.Part file);

    @GET("/{type}/{name}")
    Observable<ResponseBody> fileDownload(@Path("type") String type, @Path("name") String name);

    @Multipart
    @POST("/uploadFile")
    Observable<ResponseBody> uploadFile(@Part MultipartBody.Part file);

    @Streaming
    @GET("/file/{fileName}")
    Observable<ResponseBody> FileDownload(@Path("fileName") String FileName);

    @POST("/invite")
    @FormUrlEncoded
    Observable<List<FriendItem>> getFriendInvite(@Field("userID") String userID);
}

