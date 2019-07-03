package com.sean.chatroom.view;

public interface LoginView {
    void showLoading();
    void hideLoading();
    void setAccountError(String msg);
    void setPasswordError(String msg);
    void loginError();
    void loginFail(String msg);
    void loginSuccess();


}
