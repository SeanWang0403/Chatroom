package com.sean.chatroom.api;

import com.sean.chatroom.FileUploadObserver;
import com.sean.chatroom.bean.FriendItem;
import com.sean.chatroom.UploadProgressRequestBody;
import com.sean.chatroom.bean.LoginData;
import com.sean.chatroom.bean.RegisterData;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;


public class ApiManager {
    private Retrofit retrofit;
    private ApiClient apiClient;
    private ApiService apiService;
    private static ApiManager instance = null;

    public static ApiManager getInstance() {
        if (instance == null) {
            synchronized (ApiManager.class) {
                if (instance == null) {
                    instance = new ApiManager();
                }
            }
        }
        return instance;
    }

    private ApiManager() {
        apiClient = new ApiClient();
        retrofit = apiClient.getRetrofit();
        apiService = retrofit.create(ApiService.class);
    }

    //向伺服器請求登入
    public void postLogin(String account, String password, Observer<LoginData> observer) {
        setSubscribe(apiService.getLogin(account, password), observer);
    }

    //向伺服器請求註冊
    public void postRegister(String name, String account, String password, String mail, String phone, Observer<RegisterData> observer) {
        setSubscribe(apiService.getRegister(name, account, password, mail, phone), observer);
    }

    //向伺服器請求獲取好友名單
    public void getFriendData(String userID, Observer<List<FriendItem>> observer) {
        setSubscribe(apiService.getFriendData(userID), observer);
    }

    //向伺服器請求搜尋特定對象
    public void searchFriend(String type, String user, Observer<FriendItem> observer) {
        setSubscribe(apiService.searchFriend(type, user), observer);
    }

    //向伺服器請求新增朋友到資料庫裡
    public void addFriend(String myID, String otherID, Observer<ResponseBody> observer) {
        setSubscribe(apiService.addfriend(myID, otherID), observer);
    }

    //向伺服器請求更新資料庫的user的資料
    public void updateUserData(String userID, String type, String value, Observer<ResponseBody> observer) {
        setSubscribe(apiService.UpdateUserData(userID, type, value), observer);
    }

    //向伺服器請求更新資料庫的user的sticker or background
    public void updateUserPhoto(String userID, String photoType, String photoName, MultipartBody.Part file, Observer<ResponseBody> observer) {
        setSubscribe(apiService.updateUserPhoto(userID, photoType, photoName, file), observer);
    }

    //向伺服器請求更新資料庫的朋友關係
    public void updateFriendShip(String myUserID, String otherUserID, Observer<ResponseBody> observer) {
        setSubscribe(apiService.UpdateFriendShip(myUserID, otherUserID), observer);
    }

    //從伺服器請下載檔案
    public void downloadFile(String type, String name, Observer<ResponseBody> observer) {
        setSubscribe(apiService.fileDownload(type, name), observer);
    }

    //上傳檔案到伺服器
    public void uploadFile(File file, FileUploadObserver<ResponseBody> observer) {
        UploadProgressRequestBody uploadFileRequestBody = new UploadProgressRequestBody(file, observer);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), uploadFileRequestBody);
        setSubscribe(apiService.uploadFile(part), observer);
    }

    //上傳圖片到伺服器
    public void uploadImage(File file, Observer<ResponseBody> observer) {
        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        setSubscribe(apiService.uploadFile(part), observer);
    }

    //從伺服器請下載大檔案
    public void FileDownload(String FileName, Observer<ResponseBody> observer) {
        setDownloadSubscribe(apiService.FileDownload(FileName), observer);
    }

    //向伺服器請求獲取好友邀請的名單
    public void getFriendInvite(String userID, Observer<List<FriendItem>> observer) {
        setSubscribe(apiService.getFriendInvite(userID), observer);
    }

    private <T> void setSubscribe(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private <T> void setDownloadSubscribe(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(observer);
    }
}
