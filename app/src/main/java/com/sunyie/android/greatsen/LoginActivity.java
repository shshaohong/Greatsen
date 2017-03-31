package com.sunyie.android.greatsen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.sunyie.android.greatsen.customview.PasswordDialog;
import com.sunyie.android.greatsen.entity.LoginEntity;
import com.sunyie.android.greatsen.network.GreatsenApi;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    @InjectView(R.id.phonenumber)
    EditText etPhonenumber;
    @InjectView(R.id.password)
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.commit)
    void setCommit() {
        if (etPhonenumber.getText().toString().trim().length() == 11) {
            if (etPhonenumber.getText().toString().trim().length() != 0 &&
                    etPassword.getText().toString().trim().length() != 0) {
                if (etPassword.getText().toString().trim().length() == 6) {
                    final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
                    dialog.setMessage("正在登录...");
                    dialog.show();
                    String phoneNumber = etPhonenumber.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    String md5 = phoneNumber.concat(String.valueOf(password));
                    Log.e("md5", md5);
                    String number = Md5.getMd5(md5);
                    GreatsenApi.Api api = GreatsenApi.createApi();
                    api.login(phoneNumber,number).enqueue(new Callback<LoginEntity>() {
                        @Override
                        public void onResponse(Call<LoginEntity> call, Response<LoginEntity> response) {
                            dialog.cancel();
                            LoginEntity body = response.body();
                            Log.e("login", body.toString());
                            if (body.getStatus().equals("0")) {
                                SharedPreferences sharedPreferences = getSharedPreferences("Greatsen", Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = sharedPreferences.edit();
                                edit.putString("account", etPhonenumber.getText().toString().trim());
                                edit.putString("password", etPassword.getText().toString().trim());
                                edit.apply();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }else {
                                dialog.cancel();
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                            }

                        }
                        @Override
                        public void onFailure(Call<LoginEntity> call, Throwable t) {
                            dialog.cancel();
                            Log.e("t", t.getMessage());
                            Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "密码长度必须为6位", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "请输入账号或密码", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "请输入正确的账号", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.resetpassowrd)
    void setPassword() {
        //修改密码
        PasswordDialog dialog = new PasswordDialog(this);
        dialog.show();
    }
}
