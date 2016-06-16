package com.vrv.sdk.library.ui.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.model.ChatMsg;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.action.RequestHelper;


public class ChatTxtView extends ChatMsgItemView {

    private TextView tvTxt;

    public ChatTxtView(Context context, ChatMsg messageBean) {
        super(context, messageBean);
    }

    @Override
    protected void loadView() {
        View view = View.inflate(context, R.layout.vim_view_chat_txt, this);
        tvTxt = (TextView) view.findViewById(R.id.tv_chat_text);
    }

    @Override
    protected void display() {
        if (ChatMsgApi.reCalculateMsgType(msgBean.getMessageType()) != ChatMsgApi.TYPE_TEXT) {
            if (msgBean.getMessageType() == ChatMsgApi.TYPE_RED_ENVELOPE) {
                tvTxt.setText("[豆豆红包]:" + ChatMsgApi.parseTxtJson(msgBean.getMessage()));
            } else {
                tvTxt.setText(getResources().getString(R.string.vim_unknownType, ChatMsgApi.reCalculateMsgType(msgBean.getMessageType())));
            }
        } else {
            String msg = ChatMsgApi.parseTxtJson(msgBean.getMessage());
            tvTxt.setText(msg);
//            ChatMsgUtil.handleEmojiMsg(context, msg, tvTxt);
        }
    }

    @Override
    protected void handleData() {

    }

    @Override
    protected void onClick() {

    }

    @Override
    protected void onLongClick() {

    }
}
