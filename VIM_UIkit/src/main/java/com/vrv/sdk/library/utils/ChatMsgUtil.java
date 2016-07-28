package com.vrv.sdk.library.utils;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.model.ChatMsg;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.bean.MsgPropertiesBean;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 聊天消息工具类
 */
public class ChatMsgUtil {

    /**
     * 最近消息会话中显示消息简要
     *
     * @param context
     * @param msgType
     * @param msg
     * @return
     */
    public static String lastMsgBrief(Context context, int msgType, String msg) {
        msgType = ChatMsgApi.reCalculateMsgType(msgType);
        switch (msgType) {
            case ChatMsgApi.TYPE_HTML:
                return context.getString(R.string.vim_html);
            case ChatMsgApi.TYPE_TEXT:
                return ChatMsgApi.parseTxtJson(msg);
            case ChatMsgApi.TYPE_AUDIO:
                return context.getString(R.string.vim_audio);
            case ChatMsgApi.TYPE_POSITION:
                return context.getString(R.string.vim_position);
            case ChatMsgApi.TYPE_IMAGE:
                return context.getString(R.string.vim_image);
            case ChatMsgApi.TYPE_FILE:
                return context.getString(R.string.vim_file);
            case ChatMsgApi.TYPE_CARD:
                return context.getString(R.string.vim_card);
            case ChatMsgApi.TYPE_WEAK_HINT:
                String hint = ChatMsgApi.parseTxtJson(msg);
                return TextUtils.isEmpty(hint) ? context.getString(R.string.vim_weakHint) : hint;
            case ChatMsgApi.TYPE_RED_ENVELOPE:
                return "[豆豆红包:]" + ChatMsgApi.parseTxtJson(msg);
            default:
                return context.getString(R.string.vim_unKnownMsg) + msgType;
        }
    }

    /**
     * 解析msgProperties ，处理弱提示消息
     *
     * @param context
     * @param chat
     * @return
     */
    public static String parseMsgProperties(Context context, ChatMsg chat) {
        String msg = chat.getMessage();
        String msgJson = chat.getMsgProperties();
        try {
            if (msg.contains("msgBodyType")) {
                MsgPropertiesBean bean = new Gson().fromJson(msgJson, MsgPropertiesBean.class);
                int type = 1;
                if (!TextUtils.isEmpty(bean.getOperType())) {
                    type = Integer.parseInt(bean.getOperType());
                }
                String operUser = bean.getOperUser();
                String usersInfo = bean.getUsersInfo();
                String time = bean.getTime();
                JSONObject object = new JSONObject(msg);
                String msgBodyType = object.get("msgBodyType").toString();
                if (msgBodyType.equals("3")) {//群弱提示
                    if (type == 0) {
                        return context.getString(R.string.vim_hint_addGroup, usersInfo);
                    } else if (type == 1) {
                        return context.getString(R.string.vim_hint_invite, operUser, usersInfo);
                    } else if (type == 2) {
                        return context.getString(R.string.vim_hint_agree, operUser, usersInfo);
                    } else if (type == 3) {
                        return context.getString(R.string.vim_hint_exit, usersInfo);
                    } else if (type == 4) {
                        return context.getString(R.string.vim_hint_remove, usersInfo, operUser);
                    }
                } else if (msgBodyType.equals("4")) {//阅后回执
                    if (RequestHelper.isMyself(chat.getSendID())) {
                        return context.getString(R.string.vim_hint_receipt_read, operUser);
                    } else if (RequestHelper.isMyself(chat.getReceiveID())) {
                        return context.getString(R.string.vim_hint_receipt_auto, usersInfo);
                    } else {
                        return context.getString(R.string.vim_hint_receipt_other, operUser, usersInfo);
                    }
                } else if (msgBodyType.equals("5")) {//橡皮擦
                    if (RequestHelper.isMyself(chat.getTargetID())) {
                        return context.getString(R.string.vim_hint_delete_other, operUser, type == 1 ? context.getString(R.string.vim_accept) : context.getString(R.string.vim_deny));
                    } else {
                        return context.getString(R.string.vim_hint_delete_self, type == 1 ? context.getString(R.string.vim_accept) : context.getString(R.string.vim_deny), usersInfo);
                    }
                } else if (msgBodyType.equals("6")) {//抖一抖
                    if (RequestHelper.isMyself(chat.getTargetID())) {
                        return context.getString(R.string.vim_hint_shark_other, operUser);
                    } else {
                        return context.getString(R.string.vim_hint_shark_self, usersInfo);
                    }
                } else if (msgBodyType.equals("7")) {//红包
                    if (RequestHelper.isMyself(chat.getSendID())) {
                        if (TextUtils.isEmpty(time)) {
                            return context.getString(R.string.vim_hint_redPacket_self, operUser);
                        } else {
                            return context.getString(R.string.vim_hint_redPacket_self_done, time);
                        }
                    } else if (RequestHelper.isMyself(chat.getSendID())) {
                        return context.getString(R.string.vim_hint_redPacket_other, usersInfo);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }
}
