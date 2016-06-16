package com.vrv.sdk.library.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vrv.sdk.library.R;
import com.vrv.sdk.library.action.RequestHandler;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.utils.ToastUtil;

/**
 * 注册
 * Created by Yang on 2015/8/17 017.
 */
public class RegisterActivity extends BaseActivity {
    private final String TAG = RegisterActivity.class.getSimpleName();
    EditText edAccount;
    EditText edAuthCode;
    EditText edServerCode;
    Button getAuthCode;
    Button mButton2;
    RequestHandler requestHandler;
    private boolean register = true;//注册或修改密码


    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, RegisterActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void setToolBar() {
        toolbar.setTitle("注册");
    }

    @Override
    protected void loadContentLayout() {
        contentView = View.inflate(context,R.layout.vim_activity_register,null);
        contentLayout.addView(contentView);
    }

    @Override
    protected void findViews() {
        edAccount=(EditText) findViewById(R.id.editText);
        edAuthCode=(EditText) findViewById(R.id.editText2);
        edServerCode= (EditText) findViewById(R.id.serverCode);
        getAuthCode=(Button) findViewById(R.id.button);
        mButton2=(Button) findViewById(R.id.button2);
    }

    @Override
    protected void setViews() {

    }

    @Override
    protected void setListener() {
        getAuthCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAuthCode();
            }
        });
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAuthCode();
            }
        });
    }
    private void requestAuthCode() {
        String number = edAccount.getText().toString();
        if (TextUtils.isEmpty(number)) {
            ToastUtil.showShort(context, "手机号码为空");
            return;
        }
        number = "0086" + edAccount.getText().toString();
        String serverCode=edServerCode.getText().toString().trim();
        if (!TextUtils.isEmpty(number)) {
            requestHandler = new RegisterHandler(context, TYPE_GET_CODE);
            requestHandler.sendEmptyMessage(RegisterHandler.SHOW_PRO);
            boolean register = RequestHelper.register(this.register, number, serverCode,"0086", requestHandler);
            if (!register) {
                requestHandler.sendEmptyMessage(RequestHandler.REQUEST_FALSE);
            }
        }
    }

    private void verifyAuthCode() {
        String authCode = edAuthCode.getText().toString();
        if (TextUtils.isEmpty(authCode)) {
            ToastUtil.showShort(context, "验证码为空");
            return;
        }
        if (registerID==0) {
            ToastUtil.showShort(context, "获取验证码失败");
            return;
        }
        requestHandler = new RegisterHandler(context, TYPE_VERIFY_CODE);
        requestHandler.sendEmptyMessage(RequestHandler.SHOW_PRO);
        boolean verifyAuth = RequestHelper.verifyCode(this.register, registerID, authCode, requestHandler);
        if (!verifyAuth) {
            requestHandler.sendEmptyMessage(RequestHandler.REQUEST_FALSE);
        }
    }

    private final int WAIT = 101;
    private int waitTime = 60;
    private long registerID  = 0l;
    private final int TYPE_GET_CODE = 1;
    private final int TYPE_VERIFY_CODE = 2;

    @Override
    public void onClick(View v) {

    }

    class RegisterHandler extends RequestHandler {
        private int type;

        public RegisterHandler(Context context, int type) {
            super(context);
            this.type = type;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WAIT:
                    if (waitTime > 0) {
                        getAuthCode.setText(waitTime + "秒");
                        waitTime--;
                        sendEmptyMessageDelayed(WAIT, 1000);
                    } else {
                        getAuthCode.setEnabled(true);
                        getAuthCode.setClickable(true);
                        getAuthCode.setText("获取验证码");
                    }
                    break;
            }
        }


        @Override
        public void handleSuccess(Message msg) {
            if (type == TYPE_GET_CODE) {//获取验证码
            	registerID = msg.getData().getLong(KEY_DATA);
                getAuthCode.setEnabled(false);
                getAuthCode.setClickable(false);
                sendEmptyMessage(WAIT);
            } else {//验证验证码
                RegisterStepActivity.start((Activity) context, register, registerID);
            }
        }

        @Override
        public void handleFailure(int code, String message) {
            super.handleFailure(code, message);
            if (type == TYPE_GET_CODE) {
                ToastUtil.showShort(context, "获取验证码失败");
            } else if (type == TYPE_VERIFY_CODE) {
                ToastUtil.showShort(context, "验证码不正确");
            }
        }
    }

}

