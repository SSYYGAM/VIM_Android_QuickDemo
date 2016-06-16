package com.vrv.sdk.library.ui.view;

import android.content.Context;

import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.model.ChatMsg;

public class ChatMsgItemFactory {

    public static ChatMsgItemView createItemView(Context context, ChatMsg msgBean) {
        if (msgBean == null) {
            return null;
        }
        ChatMsgItemView itemView = null;
        int msgType = ChatMsgApi.reCalculateMsgType(msgBean.getMessageType());
        switch (msgType) {
            case ChatMsgApi.TYPE_TEXT:
                return new ChatTxtView(context, msgBean);
            case ChatMsgApi.TYPE_IMAGE:
                return new ChatImgView(context, msgBean);
            case ChatMsgApi.TYPE_FILE:
            case ChatMsgApi.TYPE_AUDIO:
            case ChatMsgApi.TYPE_CARD:
            case ChatMsgApi.TYPE_POSITION:
            case ChatMsgApi.TYPE_HTML:
            case ChatMsgApi.TYPE_WEAK_HINT:
            case ChatMsgApi.TYPE_MULTI:
                break;
        }
        if (itemView == null) {
            itemView = new ChatTxtView(context, msgBean);
        }
        return itemView;
    }
}
