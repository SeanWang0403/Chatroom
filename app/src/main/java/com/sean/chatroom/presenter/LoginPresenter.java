package com.sean.chatroom.presenter;

import android.content.Context;

import com.sean.chatroom.model.LoginModel;
import com.sean.chatroom.view.LoginView;

public class LoginPresenter implements LoginModel.LoginListener {
    private LoginView loginView;
    private LoginModel loginModel;

    public LoginPresenter(LoginView loginView, Context context) {
        this.loginView = loginView;
        this.loginModel = new LoginModel(context);
    }

    public void userLogin(String account, String password) {
        checkLoginData(account, password);
    }

    private void checkLoginData(String account, String password) {
        if (account.equals("")) {
            loginView.setAccountError("請輸入帳號");
        } else if (password.equals("")) {
            loginView.setPasswordError("請輸入密碼");
        } else {
            loginView.showLoading();
            postLoginData(account, password);
        }
    }

    private void postLoginData(String account, String password) {
        loginModel.getLoginData(account, password, this);
    }

    @Override
    public void onFail(String msg) {
        loginView.loginFail(msg);
    }

    @Override
    public void onError() {
        loginView.hideLoading();
        loginView.loginError();
    }

    @Override
    public void onSuccess() {
        loginView.loginSuccess();
    }

    @Override
    public void onFinish() {
        loginView.hideLoading();
    }

    public boolean UserDataExist() {
        return loginModel.UserDataExist();
    }
}
