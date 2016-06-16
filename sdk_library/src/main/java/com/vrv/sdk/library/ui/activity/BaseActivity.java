package com.vrv.sdk.library.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.vrv.sdk.library.R;
import com.vrv.sdk.library.action.ActivityCollector;
import com.vrv.sdk.library.action.RequestHandler;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = BaseActivity.class.getSimpleName();

    protected static Activity activity;
    protected Context context;
    protected Toolbar toolbar;
    protected RequestHandler requestHandler;
    protected RelativeLayout contentLayout;
    protected View contentView;

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        ActivityCollector.addActivity(this);
        activity = this;
        context = this;
        setContentView(R.layout.vim_activtiy_base);
        initBase();
        initView();
    }

    @Override
    protected void onDestroy() {
        if (requestHandler != null) {
            requestHandler.sendEmptyMessage(RequestHandler.DIS_PRO);
        }
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initBase() {
        toolbar = (Toolbar) findViewById(R.id.title);
        contentLayout = (RelativeLayout) findViewById(R.id.contentLayout);
        toolbar.setTitleTextAppearance(context, R.style.Vim_ToolbarStyle);
        setToolBar();
        toolbar.setNavigationIcon(R.mipmap.vim_action_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        loadContentLayout();
        findViews();
        setViews();
        setListener();
        invalidateOptionsMenu();
    }


    protected abstract void setToolBar();

    protected abstract void loadContentLayout();

    protected abstract void findViews();

    protected abstract void setViews();

    protected abstract void setListener();

}
