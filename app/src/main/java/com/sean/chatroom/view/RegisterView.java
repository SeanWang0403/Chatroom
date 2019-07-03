package com.sean.chatroom.view;

public interface RegisterView {
    void showLoading();
    void hideLoading();
    void setNameError(String msg);
    void setAccountError(String msg);
    void setPasswordError(String msg);
    void setMaildError(String msg);
    void setPhonedError(String msg);
    void registerFail(String msg);
    void registerSuccess();
    void registerError();
}
