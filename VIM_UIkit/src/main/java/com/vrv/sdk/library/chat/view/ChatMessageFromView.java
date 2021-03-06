package com.vrv.sdk.library.chat.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.sdk.library.R;


/**
 * Created by Yang on 2015/8/20 020.
 */
public class ChatMessageFromView extends ChatMessageView {

    public ChatMessageFromView(Context context) {
        super(context);
    }

    public ChatMessageFromView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatMessageFromView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View loadContentView() {
        View view = View.inflate(context, R.layout.vim_chat_item_message_from, this);
        return view;
    }

    @Override
    protected void setBackground() {
        flMsg.setBackgroundResource(R.drawable.vim_chat_item_from_bg);
    }

    @Override
    protected void setName() {
        if (ChatMsgApi.isGroup(messageBean.getTargetID())) {
            showName = true;
            tvFromName.setVisibility(showName ? View.VISIBLE : View.GONE);
            long senderID = messageBean.getSendID();
            String name = messageBean.getName();
            tvFromName.setText(TextUtils.isEmpty(name) ? senderID + "" : name);
        }
    }

    @Override
    protected void displayNormalMsg() {
        super.displayNormalMsg();
        //        flMsg.setPadding(Utils.dip2px(context, 12f), Utils.dip2px(context, 4f), Utils.dip2px(context, 4f), Utils.dip2px(context, 4f));
    }
}
