package com.vrv.sdk.library.chat.view;

import android.content.Context;
import android.support.annotation.Nullable;

import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.model.ChatMsg;
import com.vrv.sdk.library.action.RequestHelper;


public class ChatMsgItemFactory {

    public static ChatMsgItemView createItemView(Context context, ChatMsg msgBean) {
        if (msgBean == null) {
            return null;
        }
        ChatMsgItemView itemView = null;
        itemView = getChatViewByActivity(context, msgBean);
        if (itemView == null) {
            itemView = getChatViewByMsgType(context, msgBean);
        }
        if (itemView == null) {
            itemView = new ChatTxtView(context, msgBean);
        }
        return itemView;
    }

    /**
     * @param context
     * @param msgBean
     * @return
     */
    private static ChatMsgItemView getChatViewByActivity(Context context, ChatMsg msgBean) {
        int activeType = msgBean.getActiveType();
        switch (activeType) {
            case 1://阅后即焚
                if (RequestHelper.isMyself(msgBean.getSendID())){//自己发的要倒计时删除
                    return null;
                }else {
                    return new ChatBurnView(context, msgBean);
                }
        }
        return null;
    }

    @Nullable
    private static ChatMsgItemView getChatViewByMsgType(Context context, ChatMsg msgBean) {
        int msgType = ChatMsgApi.reCalculateMsgType(msgBean.getMessageType());
        switch (msgType) {
            case ChatMsgApi.TYPE_TEXT:
                return new ChatTxtView(context, msgBean);
            case ChatMsgApi.TYPE_IMAGE:
                return new ChatImgView(context, msgBean);
            case ChatMsgApi.TYPE_FILE:
                return new ChatFileView(context, msgBean);
            case ChatMsgApi.TYPE_AUDIO:
                return new ChatAudioView(context, msgBean);
            case ChatMsgApi.TYPE_CARD:
                return new ChatCardView(context, msgBean);
            case ChatMsgApi.TYPE_POSITION:
                return new ChatPositionView(context, msgBean);
            case ChatMsgApi.TYPE_DYNAMIC:
//                return new ChatDynamicView(context, msgBean);
            case ChatMsgApi.TYPE_WEB_LINK:
            case ChatMsgApi.TYPE_NEWS:
//                return new ChatWebLinkView(context, msgBean);
            case ChatMsgApi.TYPE_MULTI:
//                return new ChatCompositeView(context, msgBean);
            case ChatMsgApi.TYPE_VIDEO:
            case ChatMsgApi.TYPE_VOICE:
//                return new ChatVideoView(context,msgBean);
            case ChatMsgApi.TYPE_WEAK_HINT:
                //      return new ChatCompositeView(context, msgBean);
                break;
        }
        return null;
    }
}
