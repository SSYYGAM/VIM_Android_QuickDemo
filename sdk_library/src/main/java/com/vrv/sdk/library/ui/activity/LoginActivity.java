package com.vrv.sdk.library.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vrv.sdk.library.R;
import com.vrv.sdk.library.VimConstant;
import com.vrv.sdk.library.action.RequestHandler;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.utils.ToastUtil;

public class LoginActivity extends BaseActivity {

    private EditText edPhone;
    private EditText edPassword;
    private EditText edServerCode;
    private Button mSignIn;
    private Button mRegister;
    private String userName;
    private String password, serverCode;
    protected RequestHandler requestHandler;

    /**
     * 启动登录页面
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void setToolBar() {
        toolbar.setTitle(R.string.vim_title_login);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void loadContentLayout() {
        //不显示返回按钮需要在setToolBar方法完成之后添加
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        contentView = View.inflate(context, R.layout.vim_activity_login, null);
        contentLayout.addView(contentView);
    }

    @Override
    protected void findViews() {
        edPhone = (EditText) contentView.findViewById(R.id.phone);
        edPassword = (EditText) contentView.findViewById(R.id.password);
        edServerCode = (EditText) findViewById(R.id.serverCode);
        mSignIn = (Button) contentView.findViewById(R.id.sign_in);
        mRegister = (Button) contentView.findViewById(R.id.register);
    }

    @Override
    protected void setViews() {
        edPhone.setText(VimConstant.ACCOUNT);
        edPassword.setText(VimConstant.PASSWORD);
        edServerCode.setText(VimConstant.SERVER);
    }

    @Override
    protected void setListener() {
        mSignIn.setOnClickListener(this);
        mRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in) {
            login();
        } else if (v.getId() == R.id.register) {
            RegisterActivity.start(this);
        }
    }

    private void login() {
        userName = edPhone.getText().toString();
        password = edPassword.getText().toString();
        serverCode = edServerCode.getText().toString().trim();

        if (userName.length() >= 4 && !TextUtils.isEmpty(password)) {
            requestHandler = new LoginHandler(LoginHandler.TYPE_LOGIN, context);
            requestHandler.sendEmptyMessage(RequestHandler.SHOW_PRO);
            boolean login = RequestHelper.login(userName, password, serverCode, VimConstant.NATIONAL_CODE, requestHandler);
            if (!login) {
                requestHandler.sendEmptyMessage(RequestHandler.REQUEST_FALSE);
            }
        } else {
            Toast.makeText(context, "请检查输入", Toast.LENGTH_SHORT).show();
        }
    }

    class LoginHandler extends RequestHandler {

        public static final int TYPE_LOGIN = 1;
        public static final int TYPE_VERIFY = 2;
        private int type;

        public LoginHandler(int type, Context context) {
            super(context);
            this.type = type;
        }

        @Override
        public void handleSuccess(Message msg) {
            if (type == 1) {
                //进入主页
                MainActivity.start((Activity) context);
            }
        }

        @Override
        public void handleFailure(int code, String message) {
            super.handleFailure(code, message);
            ToastUtil.showShort(context, "登录失败");
        }
    }
}
