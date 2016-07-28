package com.vrv.sdk.library.chat;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.vrv.imsdk.model.ChatMsg;
import com.vrv.imsdk.model.GroupMember;
import com.vrv.sdk.library.bean.BaseInfoBean;
import com.vrv.sdk.library.common.view.BaseRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessageListView extends BaseRecyclerView {

    private List<ChatMsg> chatMsgs;

    public MessageListView(Context context) {
        super(context);
        init();
    }

    public MessageListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MessageListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        chatMsgs = new ArrayList<>();
        adapter = new MessageAdapter(context, chatMsgs);
        setAdapter(adapter);
        setLayoutManager(new LinearLayoutManager(context));
    }

    /**
     * 单人聊天设置个人信息，显示头像用
     * @param bean
     */
    public void setContact(BaseInfoBean bean) {
        if (adapter != null && bean != null) {
            ((MessageAdapter) adapter).setBaseInfo(bean);
        }
    }

    /**
     * 群聊设置群成员，显示头像名称
     * @param members
     */
    public void setMembers(List<GroupMember> members){
        if (adapter != null && members != null){
            ((MessageAdapter)adapter).setMembers(members);
        }
    }

    public List<ChatMsg> getData() {
        return chatMsgs;
    }
}
