package com.vrv.sdk.library.ui.activity;

import android.view.View;

import com.vrv.sdk.library.R;

/**
 * Created by zxj on 16-7-28.
 */
public class ChatWxActivity extends ChatBaseActivity {
    @Override
    protected void setToolBar() {
        super.setToolBar();
    }

    @Override
    protected void loadContentLayout() {
        contentView = View.inflate(context, R.layout.vim_activity_chat_wx, null);
        contentLayout.addView(contentView);
    }

    @Override
    protected void findViews() {
        super.findViews();
    }

    @Override
    protected void setViews() {
        super.setViews();
    }

    @Override
    protected void setListener() {
        super.setListener();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
    }

    @Override
    public void OnFuncPop(int i) {
        super.OnFuncPop(i);
    }

    @Override
    public void OnFuncClose() {
        super.OnFuncClose();
    }
}
