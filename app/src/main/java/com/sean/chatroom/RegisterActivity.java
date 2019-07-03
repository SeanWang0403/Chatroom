package com.sean.chatroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sean.chatroom.presenter.RegisterPresenter;
import com.sean.chatroom.view.RegisterView;


public class RegisterActivity extends AppCompatActivity implements RegisterView {
    private EditText edt_name, edt_account, edt_password, edt_mail, edt_phone;
    private ImageView im_passView;
    private boolean passvisibility;
    private RegisterPresenter registerPresenter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        registerPresenter = new RegisterPresenter(this);
        im_passView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passvisibility = !passvisibility;
                restorePasswordIconVisibility(passvisibility);
            }
        });
    }

    private void init() {
        edt_name = (EditText) findViewById(R.id.register_name);
        edt_account = (EditText) findViewById(R.id.register_account);
        edt_password = (EditText) findViewById(R.id.register_password);
        edt_mail = (EditText) findViewById(R.id.register_mail);
        edt_phone = (EditText) findViewById(R.id.register_phone);
        im_passView = (ImageView) findViewById(R.id.register_pass_visibility);
    }

    public void register(View view) {
        String name = edt_name.getText().toString().trim();
        String account = edt_account.getText().toString().trim();
        String password = edt_password.getText().toString().trim();
        String mail = edt_mail.getText().toString().trim();
        String phone = edt_phone.getText().toString().trim();

        registerPresenter.userRegister(name, account, password, mail, phone);
    }

    private void restorePasswordIconVisibility(boolean isShowPwd) {
        if (isShowPwd) {
            im_passView.setImageResource(R.mipmap.password_visible);
            edt_password.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            im_passView.setImageResource(R.mipmap.password_notvisible);
            edt_password.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        }
        edt_password.setSelection(edt_password.getText().length());
    }

    @Override
    public void showLoading() {
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("註冊中...");
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        progressDialog.dismiss();
    }

    @Override
    public void setNameError(String msg) {
        edt_name.setHint(msg);
        edt_name.setHintTextColor(getResources().getColor(R.color.errMsg));
    }

    @Override
    public void setAccountError(String msg) {
        edt_account.setText("");
        edt_account.setHint(msg);
        edt_account.setHintTextColor(getResources().getColor(R.color.errMsg));
    }

    @Override
    public void setPasswordError(String msg) {
        edt_password.setText("");
        edt_password.setHint(msg);
        edt_password.setHintTextColor(getResources().getColor(R.color.errMsg));
    }

    @Override
    public void setMaildError(String msg) {
        edt_mail.setText("");
        edt_mail.setHint(msg);
        edt_mail.setHintTextColor(getResources().getColor(R.color.errMsg));
    }

    @Override
    public void setPhonedError(String msg) {
        edt_phone.setText("");
        edt_phone.setHint(msg);
        edt_phone.setHintTextColor(getResources().getColor(R.color.errMsg));
    }

    @Override
    public void registerFail(String msg) {
        showDialog(msg);
    }

    @Override
    public void registerError() {
        showDialog("伺服器忙碌中，請稍後再試!!");
    }

    @Override
    public void registerSuccess() {
        Toast.makeText(this, "註冊成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    private void showDialog(String msg){
        AlertDialog builder=new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton("確定",null)
                .show();
    }

}
