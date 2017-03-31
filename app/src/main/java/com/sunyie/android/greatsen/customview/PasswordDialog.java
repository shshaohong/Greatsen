package com.sunyie.android.greatsen.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jungly.gridpasswordview.GridPasswordView;
import com.sunyie.android.greatsen.R;
import com.sunyie.android.greatsen.entity.LoginEntity;
import com.sunyie.android.greatsen.network.GreatsenApi;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shaohong on 2017-3-1.
 */

public class PasswordDialog extends Dialog {

    @InjectView(R.id.et_oldpassword)
    GridPasswordView mEtOldpassword;
    @InjectView(R.id.et_newpassword)
    GridPasswordView mEtNewpassword;
    @InjectView(R.id.et_againpassword)
    GridPasswordView mEtAgainpassword;
    @InjectView(R.id.passwordLayout)
    LinearLayout mPasswordLayout;
    @InjectView(R.id.phoneNumber)
    EditText mPhoneNumber;
    private Context mContext;

    public PasswordDialog(Context context) {
        super(context, R.style.myDialogStyle);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_dialog);
        ButterKnife.inject(this);
        init();
    }

    private void init() {
        mPhoneNumber.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @OnClick(R.id.iv_delete)
    public void setClose(){
        dismiss();
    }

    @OnClick(R.id.passwordcommit)
    public void commit(){
        String phoneNumber = mPhoneNumber.getText().toString().trim();
        String oldPassword = mEtOldpassword.getPassWord().trim();
        String newPassword = mEtNewpassword.getPassWord().trim();
        String againPassword = mEtAgainpassword.getPassWord().trim();
        if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(oldPassword) ||
                TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(againPassword)) {
            Toast.makeText(mContext, "请输入手机号码或密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phoneNumber.length() != 11) {
            Toast.makeText(mContext, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (oldPassword.length() != 6 || newPassword.length() != 6 || againPassword.length() != 6) {
            Toast.makeText(mContext, "请输入6位数的密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.equals(againPassword)) {
            Log.e("aa", newPassword + "+++++" + againPassword);
            Toast.makeText(mContext, "重置密码和确认密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        GreatsenApi.Api api = GreatsenApi.createApi();
        api.resetPassowrd(phoneNumber,oldPassword,newPassword).enqueue(new Callback<LoginEntity>() {
            @Override
            public void onResponse(Call<LoginEntity> call, Response<LoginEntity> response) {
                if (response.body().getStatus().equals("0")) {
                    Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                    dismiss();
                }else {
                    Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginEntity> call, Throwable t) {
                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
