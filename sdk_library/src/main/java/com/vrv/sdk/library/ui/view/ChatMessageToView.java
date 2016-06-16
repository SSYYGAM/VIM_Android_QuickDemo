package com.vrv.sdk.library.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.model.ChatMsg;
import com.vrv.sdk.library.R;


/**
 * 发送者显示消息view
 * Created by Yang on 2015/8/20 020.
 */
public class ChatMessageToView extends ChatMessageView {
    private final String TAG = ChatMessageToView.class.getSimpleName();
    private byte failureMsgType = 0;

    public ChatMessageToView(Context context) {
        super(context);
    }

    public ChatMessageToView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatMessageToView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View loadContentView() {
        View view = View.inflate(context, R.layout.vim_chat_item_message_to, this);
        return view;
    }

    @Override
    protected void setBackground() {
        flMsg.setBackgroundResource(R.drawable.vim_chat_item_to_bg);
    }

    @Override
    protected void setProSend() {
        View view = findViewById(R.id.img_chat_fail);
        view.setVisibility(INVISIBLE);
        proSend.setVisibility(INVISIBLE);
        reSendFailureMsg(view);

        if (messageBean.getMsgStatus() != ChatMsg.STATUS_SENDING
                && messageBean.getMsgStatus() != ChatMsg.STATUS_UPLOAD_FAILURE
                && messageBean.getMsgStatus() != ChatMsg.STATUS_SEND_FAILURE) {
            if (ChatMsgApi.isFailureMsg(messageBean.getMessageType())) {
                messageBean.setMsgStatus(ChatMsg.STATUS_SEND_FAILURE);
            }
        }

        switch (messageBean.getMsgStatus()) {
            case ChatMsg.STATUS_NORMAL:
                break;
            case ChatMsg.STATUS_SENDING:
                proSend.setVisibility(VISIBLE);
                break;
            case ChatMsg.STATUS_UPLOAD_FAILURE:
            case ChatMsg.STATUS_SEND_FAILURE:
                view.setVisibility(VISIBLE);
                break;
        }
    }

    @Override
    protected void setName() {
        tvFromName.setVisibility(View.GONE);
    }

    @Override
    protected void displayNormalMsg() {
        super.displayNormalMsg();
    }

    private void reSendFailureMsg(final View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reSendListener != null) {
                    reSendListener.onResend(messageBean);
                }
            }
        });
    }
}
