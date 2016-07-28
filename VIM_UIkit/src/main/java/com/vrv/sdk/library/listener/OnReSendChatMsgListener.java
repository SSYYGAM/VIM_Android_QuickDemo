package com.vrv.sdk.library.listener;

import com.vrv.imsdk.model.ChatMsg;

public interface OnReSendChatMsgListener {

    //重发消息
    void onResend(ChatMsg chatMsg);
}
