package com.vrv.sdk.library.common.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.vrv.sdk.library.R;
import com.vrv.sdk.library.action.ActivityCollector;
import com.vrv.sdk.library.action.RequestHandler;
import com.vrv.sdk.library.ui.activity.BaseActivity;

public abstract class VimBaseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = BaseActivity.class.getSimpleName();

    protected Activity activity;
    protected Context context;
    protected Toolbar toolbar;
    protected RequestHandler requestHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        activity = this;
        context = this;
    }

    @Override
    protected void onDestroy() {
        if (requestHandler != null) {
            requestHandler.sendEmptyMessage(RequestHandler.DIS_PRO);
        }
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * 初始化界面
     */
    protected void initView() {
        findViews();
        setViews();
        setListener();
    }

    protected void setToolBar(String title){
        if (toolbar == null){
            return;
        }
        toolbar.setTitleTextAppearance(context, R.style.Vim_ToolbarStyle);
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.mipmap.vim_action_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onClick(View v) {

    }

    protected <T extends View> T findView(int resId) {
        return (T) (findViewById(resId));
    }

    protected abstract void findViews();

    protected abstract void setViews();

    protected abstract void setListener();
}
