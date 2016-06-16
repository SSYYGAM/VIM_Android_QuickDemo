package com.vrv.sdk.library.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.model.Contact;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.ui.activity.ChatActivity;


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
        if (ChatMsgApi.isGroup(ChatActivity.getChatID())) {
            showName = true;
            tvFromName.setVisibility(showName ? View.VISIBLE : View.GONE);
            long senderID = messageBean.getSendID();
            String name = null;
            Contact member = ChatActivity.getMemberBean(senderID);
            if (member != null) {
                name = member.getName();
            }
            tvFromName.setText(TextUtils.isEmpty(name) ? senderID + "" : name);
        }
    }

    @Override
    protected void displayNormalMsg() {
        super.displayNormalMsg();
//        flMsg.setPadding(Utils.dip2px(context, 12f), Utils.dip2px(context, 4f), Utils.dip2px(context, 4f), Utils.dip2px(context, 4f));
    }
}
