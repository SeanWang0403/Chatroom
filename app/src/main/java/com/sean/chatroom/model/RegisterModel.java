package com.sean.chatroom.model;

import com.sean.chatroom.api.ApiManager;
import com.sean.chatroom.bean.RegisterData;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RegisterModel {
    //向伺服器發起註冊請求
    public void getRegister(String name, String account, String password, String mail, String phone, final RegisterListener listener) {
        ApiManager.getInstance().postRegister(name, account, password, mail, phone, new Observer<RegisterData>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(RegisterData registerData) {
                switch (registerData.getErrType()) {
                    case "帳號":
                        listener.onAccountFail(registerData.getMsg());
                        break;
                    case "密碼":
                        listener.onPasswordFail(registerData.getMsg());
                        break;
                    case "信箱":
                        listener.onMailFail(registerData.getMsg());
                        break;
                    case "手機號碼":
                        listener.onPhoneFail(registerData.getMsg());
                        break;
                    case "伺服器":
                        listener.onFail(registerData.getMsg());
                        break;
                    case "成功":
                        listener.onSuccess();
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {
                listener.onError();
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                listener.onFinish();
            }
        });

    }

    public interface RegisterListener {
        void onAccountFail(String msg);

        void onPasswordFail(String msg);

        void onMailFail(String msg);

        void onPhoneFail(String msg);

        void onFail(String msg);

        void onSuccess();

        void onError();

        void onFinish();
    }

}
