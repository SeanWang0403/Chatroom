package com.sean.chatroom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sean.chatroom.presenter.LoginPresenter;
import com.sean.chatroom.view.LoginView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity implements LoginView {
    private EditText edt_account, edt_password;
    private ImageView im_passView;
    private boolean passvisibility;
    private LoginPresenter presenter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new LoginPresenter(this, this);
        if (presenter.UserDataExist()) {
            setContentView(R.layout.login_passe);
            loginSuccess();
        } else {
            setContentView(R.layout.activity_login);
            init();
            getStoragePermission();
        }
    }

    private void init() {
        edt_account = (EditText) findViewById(R.id.User);
        edt_password = (EditText) findViewById(R.id.Pass);
        im_passView = (ImageView) findViewById(R.id.login_pass_visibility);
        im_passView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passvisibility = !passvisibility;
                restorePasswordIconVisibility(passvisibility);
            }
        });

    }

    public void login(View view) {
        String user = edt_account.getText().toString().trim();
        String pass = edt_password.getText().toString().trim();
        if (checkNetWork()) {
            presenter.userLogin(user, pass);
        } else {
            Toast.makeText(this, "沒有網路!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void createAccount(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("登入中...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

    }

    @Override
    public void hideLoading() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 3000);

    }

    @Override
    public void setAccountError(String msg) {
        edt_account.setHint(msg);
        edt_account.setHintTextColor(getResources().getColor(R.color.errMsg));
    }

    @Override
    public void setPasswordError(String msg) {
        edt_password.setHint(msg);
        edt_password.setHintTextColor(getResources().getColor(R.color.errMsg));
    }

    @Override
    public void loginError() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showDialog("伺服器忙碌中，請稍後再試!!");
            }
        }, 3000);

    }

    @Override
    public void loginFail(final String msg) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showDialog(msg);
            }
        }, 3000);
    }

    @Override
    public void loginSuccess() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("type","friend");
                startActivity(intent);
            }
        }, 3000);

    }

    private void showDialog(String msg) {
        AlertDialog builder = new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton("確定", null)
                .show();
    }

    private void getStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, 123);
        }
    }

    private boolean checkNetWork() {
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isConnected();
        } else {
            return false;
        }
    }
}
