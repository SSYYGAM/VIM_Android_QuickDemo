package com.vrv.sdk.library.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vrv.imsdk.api.ConfigApi;
import com.vrv.imsdk.bean.LastLoginInfo;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.action.RequestHandler;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.utils.ToastUtil;


/**
 * 注册下一步
 * Created by Yang on 2015/8/17 017.
 */
public class RegisterStepActivity extends BaseActivity {

    private EditText mName;
    private EditText mPsd1;
    private EditText mPsd2;
    private Button mSubmit;
    RequestHandler requestHandler;
    private boolean register;
    private long registerID;

    public static void start(Activity activity, boolean register, long registerID) {
        Intent intent = new Intent();
        intent.setClass(activity, RegisterStepActivity.class);
        intent.putExtra("register", register);
        intent.putExtra("registerID", registerID);
        activity.startActivity(intent);
    }

    @Override
    protected void setToolBar() {
        toolbar.setTitle("注册");
        Intent intent = getIntent();
        register = intent.getBooleanExtra("register", true);
        registerID = intent.getLongExtra("registerID", 0);
    }

    @Override
    protected void loadContentLayout() {
        contentView = View.inflate(context,R.layout.vim_activity_register_step,null);
        contentLayout.addView(contentView);
    }

    @Override
    protected void findViews() {
        mName=(EditText) contentView.findViewById(R.id.name);
        mPsd1=(EditText) contentView.findViewById(R.id.psd1);
        mPsd2=(EditText) contentView.findViewById(R.id.psd2);
        mSubmit=(Button) contentView.findViewById(R.id.submit);
    }

    @Override
    protected void setViews() {

    }

    @Override
    protected void setListener() {
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerDoneRequest();
            }
        });
    }

	private void registerDoneRequest() {
        String name = mName.getText().toString();
        if (register) {
            if (TextUtils.isEmpty(name)) {
                ToastUtil.showShort(context, "请输入您的名字");
                return;
            }
        }
        String password = mPsd1.getText().toString();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showShort(context, "密码不能为空");
            return;
        }
        if (!password.equals(mPsd2.getText().toString())) {
            ToastUtil.showShort(context, "密码不一致");
            return;
        }
        requestHandler = new RegisterStepHandler(context);
        requestHandler.sendEmptyMessage(RequestHandler.SHOW_PRO);
        boolean registerStep = RequestHelper.registerStep(register, registerID, name, password, requestHandler);
        if (!registerStep) {
            requestHandler.sendEmptyMessage(RequestHandler.REQUEST_FALSE);
        }
    }

    @Override
    public void onClick(View v) {

    }

    class RegisterStepHandler extends RequestHandler {

        public RegisterStepHandler(Context context) {
            super(context);
        }

        @Override
        public void handleSuccess(Message msg) {
            ConfigApi.setLoginConfig(new LastLoginInfo());
            LoginActivity.start(context);
            finish();
        }

        @Override
        public void handleFailure(int code, String message) {
            super.handleFailure(code, message);
                ToastUtil.showShort(context, "注册失败");
        }
    }
}
