package com.sean.chatroom.presenter;

import android.os.Handler;
import android.util.Patterns;

import com.sean.chatroom.model.RegisterModel;
import com.sean.chatroom.view.RegisterView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterPresenter implements RegisterModel.RegisterListener {
    private RegisterView registerView;
    private RegisterModel registerModel;

    public RegisterPresenter(RegisterView registerView) {
        this.registerView = registerView;
        this.registerModel = new RegisterModel();
    }

    public void userRegister(String name, String account, String password, String mail, String phone) {
        checkRegisterData(name, account, password, mail, phone);
    }

    private void checkRegisterData(final String name, final String account, final String password, final String mail, final String phone) {
        boolean nameCheck = NameCheck(name);
        boolean accountCheck = AccountCheck(account);
        boolean passwordCheck = PasswordCheck(password);
        boolean mailCheck = EmailChekc(mail);
        boolean phoneCheck = PhoneCheck(phone);

        if (nameCheck&&accountCheck && passwordCheck && mailCheck && phoneCheck) {
            registerView.showLoading();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    postRegisterData(name, account, password, mail, phone);
                }
            },1500);
        } else {
            registerView.hideLoading();
        }
    }

    private boolean NameCheck(String name) {
        boolean check;
        if (name.equals("")) {
            registerView.setNameError("姓名不可以為空");
            check = false;
        } else {
            check = true;
        }
        return check;
    }

    private boolean AccountCheck(String account) {
        String limitEx = ".*[`~!@#$%^&*()+=|{}':;',.<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\\\]+.*";
        Pattern pattern;
        Matcher m;
        boolean check;
        if (account.equals("")) {
            registerView.setAccountError("帳號不可為空");
            check = false;
        } else {
            String AccountType = "^.{8,12}$";
            pattern = Pattern.compile(AccountType);
            m = pattern.matcher(account);
            if (!m.matches()) {
                registerView.setAccountError("帳號格式不符");
                check = false;
            } else {
                pattern = Pattern.compile(limitEx);
                m = pattern.matcher(account);
                if (m.matches()) {
                    registerView.setAccountError("不允許有特殊符號");
                    check = false;
                } else {
                    check = true;
                }
            }
        }
        return check;
    }

    private boolean PasswordCheck(String password) {
        String limitEx = ".*[`~!@#$%^&*()+=|{}':;',.<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\\\]+.*";
        Pattern pattern;
        Matcher m;
        boolean check;
        if (password.equals("")) {
            registerView.setPasswordError("密碼不可以為空");
            check = false;
        } else {
            String PasswordType = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,12}$";
            pattern = Pattern.compile(PasswordType);
            m = pattern.matcher(password);
            if (!m.matches()) {
                registerView.setPasswordError("密碼格式不符");
                check = false;
            } else {
                pattern = Pattern.compile(limitEx);
                m = pattern.matcher(password);
                if (m.matches()) {
                    registerView.setPasswordError("不允許有特殊符號");
                    check = false;
                } else {
                    check = true;
                }
            }
        }
        return check;
    }

    private boolean EmailChekc(String email) {
        boolean check;
        if (email.equals("")) {
            registerView.setMaildError("信箱不可以為空");
            check = false;
        } else if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            registerView.setMaildError("信箱格式不符");
            check = false;
        } else {
            check = true;
        }
        return check;
    }

    private boolean PhoneCheck(String phone) {
        Pattern pattern;
        Matcher m;
        boolean check;
        if (phone.equals("")) {
            registerView.setPhonedError("電話簿可以為空");
            check = false;
        } else {
            String phoneRegex = "^09\\d{8}$";
            pattern = Pattern.compile(phoneRegex);
            m = pattern.matcher(phone);
            if (!m.matches()) {
                registerView.setPhonedError("格式不符");
                check = false;
            } else {
                check = true;
            }
        }
        return check;
    }

    private void postRegisterData(String name, String account, String password, String mail, String phone) {
        registerModel.getRegister(name, account, password, mail, phone, this);
    }

    @Override
    public void onAccountFail(String msg) {
        registerView.setAccountError(msg);
    }

    @Override
    public void onPasswordFail(String msg) {
        registerView.setPasswordError(msg);
    }

    @Override
    public void onMailFail(String msg) {
        registerView.setMaildError(msg);
    }

    @Override
    public void onPhoneFail(String msg) {
        registerView.setPhonedError(msg);
    }

    @Override
    public void onFail(String msg) {
        registerView.registerFail(msg);
    }

    @Override
    public void onError() {
        registerView.hideLoading();
        registerView.registerError();
    }

    @Override
    public void onSuccess() {
        registerView.registerSuccess();
    }


    @Override
    public void onFinish() {
        registerView.hideLoading();
    }
}

