package com.vrv.sdk.library.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.vrv.imsdk.model.ChatMsg;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.ui.adapter.ChatPhotosPagerAdapter;

import java.util.ArrayList;

/**
 * 聊天图片查看
 */
public class ChatPhotosActivity extends BaseActivity {

    private ViewPager viewPager;
    private ChatMsg currentMsg;
    private int index;
    private ArrayList<ChatMsg> chatMsgs;
    private ChatPhotosPagerAdapter adapter;

    public static void start(Context context, ChatMsg chatMsg, ArrayList<ChatMsg> chatMsgs) {
        Intent intent = new Intent();
        intent.setClass(context, ChatPhotosActivity.class);
        intent.putExtra("chatMsg", chatMsg);
        intent.putParcelableArrayListExtra("chatMsgs", chatMsgs);
        context.startActivity(intent);
    }

    @Override
    protected void setToolBar() {
        toolbar.setTitle(R.string.vim_title_photos_preview);
        currentMsg =  getIntent().getParcelableExtra("chatMsg");
        chatMsgs = getIntent().getParcelableArrayListExtra("chatMsgs");
        toolbar.setVisibility(View.GONE);
    }

    @Override
    protected void loadContentLayout() {
        contentView = View.inflate(context, R.layout.vim_activity_photo_preview, null);
        contentLayout.addView(contentView);
    }

    @Override
    protected void findViews() {
        viewPager = (ViewPager) contentView.findViewById(R.id.pager);
        contentLayout.findViewById(R.id.ll_pager_index).setVisibility(View.GONE);
    }

    @Override
    protected void setViews() {
        initData();
        adapter = new ChatPhotosPagerAdapter(context, chatMsgs);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(index);
    }

    //初始化数据，计算出显示当前位置
    private void initData() {
        if (chatMsgs == null || chatMsgs.size() <= 0) {
            chatMsgs = new ArrayList<>();
            chatMsgs.add(currentMsg);
            index = 0;
            return;
        }
        int size = chatMsgs.size();
        for (int i = 0; i < size; i++) {
            if (chatMsgs.get(i).getMessageID() == currentMsg.getMessageID()) {
                index = i;
                return;
            }
        }
    }

    @Override
    protected void setListener() {
    }

    @Override
    public void onClick(View v) {

    }
}
